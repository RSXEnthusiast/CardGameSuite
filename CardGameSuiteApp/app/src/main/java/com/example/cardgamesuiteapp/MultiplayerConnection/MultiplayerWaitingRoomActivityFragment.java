package com.example.cardgamesuiteapp.MultiplayerConnection;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.net.URISyntaxException;
import java.util.Observer;

import io.socket.client.Socket;

public abstract class MultiplayerWaitingRoomActivityFragment extends Fragment implements IMultiPlayerConnectorObserverSubscriber,  MultiPlayerConnector.MultiPlayerConnectorEventAdder{

    MultiPlayerConnector _MultiPlayerConnector = MultiPlayerConnector.get_Instance();
    Observer _MultiPlayerConnectorObserver;
    MultiplayerWaitingRoomActivity _MultiplayerWaitingRoomActivity;
    boolean _SocketEventsAdded=false; // only add socket events on once!

    public MultiplayerWaitingRoomActivityFragment(int fragment_Rid) {
        super(fragment_Rid);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _MultiplayerWaitingRoomActivity = (MultiplayerWaitingRoomActivity) getActivity();


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(!_SocketEventsAdded) {
            _SocketEventsAdded = true;
            _MultiPlayerConnector.addSocketEvents(this);
        }
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

       // _MultiPlayerConnector.addSocketEvents(this); //add back this fragments events

        if (_MultiPlayerConnectorObserver != null)
            subscribeToMultiPlayerConnector();


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _MultiPlayerConnector.removeSocketEvents(this);
    }

    /**
     * Unsubscribe observers from the MultiPlayerConnector when they are not needed, parent view is not visible, or may cause null reference errors.
     */
    public void unsubscribeFromMultiPlayerConnector() {
        _MultiPlayerConnector.deleteObserver(_MultiPlayerConnectorObserver);
       // _MultiPlayerConnector.ResetSocketEvents();
    }

    /**
     * Subscribe to the MultiPlayer Connector
     */
    public void subscribeToMultiPlayerConnector() {

        _MultiPlayerConnector.addObserver(_MultiPlayerConnectorObserver);
    }

    @Override
    public abstract void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector);

    @Override
    public void RemoveSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector){};

   /* @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                _MultiPlayerConnector.ResetConnection();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }*/
}



