package com.example.cardgamesuiteapp.gameCollectionMainMenu;

import android.content.Intent;
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
        setContentView(R.layout.activity_main_page);
        setContentView(R.layout.content_main_page);
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