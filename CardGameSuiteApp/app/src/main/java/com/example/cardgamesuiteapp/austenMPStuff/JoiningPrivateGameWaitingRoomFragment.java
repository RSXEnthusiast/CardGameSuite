package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.cardgamesuiteapp.R;

import java.util.Observable;
import java.util.Observer;

public class JoiningPrivateGameWaitingRoomFragment extends MultiplayerWaitingRoomActivityFragment {
    public JoiningPrivateGameWaitingRoomFragment() {
        super(R.layout.austen_fragment_initiators_private_game_waiting_room);
        SetMultiPlayerConnectorObserver(multiPlayerConnectorObserver);
    }

    private Observer multiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            switch ((String) arg) {

                case ServerConfig.newPlayerJoinedRoom:
                    addNewPlayerToRoomList();
                    break;
                /*case another option:
                    go to
                   break;*/

            }
        }
    };

    private void addNewPlayerToRoomList() {
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

                _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
                    new AlertDialog.Builder(context)
                            .setTitle("Close Game?")
                            .setMessage("If you leave you will have to rejoin the game.")
                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    goBackToSelectPrivateOrPublic();
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
        _MultiPlayerConnector.emitEvent(ServerConfig.privateGameJoiningPlayerLeftTheGame);
        _MultiplayerWaitingRoomActivity.changeFragment(SelectPublicOrPrivateFragment.class.getCanonicalName(), null);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bundle extras = getArguments();

        _MultiplayerWaitingRoomActivity._UIHandler.post(() -> {
            TextView roomCodeView = _MultiplayerWaitingRoomActivity.findViewById(R.id.gameCodeView);
            roomCodeView.setText(_MultiPlayerConnector.getRoomCode());
        });


        TextView statusMessage = view.findViewById(R.id.statusMessage);
        statusMessage.setText("Private Game Joined");


    }

    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        _MultiPlayerConnectorObserver = multiPlayerConnectorObserver;
    }


}