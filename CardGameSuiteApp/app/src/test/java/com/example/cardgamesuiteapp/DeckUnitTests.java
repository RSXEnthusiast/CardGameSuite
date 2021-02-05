package com.example.cardgamesuiteapp;

import com.example.cardgamesuiteapp.decks.Standard;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.Arrays;

public class DeckUnitTests {

    Standard standardDeck = new Standard(false, 3);

    @Test
    public void cardFileImageTest(){
        ArrayList<String> testCards = new ArrayList<String>(Arrays.asList("ace_h", "two_h", "three_h", "four_h", "five_h", "six_h", "seven_h", "eight_h", "nine_h", "jack_h", "queen_h", "king_h", "ace_d", "ace_s", "ace_c"));
        String test = standardDeck.getCardImageFileName(1);
        String test1 = standardDeck.getCardImageFileName(2);
        String test2 = standardDeck.getCardImageFileName(3);
        String test3 = standardDeck.getCardImageFileName(4);
        String test4 = standardDeck.getCardImageFileName(5);
        String test5 = standardDeck.getCardImageFileName(6);
        String test6 = standardDeck.getCardImageFileName(7);
        String test7 = standardDeck.getCardImageFileName(8);
        String test8 = standardDeck.getCardImageFileName(9);
        String test9 = standardDeck.getCardImageFileName(10);
        String test10 = standardDeck.getCardImageFileName(11);
        String test11 = standardDeck.getCardImageFileName(12);
        String test12 = standardDeck.getCardImageFileName(13);
        String test13 = standardDeck.getCardImageFileName(14);
        String test14 = standardDeck.getCardImageFileName(27);
        String test15 = standardDeck.getCardImageFileName(40);
        ArrayList<String> generatedCards = new ArrayList<String>(Arrays.asList(test, test1, test2, test3, test4, test5, test7, test8, test9, test10, test11, test12, test13, test14, test15));

        assertEquals(testCards, generatedCards);






    }
}
