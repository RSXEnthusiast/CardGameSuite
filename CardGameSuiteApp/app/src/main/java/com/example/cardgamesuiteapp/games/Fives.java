package com.example.cardgamesuiteapp.games;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import com.example.cardgamesuiteapp.MainActivity;
import com.example.cardgamesuiteapp.decks.Standard;

public class Fives {
    private static final int MAXPLAYERS = 6;
    private static final int MINPLAYERS = 2;

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        int numHumans = 0;
        int numAI = 0;
        int numPlayers = 7;
        while (numPlayers > MAXPLAYERS || numPlayers < MINPLAYERS) {
            System.out.print("Enter number of humans: ");
            numHumans = scanner.nextInt();
            System.out.print("Enter number of AI: ");
            numAI = scanner.nextInt();
            numPlayers = numHumans + numAI;
            if (numPlayers > MAXPLAYERS) {
                System.out
                        .println("\nMore than 6 players! Please either reduce the number of AI or humans playing!\n\n");
            }
            if (numPlayers < MINPLAYERS) {
                System.out.println(
                        "\nFewer than 2 players! Please either increase the number of AI or humans playing!\n\n");
            }
        }
        int[] totalScores = new int[numPlayers];
        Standard deck = new Standard(true, numPlayers);
        do {
            deck.deal(4);
            deck.discardFromDeck();
            playGame(deck, numHumans, numAI, scanner);
            int[] scores = scoreGame(deck);
            for (int i = 0; i < numHumans; i++) {
                totalScores[i] += scores[i];
                System.out.println("Player " + (i + 1) + " scored " + scores[i] + " this round.");
            }
            for (int i = numHumans; i < numAI; i++) {
                totalScores[i] += scores[i];
                System.out.println("AI " + (i + 1 - numHumans) + " scored " + scores[i] + " this round.");
            }
        } while (!hasWon(totalScores));
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

    private static void playGame(Standard deck, int numHumans, int numAI, Scanner scanner) throws Exception {
        HashMap<Integer, boolean[]> visibleHands = new HashMap<Integer, boolean[]>();
        for (int i = 0; i < numHumans + numAI; i++) {
            visibleHands.put(i, new boolean[]{false, false, false, false});
        }
        // Showing players their cards
        for (int i = 0; i < numHumans; i++) {
            System.out.print("\nPlayer " + (i + 1) + "'s turn! Enter any character when they have the device: ");
            scanner.next();
            System.out.println("Remember your cards, you won't be able to check again:");
            System.out.print("?  ");
            if (deck.getHand(i).get(2) % 13 == 10) {
                System.out.print(" ");
            }
            System.out.println("?");
            System.out.println(Standard.convertToString(deck.getHand(i).get(2)) + " "
                    + Standard.convertToString(deck.getHand(i).get(3)));
            System.out.print("Enter any character when you're done looking: ");
        }
        // Player turn 1
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < numHumans; j++) {
                System.out.println();
                visibleHands.put(j, turn(j, deck, scanner, visibleHands.get(j), false));
            }
            for (int j = numHumans; j < numAI; j++) {
                System.out.println();
                visibleHands.put(j, turn(j, deck, scanner, visibleHands.get(j), true));
            }
        }
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

    private static boolean[] turn(int playerNum, Standard deck, Scanner scanner, boolean[] visibleHand, boolean isAI)
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
            printVisibleHand(visibleHand, deck.getHand(playerNum), true);
            System.out.print("Would you like to draw from the (1) draw pile or (2) grab "
                    + Standard.convertToString(deck.peekTopDiscard()) + " from the discard?");
            selectionInt = getSelection(2, scanner);
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
                keepDraw = getSelection(scanner);
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
            printVisibleHand(visibleHand, deck.getHand(playerNum), false);
            int numHidden = 0;
            for (boolean isVis : visibleHand) {
                if (!isVis) {
                    numHidden++;
                }
            }
            selectionInt = getSelection(numHidden, scanner);
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
        printVisibleHand(visibleHand, deck.getHand(playerNum), true);
        return visibleHand;
    }

    private static void printVisibleHand(boolean[] visibleHand, ArrayList<Integer> hand, boolean qMarks) {
        String[] values = new String[4];
        int curVal = 1;
        for (int i = 0; i < 4; i++) {
            if (visibleHand[i]) {
                values[i] = Standard.convertToString(hand.get(i));
            } else if (qMarks) {
                values[i] = "?";
            } else {
                values[i] = "" + curVal++;
            }
        }
        System.out.print(values[0] + " ");
        if (values[2].length() >= 2) {
            System.out.print(" ");
        }
        if (values[2].length() == 3) {
            System.out.print(" ");
        }
        System.out.print(values[1] + "\n" + values[2] + " ");
        if (values[0].length() >= 2) {
            System.out.print(" ");
        }
        if (values[0].length() == 3) {
            System.out.print(" ");
        }
        System.out.println(values[3]);
    }

    private static int getSelection(int numOptions, Scanner scanner) {
        while (true) {
            System.out.print("Enter ");
            for (int i = 1; i < numOptions; i++) {
                System.out.print(i + "/");
            }
            System.out.print(numOptions + ": ");
            int selection = scanner.nextInt();
            if (selection > 0 && selection <= numOptions) {
                return selection;
            }
            System.out.println("Please make a valid selection!");
        }
    }

    private static String getSelection(Scanner scanner) {
        while (true) {
            System.out.print("Enter Y/N: ");
            String selection = scanner.next().toUpperCase();
            if (selection.equals("Y") | selection.equals("N")) {
                return selection;
            }
            System.out.println("Please make a valid selection!");
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
