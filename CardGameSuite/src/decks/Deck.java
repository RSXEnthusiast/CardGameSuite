package decks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Stack;

public abstract class Deck {
	private Queue<Integer> deck = new LinkedList<Integer>();
	private Stack<Integer> discard = new Stack<Integer>();
	private boolean shuffleOnEmptyDeck;
	private ArrayList<Integer>[] hands;
	int numPlayers;
	int myPlayerNum;

	public Deck(boolean shuffleOnEmptyDeck, int numPlayers) {
		this.shuffleOnEmptyDeck = shuffleOnEmptyDeck;
		this.numPlayers = numPlayers;
	}

	public void initialize(Queue<Integer> deck, ArrayList<Integer>[] hands) {
		this.deck = deck;
		this.hands = hands;
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
	}

	public ArrayList<Integer> getHand(int player) {
		return hands[player];
	}

	public boolean deckIsEmpty() {
		return deck.size() == 0;
	}

	public void draw(int playerNum) throws Exception {
		hands[playerNum].add(draw());
	}

	public void draw(int playerNum, int index) throws Exception {
		hands[playerNum].add(index, draw());
	}

	public void drawFromDiscard(int playerNum, int index) throws Exception {
		hands[playerNum].add(index, draw());
	}

	public void discardByValue(int player, int value) throws Exception {
		if (!hands[player].remove((Object) value)) {
			throw new Exception("Trying to discard a card that doesn't exist");
		} else {
			discard.add(value);
		}
	}

	public void discardByIndex(int player, int index) throws Exception {
		discard.add(hands[player].remove(index));
	}

	public void discardFromDeck() {
		discard.add(deck.poll());
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
}