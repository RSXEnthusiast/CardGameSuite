package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.games.FivesGame;

import java.util.Observer;

import io.socket.client.Socket;

import static android.content.Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP;

public class SelectPublicOrPrivateFragment extends  MultiplayerWaitingRoomActivityFragment {


    private static final String TAG = SelectPublicOrPrivateFragment.class.getSimpleName();

    public SelectPublicOrPrivateFragment() {
        super(R.layout.austen_fragment_select_public_or_private_buttons);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);


    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button joinPrivateButton = view.findViewById(R.id.privateButton);
        Button joinPublicButton = view.findViewById(R.id.publicButton);

        joinPrivateButton.setOnClickListener(v -> privateGameSelected());
        joinPublicButton.setOnClickListener(v -> toPublicGameWaitingRoomFragment());

    }

    @Override
    void SetMultiPlayerConnectorObserver(Observer multiPlayerConnectorObserver) {
        multiPlayerConnectorObserver=null; //don't need to observe
    }


    private void privateGameSelected() {
        //p2psocket.emit('private-game-room-request', {numPlayersRequiredForGame:2, gameType: 'fives'})
        //_ParentActivity._MultiPlayerConnector.joinToPublicGame(2, _ParentActivity._GameType);
        Bundle result = new Bundle();
        result.putString("fragmentClassName", PrivateGameOptionsFragment.class.getCanonicalName());
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("changeFragment", result);
    }


    private void toPublicGameWaitingRoomFragment() {


        //go to waiting room
        Bundle result = new Bundle();
        result.putString("fragmentClassName", PublicGameWaitingRoom.class.getCanonicalName());
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("changeFragment", result);

    }



    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        OnBackPressedCallback callback = new OnBackPressedCallback(
                true // default to enabled
        ) {
            @Override
            public void handleOnBackPressed() {
                disconnect();
                _MultiplayerWaitingRoomActivity.finish();
            }

            private void disconnect() {

                _MultiPlayerConnector.Close();


            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(
                this, // LifecycleOwner
                callback);
    }


}
