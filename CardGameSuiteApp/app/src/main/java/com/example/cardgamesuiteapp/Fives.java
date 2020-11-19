package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
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
    static Hand[] players;
    static Card discard;
    TextView instruction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_players);
        totalScores = new int[numPlayers];
        deck = new Standard(true, numPlayers);
        players = new Hand[numPlayers];
        players[0] = findViewById(R.id.player1);
        players[1] = findViewById(R.id.player2);
        discard = findViewById(R.id.discard);
        instruction = findViewById(R.id.instruction);
        newRound();
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
            visibleHands.put(i,new boolean[4]);
        }
        visibleHands.get(0)[2] = true;
        visibleHands.get(0)[3] = true;
        updateEntireScreen();
    }

    private void updateEntireScreen() {
        discard.updateImage(deck.peekTopDiscard());
        for (int i = 0; i < players.length; i++) {
            ArrayList<Integer> actualDisplay = new ArrayList<Integer>();
            ArrayList<Integer> hand = deck.getHand(i);
            for (int j = 0; j < hand.size(); j++) {
                if (visibleHands.get(i)[j]) {
                    actualDisplay.add(hand.get(j));
                } else {
                    actualDisplay.add(-1);
                }
            }
            players[i].initHand(actualDisplay);
        }
        instruction.setText("Instructions");
    }

    private static void playGame(Standard deck, int numHumans, int numAI) throws Exception {
        // Player turn 1
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < numHumans; j++) {
                System.out.println();
                visibleHands.put(j, turn(j, deck, visibleHands.get(j), false));
            }
            for (int j = numHumans; j < numAI; j++) {
                System.out.println();
                visibleHands.put(j, turn(j, deck, visibleHands.get(j), true));
            }
        }
    }

    private static boolean[] turn(int playerNum, Standard deck, boolean[] visibleHand, boolean isAI)
            throws Exception {
        int selectionInt = 0;
        if (isAI) {
            selectionInt = getAIDrawSelection(playerNum, visibleHand, deck.getHand(playerNum));
            if (selectionInt == 1) {
                System.out.println("AI decides to draw.");
            } else {
                System.out.println(
                        "AI picks up a " + Standard.convertToString(deck.peekTopDiscard()) + " from the discard.");
            }
        } else {
            System.out.println("Player " + (playerNum + 1) + "'s turn! These are your current cards:");
            System.out.print("Would you like to draw from the (1) draw pile or (2) grab "
                    + Standard.convertToString(deck.peekTopDiscard()) + " from the discard?");
        }
        int pickup = 0;
        boolean drawFromDrawPile = false;
        switch (selectionInt) {
            case 1:
                pickup = deck.peekTopDraw();
                drawFromDrawPile = true;
                break;
            case 2:
                pickup = deck.peekTopDiscard();
                drawFromDrawPile = false;
                break;
        }
        String keepDraw = "Y";
        if (drawFromDrawPile) {
            if (isAI) {
                keepDraw = getAIKeepSelection(pickup, deck.getHand(playerNum), visibleHand);
                System.out.print("AI decides to ");
                if (keepDraw.equals("Y")) {
                    System.out.print("keep");
                } else {
                    System.out.print("discard");
                }
                System.out.println(" the card.");
            } else {
                System.out.println(
                        "You picked up a " + Standard.convertToString(pickup) + ", would you like to keep it?");
            }
        }
        if (isAI) {
            selectionInt = getAILocationSelection(keepDraw, deck.getHand(playerNum), visibleHand);
        } else {
            switch (keepDraw) {
                case "Y":
                    System.out.println("Where would you like to put it?");
                    break;
                case "N":
                    System.out.println("Which card would you like to flip up?");
                    break;
            }
            int numHidden = 0;
            for (boolean isVis : visibleHand) {
                if (!isVis) {
                    numHidden++;
                }
            }
            for (int i = 0; i < selectionInt; i++) {
                if (visibleHand[i]) {
                    selectionInt++;
                }
            }
        }
        switch (keepDraw) {
            case "Y":
                if (drawFromDrawPile) {
                    deck.draw(playerNum, selectionInt - 1);
                } else {
                    deck.drawFromDiscard(playerNum, selectionInt - 1);
                }
                deck.discardByIndex(playerNum, selectionInt);
                System.out.println("You swapped a " + Standard.convertToString(pickup) + " for a "
                        + Standard.convertToString(deck.peekTopDiscard()) + ".");
                break;
            case "N":
                deck.discardFromDeck();
                System.out.println("You flipped up a "
                        + Standard.convertToString(deck.getHand(playerNum).get(selectionInt - 1)) + ".");
                break;
        }
        visibleHand[selectionInt - 1] = true;
        System.out.println("This is your hand after your turn:");
        return visibleHand;
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

    private void setUp2Players(ArrayList<Integer>[] hands) {
        LinearLayout player1 = findViewById(R.id.player1);
        LinearLayout columnsTopP1 = new LinearLayout(this);
        columnsTopP1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        columnsTopP1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        columnsTopP1.setLayoutParams(layoutParams);
        LinearLayout columnsBottom = new LinearLayout(this);
        columnsBottom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        columnsBottom.setOrientation(LinearLayout.HORIZONTAL);
        columnsBottom.setLayoutParams(layoutParams);
        player1.addView(columnsTopP1);
        player1.addView(columnsBottom);
        ArrayList<Card> cards = new ArrayList<Card>();
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        for (int i = 0; i < hands[0].size(); i++) {
            cards.add(new Card(this));
            cards.get(i).setLayoutParams(layoutParams);
            cards.get(i).updateImage(i);
        }
        columnsTopP1.addView(cards.get(0));
        columnsTopP1.addView(cards.get(1));
        columnsBottom.addView(cards.get(2));
        columnsBottom.addView(cards.get(3));
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).updateImage(i + 4);
        }
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
}