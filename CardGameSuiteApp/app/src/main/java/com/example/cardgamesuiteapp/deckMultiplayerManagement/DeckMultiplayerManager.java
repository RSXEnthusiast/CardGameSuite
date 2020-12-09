package com.example.cardgamesuiteapp.deckMultiplayerManagement;

import java.util.HashMap;
import java.util.Queue;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.example.cardgamesuiteapp.decks.Deck;
import com.example.cardgamesuiteapp.multiplayerDataManagement.DataSender;
import com.example.cardgamesuiteapp.multiplayerDataManagement.Operation;

public abstract class DeckMultiplayerManager {
	Deck deck;

	public DeckMultiplayerManager(Deck deck) {
		this.deck = deck;
	}

	public void initialize(Deck deck) {
		DataSender.sendCompleteDeck(new Pair<Operation, Deck>(Operation.initialize, deck));
	}

	public void recovery(Deck deck) {
		// in case sync is lost, send entire deck over again
	}

	public static void deal(int numCards) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("numCards", numCards);
		DataSender.sendIntegers(new Pair<Operation, HashMap<String, Integer>>(Operation.deal, variables));
	}

	public static void playerDraw(int playerNum) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("playerNum", playerNum);
		DataSender.sendIntegers(new Pair<Operation, HashMap<String, Integer>>(Operation.playerDraw, variables));
	}

	public static void playerDrawIntoIndex(int playerNum, int index) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("playerNum", playerNum);
		variables.put("index", index);
		DataSender
				.sendIntegers(new Pair<Operation, HashMap<String, Integer>>(Operation.playerDrawIntoIndex, variables));
	}

	public static void playerDrawFromDiscard(int playerNum) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("playerNum", playerNum);
		DataSender.sendIntegers(
				new Pair<Operation, HashMap<String, Integer>>(Operation.playerDrawFromDiscard, variables));
	}

	public static void playerDrawFromDiscardIntoIndex(int playerNum, int index) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("playerNum", playerNum);
		variables.put("index", index);
		DataSender.sendIntegers(
				new Pair<Operation, HashMap<String, Integer>>(Operation.playerDrawIntoIndexFromDiscard, variables));
	}

	public static void discardByValue(int playerNum, int value) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("playerNum", playerNum);
		variables.put("value", value);
		DataSender.sendIntegers(new Pair<Operation, HashMap<String, Integer>>(Operation.discardByValue, variables));
	}

	public static void discardByIndex(int playerNum, int index) {
		HashMap<String, Integer> variables = new HashMap<String, Integer>();
		variables.put("playerNum", playerNum);
		variables.put("index", index);
		DataSender.sendIntegers(new Pair<Operation, HashMap<String, Integer>>(Operation.discardByIndex, variables));
	}

	public static void discardFromDeck() {
		DataSender.sendCommand(Operation.discardFromDeck);
	}

	public static void shuffle(Queue<Integer> deck) {
		DataSender.sendJustDeck(new Pair<Operation, Queue<Integer>>(Operation.shuffle, deck));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleIncomingData(Triplet data) throws Exception {
		switch ((Operation) data.getValue1()) {
		case deal:
			dealReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case discardByIndex:
			discardByIndexReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case discardByValue:
			discardByValueReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case discardFromDeck:
			discardFromDeckReceived();
			break;
		case initialize:
			initializeReceived((Deck) data.getValue2());
			break;
		case playerDraw:
			playerDrawReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case playerDrawIntoIndex:
			playerDrawIntoIndexReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case playerDrawFromDiscard:
			playerDrawFromDiscardReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case playerDrawIntoIndexFromDiscard:
			playerDrawIntoIndexFromDiscardReceived((HashMap<String, Integer>) data.getValue2());
			break;
		case recover:
			recoverReceived((Deck) data.getValue2());
			break;
		case shuffle:
			shuffleReceived((Queue<Integer>) data.getValue2());
		default:
			break;
		}
	}

	private void playerDrawFromDiscardReceived(HashMap<String, Integer> data) throws Exception {
		deck.drawFromDiscard(data.get("playerNum"));
	}

	private void shuffleReceived(Queue<Integer> deck) {
		this.deck.setDeck(deck);
	}

	private void recoverReceived(Deck data) {
	}

	private void playerDrawIntoIndexFromDiscardReceived(HashMap<String, Integer> data) throws Exception {
		deck.drawFromDiscard(data.get("playerNum"), data.get("index"));
	}

	private void playerDrawIntoIndexReceived(HashMap<String, Integer> data) throws Exception {
		deck.draw(data.get("playerNum"), data.get("index"));
	}

	private void playerDrawReceived(HashMap<String, Integer> data) throws Exception {
		deck.draw(data.get("playerNum"));
	}

	private void initializeReceived(Deck deck) {
		deck.initializeFromPeer(deck);
	}

	private void discardFromDeckReceived() throws Exception {
		deck.discardFromDeck();
	}

	private void discardByValueReceived(HashMap<String, Integer> data) throws Exception {
		deck.discardByValue(data.get("player"), data.get("value"));
	}

	private void discardByIndexReceived(HashMap<String, Integer> data) throws Exception {
		deck.discardByIndex(data.get("player"), data.get("index"));
	}

	private void dealReceived(HashMap<String, Integer> data) throws Exception {
		deck.deal(data.get("numCards"));
	}
}
