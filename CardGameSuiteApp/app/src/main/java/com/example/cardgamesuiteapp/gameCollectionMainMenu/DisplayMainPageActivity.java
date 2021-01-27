package com.example.cardgamesuiteapp.gameCollectionMainMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardgamesuiteapp.games.Fives;
import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.games.Solitaire;

public class DisplayMainPageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_collection_activity_main_page);
        setContentView(R.layout.app_collection_content_main_page);
        SharedPreferences sp = getSharedPreferences("preferences", MODE_PRIVATE);
        if (sp.getString("cardStyle", "default").equals("default") || sp.getInt("animationSpeed", 0) == 0 || sp.getString("backStyle", "default").equals("default")) {
            sp.edit().putString("cardStyle", "light_").apply();
            sp.edit().putInt("animationSpeed", 2).apply();
            sp.edit().putString("backStyle", "light_").apply();
        }
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
        Intent intent = new Intent(this, Fives.class);
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