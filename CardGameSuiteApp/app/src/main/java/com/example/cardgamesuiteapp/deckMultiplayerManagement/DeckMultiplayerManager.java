package com.example.cardgamesuiteapp.deckMultiplayerManagement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.cardgamesuiteapp.decks.Deck;
import com.example.cardgamesuiteapp.multiplayerDataManagement.DataSender;
import com.example.cardgamesuiteapp.multiplayerDataManagement.Operation;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public abstract class DeckMultiplayerManager {

    public static void initialize(Deck deck) {
        JSONObject jsonObject = new JSONObject();
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            jsonObject.put("operation", gson.toJson(Operation.initialize));
            jsonObject.put("deck", gson.toJson(deck.getDeck(), new TypeToken<Queue<Integer>>() {
            }.getType()));
            jsonObject.put("discard", gson.toJson(deck.getDiscard(), new TypeToken<Stack<Integer>>() {
            }.getType()));
            for (int i = 0; i < deck.getNumPlayers(); i++) {
                jsonObject.put("hand" + i, gson.toJson(deck.getHand(i), new TypeToken<ArrayList<Integer>>() {
                }.getType()));
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
