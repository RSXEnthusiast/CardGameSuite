package com.example.cardgamesuiteapp.decks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

import com.example.cardgamesuiteapp.deckMultiplayerManagement.DeckMultiplayerManager;

public abstract class Deck {
    private Queue<Integer> deck = new LinkedList<Integer>();
    private Stack<Integer> discard = new Stack<Integer>();
    private boolean shuffleOnEmptyDeck;
    private ArrayList<Integer>[] hands;
    int numPlayers;
    int myPlayerNum;
    int playersTurn;
    int curPlayersTurn;

    public Deck(boolean shuffleOnEmptyDeck, int numPlayers) {
        this.shuffleOnEmptyDeck = shuffleOnEmptyDeck;
        this.numPlayers = numPlayers;
    }

    public void initializeFromSubclass(Queue<Integer> deck, ArrayList<Integer>[] hands) {
        this.deck = deck;
        this.hands = hands;
    }

    public void initializeMyPlayerNum(int myPlayerNum) {
        this.myPlayerNum = myPlayerNum;
    }

    public void initializeFromPeer(Deck deck) {
        this.deck = deck.getDeck();
        this.discard = deck.getDiscard();
        this.shuffleOnEmptyDeck = deck.getShuffleOnEmptyDeck();
        this.hands = deck.getHands();
        this.numPlayers = deck.getNumPlayers();
        this.playersTurn = deck.getPlayersTurn();
        this.curPlayersTurn = deck.getCurPlayersTurn();
    }

    protected int getPlayersTurn() {
        return playersTurn;
    }

    public ArrayList<Integer>[] getHands() {
        return hands;
    }

    private boolean getShuffleOnEmptyDeck() {
        return shuffleOnEmptyDeck;
    }

    private Stack<Integer> getDiscard() {
        return discard;
    }

    private Queue<Integer> getDeck() {
        return deck;
    }

    private int draw() throws Exception {
        if (deck.isEmpty() && !shuffleOnEmptyDeck) {
            // TODO choose better exception
            throw new Exception("Trying to draw card that doesn't exist");
        } else if (deck.isEmpty()) {
            shuffle();
        }
        return deck.poll();
    }

    public void shuffle() {
        while (!discard.isEmpty()) {
            deck.add(discard.pop());
        }
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
        DeckMultiplayerManager.shuffle(this.deck);
    }

    private Integer drawFromDiscard() throws Exception {
        if (discard.isEmpty()) {
            // TODO choose better exception
            throw new Exception("Trying to draw a card from discard that doesn't exist");
        }
        return discard.pop();
    }

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
                    shuffle();
                }
                hands[j].add(draw());
            }
        }
        DeckMultiplayerManager.deal(numCards);
    }

    public ArrayList<Integer> getHand(int player) {
        return hands[player];
    }

    public boolean deckIsEmpty() {
        return deck.size() == 0;
    }

    public void draw(int playerNum) throws Exception {
        hands[playerNum].add(draw());
        DeckMultiplayerManager.playerDraw(playerNum);
    }

    public void draw(int playerNum, int index) throws Exception {
        hands[playerNum].add(index, draw());
        DeckMultiplayerManager.playerDrawIntoIndex(playerNum, index);
    }

    public void drawFromDiscard(int playerNum) throws Exception {
        hands[playerNum].add(drawFromDiscard());
        DeckMultiplayerManager.playerDrawFromDiscard(playerNum);
    }

    public void drawFromDiscard(int playerNum, int index) throws Exception {
        hands[playerNum].add(index, drawFromDiscard());
        DeckMultiplayerManager.playerDrawFromDiscardIntoIndex(playerNum, index);
    }

    public void discardByValue(int playerNum, int value) throws Exception {
        if (!hands[playerNum].remove((Object) value)) {
            throw new Exception("Trying to discard a card that doesn't exist");
        } else {
            discard.add(value);
        }
        DeckMultiplayerManager.discardByValue(playerNum, value);
    }

    public void discardByIndex(int playerNum, int index) throws Exception {
        discard.add(hands[playerNum].remove(index));
        DeckMultiplayerManager.discardByIndex(playerNum, index);
    }

    public void discardFromDeck() {
        discard.add(deck.poll());
        DeckMultiplayerManager.discardFromDeck();
    }

    public int peekTopDiscard() {
        return discard.peek();
    }

    public int peekTopDraw() {
        return deck.peek();
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getMyPlayerNum() {
        return myPlayerNum;
    }

    public void setDeck(Queue<Integer> deck) {
        this.deck = deck;
    }

    public boolean isMyTurn() {
        return playersTurn == myPlayerNum;
    }

    public int getCurPlayersTurn() {
        return curPlayersTurn;
    }
}