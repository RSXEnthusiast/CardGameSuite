package com.example.cardgamesuiteapp.MultiplayerConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.cardgamesuiteapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;




public class CreatePrivateGameFragment extends MultiplayerWaitingRoomActivityFragment  {
    public CreatePrivateGameFragment() {
        super(R.layout.mpconnection_fragment_create_private_game);

        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);


        Observer multiPlayerConnectorObservers = new Observer() {
            @Override
            public void update(Observable o, Object arg) {

            }
        };

    }

    private static final String TAG = CreatePrivateGameFragment.class.getSimpleName();


    String _PlayerName="";
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("playerInfo", Context.MODE_PRIVATE);
        if (sp.contains("playerName")){
            _PlayerName= sp.getString("playerName", "");
        }
        TextView playerNameInput = view.findViewById(R.id.playerNameInput);
        playerNameInput.setText(_PlayerName);

        Button createButton = view.findViewById(R.id.createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView playerNameInput = view.findViewById(R.id.playerNameInput);

                createButton.setClickable(false); ///wait until success

                if (emitCreatePrivateGame(playerNameInput)) {

                } else {
                    createButton.setClickable(true);
                }

            }


        });

    }

    @Override
    void SetMultiPlayerConnectorObserver(Observer thisMultiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver=thisMultiPlayerConnectorObserver;
    }


    /**
     * if Player name is valid, emits a privateGameRoomRequest, else returns false
     *
     * @param playerNameTextInput
     * @return
     */
    private boolean emitCreatePrivateGame(TextView playerNameTextInput) {
        String playerName = playerNameTextInput.getText().toString();

        if (playerName.isEmpty()) {
            _MultiplayerWaitingRoomActivity.badInputDialog("Player name cannot be empty");
            return false;
        }

        JSONObject args = new JSONObject();
        try {
            args.put("playerName", playerName);
            args.put("minPlayersRequiredForGame", _MultiplayerWaitingRoomActivity._MultiPlayerGameInfo.minNumberPlayers);
            args.put("maxNumberPlayers", _MultiplayerWaitingRoomActivity._MultiPlayerGameInfo.maxNumberPlayers);
            args.put("gameType", MultiplayerWaitingRoomActivity._GameType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _PlayerName=playerName;
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("playerInfo", Context.MODE_PRIVATE);
        sp.edit().putString("playerName", _PlayerName).apply();
        _MultiPlayerConnector.emitEvent(ServerConfig.privateGameRoomRequest, args);

        return true;

    }



    private void goToPrivateGameWaitingRoom() {
        Bundle result = new Bundle();
        result.putString("fragmentClassName", PrivateGameWaitingRoomFragment.class.getCanonicalName());
        result.putBoolean("gameCreator", true);
        result.putString("playerName", _PlayerName);
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("changeFragment", result);
    }


    private Observer multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            if(!socketIOEventArg.CompareEventWatcher(TAG)) return;

            switch (socketIOEventArg._EventName) {

                case ServerConfig.privateGameRoomRequestComplete:
                    goToPrivateGameWaitingRoom();
                    break;
                /*case another option:
                    go to
                   break;*/

            }
        }
    };


    @Override
    public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {

        socket.on(ServerConfig.privateGameRoomRequestComplete, args -> {
            Log.d(TAG, "created Room");
            multiPlayerConnector.setRoomCode(((JSONObject) args[0]).opt("gameRoomName").toString());
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.privateGameRoomRequestComplete,TAG, null);
            multiPlayerConnector.notifyObservers(socketIOEventArg);

        });

    }

}
