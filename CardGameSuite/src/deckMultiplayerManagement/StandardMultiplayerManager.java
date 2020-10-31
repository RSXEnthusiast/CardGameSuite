package deckMultiplayerManagement;

import java.util.HashMap;

import org.javatuples.Pair;

import multiplayerDataManagement.Operation;
import decks.Standard;
import multiplayerDataManagement.MultiplayerManager;

public class StandardMultiplayerManager extends DeckMultiplayerManager {
	MultiplayerManager mm;

	public StandardMultiplayerManager(MultiplayerManager mm, Standard deck) {
		super(deck);
	}

	private void initialize(Standard deck) {
	}

	public void recovery(Standard deck) {
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
