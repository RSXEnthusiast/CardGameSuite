package app.deckMultiplayerManagement;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import app.decks.Deck;
import app.multiplayerDataManagement.DataSender;
import app.multiplayerDataManagement.Operation;
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

    public static void flipDeck() {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.flipDeck));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void discardedFromDeck() {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.discardFromDeck));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void drewFromDeck(int cardLocation) {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.drawIntoIndex).put("location", cardLocation));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void flipCardInHand(int cardNum) {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.flipCardInHand).put("cardNum", cardNum));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void drewFromDiscard(int cardLocation) {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.drawIntoIndexFromDiscard).put("location", cardLocation));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void readyToContinue() {
        try {
            DataSender.sendData(new JSONObject().put("operation", Operation.readyToContinue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
