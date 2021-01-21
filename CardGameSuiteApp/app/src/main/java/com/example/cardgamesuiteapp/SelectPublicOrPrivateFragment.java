package com.example.cardgamesuiteapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import io.socket.client.Socket;

public class SelectPublicOrPrivateFragment extends Fragment implements IMultiplayerConnectorSocketEventUser {


    private static final String TAG = SelectPublicOrPrivateFragment.class.getSimpleName();

    public SelectPublicOrPrivateFragment() {
        super(R.layout.fragment_select_public_or_private_buttons);
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


    public static void AddSocketEvents(Socket socket, MultiPlayerConnector multiPlayerConnector) {
        //no events needed by this Fragment
    }

}
