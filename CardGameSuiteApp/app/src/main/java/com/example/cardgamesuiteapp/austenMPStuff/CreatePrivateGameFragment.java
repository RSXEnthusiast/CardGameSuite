package com.example.cardgamesuiteapp.austenMPStuff;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cardgamesuiteapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;

public class CreatePrivateGameFragment extends Fragment implements IMultiplayerConnectorSocketEventUser {
    public CreatePrivateGameFragment() {
        super(R.layout.austen_fragment_create_private_game);
        _MultiPlayerConnector.addObserver(_MultiPlayerConnectorObserver);
    }

    private static final String TAG = CreatePrivateGameFragment.class.getSimpleName();

    MultiPlayerConnector _MultiPlayerConnector = MultiPlayerConnector.get_Instance();
    MultiplayerWaitingRoomActivity _MultiplayerWaitingRoomActivity = (MultiplayerWaitingRoomActivity) getActivity();

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button createButton = view.findViewById(R.id.createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //p2psocket.emit('private-game-room-request', {numPlayersRequiredForGame:2, gameType: 'fives'})
                //_ParentActivity._MultiPlayerConnector.joinToPublicGame(2, _ParentActivity._GameType);
                TextView playerNameInput = view.findViewById(R.id.playerNameInput);

                createButton.setClickable(false); ///wait until success

                if (emitCreatePrivateGame(playerNameInput)) {

                } else {
                    createButton.setClickable(true);
                }
                //getParentFragmentManager().setFragmentResult("setGameCreator", result);
            }


        });

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
            args.put("minPlayersRequiredForGame", MultiplayerWaitingRoomActivity._MinNumPlayersRequiredForGame);
            args.put("gameType", MultiplayerWaitingRoomActivity._GameType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _MultiPlayerConnector.emitEvent(ServerConfig.privateGameRoomRequest, args);


        return true;

    }


    private void goToPrivateGameWaitingRoom() {
        Bundle result = new Bundle();
        result.putString("fragmentClassName", InitiatorsPrivateGameWaitingRoomFragment.class.getCanonicalName());
        result.putBoolean("gameCreator", true);
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("changeFragment", result);
    }


    private Observer _MultiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;

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

    //Implementation of AddSocketEvents from IMultiplayerConnectorSocketEventUser
    static void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {

        socket.on(ServerConfig.privateGameRoomRequestComplete, args -> {
            Log.d(TAG, "created Room");
            multiPlayerConnector.setRoomCode(((JSONObject) args[0]).opt("gameRoomName").toString());
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.privateGameRoomRequestComplete, null);
            multiPlayerConnector.notifyObservers(socketIOEventArg);

        });

    }

}
