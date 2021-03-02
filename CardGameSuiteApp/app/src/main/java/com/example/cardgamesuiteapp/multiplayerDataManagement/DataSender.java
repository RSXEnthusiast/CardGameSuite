package com.example.cardgamesuiteapp.multiplayerDataManagement;

import org.json.JSONObject;

import com.example.cardgamesuiteapp.MultiplayerConnection.MultiPlayerConnector;
import com.example.cardgamesuiteapp.MultiplayerConnection.ServerConfig;

public class DataSender {
    @SuppressWarnings("rawtypes")
    public static void sendData(JSONObject data) {
        MultiPlayerConnector.get_Instance().emitEvent(ServerConfig.gameData, data);
    }
}