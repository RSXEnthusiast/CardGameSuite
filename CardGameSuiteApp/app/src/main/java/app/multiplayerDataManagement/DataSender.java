package app.multiplayerDataManagement;

import org.json.JSONObject;

import app.MultiplayerConnection.MultiPlayerConnector;
import app.MultiplayerConnection.ServerConfig;

public class DataSender {
    @SuppressWarnings("rawtypes")
    public static void sendData(JSONObject data) {
        MultiPlayerConnector.get_Instance().emitEvent(ServerConfig.gameData, data);
    }
}