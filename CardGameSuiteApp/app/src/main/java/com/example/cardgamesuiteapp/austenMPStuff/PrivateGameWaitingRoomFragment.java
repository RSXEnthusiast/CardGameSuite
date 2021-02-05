package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.cardgamesuiteapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;

public class PrivateGameWaitingRoomFragment extends MultiplayerWaitingRoomActivityFragment {
    boolean _GameCreator=false;
    String _CreatorStatusMessage = "Private Game Creator";
    String _JoinStatusMessage = "Private Game Joined";
    public PrivateGameWaitingRoomFragment() {

        super(R.layout.austen_fragment_private_game_waiting_room);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);


    }

    ListView _PlayerListView;
    ArrayList<String> _PlayerList = new ArrayList<>();
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

         Bundle extras = getArguments();
        _GameCreator= extras.getBoolean("gameCreator",false);

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            TextView roomCodeView = _MultiplayerWaitingRoomActivity.findViewById(R.id.gameCodeView);
            roomCodeView.setText(_MultiPlayerConnector.getRoomCode());

            TextView statusMessage = _MultiplayerWaitingRoomActivity.findViewById(R.id.statusMessage);
            statusMessage.setText(_GameCreator ? _CreatorStatusMessage: _JoinStatusMessage );
        });

       _PlayerListView = view.findViewById(R.id.playerList);



    }



    private void addNewPlayerToRoomList(JSONArray playerNames) {
        _PlayerList.clear();
        for (int i = 0; i < playerNames.length(); i++) {
            JSONObject player=null;
            try {
                player =playerNames.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String playerName = (String)(player.opt("playerName"));
            boolean initiator = (boolean)(player.opt("initiator"));
            String playerId = (String) (player.opt("playerId"));

            if(initiator){
                _PlayerList.add(playerName+"*"); //signify that this is the player who created the private game
            }
            else{
                _PlayerList.add(playerName);
            }



        }
        updatePlayerCountView(_PlayerList.size());

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            ArrayAdapter<String> arrayAdapter =
                    new ArrayAdapter<String>(_MultiplayerWaitingRoomActivity, android.R.layout.simple_list_item_1, _PlayerList);
            // Set The Adapter
            _PlayerListView.setAdapter(arrayAdapter);
        });
    }

    private void updatePlayerCountView(int numPlayers) {
        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            TextView numPlayersInRoomView = this.getActivity().getParent().findViewById(R.id.numPlayersInRoomView);
            numPlayersInRoomView.setText(numPlayers);
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
                showAreYouSureDialog();
            }

            private void showAreYouSureDialog() {

                String message = _GameCreator ? "If you leave, the game room will be deleted."  : "If you leave you will have to rejoin the game.";
                _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Close Game?")
                            .setMessage(message)
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    goBackToSelectPrivateOrPublic();
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                });

            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }

    private void goBackToSelectPrivateOrPublic() {
        _MultiPlayerConnector.emitEvent(ServerConfig.privateGameJoiningPlayerLeftTheGame);
        _MultiplayerWaitingRoomActivity.changeFragment(SelectPublicOrPrivateFragment.class.getCanonicalName(), null);
    }



    private Observer multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            switch (socketIOEventArg._EventName) {

               /* case ServerConfig.newPlayerJoinedRoom:
                    //addNewPlayerToRoomList((String) socketIOEventArg._JsonObject.opt("playerName"));
                    break;*/
                case ServerConfig.playerJoined:
                    ///OnRoomNotFound();
                    addNewPlayerToRoomList( (JSONArray) socketIOEventArg._JsonObject.opt("playerNames") );
                    break;
                /*case another option:
                    go to
                   break;*/

            }
        }
    };

    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }

    static void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {

        socket.on(ServerConfig.playerJoined, args -> {
            //Log.d(TAG, "");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.playerJoined, args);
            multiPlayerConnector.notifyObservers(socketIOEventArg);
        });

    }
}
