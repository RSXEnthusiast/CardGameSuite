//package app.multiplayerDataManagement;
//
//import android.content.SharedPreferences;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import org.javatuples.Triplet;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.example.cardgamesuiteapp.austenMPStuff.ServerConfig;
//import com.example.cardgamesuiteapp.austenMPStuff.SocketIOEventArg;
//import app.deckMultiplayerManagement.DeckMultiplayerManager;
//
//import java.util.Observable;
//import java.util.Observer;
//
//public class DataReceiver extends AppCompatActivity {
//    DeckMultiplayerManager dmm;
//
//    public DataReceiver(DeckMultiplayerManager dmm) {
//        this.dmm = dmm;
//    }
//
//    private Observer _MultiPlayerConnectorObserver = new Observer() {
//        @Override
//        public void update(Observable o, Object arg) {
//            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
//            if (socketIOEventArg._EventName.equals(ServerConfig.gameData)) {
//                Triplet triplet = deseralizeData((JSONObject) socketIOEventArg._JsonObject.opt("data"));
//                try {
//                    dmm.handleIncomingData(triplet);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    };
//
//    public void recieveData(JSONObject data) throws Exception {
//        /*
//         * This WILL NOT work as I believe that javascript needs to be able to call a
//         * static method. I'm not sure of a solution at this point, as it needs to be
//         * able to modify a non-static object, which obviously isn't possible using a
//         * static method
//         */
//        dmm.handleIncomingData(deseralizeData(data));
//    }
//
//    @SuppressWarnings({"rawtypes", "unchecked"})
//    private Triplet deseralizeData(JSONObject data) {
//        Triplet result = null;
//        try {
//            result = new Triplet(data.get("Time"), data.get("Operation"), data.get("Data"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }
//}
