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

public class JoiningPrivateGameWaitingRoomFragment extends PrivateGameWaitingRoom {

    @Override
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




}