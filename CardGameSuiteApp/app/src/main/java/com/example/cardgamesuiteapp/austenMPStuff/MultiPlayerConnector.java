package com.example.cardgamesuiteapp.austenMPStuff;

import android.os.CountDownTimer;
import android.util.Log;

import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.example.cardgamesuiteapp.BuildConfig;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
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

    private String serverUrl = (BuildConfig.DEBUG) ? ServerConfig.ServerURLDebug : ServerConfig.ServerURLProduction;
    private Socket _Socket;

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
            _Socket = IO.socket(serverUrl); //initialize here because we don't want to do it in the constructor
            configureSocketEvents();
        }

        if (!_Socket.connected()) {
            _Socket.connect();

        }

    }



    public interface MultiPlayerConnectorEventAdder {
        public void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector);
    }

   // ArrayList<String> _SocketEventAdders = new ArrayList<>();
    public void addSocketEvents(MultiPlayerConnectorEventAdder multiPlayerConnectorEventAdder){
        //String socketEventAdderName=multiPlayerConnectorEventAdder.getClass().getCanonicalName();
       // if(_SocketEventAdders.contains(socketEventAdderName)){return;} //don't add events twice!
        //else {
            //_SocketEventAdders.add(socketEventAdderName);
            multiPlayerConnectorEventAdder.AddSocketEvents(_Socket, this);
        //}
    }


    private void configureSocketEvents() {

        IO.Options opts = new IO.Options();
        opts.transports = new String[]{WebSocket.NAME};

        addEssentialEvents();
    }


    private void addEssentialEvents() {


        _Socket.on(EVENT_CONNECT, args -> {
            //JSONObject obj = (JSONObject)args[0];
            Log.d(TAG, "Connected to server");

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

        });/*.on("ipaddr", args -> {
                Log.d(TAG, "connectToSignallingServer: ipaddr");
            }).on("created", args -> {
                Log.d(TAG, "connectToSignallingServer: created");
                isInitiator = true;
            }).on("full", args -> {
                Log.d(TAG, "connectToSignallingServer: full");
            }).on("join", args -> {
                Log.d(TAG, "connectToSignallingServer: join");
                Log.d(TAG, "connectToSignallingServer: Another peer made a request to join room");
                Log.d(TAG, "connectToSignallingServer: This peer is the initiator of room");
                isChannelReady = true;
            }).on("joined", args -> {
                Log.d(TAG, "connectToSignallingServer: joined");
                isChannelReady = true;
            }).on("log", args -> {
                for (Object arg : args) {
                    Log.d(TAG, "connectToSignallingServer: " + String.valueOf(arg));
                }
            }).on("message", args -> {
                Log.d(TAG, "connectToSignallingServer: got a message");
            }).on("message", args -> {
                try {
                    if (args[0] instanceof String) {
                        String message = (String) args[0];
                        if (message.equals("got user media")) {
                            maybeStart();
                        }
                    } else {
                        JSONObject message = (JSONObject) args[0];
                        Log.d(TAG, "connectToSignallingServer: got message " + message);
                        if (message.getString("type").equals("offer")) {
                            Log.d(TAG, "connectToSignallingServer: received an offer " + isInitiator + " " + isStarted);
                            if (!isInitiator && !isStarted) {
                                maybeStart();
                            }
                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(OFFER, message.getString("sdp")));
                            doAnswer();
                        } else if (message.getString("type").equals("answer") && isStarted) {
                            peerConnection.setRemoteDescription(new SimpleSdpObserver(), new SessionDescription(ANSWER, message.getString("sdp")));
                        } else if (message.getString("type").equals("candidate") && isStarted) {
                            Log.d(TAG, "connectToSignallingServer: receiving candidates");
                            IceCandidate candidate = new IceCandidate(message.getString("id"), message.getInt("label"), message.getString("candidate"));
                            peerConnection.addIceCandidate(candidate);
                        }
                        *//*else if (message === 'bye' && isStarted) {
                        handleRemoteHangup();
                    }*//*
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }).on(EVENT_DISCONNECT, args -> {
                Log.d(TAG, "connectToSignallingServer: disconnect");
            });*/

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
    }


    Queue<SocketIOEventArg> _SocketEventsReceivedWhileAppSleeping = new LinkedList<>();
 boolean _AppIsSleeping = false;
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    @Override
    public void onResume(LifecycleOwner owner) {
        _AppIsSleeping=false;
        new Thread(new Runnable(){
            public void run() {
                try {
                    Thread.sleep(100); // give time for subscribers to resubscribe
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // do something here
                while(!_SocketEventsReceivedWhileAppSleeping.isEmpty()){
                    notifyObservers(_SocketEventsReceivedWhileAppSleeping.poll());
                }
               //send events
            }
        }).start();

    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    @Override
    public void onPause(LifecycleOwner owner) {
             _AppIsSleeping=true;
    }

    @Override
    public void notifyObservers(Object arg) {
        if(_AppIsSleeping){
            _SocketEventsReceivedWhileAppSleeping.add((SocketIOEventArg) arg);
            return;
        }
        setChanged();
        super.notifyObservers(arg);

    }


    /*public void emitEvent(String emitEvent, boolean emitWithObject) {
        if (emitWithObject){
            _Socket.emit(emitEvent,_EmitObject);
        }
        else _Socket.emit(emitEvent);
    }*/

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

    /**
     * Closes the socket.io socket.
     */
    public void Close() {

        _Socket.disconnect();
        _RoomCode="";

    }

    public void Reset() {

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


