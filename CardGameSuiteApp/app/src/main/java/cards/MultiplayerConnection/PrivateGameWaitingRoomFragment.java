package cards.MultiplayerConnection;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import cards.R;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import io.socket.client.Socket;

import static android.graphics.Color.BLACK;

public class PrivateGameWaitingRoomFragment extends MultiplayerWaitingRoomActivityFragment  {
    boolean _GameCreator=false;
    String _CreatorStatusMessage = "Private Game Creator";
    String _JoinStatusMessage = "Private Game Joined";
    private static final String TAG = PrivateGameWaitingRoomFragment.class.getSimpleName();
    public PrivateGameWaitingRoomFragment() {

        super(R.layout.mpconnection_fragment_private_game_waiting_room);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);

    }

    ListView _PlayerListView;
    TextView _GameCodeView;
    TextView _NumberOfPlayersInRoomView;
    MaterialButton _StartButton;
    MaterialButton _ShareButton;
    ArrayAdapter<String> _PlayerListViewArrayAdapter;
    ArrayList<String> _PlayerList = new ArrayList<>();

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        _GameCodeView = view.findViewById(R.id.gameCodeView);
        _NumberOfPlayersInRoomView= view.findViewById(R.id.numPlayersInRoomView);
        _PlayerListView = view.findViewById(R.id.playerList);
        _StartButton = view.findViewById(R.id.startButton);

        TypedValue typedValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorOnPrimary,typedValue,true);
        @ColorInt int primaryTextColor=typedValue.data;

        int [] buttonTextColors = new int[] {
                primaryTextColor,
                Color.LTGRAY,
                BLACK,
                BLACK
        };

        ColorStateList buttonTextColorStateList= new ColorStateList(startButtonColorStates, buttonTextColors);

        TypedValue backGroundColorTypeValue = new TypedValue();
        getContext().getTheme().resolveAttribute(R.attr.colorPrimaryVariant,backGroundColorTypeValue,true);
        @ColorInt int backGroundColor=backGroundColorTypeValue.data;

        int [] buttonBackgroundColors = new int[] {
                backGroundColor,
                Color.GRAY,
                BLACK,
                BLACK
        };

        ColorStateList buttonBackgroundColorStateList= new ColorStateList(startButtonColorStates, buttonBackgroundColors);


        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {  _StartButton.setBackgroundTintList(buttonBackgroundColorStateList); _StartButton.setTextColor(buttonTextColorStateList); _StartButton.setEnabled(false);});

         Bundle extras = getArguments();
        _GameCreator= extras.getBoolean("gameCreator",false);//also get player name if creator
        String playerName=extras.getString("playerName","noName");

        _StartButton.setVisibility((_GameCreator ? View.VISIBLE:View.GONE));
        _StartButton.setOnClickListener(v -> startGame());

        _ShareButton = view.findViewById(R.id.privateGameCodeShareButton);
        _ShareButton.setOnClickListener(v -> shareGameCodeIntent());


        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            _GameCodeView.setText(_MultiPlayerConnector.getRoomCode());
            TextView statusMessage = _MultiplayerWaitingRoomActivity.findViewById(R.id.statusMessage);
            statusMessage.setText(_GameCreator ? _CreatorStatusMessage: _JoinStatusMessage);


        _PlayerListViewArrayAdapter =
                new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, _PlayerList);
        // Set The Adapter
        _PlayerListView.setAdapter(_PlayerListViewArrayAdapter);
        _PlayerListViewArrayAdapter.add(playerName);

        });



    }

    public void startGame() {
        _MultiPlayerConnector.emitEvent(ServerConfig.initiatorSaysToStartGame);
    }

    public void shareGameCodeIntent() {

        _MultiplayerWaitingRoomActivity._UIHandler.post(()-> {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = _MultiPlayerConnector.getRoomCode();
            String shareSub = "Card Game Room Code";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share room code using"));
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        _MultiPlayerConnector.emitEvent(ServerConfig.playerListRequest); //wait until subscribed to get player list
    }



    private void addNewPlayerToRoomList(JSONArray playerNames) {
        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
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

        int numberOfPlayersInRoom = _PlayerList.size();
        updatePlayerCountView(numberOfPlayersInRoom);

        startButtonIsEnabled(numberOfPlayersInRoom>=2);//change 2 to the specific  min number of players required for type of game


            _PlayerListViewArrayAdapter.notifyDataSetChanged();
        });
    }

    private void updatePlayerCountView(int numPlayers) {
        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            _NumberOfPlayersInRoomView.setText(String.valueOf(numPlayers));
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
                                    _MultiPlayerConnector.ResetConnection();

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
        _MultiPlayerConnector.emitEvent(ServerConfig.privateGameWaitingRoomPlayerLeft);
        _MultiplayerWaitingRoomActivity.changeFragment(SelectPublicOrPrivateFragment.class.getCanonicalName(), null);
    }


    private Observer multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            if(!socketIOEventArg.CompareEventWatcher(TAG)) return;

            switch (socketIOEventArg._EventName) {

               /* case ServerConfig.privateGameReadyToPlay: //not needed
                    showStartButton();
                    //addNewPlayerToRoomList((String) socketIOEventArg._JsonObject.opt("playerName"));
                    break;*/
                case ServerConfig.roomPlayerCountUpdate:

                    addNewPlayerToRoomList( (JSONArray) socketIOEventArg._JsonObject.opt("playerNames") );
                    break;
                case ServerConfig.gameRoomDeletedByInitiator:

                    informPlayerGameIsDeleted();

                    break;
                case ServerConfig.getReady:
                    //go to game
                    _MultiplayerWaitingRoomActivity.GoToGameActivity();
                    break;

            }
        }
    };

    private void informPlayerGameIsDeleted() {

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            new AlertDialog.Builder(_MultiplayerWaitingRoomActivity)
                    .setTitle("Sorry")
                    .setMessage("The creator of this game room has left and the room has been deleted.")
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    .setPositiveButton("close", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Continue with delete operation
                            goBackToSelectPrivateOrPublic();
                        }
                    })
                    // A null listener allows the button to dismiss the dialog and take no further action.
                    //.setNegativeButton(android.R.string.no, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }


    int[][] startButtonColorStates = new int[][] {
            new int[] { android.R.attr.state_enabled}, // enabled
            new int[] {-android.R.attr.state_enabled}, // disabled
            new int[] {-android.R.attr.state_checked}, // unchecked
            new int[] { android.R.attr.state_pressed}  // pressed
    };



    private void startButtonIsEnabled(boolean isEnabled) {
        if(_GameCreator){
            _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
                _StartButton.setEnabled(isEnabled);
              /*  if(isEnabled) _StartButton.setTextColor(BLACK);
                else _StartButton.setTextColor(LTGRAY); */
            });
        }
    }



    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }


    @Override
    public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {
        socket.on(ServerConfig.roomPlayerCountUpdate, args -> {
            //Log.d(TAG, "");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.roomPlayerCountUpdate, TAG, args);
            multiPlayerConnector.notifyObservers(socketIOEventArg);
        });
        socket.on(ServerConfig.gameRoomDeletedByInitiator, args -> {
            //Log.d(TAG, "unable to find room");
            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.gameRoomDeletedByInitiator, TAG, null));

        });

        socket.on(ServerConfig.getReady, args -> {
            Log.d(TAG, "start private game received");
            multiPlayerConnector.notifyObservers(new SocketIOEventArg(ServerConfig.getReady, TAG, args));
        });
    }
}
