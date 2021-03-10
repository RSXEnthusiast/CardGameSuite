package cards.MultiplayerConnection;

import org.json.JSONObject;

public class SocketIOEventArg {

    public String _EventName;
    public String _EventWatcher="";
    public JSONObject _JsonObject;
    public Exception _Exception;

    public SocketIOEventArg(String eventName, Object[] args) {
        _EventName = eventName;
        if (args != null) {
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

    public SocketIOEventArg(String eventName, String eventWatcher, Object[] args) {
        _EventName = eventName;
        _EventWatcher= eventWatcher;
        if (args != null) {
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

    public boolean CompareEventWatcher(String eventWatcher){
        if (_EventWatcher!=null){
            return _EventWatcher.equalsIgnoreCase(eventWatcher);
        }
        else{
            return false;
        }
    }


}
