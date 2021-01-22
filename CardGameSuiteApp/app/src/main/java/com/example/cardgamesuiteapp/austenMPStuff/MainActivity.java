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


public class MainActivity extends AppCompatActivity {

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
        Intent intent = new Intent(this, MultiplayerWaitingRoomActivity.class);
        String selectedGame= "fives";
        intent.putExtra(GAME_TYPE, selectedGame);
        startActivity(intent);
    }

    public void startGame(View view) {
        Intent intent = new Intent(this, MultiplayerWaitingRoomActivity.class);

        String selectedGame = "fives";
        intent.putExtra(GAME_TYPE, selectedGame);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}