package cards.deckMultiplayerManagement;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

import org.json.JSONException;
import org.json.JSONObject;

import cards.MultiplayerConnection.MultiPlayerConnector;
import cards.MultiplayerConnection.ServerConfig;
import cards.decks.Deck;

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
        sendData(jsonObject);
    }

    public static void flipDeck() {
        try {
            sendData(new JSONObject().put("operation", Operation.flipDeck));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void discardedFromDeck() {
        try {
            sendData(new JSONObject().put("operation", Operation.discardFromDeck));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void drewFromDeck(int cardLocation) {
        try {
            sendData(new JSONObject().put("operation", Operation.drawIntoIndex).put("location", cardLocation));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void flipCardInHand(int cardNum) {
        try {
            sendData(new JSONObject().put("operation", Operation.flipCardInHand).put("cardNum", cardNum));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void drewFromDiscard(int cardLocation) {
        try {
            sendData(new JSONObject().put("operation", Operation.drawIntoIndexFromDiscard).put("location", cardLocation));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void readyToContinue() {
        try {
            sendData(new JSONObject().put("operation", Operation.readyToContinue));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("rawtypes")
    public static void sendData(JSONObject data) {
        MultiPlayerConnector.get_Instance().emitEvent(ServerConfig.gameData, data);
    }
}
