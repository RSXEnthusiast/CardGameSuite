package cards;

import cards.decks.Standard;

import org.junit.Test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

public class DeckUnitTests {

    Standard standardDeck = new Standard(false, 3);

    @Test
    public void cardFileImageTest() {
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
    public void cardComparisonTest() {
        assertEquals(true, standardDeck.compareNumericalValues(1, 1));
    }

    @Test
    public void cardValue() {
        assertEquals(1, standardDeck.getNumericalValue(1));
    }

    @Test
    public void curPlayersTurn() {
        assertEquals(0, standardDeck.getCurPlayersTurn());
    }

    @Test
    public void advancePlayerTurns() {
        standardDeck.nextPlayer();
        assertEquals(1, standardDeck.getCurPlayersTurn());
    }

    @Test
    public void deckCreation() {
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
        int discardedCard = standardDeck.discardByIndex(0, 0);
        standardDeck.discardByIndex(0, 0);
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
    public void curPlayerTurn() {
        assertEquals(0, standardDeck.getCurPlayersTurn());
    }

    @Test
    public void isMyTurnTest() {
        assertTrue(standardDeck.isMyTurn());
    }

    @Test
    public void peekTopDrawTest() {
        int topOfDeck = standardDeck.peekTopDraw();
        assertTrue(topOfDeck >= 0);
    }

    @Test
    public void peekTopDiscard() throws Exception {
        standardDeck.deal(5);
        standardDeck.discardByIndex(0, 0);
        int topOfDiscard = standardDeck.peekTopDiscard();
        assertTrue(topOfDiscard >= 0);
    }

    @Test
    public void numberOfPlayers() {
        assertEquals(3, standardDeck.getNumPlayers());
    }

    @Test
    public void discardIsEmpty() {
        assertTrue(standardDeck.discardIsEmpty());
    }

    @Test
    public void discardFromDeck() throws Exception {
        int topOfDeck = standardDeck.peekTopDraw();
        standardDeck.discardFromDeck();
        int topOfDiscard = standardDeck.peekTopDiscard();
        assertEquals(topOfDeck, topOfDiscard);
    }

    @Test
    public void discardByNumericalValue() throws Exception {
        int topOfDeck = standardDeck.peekTopDraw();
        standardDeck.deal(5);
        assertTrue(standardDeck.discardByNumericalValue(0, topOfDeck));
    }

    @Test
    public void deckShuffle() {
        int topOfDeck = standardDeck.peekTopDraw();
        standardDeck.shuffleJustDeck();
        int newTopOfDeck = standardDeck.peekTopDraw();
        assertTrue(newTopOfDeck != topOfDeck);
    }

    @Test
    public void discardByValue() throws Exception {
        int cardToRemove = standardDeck.peekTopDraw();
        standardDeck.deal(4);
        int removedCard = standardDeck.discardByIndex(0, 0);
        assertEquals(cardToRemove, removedCard);

    }

    @Test
    public void initializeFromPeer() throws Exception {
        standardDeck.deal(1);
        ArrayList<Integer>[] beforeHands = standardDeck.getHands();

        Standard testDeck = new Standard(false, 4);
        testDeck.deal(2);
        ArrayList<Integer>[] newHands = testDeck.getHands();
//        standardDeck.initializeFromPeer(testDeck);
        ArrayList<Integer>[] hands = standardDeck.getHands();
        assertEquals(newHands, hands);
        assertNotEquals(beforeHands, hands);
    }

    @Test
    public void getHands() throws Exception {
        standardDeck.deal(1);
        ArrayList<Integer>[] hand = standardDeck.getHands();
        int test = hand[0].get(0);

        assertTrue(test >= 0);
    }

    @Test
    public void getHand() throws Exception {
        standardDeck.deal(2);
        ArrayList<Integer> player1Hand = standardDeck.getHand(0);
        int player1Card = player1Hand.get(0);
        int firstCard = standardDeck.discardByIndex(0, 0);

        assertTrue(player1Card == firstCard);

    }

    @Test
    public void shuffleDiscardIntoDeck() throws Exception {
        standardDeck.deal(3);
        standardDeck.discardByIndex(0, 0);
        int topOfDeck = standardDeck.peekTopDraw();
        int topOfDiscard = standardDeck.peekTopDiscard();
        standardDeck.shuffleDiscardIntoDeck();

        assertFalse(topOfDeck == standardDeck.peekTopDraw());
        assertTrue(standardDeck.discardIsEmpty());
    }

    @Test
    public void shuffleJustDeckTest() throws Exception {
        standardDeck.deal(4);
        int topOfDeck = standardDeck.peekTopDraw();
        int discardedCard = standardDeck.discardByIndex(0, 0);
        standardDeck.shuffleJustDeck();
        assertEquals(discardedCard, standardDeck.peekTopDiscard());
        assertNotEquals(topOfDeck, standardDeck.peekTopDraw());
    }

    @Test
    public void shuffleLeavingTopDiscardTest() throws Exception {
        standardDeck.deal(4);
        standardDeck.discardByIndex(0, 0);
        standardDeck.discardByIndex(1, 0);
        standardDeck.discardByIndex(2, 0);

        int topOfDiscard = standardDeck.peekTopDiscard();

        standardDeck.shuffleLeavingTopDiscard();
        int newTopOfDiscard = standardDeck.peekTopDiscard();
        boolean flag = true;
        int discardSize = 0;
        while (!standardDeck.discardIsEmpty()) {
            standardDeck.drawFromDiscard(0);
            discardSize++;
        }
        assertEquals(topOfDiscard, newTopOfDiscard);
        assertEquals(1, discardSize);

    }

    @Test
    public void deckIsEmptyTest() throws Exception {
        boolean initialDeckIsEmpty = standardDeck.deckIsEmpty();

        boolean deckIsEmpty = true;
        while (deckIsEmpty) {
            try {
                standardDeck.draw(0);
            } catch (Exception e) {
                deckIsEmpty = false;
            }
        }
        boolean postDeckIsEmpty = standardDeck.deckIsEmpty();

        assertFalse(initialDeckIsEmpty);
        assertTrue(postDeckIsEmpty);
    }
}
