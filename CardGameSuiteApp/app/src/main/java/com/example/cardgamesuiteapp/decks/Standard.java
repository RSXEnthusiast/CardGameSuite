package com.example.cardgamesuiteapp.decks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Standard extends Deck {
    /**
     * Constructor for a standard deck
     * Also contains logic for creation of a standard deck and the player hand arrays
     *
     * @param shuffleOnEmptyDeck Shuffles automatically when deck is empty if true.
     * @param numPlayers         How many people are playing.
     */
    @SuppressWarnings("unchecked")
    public Standard(boolean shuffleOnEmptyDeck, int numPlayers) {
        super(shuffleOnEmptyDeck, numPlayers);
        Queue<Integer> deck = new LinkedList<Integer>();
        for (int i = 1; i <= 13 * 4; i++) {
            deck.add(i);
        }
        ArrayList<Integer>[] hands;
        hands = new ArrayList[numPlayers];
        for (int i = 0; i < hands.length; i++) {
            hands[i] = new ArrayList<Integer>();
        }
        super.initializeDeckAndHands(deck, hands);
        this.shuffleDiscardIntoDeck();
    }

    /**
     * @param num number representing card
     * @return name of file corresponding to num
     */
    public static String getCardImageFileName(int num) {
        String result = "";
        switch (num % 13) {
            case 1:
                result += "ace";
                break;
            case 2:
                result += "two";
                break;
            case 3:
                result += "three";
                break;
            case 4:
                result += "four";
                break;
            case 5:
                result += "five";
                break;
            case 6:
                result += "six";
                break;
            case 7:
                result += "seven";
                break;
            case 8:
                result += "eight";
                break;
            case 9:
                result += "nine";
                break;
            case 10:
                result += "ten";
                break;
            case 11:
                result += "jack";
                break;
            case 12:
                result += "queen";
                break;
            case 0:
                result += "king";
                break;
        }
        result += "_" + getCardSuit(num);

        return result;
    }

    /**
     * @return a lowercase string representing the cards suit
     */
    public static String getCardSuit(int num) {
        switch ((num - 1) / 13) {
            case 0:
                return "h";
            case 1:
                return "c";
            case 2:
                return "d";
            case 3:
                return "s";
        }
        return "-1";
    }

    /**
     * Compares the numerical values of two cards considering an ace to be worth 1.
     *
     * @param one first integer
     * @param two second integer
     * @return difference between two values
     */
    @Override
    public boolean compareNumericalValues(Integer one, int two) {
        return one % 13 == two % 13;
    }

    /**
     * @param i The number representing the card considering an ace to be worth 1.
     * @return the numerical value of a card.
     */
    public static int getNumericalValue(int i) {
        return (i - 1) % 13 + 1;
    }
}
