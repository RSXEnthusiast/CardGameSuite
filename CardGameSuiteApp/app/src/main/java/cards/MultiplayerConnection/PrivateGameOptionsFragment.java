package cards.MultiplayerConnection;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import cards.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;

import static android.content.Context.MODE_PRIVATE;

public class PrivateGameOptionsFragment extends MultiplayerWaitingRoomActivityFragment {
    public PrivateGameOptionsFragment() {
        super(R.layout.mpconnection_fragment_private_options);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);
    }

    private static final String TAG = PrivateGameOptionsFragment.class.getSimpleName();

    private String _PlayerName = "";

    private boolean createdGame;

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button createButton = view.findViewById(R.id.createPrivateButton);

        Button joinButton = view.findViewById(R.id.joinPrivateButton);
        SharedPreferences sp = getActivity().getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        _PlayerName = sp.getString("name", "default name");

        TextView gameCodeTextInput = view.findViewById(R.id.roomCodeInput);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdGame = true;
                TextView playerNameInput = view.findViewById(R.id.playerNameInput);
                createButton.setClickable(false); ///wait until success
                if (emitCreatePrivateGame(playerNameInput)) {
                } else {
                    createButton.setClickable(true);
                }
            }
        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createdGame = false;
                emitJoinPrivateGame(gameCodeTextInput);
            }
        });
    }

    private boolean emitCreatePrivateGame(TextView playerNameTextInput) {
        JSONObject args = new JSONObject();
        try {
            args.put("playerName", _PlayerName);
            args.put("minPlayersRequiredForGame", _MultiplayerWaitingRoomActivity._MultiPlayerGameInfo.minNumberPlayers);
            args.put("maxNumberPlayers", _MultiplayerWaitingRoomActivity._MultiPlayerGameInfo.maxNumberPlayers);
            args.put("gameType", MultiplayerWaitingRoomActivity._GameType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        _MultiPlayerConnector.emitEvent(ServerConfig.privateGameRoomRequest, args);
        return true;
    }


    private void goToPrivateGameWaitingRoom() {


        Bundle result = new Bundle();
        result.putString("fragmentClassName", PrivateGameWaitingRoomFragment.class.getCanonicalName());
        result.putBoolean("gameCreator", true);
        result.putString("playerName", _PlayerName);

        _MultiplayerWaitingRoomActivity.changeFragment(PrivateGameWaitingRoomFragment.class.getCanonicalName(),result);
        //getParentFragmentManager().setFragmentResult("changeFragment", result);
    }

    private boolean emitJoinPrivateGame(TextView gameCodeTextInput) {
        String gameCode = gameCodeTextInput.getText().toString();

        if (gameCode.isEmpty()) {
            _MultiplayerWaitingRoomActivity.badInputDialog("game code cannot be empty");
            return false;
        }

        JSONObject args = new JSONObject();
        try {
            args.put("roomName", gameCode);
            args.put("playerName", _PlayerName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        _MultiPlayerConnector.emitEvent(ServerConfig.joinPrivateGameRoom, args);

        return true;

    }

    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }

    @Override
    public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {
        socket.on(ServerConfig.unableToFindRoom, args -> {
            Log.d(TAG, "unable to find room");

            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.unableToFindRoom, TAG, null));

        });
        socket.on(ServerConfig.privateGameRoomRequestComplete, args -> {
            Log.d(TAG, "found room");
            multiPlayerConnector.setRoomCode(((JSONObject) args[0]).opt("gameRoomName").toString());
            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.privateGameRoomRequestComplete, TAG, null));
        });
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
            if (!socketIOEventArg.CompareEventWatcher(TAG)) return;

            switch (socketIOEventArg._EventName) {

                case ServerConfig.privateGameRoomRequestComplete:
                    if (createdGame) {
                        goToPrivateGameWaitingRoom();
                    } else {
                        onPrivateRoomJoin();
                    }
                    break;
                case ServerConfig.unableToFindRoom:
                    OnRoomNotFound();
                    break;
            }
        }

    };


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
               _MultiplayerWaitingRoomActivity.changeFragment(SelectPublicOrPrivateFragment.class.getCanonicalName(),null);
                //getActivity().getSupportFragmentManager().popBackStackImmediate();
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }
}
