package com.example.cardgamesuiteapp.MultiplayerConnection;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.cardgamesuiteapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;

import static android.content.Context.MODE_PRIVATE;

public class JoinPrivateGameFragment extends MultiplayerWaitingRoomActivityFragment {


    public JoinPrivateGameFragment() {
        super(R.layout.mpconnection_fragment_join_private_game);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);

    }


    private static final String TAG = JoinPrivateGameFragment.class.getSimpleName();

    private String _PlayerName="";
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("playerInfo", MODE_PRIVATE);
        if (sp.contains("playerName")){
            _PlayerName= sp.getString("playerName", "");
        }

        TextView playerNameTextInput = view.findViewById(R.id.playerNameInput);
        playerNameTextInput.setText(_PlayerName);
        TextView gameCodeTextInput = view.findViewById(R.id.gameCodeInput);

        Button joinButton = view.findViewById(R.id.joinButton);

        joinButton.setOnClickListener(v -> {

            emitJoinPrivateGame(playerNameTextInput, gameCodeTextInput);

        });
    }

    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }

    private boolean emitJoinPrivateGame(TextView playerNameTextInput, TextView gameCodeTextInput) {
        String playerName = playerNameTextInput.getText().toString();
        String gameCode = gameCodeTextInput.getText().toString();

        if (playerName.isEmpty()) {

            _MultiplayerWaitingRoomActivity.badInputDialog("Player name cannot be empty");
            return false;
        }
        if (gameCode.isEmpty()) {

            _MultiplayerWaitingRoomActivity.badInputDialog("game code cannot be empty");
            return false;
        }

        JSONObject args = new JSONObject();
        try {
            args.put("playerName", playerName);
            args.put("roomName", gameCode);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("playerInfo", MODE_PRIVATE);
        sp.edit().putString("playerName", playerName).apply();

        _MultiPlayerConnector.emitEvent(ServerConfig.joinPrivateGameRoom, args);

        return true;

    }

    public void onPrivateRoomJoin() {
        Bundle result = new Bundle();
        result.putString("fragmentClassName", PrivateGameWaitingRoomFragment.class.getCanonicalName());
        _MultiplayerWaitingRoomActivity.changeFragment(PrivateGameWaitingRoomFragment.class.getCanonicalName(), result);

    }


    public void OnRoomNotFound() {

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            new AlertDialog.Builder(_MultiplayerWaitingRoomActivity)
                    .setTitle("Unable to find game")
                    .setMessage("Check with your friend to make sure your game code is still active and correct.")


                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }




    private Observer multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            if(!socketIOEventArg.CompareEventWatcher(TAG)) return;
            
            switch (socketIOEventArg._EventName) {

                case ServerConfig.privateGameRoomRequestComplete:
                    onPrivateRoomJoin();
                    break;
                case ServerConfig.unableToFindRoom:
                    OnRoomNotFound();
                    break;


            }
        }

    };

    @Override
    public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {

        socket.on(ServerConfig.unableToFindRoom, args -> {
            Log.d(TAG, "unable to find room");

            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.unableToFindRoom,TAG, null));

        });
        socket.on(ServerConfig.privateGameRoomRequestComplete, args -> {
            Log.d(TAG, "found room");
            multiPlayerConnector.setRoomCode(((JSONObject) args[0]).opt("gameRoomName").toString());
            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.privateGameRoomRequestComplete, TAG, null));
        });


    }


}
