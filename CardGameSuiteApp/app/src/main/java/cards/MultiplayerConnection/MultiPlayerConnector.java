package cards.MultiplayerConnection;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import cards.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Queue;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.engineio.client.transports.WebSocket;


import static io.socket.client.Socket.EVENT_CONNECT;
import static io.socket.client.Socket.EVENT_CONNECT_ERROR;
import static io.socket.client.Socket.EVENT_DISCONNECT;


public class MultiPlayerConnector extends Observable implements DefaultLifecycleObserver {
    private static final String TAG = "MultiPlayerConnector";

    private static MultiPlayerConnector _Instance = null;

    public String serverUrl = (BuildConfig.DEBUG) ? ServerConfig.ServerURLDebug : ServerConfig.ServerURLProduction;
    private Socket _Socket;
    private boolean _Connecting;

    public boolean Connected() {

        return (_Socket != null) ?  _Socket.connected(): false;
    }

    private String _RoomCode;

    public String getRoomCode() {
        return _RoomCode;
    }

    public void setRoomCode(String roomCode) {
        setChanged();
        this._RoomCode = roomCode;
    }




    private JSONArray _TurnStunServers;

    private MultiPlayerConnector() {

    }

    public static MultiPlayerConnector get_Instance() {
        if (_Instance == null)
            _Instance = new MultiPlayerConnector();

        return _Instance;
    }


    /**
     * Call this method to connected to the Socket.io Server associated with your game. Uses url placed in ServerConfig.ServerURLProduction or ServerConfig.ServerURLDebug
     *
     * @throws URISyntaxException
     */
    public void connectToServer() throws URISyntaxException {
        if (_Socket == null) {
            IO.Options opts = new IO.Options();
            opts.transports = new String[]{WebSocket.NAME};

            _Socket = IO.socket(serverUrl); //initialize here because we don't want to do it in the constructor
            addEssentialEvents();
        }

        if (!_Socket.connected() && !_Connecting) {
            _Connecting=true;
            _Socket.connect();
        }

    }



