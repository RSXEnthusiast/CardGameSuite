package com.example.cardgamesuiteapp.MultiplayerConnection;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.cardgamesuiteapp.R;

public class PrivateGameOptionsFragment extends Fragment {
    public PrivateGameOptionsFragment() {
        super(R.layout.mpconnection_fragment_private_options);
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        Button createButton = view.findViewById(R.id.createPrivateButton);

        Button joinButton = view.findViewById(R.id.joinPrivateButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //p2psocket.emit('private-game-room-request', {numPlayersRequiredForGame:2, gameType: 'fives'})
                //_ParentActivity._MultiPlayerConnector.joinToPublicGame(2, _ParentActivity._GameType);

                Bundle result = new Bundle();
                result.putString("fragmentClassName", CreatePrivateGameFragment.class.getCanonicalName());
                result.putBoolean("gameCreator", true);
                // The child fragment needs to still set the result on its parent fragment manager
                getParentFragmentManager().setFragmentResult("changeFragment", result);

            }


        });

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //p2psocket.emit('public-game-room-request', {numPlayersRequiredForGame:2, gameType: 'fives'})
                //_ParentActivity._MultiPlayerConnector.joinToPublicGame(2, _ParentActivity._GameType);

                Bundle result = new Bundle();
                result.putString("fragmentClassName", JoinPrivateGameFragment.class.getCanonicalName());
                result.putBoolean("gameCreator", false);
                getParentFragmentManager().setFragmentResult("changeFragment", result);
               /* Bundle result = new Bundle();
                result.putString("fragmentClassName", Public.class.getCanonicalName());
                // The child fragment needs to still set the result on its parent fragment manager
                getParentFragmentManager().setFragmentResult("changeFragment", result);*/
            }


        });


    }


}
