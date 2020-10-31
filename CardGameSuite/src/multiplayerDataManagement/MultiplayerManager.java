package multiplayerDataManagement;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.TimeZone;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import decks.Deck;

public class MultiplayerManager {
	Calendar calendar;

	public MultiplayerManager() {
		TimeZone pst = TimeZone.getTimeZone("PST");
		calendar = new GregorianCalendar(pst);
		calendar.setTimeZone(pst);
	}

	public void sendData(Pair<Operation, HashMap<String, Integer>> data) {
		Triplet<Long, Operation, HashMap<String, Integer>> dataToSend = new Triplet<Long, Operation, HashMap<String, Integer>>(
				getTime(), data.getValue0(), data.getValue1());
		sendTriplet(dataToSend);
	}

	public void sendDeck(Pair<Operation, Deck> data) {
		Triplet<Long, Operation, Deck> dataToSend = new Triplet<Long, Operation, Deck>(getTime(), data.getValue0(),
				data.getValue1());
		sendTriplet(dataToSend);
	}

	@SuppressWarnings("rawtypes")
	private void sendTriplet(Triplet dataToSend) {
		// code to send over socket.io-p2p goes here
	}

	private Long getTime() {
		return calendar.getTimeInMillis();
	}
}