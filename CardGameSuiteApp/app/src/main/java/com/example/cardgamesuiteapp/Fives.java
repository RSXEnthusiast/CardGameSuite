package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.cardgamesuiteapp.decks.Standard;
import com.example.cardgamesuiteapp.display.Card;
import com.example.cardgamesuiteapp.display.Hand;

import java.util.ArrayList;
import java.util.HashMap;

public class Fives extends AppCompatActivity {
    final static int numPlayers = 2;
    final static int numAI = 0;
    static Standard deck;
    static HashMap<Integer, boolean[]> visibleHands = new HashMap<Integer, boolean[]>();
    static int[] totalScores;
    static Hand[] viewPlayers;
    static Card viewDiscard;
    static Card viewDeck;
    static TextView viewInstruction;
    static Button viewMemorized;
    static fivesStage stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_players);
        totalScores = new int[numPlayers];
        deck = new Standard(true, numPlayers);
        viewPlayers = new Hand[numPlayers];
        viewPlayers[0] = findViewById(R.id.player1);
        viewPlayers[1] = findViewById(R.id.player2);
        viewDiscard = findViewById(R.id.discard);
        viewDeck = findViewById(R.id.deck);
        viewInstruction = findViewById(R.id.instruction);
        viewMemorized = findViewById(R.id.confirmMemorizedButton);
        viewMemorized.setOnClickListener(v -> cardsMemorized());
        newRound();
    }

    private void cardsMemorized() {
        stage = fivesStage.draw;
        updateViewInstruction();
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
        viewMemorized.setVisibility(View.INVISIBLE);
    }

    private static void updateViewInstruction() {
        viewInstruction.setText(getCurInstruction());
    }

    public static void cardTouched(int imageId, int cardNum) {
        if (!deck.isMyTurn()) {
            return;//Not your turn
        }
        switch (stage) {
            case draw:
                stageDraw(imageId, cardNum);
                break;
            case drewFromDraw:
                stageDrewFromDraw(imageId, cardNum);
                break;
            case discardedFromDraw:
                stageDiscardFromDraw(imageId, cardNum);
                break;
            case drewFromDiscard:
                stageDrewFromDiscard(imageId, cardNum);
                break;
        }
    }

    private static void stageDraw(int imageId, int cardNum) {
        if (cardNum == deck.peekTopDraw()) {
            stage = fivesStage.drewFromDraw;
            //Logic for drawn from draw pile
            viewDeck.flipCard();
            viewInstruction.setText(getCurInstruction());
        } else if (cardNum == deck.peekTopDiscard()) {
            stage = fivesStage.drewFromDiscard;
            //Logic for drawn from discard pile
            viewInstruction.setText(getCurInstruction());
        }
    }

    private static void stageDrewFromDraw(int imageId, int cardNum) {
        if (deck.peekTopDiscard() == cardNum) {
            stage = fivesStage.discardedFromDraw;
            //Logic for discarded from draw
            deck.discardFromDeck();
            System.out.println(deck.peekTopDraw());
            viewDiscard.updateImage(deck.peekTopDiscard());
            viewDeck.flipCard();
            viewDeck.updateImage(deck.peekTopDraw());
            viewInstruction.setText(getCurInstruction());
        } else if (isValidTapOnCardInHand(cardNum)) {
            stage = fivesStage.draw;//reset stage, turn over.
            //Logic for swapped with hand
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.peekTopDraw());
            viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
            try {
                deck.discardByValue(deck.getMyPlayerNum(), cardNum);
                deck.draw(deck.getMyPlayerNum(),cardLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDeck.flipCard();
            viewDeck.updateImage(deck.peekTopDraw());
            viewDiscard.updateImage(deck.peekTopDiscard());
        }
    }

    private static void stageDiscardFromDraw(int imageId, int cardNum) {
        if (isValidTapOnCardInHand(cardNum)) {
            stage = fivesStage.draw; //reset stage, turn over.
            //logic for flipping over card in hand.
            viewPlayers[deck.getMyPlayerNum()].flipCardByNum(cardNum);
            visibleHands.get(deck.getMyPlayerNum())[deck.getCardLocation(deck.getMyPlayerNum(), cardNum)] = true;
            updateViewInstruction();
        }
    }

    private static void stageDrewFromDiscard(int imageId, int cardNum) {
        if (isValidTapOnCardInHand(cardNum)) {
            stage = fivesStage.draw; //reset stage, turn over.
            //logic for drawn from discard
            viewPlayers[deck.getMyPlayerNum()].flipCardByNum(cardNum);
            visibleHands.get(deck.getMyPlayerNum())[deck.getCardLocation(deck.getMyPlayerNum(), cardNum)] = true;
            updateViewInstruction();
        }
    }

    private static boolean isValidTapOnCardInHand(int cardNum) {
        ArrayList<Integer> hand = deck.getHand(deck.getMyPlayerNum());
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) == cardNum && !visibleHands.get(deck.getMyPlayerNum())[i]) {
                return true;
            }
        }
        return false;
    }

    public void newRound() {
        try {
            deck.deal(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Flipping up discard card
        deck.discardFromDeck();
        for (int i = 0; i < numPlayers + numAI; i++) {
            visibleHands.put(i, new boolean[4]);
        }
        stage = fivesStage.memCards;
        viewMemorized.setVisibility(View.VISIBLE);
        updateEntireScreen();
    }

    private static void updateEntireScreen() {
        viewDiscard.updateImage(deck.peekTopDiscard());
        viewDeck.updateImage(deck.peekTopDraw());
        viewDeck.flipCard();
        for (int i = 0; i < viewPlayers.length; i++) {
            viewPlayers[i].initHand(deck.getHand(i));
            viewPlayers[i].flipAllCards();
        }
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
        viewInstruction.setText(getCurInstruction());
    }

    private static String getCurInstruction() {
        if (deck.isMyTurn()) {
            switch (stage) {
                case memCards:
                    return "Memorize your bottom two cards";
                case draw:
                    return "Draw a card";
                case drewFromDraw:
                    return "Discard the new card, or swap it with a face down card";
                case discardedFromDraw:
                    return "Select a face down card to flip over";
                case drewFromDiscard:
                    return "Select a face down card to swap the new card with";
            }
        }
        return "Player " + deck.getCurPlayersTurn() + "'s Turn";

    }

    public static int[] scoreGame(Standard deck) {
        int[] scores = new int[deck.getNumPlayers()];
        for (int i = 0; i < deck.getNumPlayers(); i++) {
            ArrayList<Integer> cards = deck.getHand(i);
            // Duplicates
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            for (int j = 0; j < cards.size() - 1; j++) {
                for (int k = j + 1; k < cards.size(); k++) {
                    if (Standard.compareNumericalValues(cards.get(j), cards.get(k))
                            && Standard.getNumericalValue(cards.get(j)) != 5) {
                        toRemove.add(cards.get(j));
                    }
                }
            }
            for (int num : toRemove) {
                while (cards.remove((Object) num)) {
                    // empty while
                }
            }
            // Values
            while (!cards.isEmpty()) {
                scores[i] += getFivesValue(Standard.getNumericalValue(cards.remove(0)));
            }
        }
        return scores;
    }

    private static int getFivesValue(int i) {
        if (i == 5) {
            return -5;
        }
        if (i == 13) {
            return 0;
        }
        if (i > 10) {
            return 10;
        }
        return i;
    }

    private static boolean hasWon(int[] totalScores) {
        int maxScore = 0;
        for (int i = 1; i < totalScores.length; i++) {
            if (totalScores[i] > maxScore) {
                maxScore = totalScores[i];
            }
        }
        if (maxScore < 50) {
            return false;
        }
        int numWinners = 0;
        int winnerIndex = 0;
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] == maxScore) {
                numWinners++;
                winnerIndex = i;
            }
        }
        if (numWinners > 1) {
            System.out.println(
                    "Overtime! Multiple players have the same score over 50, keep playing until one is ahead!");
            return false;
        }
        System.out.println("Player " + (winnerIndex + 1) + " wins!");
        return false;
    }

    private static int getAIDrawSelection(int playerNum, boolean[] visibleHand, ArrayList<Integer> arrayList) {
        // TODO
        return 0;
    }

    private static String getAIKeepSelection(int pickup, ArrayList<Integer> arrayList, boolean[] visibleHand) {
        // TODO
        return null;
    }

    private static int getAILocationSelection(String selectionString, ArrayList<Integer> hand, boolean[] visibleHand) {
        // TODO
        return 0;
    }

    private enum fivesStage {
        memCards, draw, drewFromDraw, discardedFromDraw, drewFromDiscard
    }
}