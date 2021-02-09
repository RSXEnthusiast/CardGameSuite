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
        ArrayList<String> testCards = new ArrayList<String>(Arrays.asList("ace_h", "two_h", "three_h", "four_h", "five_h", "six_h", "seven_h", "eight_h", "nine_h", "ten_h", "jack_h", "queen_h", "king_h", "ace_c", "ace_d", "ace_s"));
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
        ArrayList<String> generatedCards = new ArrayList<String>(Arrays.asList(test, test1, test2, test3, test4, test5, test6, test7, test8, test9, test10, test11, test12, test13, test14, test15));

        assertEquals(testCards, generatedCards);
    }

    @Test
    public void cardComparisonTest(){
        assertEquals(true, standardDeck.compareNumericalValues(1,1));
    }

    @Test
    public void cardValueTest(){
        assertEquals(1, standardDeck.getNumericalValue(1));
    }

    @Test
    public void curPlayersTurnTest(){
        assertEquals(0, standardDeck.getCurPlayersTurn());
    }

    @Test
    public void advancePlayerTurnsTest(){
        standardDeck.nextPlayer();
        assertEquals(1, standardDeck.getCurPlayersTurn());
    }

    @Test
    public void deckCreationTest(){
        Standard testDeck = new Standard(false, 2);
        testDeck.nextPlayer();
        assertEquals(testDeck.getCurPlayersTurn(), 1);
        assertNotEquals(testDeck.getHand(0), null);
        assertNotEquals(testDeck.getDiscardedCard(0), null);
        assertEquals(testDeck.getNumPlayers(), 2);
    }

    @Test
    public void discardCardByIndex() throws Exception {
        standardDeck.deal(5);
        int discardedCard = standardDeck.discardByIndex(0,0);
        standardDeck.discardByIndex(0,0);
        int cardInDiscard = standardDeck.getDiscardedCard(1);

        assertEquals(discardedCard, cardInDiscard);
    }

    @Test
    public void dealTest() throws Exception {
        standardDeck.deal(5);
        int cardInHand1 = standardDeck.discardByIndex(0, 0);
        int cardInHand2 = standardDeck.discardByIndex(1, 0);
        int cardInHand3 = standardDeck.discardByIndex(2, 0);

        assertTrue(cardInHand1 >= 0);
        assertTrue(cardInHand2 >= 0);
        assertTrue(cardInHand3 >= 0);
    }

    @Test
    public void curPlayerTurnTest(){
        assertEquals(0, standardDeck.getCurPlayersTurn());
    }

    @Test
    public void isMyTurnTest(){
        assertTrue(standardDeck.isMyTurn());
    }

    @Test
    public void peekTopDrawTest(){
        int topOfDeck = standardDeck.peekTopDraw();
        assertTrue(topOfDeck >= 0);
    }

    @Test
    public void peekTopDiscardTest() throws Exception {
        standardDeck.deal(5);
        standardDeck.discardByIndex(0,0);
        int topOfDiscard = standardDeck.peekTopDiscard();
        assertTrue(topOfDiscard >= 0);
    }

    @Test
    public void numberOfPlayersTest(){
        assertEquals(3, standardDeck.getNumPlayers());
    }

    @Test
    public void discardIsEmptyTest(){
        assertTrue(standardDeck.discardIsEmpty());
    }

    @Test
    public void discardFromDeckTest() throws Exception {
        int topOfDeck = standardDeck.peekTopDraw();
        standardDeck.discardFromDeck();
        int topOfDiscard = standardDeck.peekTopDiscard();
        assertEquals(topOfDeck, topOfDiscard);
    }





}
