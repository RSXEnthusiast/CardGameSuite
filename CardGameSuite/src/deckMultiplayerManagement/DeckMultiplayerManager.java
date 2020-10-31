package deckMultiplayerManagement;

import java.util.HashMap;

import org.javatuples.Pair;

import decks.Deck;
import multiplayerDataManagement.MultiplayerManager;
import multiplayerDataManagement.Operation;

public abstract class DeckMultiplayerManager {
	MultiplayerManager mm;

	public DeckMultiplayerManager(Deck deck) {
		this.mm = new MultiplayerManager();
		initialize(deck);
	}

	private void initialize(Deck deck) {
		mm.sendDeck(new Pair<Operation, Deck>(Operation.initialize, deck));
	}

	public void recovery(Deck deck) {
		// in case sync is lost, send entire deck over again
	}

	public void deal(int numCards) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("numCards", numCards);
		mm.sendData(new Pair<Operation, HashMap<String, Integer>>(Operation.deal, variables));
	}

	public void playerDraw(int playerNum) {

	}

	public void playerDrawIntoIndex(int playerNum, int index) {

	}

	public void playerDrawIntoIndexFromDiscard(int playerNum, int index) {

	}

	public void discard(int player, int index) {

	}

	public void discardFromDeck() {

	}
}
