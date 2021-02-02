package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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

public abstract class PrivateGameWaitingRoom extends MultiplayerWaitingRoomActivityFragment {
    public PrivateGameWaitingRoom() {
        super(R.layout.austen_fragment_initiators_private_game_waiting_room);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);
    }

    ListView _PlayerListView;
    ArrayList<String> _PlayerList = new ArrayList<>();
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bundle extras = getArguments();

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            TextView roomCodeView = _MultiplayerWaitingRoomActivity.findViewById(R.id.gameCodeView);
            roomCodeView.setText(_MultiPlayerConnector.getRoomCode());
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

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(_MultiplayerWaitingRoomActivity,android.R.layout.simple_list_item_1, _PlayerList);
        // Set The Adapter
        _PlayerListView.setAdapter(arrayAdapter);
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

                _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Close Game?")
                            .setMessage("If you leave you will have to rejoin the game.")
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

                case ServerConfig.newPlayerJoinedRoom:
                    //addNewPlayerToRoomList((String) socketIOEventArg._JsonObject.opt("playerName"));
                    break;
                case ServerConfig.playerNumber:
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
}
