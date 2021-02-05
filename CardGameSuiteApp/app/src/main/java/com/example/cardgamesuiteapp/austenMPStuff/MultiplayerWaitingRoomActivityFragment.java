package com.example.cardgamesuiteapp.austenMPStuff;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.Observer;

public abstract class MultiplayerWaitingRoomActivityFragment extends Fragment implements IMultiPlayerConnectorObserverSubscriber {

    MultiPlayerConnector _MultiPlayerConnector = MultiPlayerConnector.get_Instance();
    Observer _MultiPlayerConnectorObserver;
    MultiplayerWaitingRoomActivity _MultiplayerWaitingRoomActivity;

    public MultiplayerWaitingRoomActivityFragment(int fragment_Rid) {
        super(fragment_Rid);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        _MultiplayerWaitingRoomActivity = (MultiplayerWaitingRoomActivity) getActivity();

    }

    /**
     * If the fragment has an observer property, assign it to _MultiPlayerConnectorObserver in the constructor of your fragment
     *
     * @param multiPlayerConnectorObserver
     */
    abstract void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver);

    @Override
    public void onPause() { //make sure events don't fire unless fragment is in use
        super.onPause();
        if (_MultiPlayerConnectorObserver != null)
            unsubscribeFromMultiPlayerConnector();


    }

    @Override
    public void onResume() { //make sure events don't fire unless fragment is in use

        super.onResume();
        if (_MultiPlayerConnectorObserver != null)
            subscribeToMultiPlayerConnector();

    }

    /**
     * Unsubscribe observers from the MultiPlayerConnector when they are not needed, parent view is not visible, or may cause null reference errors.
     */
    public void unsubscribeFromMultiPlayerConnector() {
        _MultiPlayerConnector.deleteObserver(_MultiPlayerConnectorObserver);
    }

    /**
     * Subscribe to the MultiPlayer Connector
     */
    public void subscribeToMultiPlayerConnector() {

        _MultiPlayerConnector.addObserver(_MultiPlayerConnectorObserver);
    }
}



