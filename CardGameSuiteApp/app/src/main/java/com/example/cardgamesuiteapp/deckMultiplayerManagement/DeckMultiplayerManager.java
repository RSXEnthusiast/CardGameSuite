package com.example.cardgamesuiteapp.deckMultiplayerManagement;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cardgamesuiteapp.decks.Deck;
import com.example.cardgamesuiteapp.multiplayerDataManagement.DataSender;
import com.example.cardgamesuiteapp.multiplayerDataManagement.Operation;

public abstract class DeckMultiplayerManager {

    public static void initialize(Deck deck) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("operation", Operation.initialize);
            jsonObject.put("deck", (LinkedList<Integer>) deck.getDeck());
            jsonObject.put("discard", deck.getDiscard());
            for (int i = 0; i < deck.getNumPlayers(); i++) {
                jsonObject.put("hand" + i, deck.getHand(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DataSender.sendData(jsonObject);
    }

    public static void nextPlayer() {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.nextPlayer));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
