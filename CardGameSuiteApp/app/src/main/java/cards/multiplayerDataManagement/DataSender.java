package cards.multiplayerDataManagement;

import org.json.JSONObject;

import cards.MultiplayerConnection.MultiPlayerConnector;
import cards.MultiplayerConnection.ServerConfig;

public class DataSender {
    @SuppressWarnings("rawtypes")
    public static void sendData(JSONObject data) {
        MultiPlayerConnector.get_Instance().emitEvent(ServerConfig.gameData, data);
    }
}