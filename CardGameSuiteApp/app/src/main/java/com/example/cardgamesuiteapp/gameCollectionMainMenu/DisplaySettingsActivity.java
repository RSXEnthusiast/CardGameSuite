package com.example.cardgamesuiteapp.gameCollectionMainMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardgamesuiteapp.R;

public class DisplaySettingsActivity extends AppCompatActivity {

    static SharedPreferences.Editor editor;
    static SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_collection_settings);
        switch (getSharedPreferences("preferences", Context.MODE_PRIVATE).getString("cardStyle", "cardStyle not found")) {
            case "light_":
                findViewById(R.id.selectedLight).setVisibility(View.VISIBLE);
                break;
            case "big_":
                findViewById(R.id.selectedBig).setVisibility(View.VISIBLE);
                break;
            case "dark_":
                findViewById(R.id.selectedDark).setVisibility(View.VISIBLE);
                break;
            case "bigdark_":
                findViewById(R.id.selectedBigDark).setVisibility(View.VISIBLE);
                break;
        }
        findViewById(R.id.buttonLightStyle).setOnClickListener(v -> setLightStyle());
        findViewById(R.id.buttonBigStyle).setOnClickListener(v -> setBigStyle());
        findViewById(R.id.buttonDarkStyle).setOnClickListener(v -> setDarkStyle());
        findViewById(R.id.buttonBigDarkStyle).setOnClickListener(v -> setBigDarkStyle());
    }

    private void setLightStyle() {
        setAllSelectedViewsToInvisible();
        findViewById(R.id.selectedLight).setVisibility(View.VISIBLE);
        setDeckStyle("light_");
    }

    private void setBigStyle() {
        setAllSelectedViewsToInvisible();
        findViewById(R.id.selectedBig).setVisibility(View.VISIBLE);
        setDeckStyle("big_");
    }

    private void setDarkStyle() {
        setAllSelectedViewsToInvisible();
        findViewById(R.id.selectedDark).setVisibility(View.VISIBLE);
        setDeckStyle("dark_");
    }

    private void setBigDarkStyle() {
        setAllSelectedViewsToInvisible();
        findViewById(R.id.selectedBigDark).setVisibility(View.VISIBLE);
        setDeckStyle("bigdark_");
    }

    private void setDeckStyle(String style) {
        getSharedPreferences("preferences", MODE_PRIVATE).edit().putString("cardStyle", style).apply();
    }

    private void setAllSelectedViewsToInvisible() {
        findViewById(R.id.selectedLight).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedBig).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedDark).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedBigDark).setVisibility(View.INVISIBLE);
    }
}