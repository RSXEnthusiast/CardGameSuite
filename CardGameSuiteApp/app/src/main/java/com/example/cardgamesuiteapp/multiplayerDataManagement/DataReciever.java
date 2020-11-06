package com.example.cardgamesuiteapp.multiplayerDataManagement;

import org.javatuples.Triplet;
import org.json.JSONObject;

import com.example.cardgamesuiteapp.deckMultiplayerManagement.DeckMultiplayerManager;
import com.example.cardgamesuiteapp.decks.Standard;

public class DataReciever {
	DeckMultiplayerManager dmm;

	public void recieveData(JSONObject data) throws Exception {
		/*
		 * This WILL NOT work as I believe that javascript needs to be able to call a
		 * static method. I'm not sure of a solution at this point, as it needs to be
		 * able to modify a non-static object, which obviously isn't possible using a
		 * static method
		 */
		dmm.handleIncomingData(deseralizeData(data));
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Triplet deseralizeData(JSONObject data) {
		return new Triplet(data.get("Time"), data.get("Operation"), data.get("Data"));
	}
}
