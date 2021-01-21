package com.example.cardgamesuiteapp.austenMPStuff;

public interface IMultiPlayerConnectorObserverSubscriber {

    /**
     * Unsubscribe observers from the MultiPlayerConnector when they are not needed, parent view is not visible, or may cause null reference errors.
     */
    void unsubscribeFromMultiPlayerConnector();

    /**
     * Subscribe to the MultiPlayer Connector
     */
    void subscribeToMultiPlayerConnector();
}