    public interface MultiPlayerConnectorEventAdder {
        public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector);
        public void RemoveSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector);
    }

   // ArrayList<String> _SocketEventAdders = new ArrayList<>();
    public void addSocketEvents(MultiPlayerConnectorEventAdder multiPlayerConnectorEventAdder){

            multiPlayerConnectorEventAdder.AddSocketEvents(_Socket, this);
    }
    public void removeSocketEvents(MultiPlayerConnectorEventAdder multiPlayerConnectorEventAdder){

        multiPlayerConnectorEventAdder.RemoveSocketEvents(_Socket, this);
    }


    private void addEssentialEvents() {


        _Socket.on(EVENT_CONNECT, args -> {
            //JSONObject obj = (JSONObject)args[0];
            Log.d(TAG, "Connected to server");
            _Connecting=false;
        }).on(EVENT_CONNECT_ERROR, args -> {
            Log.d(TAG, "Unable to connect to server");
            _Connecting=false;
        }).on("public-game-room-request", args -> {
            Log.d(TAG, "requesting public game room");
        }).on(EVENT_CONNECT_ERROR, args -> {
            Log.d(TAG, "failed to connect:");
            for (int i = 0; i < args.length; i++) {
                Log.d(TAG, args[i].toString());

            }
            notifyObservers(new SocketIOEventArg(ServerConfig.eventConnectError, args));

        }).on("token-offer", args -> {
            Log.d(TAG, "tokens for twilio recieved");
            _TurnStunServers = (JSONArray) args[0];

        }).on(EVENT_DISCONNECT, args -> {
            Log.d(TAG, "socket.io socket disconnected");

        });

        _Socket.on(ServerConfig.startGame, args -> {
            Log.d(TAG, "start game received");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.startGame, null);
            notifyObservers(socketIOEventArg);
        });
        _Socket.on(ServerConfig.gameData, args -> {
//            Log.d(TAG, "gameData");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.gameData, args);
            notifyObservers(socketIOEventArg);
        });
        _Socket.on(ServerConfig.playerNumber, args -> {
            Log.d(TAG, "playerNumber received");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.playerNumber, args);
            notifyObservers(socketIOEventArg);
        });
        _Socket.on(ServerConfig.playerNumbers, args -> {
            Log.d(TAG, "playerNumbersReceived");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.playerNumbers, args);
            notifyObservers(socketIOEventArg);
        });
        _Socket.on(ServerConfig.playerDisconnected, args -> {
            Log.d(TAG, "playerDisconnected");
            SocketIOEventArg socketIOEventArg = new SocketIOEventArg(ServerConfig.playerDisconnected, args);
            notifyObservers(socketIOEventArg);
        });
    }

    public void setLifeCycleOwner(MultiPlayerConnectorLifeCycleOwner owner){
        owner.setMultiPlayerConnectorAsLifeCycleObserver(this);
        _CurrentLifeCycleOwner=owner;
    }

    public void removeLifeCycleOwner(MultiPlayerConnectorLifeCycleOwner owner){
        owner.removeMultiPlayerConnectorAsLifeCycleObserver(this);
        _CurrentLifeCycleOwner=null;
    }


    public void changeLifeCycleOwner(MultiPlayerConnectorLifeCycleOwner owner){
        if(_CurrentLifeCycleOwner!=null) {
            Activity activity = (Activity) _CurrentLifeCycleOwner;
            if (activity != null)
                _CurrentLifeCycleOwner.removeMultiPlayerConnectorAsLifeCycleObserver(this);
        }

        setLifeCycleOwner(owner);
    }

    MultiPlayerConnectorLifeCycleOwner _CurrentLifeCycleOwner=null;

    public interface MultiPlayerConnectorLifeCycleOwner { // make sure your activity only has control over the MultiPlayerConnector's lifecycle events when it needs to
         void setMultiPlayerConnectorAsLifeCycleObserver(MultiPlayerConnector multiPlayerConnector);

         void removeMultiPlayerConnectorAsLifeCycleObserver(MultiPlayerConnector multiPlayerConnector);
    }

    Queue<SocketIOEventArg> _SocketEventsReceivedWhileCurrentLifeCycleActivityPause = new LinkedList<>();
    boolean _CurrentLifeCycleActivityIsPaused = false;

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume(LifecycleOwner owner) {
        if(!(owner instanceof MultiPlayerConnectorLifeCycleOwner)){
            return;
        }
        _CurrentLifeCycleActivityIsPaused =false;
        new Thread(new Runnable(){
            public void run() {
                try {
                    Thread.sleep(100); // give time for subscribers to resubscribe
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // do something here
                while(!_SocketEventsReceivedWhileCurrentLifeCycleActivityPause.isEmpty()){
                    notifyObservers(_SocketEventsReceivedWhileCurrentLifeCycleActivityPause.poll());
                }
               //send events
            }
        }).start();

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause(LifecycleOwner owner) {
        if(!(owner instanceof MultiPlayerConnectorLifeCycleOwner)){
            return;
        }
        _CurrentLifeCycleActivityIsPaused =true;
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        if(owner instanceof MultiPlayerConnectorLifeCycleOwner){
            ((MultiPlayerConnectorLifeCycleOwner) owner).removeMultiPlayerConnectorAsLifeCycleObserver(this);
            _CurrentLifeCycleActivityIsPaused =false;
        }
    }

    @Override
    public void notifyObservers(Object arg) {

        if(_CurrentLifeCycleActivityIsPaused){
            _SocketEventsReceivedWhileCurrentLifeCycleActivityPause.add((SocketIOEventArg) arg);
            return;
        }
        setChanged();
        super.notifyObservers(arg);

    }



    public void emitEvent(String emitEvent, JSONObject obj) {
        _Socket.emit(emitEvent, obj);
    }

    public void emitEvent(String emitEvent) {
        _Socket.emit(emitEvent);
    }

    public void ResetSocketEvents() {
        _Socket.off();
        addEssentialEvents();
    }

    public void FullReset() {

        if(_Socket!=null) {
            _Socket.disconnect();

            _Socket = null;
        }
        _RoomCode = "";
        try {
            connectToServer();
        } catch (URISyntaxException e) {

        }

    }

    /**
     * Closes the socket.io socket.
     */
    public void Close() {

        _Socket.disconnect();
        _RoomCode="";

    }

    public void ResetConnection() {

       Close();
       //_Socket.off();
       //addEssentialEvents();
        try {
            connectToServer();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


}


