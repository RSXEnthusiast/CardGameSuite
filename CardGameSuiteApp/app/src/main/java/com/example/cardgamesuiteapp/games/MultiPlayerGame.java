package com.example.cardgamesuiteapp.games;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public abstract class MultiPlayerGame extends AppCompatActivity {
    Handler _UIHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _UIHandler = new Handler();
    }

    public static MultiPlayerGameInfo getGameInfo() {
        throw new IllegalStateException(
                "getGameInfo() hasn't been set up in the subclass");
    }

}
