package com.example.cardgamesuiteapp;

import io.socket.client.Socket;

public interface IMultiplayerConnectorSocketEventUser {

    /**
     * Add events related to your class (that are not already added by MultiplayerConnector) to the Socket.IO socket in the singleton Instance
     * of the MultiplayerConnector
     *
     * @param socket
     * @param multiPlayerConnector
     */
    static void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {
    }

    ;
}
