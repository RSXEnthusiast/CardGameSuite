package com.example.cardgamesuiteapp.decks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Standard extends Deck {

    @SuppressWarnings("unchecked")
    public Standard(boolean shuffleOnEmptyDeck, int numPlayers) {
        super(shuffleOnEmptyDeck, numPlayers);
        Queue<Integer> deck = new LinkedList<Integer>();
        for (int i = 1; i <= 13 * 4; i++) {
            deck.add(i);
        }
        this.shuffle();
        ArrayList<Integer>[] hands;
        hands = new ArrayList[numPlayers];
        for (int i = 0; i < hands.length; i++) {
            hands[i] = new ArrayList<Integer>();
        }
        super.initializeFromSubclass(deck, hands);
    }

    public static String convertToString(int num) {
        String result = "";
        switch (num % 13) {
            case 1:
                result += "A";
                break;
            case 11:
                result += "J";
                break;
            case 12:
                result += "Q";
                break;
            case 0:
                result += "K";
                break;
            default:
                result += num % 13;
                break;
        }
        switch ((num - 1) / 13) {
            case 0:
                result += "H";
                break;
            case 1:
                result += "C";
                break;
            case 2:
                result += "D";
                break;
            case 3:
                result += "S";
                break;
        }
        return result;
    }

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
        result += "_";
        switch ((num - 1) / 13) {
            case 0:
                result += "h";
                break;
            case 1:
                result += "c";
                break;
            case 2:
                result += "d";
                break;
            case 3:
                result += "s";
                break;
        }
        result += ".png";
        return result;
    }

    public static boolean compareNumericalValues(int one, int two) {
        return one % 13 == two % 13;
    }

    public static int getNumericalValue(int i) {
        return (i - 1) % 13 + 1;
    }
}
