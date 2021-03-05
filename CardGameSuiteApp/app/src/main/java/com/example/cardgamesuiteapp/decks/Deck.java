package com.example.cardgamesuiteapp.decks;

import com.example.cardgamesuiteapp.deckMultiplayerManagement.DeckMultiplayerManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import org.json.JSONObject;

public abstract class Deck implements Serializable {
    private Queue<Integer> deck = new LinkedList<Integer>();
    private Stack<Integer> discard = new Stack<Integer>();
    private boolean shuffleOnEmptyDeck;
    private ArrayList<Integer>[] hands;
    int numPlayers;
    int myPlayerNum;
    int curPlayersTurn;

    /**
     * Constructor for a base deck
     *
     * @param shuffleOnEmptyDeck decides whether the deck will shuffle automatically when out of cards
     * @param numPlayers         How many players are in the game
     */
    public Deck(boolean shuffleOnEmptyDeck, int numPlayers, int myPlayerNum) {
        this.shuffleOnEmptyDeck = shuffleOnEmptyDeck;
        this.numPlayers = numPlayers;
        this.myPlayerNum = myPlayerNum;
    }

    public Deck(boolean shuffleOnEmptyDeck, int numPlayers) {
        this.shuffleOnEmptyDeck = shuffleOnEmptyDeck;
        this.numPlayers = numPlayers;
    }

    /**
     * A method to set the deck object and the hand object at the same time, intended to be called from a sub class.
     *
     * @param deck  a deck object parameter to be set to the deck object of this class
     * @param hands the hand object parameter to be set to the hands object of this class
     */
    public void initializeDeckAndHands(Queue<Integer> deck, ArrayList<Integer>[] hands) {
        setHands(hands);
        setDeck(deck);
    }

