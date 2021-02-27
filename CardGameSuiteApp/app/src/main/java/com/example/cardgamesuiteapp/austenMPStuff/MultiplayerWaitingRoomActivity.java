package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.example.cardgamesuiteapp.R;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentResultListener;

import android.os.Handler;
import android.util.Log;

import java.net.URISyntaxException;
import java.util.Observable;
import java.util.Observer;

public class MultiplayerWaitingRoomActivity extends AppCompatActivity implements MultiPlayerConnector.MultiPlayerConnectorLifeCycleOwner {

    MultiPlayerConnector _MultiPlayerConnector = MultiPlayerConnector.get_Instance();
    public Handler _UIHandler;
    public static String _GameType;
    public static int _MinNumPlayersRequiredForGame = 2;

    String _CurrentFragmentClassName;

    private static final String TAG = MultiplayerWaitingRoomActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.austen_activity_multiplayer_waiting_room);
        initializeFragmentResultListeners();
        _UIHandler = new Handler();

        // get notified on updates from the MultiplayerConnector
        Intent intent = getIntent();
        _GameType = (String) intent.getSerializableExtra("gameName");


        _MultiPlayerConnector.setLifeCycleOwner(this);

        _MultiPlayerConnector.FullReset();

       /* try {
            _MultiPlayerConnector.connectToServer();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }*/

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container_view, SelectPublicOrPrivateFragment.class, null)
                    .commit();
        }


       /* Snackbar.make(findViewById(R.id.multiPlayerWaitingRoomCoordinatorLayout), _GameType, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();*/

    }

    /**
     * Probably need to have parameters for specifying what type of game to start and and how many players,
     * like an array of player names if its a private game.
     */
    public void GoToGameActivity() {
        //switch on game type. Then load the correct game...
        _MultiPlayerConnector.removeLifeCycleOwner(this);
        
        Intent oldIntent = getIntent();
        Intent newIntent = new Intent(this, (Class) oldIntent.getSerializableExtra("gameClass"));
        newIntent.putExtra("multiplayer", true);
        newIntent.putExtra("numOnlineOpponents", _MinNumPlayersRequiredForGame - 1);
        newIntent.putExtra("numAI", 0);
        startActivity(newIntent);

        this.finish();

    }





    @Override
    protected void onStop() {
        super.onStop();
        _MultiPlayerConnector.deleteObserver(_MultiPlayerConnectorObserver);
        //_MultiPlayerConnector.removeLifeCycleOwner(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        _MultiPlayerConnector.addObserver(_MultiPlayerConnectorObserver);
        //_MultiPlayerConnector.setLifeCycleOwner(this); //allows the MultiPlayerConnector to be aware of lifecycle

    }

    @Override
    public void setMultiPlayerConnectorAsLifeCycleObserver(MultiPlayerConnector multiPlayerConnector) {
        _UIHandler.post(() -> {
            getLifecycle().addObserver(multiPlayerConnector);
        });
    }

    @Override
    public void removeMultiPlayerConnectorAsLifeCycleObserver(MultiPlayerConnector multiPlayerConnector) {
        _UIHandler.post(() -> {
            getLifecycle().removeObserver(multiPlayerConnector);
        });
    }

    static class PlayerStatus {
        static boolean _initiator = false;
        static String _PlayerName;
    }


    //Delete and just use _MultiplayerWaitingRoomActivity.changeFragment
    private void initializeFragmentResultListeners() {
        getSupportFragmentManager().setFragmentResultListener("changeFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String fragmentClassName = bundle.getString("fragmentClassName");
                _CurrentFragmentClassName = fragmentClassName;
                // Do something with the result
                changeFragment(fragmentClassName, bundle);
            }
        });


    }


    private void resetPlayerStatus() {
        PlayerStatus._initiator = false;
    }

    protected void changeFragment(String className, Bundle bundle) {
        Class fragmentClass = null;
        try {
            fragmentClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        getSupportFragmentManager().beginTransaction()
                .addToBackStack(null)
                .replace(R.id.fragment_container_view, fragmentClass, bundle)
                .commit();

    }


    /**
     * Displays a warning dialog to the user with passed message as the contents
     *
     * @param message
     */
    public void badInputDialog(String message) {

        _UIHandler.post(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Check Input")
                    .setMessage(message)
                    // Specifying a listener allows you to take an action before dismissing the dialog.
                    // The dialog is automatically dismissed when a dialog button is clicked.
                    /* .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                         public void onClick(DialogInterface dialog, int which) {
                             // Continue with delete operation
                         }
                     })*/

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }



   /* public void retryConnectionDialog(String message) {

        _UIHandler.post(() -> {
            new AlertDialog.Builder(this)
                    .setTitle("Unable to connect to server")
                    .setMessage(message)

                    .setPositiveButton("Yes", (dialog, id) -> {

                        retryConnectingToServer();
                    })
                    .setNegativeButton("No", (dialog, id) -> {
                        // if this button is clicked, just close
                        // the dialog box and do nothing
                        dialog.cancel();
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }*/

   /* private void retryConnectingToServer(){
        try {
            _MultiPlayerConnector.connectToSignallingServer();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
*/




    private Observer _MultiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            SharedPreferences sp = getSharedPreferences("fivesGameInfo", MODE_PRIVATE);
            switch (socketIOEventArg._EventName) {
                case ServerConfig.eventConnectError:
                    Snackbar.make(findViewById(R.id.multiPlayerWaitingRoomCoordinatorLayout), "Unable To Connect To Server: "+_MultiPlayerConnector.serverUrl, Snackbar.LENGTH_LONG)
                            .show();
                    break;
                case ServerConfig.playerNumber:
                    int myPlayerNumber = (int) socketIOEventArg._JsonObject.opt("playerNumber");
                    sp.edit().putInt("myNumber", myPlayerNumber).apply();
                    break;
                case ServerConfig.playerNumbers:
                    break;
            }
        }
    };

  /*  private void addPeerMessage() {

        _UIHandler.post(new Runnable() {
            @Override
            public void run() {
                TextView roomCodeView= findViewById(R.id.message_view);
                roomCodeView.setText(_MultiPlayerConnector.msg);

            }
        });
    }*/


    public void onGameReadyToPlay() {

    }
}