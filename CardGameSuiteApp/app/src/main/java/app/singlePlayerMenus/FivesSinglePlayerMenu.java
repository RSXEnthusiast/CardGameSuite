package com.example.cardgamesuiteapp.singlePlayerMenus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.games.Fives;

public class FivesSinglePlayerMenu extends AppCompatActivity {
    static View[] viewAINumButtons;// The buttons the user would press to select the number of AI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fives_offline_player_menu);
        initAISelectionMenu();
    }

    private void initAISelectionMenu() {
        viewAINumButtons = new View[5];
        viewAINumButtons[0] = findViewById(R.id.oneAI);
        viewAINumButtons[0].setOnClickListener(v -> selectedOneAI());
        viewAINumButtons[1] = findViewById(R.id.twoAI);
        viewAINumButtons[1].setOnClickListener(v -> selectedTwoAI());
        viewAINumButtons[2] = findViewById(R.id.threeAI);
        viewAINumButtons[2].setOnClickListener(v -> selectedThreeAI());
        viewAINumButtons[3] = findViewById(R.id.fourAI);
        viewAINumButtons[3].setOnClickListener(v -> selectedFourAI());
        viewAINumButtons[4] = findViewById(R.id.fiveAI);
        viewAINumButtons[4].setOnClickListener(v -> selectedFiveAI());
    }

    private void selectedOneAI() {
        numAISelected(1);
    }

    private void selectedTwoAI() {
        numAISelected(2);
    }

    private void selectedThreeAI() {
        numAISelected(3);
    }

    private void selectedFourAI() {
        numAISelected(4);
    }

    private void selectedFiveAI() {
        numAISelected(5);
    }

    private void numAISelected(int numAI) {
        Intent intent = new Intent(this, Fives.class);
        intent.putExtra("numAI", numAI);
        intent.putExtra("numOnlineOpponents", 0);
        intent.putExtra("multiplayer", false);
        SharedPreferences sp = getSharedPreferences("fivesGameInfo", MODE_PRIVATE);
//        sp.edit().putInt("myNumber", new Random().nextInt(numAI)).apply();
        sp.edit().putInt("myNumber", 0).apply();
        startActivity(intent);
    }
}