    /**
     * Initializes this object to be identical to one passed from a peer (used for multi-player)
     *
     * @param deck The Deck object passed from a peer.
     */
    public void initializeFromPeer(JSONObject deck) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        this.deck = gson.fromJson((String) deck.opt("deck"), new TypeToken<Queue<Integer>>() {
        }.getType());
        this.discard = gson.fromJson((String) deck.opt("discard"), new TypeToken<Stack<Integer>>() {
        }.getType());
        for (int i = 0; i < numPlayers; i++) {
            this.hands[i] = gson.fromJson((String) deck.opt("hand" + i), new TypeToken<ArrayList<Integer>>() {
            }.getType());
        }
    }

    /**
     * Moves to the next player
     */
    public void nextPlayer(boolean multiplayer) {
        nextPlayerFromPeer();
//        if (multiplayer) {
//            DeckMultiplayerManager.nextPlayer();
//        }
    }

    public void nextPlayerFromPeer() {
        curPlayersTurn = (curPlayersTurn + 1) % numPlayers;
    }

    /**
     * @return the hands array
     */
    public ArrayList<Integer>[] getHands() {
        return hands;
    }

    /**
     * @return the shuffleOnEmptyDeck variable
     */
    private boolean getShuffleOnEmptyDeck() {
        return shuffleOnEmptyDeck;
    }

    /**
     * @return the discard object
     */
    public Stack<Integer> getDiscard() {
        return discard;
    }

    /**
     * @return the deck object
     */
    public Queue<Integer> getDeck() {
        return deck;
    }

    /**
     * A helper method for when a method wants the value of the top of the deck. This method removes the value.
     *
     * @return The card that is on top of the deck
     * @throws Exception if the deck is empty and shuffleOnEmptyDeck is false
     */
    private int draw() throws Exception {
        if (deck.isEmpty() && !shuffleOnEmptyDeck) {
            // TODO choose better exception
            throw new Exception("The deck is empty. You cannot draw a card from an empty deck.");
        } else if (deck.isEmpty()) {
            shuffleLeavingTopDiscard();
        }
        return deck.poll();
    }

    /**
     * A helper method for when a method wants the value of the top of the deck. This method removes the value.
     *
     * @return the card that is on top of the discard
     * @throws Exception if the discard is empty
     */
    private Integer drawFromDiscard() throws Exception {
        if (discard.isEmpty()) {
            // TODO choose better exception
            throw new Exception("Trying to draw a card from discard that doesn't exist");
        }
        return discard.pop();
    }

    /**
     * Removes all cards from the discard, puts them back in the deck, and shuffles the deck.
     */
    public void shuffleDiscardIntoDeck() {
        while (!discard.isEmpty()) {
            deck.add(discard.pop());
        }
        shuffleJustDeck();
    }

    /**
     * Shuffles the deck without touching the discard
     */
    public void shuffleJustDeck() {
        int[] temp = new int[deck.size()];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = deck.poll();
        }
        Random rand = new Random();
        for (int i = 0; i < temp.length * 2; i++) {
            int spot1 = rand.nextInt(temp.length);
            int spot2 = rand.nextInt(temp.length);
            int tempInt = temp[spot1];
            temp[spot1] = temp[spot2];
            temp[spot2] = tempInt;
        }
        for (int i : temp) {
            deck.add(i);
        }
    }

    /**
     * Removes all but the top card from the discard, puts them back in the deck, and shuffles the deck.
     */
    public void shuffleLeavingTopDiscard() {
        int topDiscard = discard.pop();
        while (!discard.isEmpty()) {
            deck.add(discard.pop());
        }
        discard.add(topDiscard);
        shuffleJustDeck();
    }

    /**
     * Deals cards to the players, based upon the number of players set in the constructor.
     *
     * @param numCards How many cards to deal to each player.
     * @throws Exception if there are not enough cards in the deck and discard combined, or if there are not enough cards in the deck and shuffleOnEmpty is false.
     */
    public void deal(int numCards) throws Exception {
        if (numCards * hands.length > deck.size() + discard.size()) {
            // TODO choose better exception
            throw new Exception("Trying to deal more cards than there are in the entire deck");
        }
        if (numCards * hands.length > deck.size() && !shuffleOnEmptyDeck) {
            // TODO choose better exception
            throw new Exception("Trying to deal more cards than there are left in the remaining deck.");
        }
        for (int i = 0; i < numCards; i++) {
            for (int j = 0; j < hands.length; j++) {
                if (deck.isEmpty()) {
                    shuffleDiscardIntoDeck();
                }
                hands[j].add(draw());
            }
        }
    }

    /**
     * Returns the hand of a player
     *
     * @param player the player or which hand needs to be returned.
     * @return hand of the passed in player.
     */
    public ArrayList<Integer> getHand(int player) {
        return hands[player];
    }

    /**
     * Returns whether the deck is empty or not
     *
     * @return true if the deck is empty, false otherwise
     */
    public boolean deckIsEmpty() {
        return deck.size() == 0;
    }

    /**
     * Takes a card from the deck and puts it in a player's hand
     *
     * @param playerNum the number of the player drawing the card
     * @throws Exception if the deck is empty and shuffleOnEmptyDeck is false
     */
    public void draw(int playerNum) throws Exception {
        hands[playerNum].add(draw());
    }

    /**
     * Takes a card from the deck and puts it in a players hand in a specific spot.
     *
     * @param playerNum the number of the player drawing the card
     * @param index     Where to put the card in the specified player's hand
     * @throws Exception if the deck is empt and shuffleOnEmptyDeck is false
     */
    public void draw(int playerNum, int index) throws Exception {
        hands[playerNum].add(index, draw());
    }

    /**
     * Takes a card from the discard and puts it in a player's hand
     *
     * @param playerNum The player of which to put the card in
     * @throws Exception if the discard is empty
     */
    public void drawFromDiscard(int playerNum) throws Exception {
        hands[playerNum].add(drawFromDiscard());
    }

    /**
     * Takes a card from the discard and puts it in a players hand in a specific spot.
     *
     * @param playerNum the number of the player drawing the card
     * @param index     Where to put the card in the specified player's hand
     * @throws Exception if the discard is empty
     */
    public void drawFromDiscard(int playerNum, int index) throws Exception {
        hands[playerNum].add(index, drawFromDiscard());
    }

    public boolean discardByNumericalValue(int playerNum, int value) {
        for (int i = 0; i < hands[playerNum].size(); i++) {
            if (compareNumericalValues(hands[playerNum].get(i), value)) {
                discard.add(hands[playerNum].remove(i));
                return true;
            }
        }
        return false;
    }

    /**
     * Takes a card from a player's hand and puts it on the discard based upon the value.
     *
     * @param playerNum the number of the player discarding the card
     * @param value     The value to be discarded
     */
    public boolean discardByValue(int playerNum, int value) {
        if (!hands[playerNum].remove((Object) value)) {
            return false;
        }
        discard.add(value);
        return true;
    }

    /**
     * Takes a card from a player's hand and puts it on the discard based upon the index.
     *
     * @param playerNum the number of the player drawing the card
     * @param index     The location of the card to be discarded
     */
    public int discardByIndex(int playerNum, int index) {
        int temp = hands[playerNum].remove(index);
        discard.add(temp);
        return temp;
    }

    /**
     * Takes a card directly from the deck and puts it in the discard
     */
    public void discardFromDeck() throws Exception {
        if (deck.isEmpty() && !shuffleOnEmptyDeck) {
            throw new Exception("Trying to discard from an empty deck");
        } else if (deck.isEmpty()) {
            shuffleDiscardIntoDeck();
        }
        discard.add(deck.poll());
    }

    /**
     * Does not modify the discard.
     *
     * @return The value of the top card on the discard.
     */
    public int peekTopDiscard() {
        return discard.peek();
    }

    /**
     * Does not modify the draw pile.
     *
     * @return The value of the top card on the draw pile.
     */
    public int peekTopDraw() {
        if (deck.isEmpty() && shuffleOnEmptyDeck) {
            shuffleLeavingTopDiscard();
        }
        return deck.peek();
    }

    /**
     * @return The number of players in the game
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * @return the current player number
     */
    public int getMyPlayerNum() {
        return myPlayerNum;
    }

    /**
     * Sets the deck to a different deck
     *
     * @param deck The new deck value
     */
    public void setDeck(Queue<Integer> deck) {
        this.deck = deck;
    }

    /**
     * @return which hand in the array who's turn it is
     */
    public boolean isMyTurn() {
        return curPlayersTurn == myPlayerNum;
    }

    /**
     * @return the current player's turn
     */
    public int getCurPlayersTurn() {
        return curPlayersTurn;
    }

    /**
     * Sets the hands to a different set of hands
     *
     * @param hands The new hands value
     */
    public void setHands(ArrayList<Integer>[] hands) {
        this.hands = hands;
    }

    /**
     * Gets the location of a card in a player's hand based upon the card value
     *
     * @param playerNum the number of the player drawing the card
     * @param cardNum   The value representing the card
     * @return where the card is in the player's hand
     */
    public int getCardLocation(int playerNum, int cardNum) {
        for (int i = 0; i < getHand(playerNum).size(); i++) {
            if (getHand(playerNum).get(i) == cardNum) {
                return i;
            }
        }
        return -1;
    }

    public int whichPlayerHasCard(int cardNum){
        for(int i = 0; i < hands.length; i++){
            ArrayList<Integer> hand = getHand(i);
            for(int j = 0; j < hand.size(); j++){
                if(hand.get(j) == cardNum)
                    return i;
            }
        }
        return -1;
    }

    protected abstract boolean compareNumericalValues(Integer integer, int value);

    /**
     * @return true if the discard is empty, false if not.
     */
    public boolean discardIsEmpty() {
        return discard.isEmpty();
    }

    public int getDiscardedCard(int i) {
        if (discard.size() > i) {
            int temp = discard.pop();
            int result = discard.peek();
            discard.push(temp);
            return result;
        } else {
            return -1;
        }
    }
}