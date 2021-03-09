package com.example.cardgamesuiteapp.MultiplayerConnection;

public class ServerConfig {

    public static final String ServerURLProduction = "http://thecardgameserver.azurewebsites.net/";//"http://chatserver.azurewebsites.net/"; //Joel's IP
    //    public static final String ServerURLProduction = "http://192.168.1.160:3030/";//"http://chatserver.azurewebsites.net/"; //Austen's IP
//    public static final String ServerURLDebug = "http://192.168.56.1:3030/"; // Joel's IP
    //public static final String ServerURLDebug = "http://10.42.0.145:3030/"; // Austen's IP
    public static final String ServerURLDebug = "http://10.10.8.95:3030/";
   //public static final String ServerURLDebug = "http://thecardgameserver.azurewebsites.net/";
     //public static final String ServerURLDebug = "http://67.182.152.56:3030/"; // Grandpa Server

    //events from Server:

    //token for turn and stun servers available from server
    public static final String eventTokenOffer = "token-offer";


    public static final String eventConnectError = "EVENT_CONNECT_ERROR";

    public static final String startGame = "start-game";
    public static final String gameData = "game-data";
    public static final String playerReadyForInitialGameData = "player-ready-for-game-data";
    public static final String getReady= "get-ready";
    public static final String leaveRoom = "leave-room";
    public static final String playerNumbers = "player_numbers"; //the server sends all players number and associated name
    public static final String playerNumber = "player_number"; //the players number and the players name
    public static final String playerListRequest= "player-list-request";
    public static final String playerDisconnected= "player-disconnected";
    public static final String error= "error_";


    //------------------------- public game
    public static final String publicGameRoomRequestComplete = "public-game-room-request-complete";
    public static final String publicGameRoomRequest = "public-game-room-request";

    public static final String numActivePublicPlayers =  "num-active-public-players";
    public static final String public_game_waiting_room_player_left = "public-game-waiting-room-player-left";

    //---------------------private game

    public static final String privateGameRoomRequest = "private-game-room-request";
    public static final String privateGameInitiatorLeftTheGame = "private-game-initiator-left-game";
    public static final String joinPrivateGameRoomRequestComplete = "join-private-game-room-request-complete";
    public static final String privateGameWaitingRoomPlayerLeft = "private-game-waiting-room-player-left";
    public static final String privateGameRoomRequestComplete = "private-game-room-request-complete";
    public static final String joinPrivateGameRoom = "join-private-game-room";
    public static final String unableToFindRoom = "unable-to-find-room";
    public static final String initiatorSaysToStartGame = "initiator-start-game";
    public static final String roomPlayerCountUpdate = "room-player-count-changed";
    public static final String privateGameReadyToPlay = "game-ready-to-play";
    public static final String gameRoomDeletedByInitiator = "game-room-deleted-by-initiator";




}
