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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.austen_fragment_first);
        ((TextView) findViewById(R.id.gameTitle)).setText(((String) getIntent().getSerializableExtra("gameName")).toUpperCase());
        Button viewOfflineSinglePlayer = findViewById(R.id.offlineSinglePlayer);
        Button viewOnlineMultiplayer = findViewById(R.id.onlineMultiplayer);
        viewOfflineSinglePlayer.setOnClickListener(v -> startOfflineSinglePlayer());
        viewOnlineMultiplayer.setOnClickListener(v -> startOnlineMultiplayer());
    }

    private void startOfflineSinglePlayer() {
        Intent intent = new Intent(this, (Class) getIntent().getSerializableExtra("singlePlayerClass"));
        startActivity(intent);
    }

    private void startOnlineMultiplayer() {
        Intent oldIntent = getIntent();
        Intent newIntent = new Intent(this, MultiplayerWaitingRoomActivity.class);
        newIntent.putExtra("gameName", oldIntent.getSerializableExtra("gameName"));
        newIntent.putExtra("gameClass", oldIntent.getSerializableExtra("gameClass"));
        startActivity(newIntent);
    }
}