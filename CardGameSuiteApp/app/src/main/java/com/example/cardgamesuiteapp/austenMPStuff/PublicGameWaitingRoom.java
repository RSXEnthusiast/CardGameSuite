package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.cardgamesuiteapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;


public class PublicGameWaitingRoom extends MultiplayerWaitingRoomActivityFragment  {
    private static final String TAG = PublicGameWaitingRoom.class.getSimpleName();

    public PublicGameWaitingRoom() {
        super(R.layout.austen_fragment_public_game_waiting_room);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);


    }

    String _DefaultFragmentStatusMessage = "Private Game Joined";
    String _StatusMessage = "Finding An Opponent";


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        requestPublicGameRoom();
       /* Bundle extras = getArguments();

        boolean gameCreator=false;
        gameCreator= extras.getBoolean("gameCreator");

        if (gameCreator) {
            TextView statusMessage = view.findViewById(R.id.statusMessage);
            statusMessage.setText(_CreatorStatusMessage);
        }*/


    }

    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }

    private void requestPublicGameRoom() {
        JSONObject obj = new JSONObject();
        try {
            obj.put("minPlayersRequiredForGame", MultiplayerWaitingRoomActivity._MinNumPlayersRequiredForGame);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            obj.put("gameType", MultiplayerWaitingRoomActivity._GameType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //emit events
        _MultiPlayerConnector.emitEvent(ServerConfig.publicGameRoomRequest, obj);
        _MultiPlayerConnector.emitEvent(ServerConfig.getNumActivePlayers);


        Log.d(TAG, "requesting public game room");
    }


    private Observer multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;

            if(!socketIOEventArg.CompareEventWatcher(TAG)) return;
            switch (socketIOEventArg._EventName) {

                case ServerConfig.NUM_ACTIVE_PUBLIC_PLAYERS:
                    updatePublicWaitingRoomActivePlayerCount(socketIOEventArg._JsonObject);
                    break;
                case ServerConfig.startGame:
                    //go to game
                    _MultiplayerWaitingRoomActivity.GoToGameActivity();
                    break;
                case ServerConfig.eventConnectError:
                    _MultiplayerWaitingRoomActivity.changeFragment(SelectPublicOrPrivateFragment.class.getCanonicalName(), null);
                    //_MultiplayerWaitingRoomActivity.badInputDialog("Unable To Connect To Server" + TAG);
                    //showBadInputDialogForTesting();
                    break;
            }
        }
    };




    public void updatePublicWaitingRoomActivePlayerCount(JSONObject jsonObject) {


        int numberOfPlayers = (int) jsonObject.opt("numPlayers");

        _MultiplayerWaitingRoomActivity._UIHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView activePlayersTextView = getActivity().findViewById(R.id.numActivePublicPlayers);
                activePlayersTextView.setText("" + numberOfPlayers);


            }
        });

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {

                _MultiPlayerConnector.emitEvent(ServerConfig.public_game_waiting_room_player_left);
                _MultiplayerWaitingRoomActivity.changeFragment(SelectPublicOrPrivateFragment.class.getCanonicalName(), null);
            }


        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

@Override
    public  void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {

        socket.on(ServerConfig.NUM_ACTIVE_PUBLIC_PLAYERS, args -> {
            Log.d(TAG, "num active players received");

            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.NUM_ACTIVE_PUBLIC_PLAYERS, TAG ,args);
            multiPlayerConnector.notifyObservers(socketIOEventArg);
        });
        socket.on(ServerConfig.publicGameRoomRequestComplete, args -> {
            Log.d(TAG, "public game room found");
            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.publicGameRoomRequestComplete, TAG, args));
        });

    socket.on(ServerConfig.startGame, args -> {
        Log.d(TAG, "start public game received");
        multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.startGame, TAG, args));
    });


    }

}
