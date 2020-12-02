package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cardgamesuiteapp.decks.Standard;
import com.example.cardgamesuiteapp.display.Card;
import com.example.cardgamesuiteapp.display.Hand;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Fives extends AppCompatActivity {
    final static int numHumans = 1;
    final static int numAI = 1;
    static Standard deck;
    static int[] totalScores;// Keeps track of the cumulative score of the game
    static Hand[] viewPlayers;// The custom player views
    static Card viewDiscard;// The discard view
    static Card viewDeck;// The deck view
    static TextView viewInstruction;// The instruction view
    static Button viewConfirm;// The button the user will press once they've memorized their cards
    static TextView[] viewPlayerNames;// The textViews of the player names
    static TextView[] viewPlayerScores;// The textView of the player scores.
    static fivesStage stage;// The current stage of play
    static int winnerIndex;
    ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_players);
        totalScores = new int[numHumans + numAI];
        deck = new Standard(true, numHumans + numAI);
        viewPlayers = new Hand[numHumans + numAI];
        viewPlayers[0] = findViewById(R.id.player1);
        viewPlayers[1] = findViewById(R.id.player2);
        viewDiscard = findViewById(R.id.discard);
        viewDeck = findViewById(R.id.deck);
        viewInstruction = findViewById(R.id.instruction);
        viewConfirm = findViewById(R.id.confirmButton);
        viewConfirm.setOnClickListener(v -> confirmButtonTapped());//call confirmButtonTapped when that button is tapped
        viewPlayerNames = new TextView[numAI + numHumans];
        viewPlayerScores = new TextView[numHumans + numAI];
        viewPlayerNames[0] = findViewById(R.id.player1name);
        viewPlayerNames[1] = findViewById(R.id.player2name);
        viewPlayerScores[0] = findViewById(R.id.player1score);
        viewPlayerScores[1] = findViewById(R.id.player2score);
        newGame();
    }

    /**
     * When confirmMemorizedButton is tapped, this method is called.
     */
    private void confirmButtonTapped() {
        switch ((String) viewConfirm.getText()) {
            case "Memorized":
                stage = fivesStage.draw;
                updateViewInstruction();
                viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
                viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
                viewConfirm.setVisibility(View.INVISIBLE);
                break;
            case "Continue":
                stage = fivesStage.memCards;
                viewConfirm.setText("Memorized");
                newRound();
                break;
            case "New Game":
                stage = fivesStage.memCards;
                viewConfirm.setText("Memorized");
                newGame();
        }
    }

    /**
     * Updates view instruction based upon current stage
     */
    private static void updateViewInstruction() {
        viewInstruction.setText(getCurInstruction());
    }

    private static void updateViewScores() {
        for (int i = 0; i < viewPlayerScores.length; i++) {
            viewPlayerScores[i].setText("" + totalScores[i]);
        }
    }

    /**
     * Walks through the stages of a players turn and switches the value of the enum and calls the relevant game method during each phase
     *
     * @param cardNum the value of the card that was tapped on
     */
    public static void cardTouched(int cardNum) {
        if (!deck.isMyTurn()) {
            return;//Not your turn
        }
        switch (stage) {
            case draw:
                stageDraw(cardNum);
                break;
            case drewFromDraw:
                stageDrewFromDraw(cardNum);
                break;
            case discardedFromDraw:
                stageDiscardFromDraw(cardNum);
                break;
            case drewFromDiscard:
                stageDrewFromDiscard(cardNum);
                break;
        }
    }

    /**
     * This method is called when a card is touched and the current stage is draw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDraw(int cardNum) {
        if (cardNum == deck.peekTopDraw()) {
            stage = fivesStage.drewFromDraw;
            viewDeck.flipCard();
            viewInstruction.setText(getCurInstruction());
        } else if (cardNum == deck.peekTopDiscard()) {
            stage = fivesStage.drewFromDiscard;
            viewInstruction.setText(getCurInstruction());
        }
    }

    /**
     * This method is called when a card is touched and the current stage is drewFromDraw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDrewFromDraw(int cardNum) {
        boolean endTurn = false;
        if (deck.peekTopDiscard() == cardNum) {
            stage = fivesStage.discardedFromDraw;
            //Logic for discarded from draw
            deck.discardFromDeck();
            viewDiscard.updateImage(deck.peekTopDiscard());
            viewDeck.flipCard();
            viewDeck.updateImage(deck.peekTopDraw());
            updateViewInstruction();
        } else if (isValidTapOnCardInHand(cardNum)) {
            endTurn = true;
            stage = fivesStage.draw;//reset stage, turn over.
            //Logic for swapped with hand
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.peekTopDraw());
            viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
            try {
                deck.discardByValue(deck.getMyPlayerNum(), cardNum);
                deck.draw(deck.getMyPlayerNum(), cardLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDeck.flipCard();
            viewDeck.updateImage(deck.peekTopDraw());
            viewDiscard.updateImage(deck.peekTopDiscard());
            updateViewInstruction();
        }
        if (endTurn) {
            if (roundOver()) {
                scoreRound();
            }
            deck.nextPlayer();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numHumans) {
                runAITurns();
            }
        }
    }


    /**
     * This method is called when a card is touched and the current stage is discardedFromDraw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDiscardFromDraw(int cardNum) {
        boolean endTurn = false;
        if (isValidTapOnCardInHand(cardNum)) {
            endTurn = true;
            stage = fivesStage.draw; //reset stage, turn over.
            //logic for flipping over card in hand.
            viewPlayers[deck.getMyPlayerNum()].flipCardByNum(cardNum);
            updateViewInstruction();
        }
        if (endTurn) {
            if (roundOver()) {
                scoreRound();
            }
            deck.nextPlayer();
            System.out.println(deck.getCurPlayersTurn());
            if (numAI > 0 && deck.getCurPlayersTurn() >= numHumans) {
                runAITurns();
            }
        }
    }

    /**
     * This method is called when a card is touched and the current stage is drewFromDiscard
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDrewFromDiscard(int cardNum) {
        boolean endTurn = false;
        if (isValidTapOnCardInHand(cardNum)) {
            endTurn = true;
            stage = fivesStage.draw; //reset stage, turn over.
            //logic for drawn from discard
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.peekTopDiscard());
            viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
            try {
                deck.drawFromDiscard(deck.getMyPlayerNum(), cardLocation);
                deck.discardByValue(deck.getMyPlayerNum(), cardNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDiscard.updateImage(deck.peekTopDiscard());
            updateViewInstruction();
        }
        if (endTurn) {
            if (roundOver()) {
                scoreRound();
            }
            deck.nextPlayer();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numHumans) {
                runAITurns();
            }
        }
    }


    /**
     * @param cardNum the value of the card that was tapped on
     * @return true if the card is in the hand and is face down
     */
    private static boolean isValidTapOnCardInHand(int cardNum) {
        ArrayList<Integer> hand = deck.getHand(deck.getMyPlayerNum());
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) == cardNum && !viewPlayers[deck.getMyPlayerNum()].isCardFaceUp(i)) {
                return true;
            }
        }
        return false;
    }

    private void newGame() {
        deck.shuffleDiscardIntoDeck();
        for (int i = 0; i < totalScores.length; i++) {
            totalScores[i] = 0;
        }
        updateViewScores();
        newRound();
    }


    /**
     * Called every time that there is a new round, to reset the table
     */
    public static void newRound() {
        try {
            deck.deal(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Flipping up discard card
        deck.discardFromDeck();
        stage = fivesStage.memCards;
        viewConfirm.setVisibility(View.VISIBLE);
        updateEntireScreen();
    }

    /**
     * This method updates every single dynamic item on the screen.
     */
    private static void updateEntireScreen() {
        viewDiscard.updateImage(deck.peekTopDiscard());
        viewDeck.updateImage(deck.peekTopDraw());
        viewDeck.setFaceUp(false);
        for (int i = 0; i < viewPlayers.length; i++) {
            viewPlayers[i].initHand(deck.getHand(i));
            viewPlayers[i].flipAllCards();
        }
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
        viewInstruction.setText(getCurInstruction());
    }

    /**
     * @return the instruction text based on the current stage, or which player's turn it is
     */
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
                case gameOver:
                    return viewPlayerNames[winnerIndex].getText() + " won!";
            }
        }
        return "Player " + deck.getCurPlayersTurn() + "'s Turn";
    }

    /**
     * A simple method to check whether the round is complete or not.
     *
     * @return true if the round is over, false if not.
     */
    private static boolean roundOver() {
        for (int i = 0; i < viewPlayers.length; i++) {
            for (int j = 0; j < viewPlayers[i].getNumCards(); j++) {
                if (!viewPlayers[i].isCardFaceUp(j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * This method is used to score the game after each round.
     */
    public static void scoreRound() {
        for (int i = 0; i < deck.getNumPlayers(); i++) {
            // Duplicates
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            for (int j = 0; j < deck.getHand(i).size() - 1; j++) {
                for (int k = j + 1; k < deck.getHand(i).size(); k++) {
                    // If it isn't a 5(worth -5) or a K(worth 0) remove it.
                    if (deck.compareNumericalValues(deck.getHand(i).get(j), deck.getHand(i).get(k))
                            && Standard.getNumericalValue(deck.getHand(i).get(j)) != 5) {
                        toRemove.add(deck.getHand(i).get(j));
                    }
                }
            }
            //Remove all instances of the duplicate numbers
            for (int num : toRemove) {
                while (deck.discardByNumericalValue(i, num)) {
                    //Empty while
                }
            }
            // Adding current round values to score
            while (!deck.getHand(i).isEmpty()) {
                totalScores[i] += getFivesValue(Standard.getNumericalValue(deck.discardByIndex(i, 0)));
            }
        }
        updateViewScores();
        if (hasWon()) {
            stage = fivesStage.gameOver;
            updateViewInstruction();
            viewConfirm.setText("New Game");
        } else {
            viewConfirm.setText("Continue");
        }
        viewConfirm.setVisibility(View.VISIBLE);
    }

    /**
     * @param i the card value
     * @return the numerical value of the card in fives
     */
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

    /**
     * @return true if a player has won
     */
    private static boolean hasWon() {
        int maxScore = 0;
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] > maxScore) {
                maxScore = totalScores[i];
            }
        }
        if (maxScore < 50) {
            return false;
        }
        int numWinners = 0;
        winnerIndex = 0;
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] == maxScore) {
                numWinners++;
                winnerIndex = i;
            }
        }
        if (numWinners > 1) {
            return false;
        }
        return true;
    }

    /**
     * Called when it's the AI turns
     */
    private static void runAITurns() {
        for (int i = numHumans; i < numAI + numHumans; i++) {
            boolean drawFromDiscard = getAIDrawFromDiscard();
            boolean keepDraw = true;
            if (!drawFromDiscard) {
                AIDrawFromPile();
                if (!getAIKeepSelection()) {
                    keepDraw = false;
                }
            }
            int location = getAILocationSelection(drawFromDiscard, keepDraw);
            if (drawFromDiscard) {
                AIDrewFromDiscard(location);
            } else if (keepDraw) {
                AIKeptDraw(location);
            } else {
                AIDiscardedDraw(location);
            }
            deck.nextPlayer();
        }
    }

    /**
     * This updates the screen after the AI has decided to draw from the draw pile.
     */
    private static void AIDrawFromPile() {
        viewDeck.flipCard();
        viewInstruction.setText(getCurInstruction());
    }

    /**
     * This is the logic for updating the screen after the AI has kept a drawn card
     *
     * @param location where the card should be swapped to
     */
    private static void AIKeptDraw(int location) {
        //Logic for swapped with hand
        viewPlayers[deck.getCurPlayersTurn()].updateCard(location, deck.peekTopDraw());
        viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
        try {
            deck.discardByIndex(deck.getCurPlayersTurn(), location);
            deck.draw(deck.getCurPlayersTurn(), location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewDeck.flipCard();
        viewDeck.updateImage(deck.peekTopDraw());
        viewDiscard.updateImage(deck.peekTopDiscard());
        if (roundOver()) {
            scoreRound();
        }
    }


    private static void AIDiscardedDraw(int location) {
        //logic for flipping over card in hand.
        viewPlayers[deck.getMyPlayerNum()].flipCardByNum(location);
    }

    private static void AIDrewFromDiscard(int location) {
        //logic for drawn from discard
        viewPlayers[deck.getCurPlayersTurn()].updateCard(location, deck.peekTopDiscard());
        viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
        try {
            deck.drawFromDiscard(deck.getCurPlayersTurn(), location);
            deck.discardByValue(deck.getCurPlayersTurn(), location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewDiscard.updateImage(deck.peekTopDiscard());
        if (roundOver()) {
            scoreRound();
        }
    }

    /**
     * The AI will decide whether to pickup the discard or not.
     *
     * @return true if draw from deck, false if draw from discard
     */
    private static boolean getAIDrawFromDiscard() {
        return false;
    }

    /**
     * If the AI drew from the pile they will decide whether to keep the card or not
     *
     * @return true if the AI wants to pick it up, false if not.
     */
    private static boolean getAIKeepSelection() {
        return true;
    }

    /**
     * The AI will decide which card to flip up or swap
     *
     * @return the location of the card to flip up or swap
     */
    private static int getAILocationSelection(boolean drawFromDiscard, boolean keepDraw) {
        for (int i = 0; i < viewPlayers[deck.getCurPlayersTurn()].getNumCards(); i++) {
            if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(i)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * The stages of the fives game turns for each player
     */
    private enum fivesStage {
        memCards, draw, drewFromDraw, discardedFromDraw, drewFromDiscard, gameOver
    }
}