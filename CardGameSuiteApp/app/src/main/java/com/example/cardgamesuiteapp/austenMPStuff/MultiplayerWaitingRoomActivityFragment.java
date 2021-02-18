package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import java.net.URISyntaxException;
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
     * If your fragment has an observer property, assign it to _MultiPlayerConnectorObserver in the constructor of your fragment
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

        try {
            _MultiPlayerConnector.connectToServer(); //always try to connect back to the server. pressing back disconnects so that the socket is reset and leaves rooms.
            // so now reconnect fresh
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

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

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                disconnect();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }

            private void disconnect() {

              _MultiPlayerConnector.Close();

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }
}



