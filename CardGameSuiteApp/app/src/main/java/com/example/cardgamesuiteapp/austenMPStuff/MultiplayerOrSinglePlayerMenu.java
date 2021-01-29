package com.example.cardgamesuiteapp.austenMPStuff;

import android.content.Intent;
import android.os.Bundle;

import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.singlePlayerMenus.FivesSinglePlayerMenu;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;


public class MultiplayerOrSinglePlayerMenu extends AppCompatActivity {

    public static final String GAME_TYPE = "gameType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.austen_fragment_first);
        TextView title = findViewById(R.id.gameTitle);
        title.setText((String) getIntent().getSerializableExtra("GameName"));
        Button viewOfflineSinglePlayer = findViewById(R.id.offlineSinglePlayer);
        Button viewOnlineMultiplayer = findViewById(R.id.onlineMultiplayer);
        viewOfflineSinglePlayer.setOnClickListener(v -> startOfflineSinglePlayer());
        viewOnlineMultiplayer.setOnClickListener(v -> startOnlineMultiplayer());
    }

    private void startOfflineSinglePlayer() {
        Intent intent = new Intent(this, FivesSinglePlayerMenu.class);
        startActivity(intent);
    }

    private void startOnlineMultiplayer() {
        Intent newIntent = new Intent(this, MultiplayerWaitingRoomActivity.class);
        Intent oldIntent = getIntent();
        newIntent.putExtra(GAME_TYPE, oldIntent.getSerializableExtra("gameName"));
        newIntent.putExtra("class", oldIntent.getSerializableExtra("class"));
        startActivity(newIntent);
    }
}