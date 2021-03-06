package multiplayerDataManagement;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Queue;
import java.util.TimeZone;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.json.JSONObject;

import decks.Deck;

public class DataSender {
	private static Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("PST"));

	public static void sendCompleteDeck(Pair<Operation, Deck> data) {
		Triplet<Long, Operation, Deck> dataToSend = new Triplet<Long, Operation, Deck>(getTime(), data.getValue0(),
				data.getValue1());
		sendIntegers(dataToSend);
	}

	public static void sendJustDeck(Pair<Operation, Queue<Integer>> data) {
		Triplet<Long, Operation, Queue<Integer>> dataToSend = new Triplet<Long, Operation, Queue<Integer>>(getTime(),
				data.getValue0(), data.getValue1());
		sendIntegers(dataToSend);
	}

	public static void sendIntegers(Pair<Operation, HashMap<String, Integer>> data) {
		Triplet<Long, Operation, HashMap<String, Integer>> dataToSend = new Triplet<Long, Operation, HashMap<String, Integer>>(
				getTime(), data.getValue0(), data.getValue1());
		sendIntegers(dataToSend);
	}

	public static void sendCommand(Operation op) {
		Triplet<Long, Operation, Object> dataToSend = new Triplet<Long, Operation, Object>(getTime(), op, null);
		sendIntegers(dataToSend);
	}

	@SuppressWarnings("rawtypes")
	public static void sendIntegers(Triplet data) {
		JSONObject serialized = new JSONObject().put("data", data);
		// Code to send serialized here
	}

	private static Long getTime() {
		return calendar.getTimeInMillis();
	}

}