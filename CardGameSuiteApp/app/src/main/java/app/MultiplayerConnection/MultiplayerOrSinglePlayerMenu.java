package app.MultiplayerConnection;

import android.content.Intent;
import android.os.Bundle;

import com.example.cardgamesuiteapp.R;
import app.gameCollectionMainMenu.DisplayMainPageActivity;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.TextView;


public class MultiplayerOrSinglePlayerMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mpconnection_offline_or_online);
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

    @Override
    public void onBackPressed() {
        Intent newIntent = new Intent(this, DisplayMainPageActivity.class);
        startActivity(newIntent);
    }

    private void startOnlineMultiplayer() {
        Intent oldIntent = getIntent();
        Intent newIntent = new Intent(this, MultiplayerWaitingRoomActivity.class);
        newIntent.putExtra("gameName", oldIntent.getSerializableExtra("gameName"));
        newIntent.putExtra("gameClass", oldIntent.getSerializableExtra("gameClass"));
        startActivity(newIntent);
    }
}