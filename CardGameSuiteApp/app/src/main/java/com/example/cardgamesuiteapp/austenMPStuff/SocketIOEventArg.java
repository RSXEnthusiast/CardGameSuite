package com.example.cardgamesuiteapp.austenMPStuff;

public class SocketIOEventArg {

    public String _EventName;
    public Object _Arg;//JSONObject

    public SocketIOEventArg(String eventName, Object arg) {
        _EventName = eventName;
        _Arg = arg;

    }


}
