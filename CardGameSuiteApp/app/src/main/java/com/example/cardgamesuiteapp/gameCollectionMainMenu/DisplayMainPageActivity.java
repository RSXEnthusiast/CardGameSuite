package com.example.cardgamesuiteapp.gameCollectionMainMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardgamesuiteapp.austenMPStuff.MultiplayerOrSinglePlayerMenu;
import com.example.cardgamesuiteapp.games.FivesGame;
import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.games.Solitaire;

import java.io.Serializable;

public class DisplayMainPageActivity extends AppCompatActivity implements Serializable {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_collection_activity_main_page);
        setContentView(R.layout.app_collection_content_main_page);
        SharedPreferences sp = getSharedPreferences("preferences", MODE_PRIVATE);
        if (sp.getString("cardStyle", "default").equals("default")) {
            sp.edit().putString("cardStyle", "light_").apply();
        }
        if (sp.getInt("animationSpeed", 0) == 0) {
            sp.edit().putInt("animationSpeed", 2).apply();
        }
        if (sp.getString("backStyle", "default").equals("default")) {
            sp.edit().putString("backStyle", "light_").apply();
        }
        if (sp.getString("name", "name not found").equals("name not found")) {
            sp.edit().putString("name", "Name").apply();
        }
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    public void clickSettingsButton(View view) {
        Intent intent = new Intent(this, DisplaySettingsActivity.class);
        startActivity(intent);
    }

    public void clickPlayFivesButton(View view) {
        Intent intent = new Intent(this, MultiplayerOrSinglePlayerMenu.class);
        intent.putExtra("class", FivesGame.class);
        intent.putExtra("gameName", "Fives");
        startActivity(intent);
    }

    public void clickFivesRulesButton(View view) {
        Intent intent = new Intent(this, DisplayFivesRulesActivity.class);
        startActivity(intent);
    }

    public void clickPlaySolitaireButton(View view) {
        Intent intent = new Intent(this, Solitaire.class);
        startActivity(intent);
    }

    public void clickSolitaireRulesButton(View view) {
        Intent intent = new Intent(this, DisplaySolitaireRulesActivity.class);
        startActivity(intent);
    }

}