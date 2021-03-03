package com.example.cardgamesuiteapp.games;

import androidx.appcompat.app.AppCompatActivity;

public abstract class MultiPlayerGame extends AppCompatActivity {

    public static MultiPlayerGameInfo getGameInfo() {
        throw new IllegalStateException(
                "getGameInfo() hasn't been set up in the subclass");
    }

}
