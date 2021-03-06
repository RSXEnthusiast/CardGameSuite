package cards.MultiplayerConnection;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import cards.R;
import cards.games.MultiPlayerGameInfo;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Observable;
import java.util.Observer;

public class MultiplayerWaitingRoomActivity extends AppCompatActivity implements MultiPlayerConnector.MultiPlayerConnectorLifeCycleOwner {

    MultiPlayerConnector _MultiPlayerConnector = MultiPlayerConnector.get_Instance();
    public Handler _UIHandler;
    public static String _GameType;
    public MultiPlayerGameInfo _MultiPlayerGameInfo;



    String _CurrentFragmentClassName;

    private static final String TAG = MultiplayerWaitingRoomActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mpconnection_activity_multiplayer_waiting_room);
        //initializeFragmentResultListeners();
        _UIHandler = new Handler();

        // get notified on updates from the MultiplayerConnector
        Intent intent = getIntent();
        _GameType = (String) intent.getSerializableExtra("gameName");

        Class gameClass = ((Class) intent.getSerializableExtra("gameClass"));

        Method method = null;
        try {
            method = gameClass.getMethod("getGameInfo");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        try {
            _MultiPlayerGameInfo = (MultiPlayerGameInfo) method.invoke(null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


        _MultiPlayerConnector.setLifeCycleOwner(this);

        //_MultiPlayerConnector.FullReset(); do this in selectepublic or private fragment


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .addToBackStack(null)
                    .replace(R.id.fragment_container_view, SelectPublicOrPrivateFragment.class, null)
                    .commit();
        }


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



    protected void changeFragment(String className, Bundle bundle) {
        Class fragmentClass = null;
        try {
            fragmentClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        // a more advanced way to save memory...
     /*   FragmentManager fragmentManager = getSupportFragmentManager();

        // HideTheExistingFragment...

        if (getSupportFragmentManager().findFragmentByTag(className) != null) {
            //if the fragment exists, show it.
            fragmentManager.beginTransaction().show(fragmentManager.findFragmentByTag(className)).commit();
        } else {
            //if the fragment does not exist, add it to fragment manager.
            fragmentManager.beginTransaction().add(R.id.fragment_container_view, fragmentClass, bundle).commit();
        }
        if (fragmentManager.findFragmentByTag(className) != null) {
            //if the other fragment is visible, hide it.
            fragmentManager.beginTransaction().hide(fragmentManager.findFragmentByTag(className)).commit();
        }*/


        // this way causes a new fragment to be created each time. This means that the on create method with be fired for each fragment.
        // currently socket events are added by the fragment during on create. The onCreate for SelectPublicOrPrivate resets the socket On subscribers, so when changing to a new fragment
        // multiple on socket event callbacks don't get added. The better way would be to use the above method (more advanced way), but just reseting the socket works,
        // and adding a method to do socket.off to each fragment is not required.
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

                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        });
    }


    private Observer _MultiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {

            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            SharedPreferences sp = getSharedPreferences("fivesGameInfo", MODE_PRIVATE);
            switch (socketIOEventArg._EventName) {
                case ServerConfig.eventConnectError:
                    Snackbar.make(findViewById(R.id.multiPlayerWaitingRoomCoordinatorLayout), "Unable To Connect To Server: " + _MultiPlayerConnector.serverUrl, Snackbar.LENGTH_LONG)
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


}