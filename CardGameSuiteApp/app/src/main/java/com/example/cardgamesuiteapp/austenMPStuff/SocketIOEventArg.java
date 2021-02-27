package com.example.cardgamesuiteapp.austenMPStuff;

import android.os.Message;

import org.json.JSONObject;

public class SocketIOEventArg {

    public String _EventName;
    public JSONObject _JsonObject;
    public Exception _Exception;

    public SocketIOEventArg(String eventName, Object[] args) {
        _EventName = eventName;
        if(args!=null) {
            try {
                _JsonObject = (JSONObject) args[0];
            } catch (Exception notAJsonObjectException) {

                try {
                    _Exception = (Exception) args[0];

                } catch (Exception notAnExceptionException) {
                    _JsonObject = null;
                }

            }
        }



    }


}
