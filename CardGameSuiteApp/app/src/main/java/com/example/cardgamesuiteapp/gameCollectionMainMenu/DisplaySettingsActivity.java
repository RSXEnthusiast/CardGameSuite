package com.example.cardgamesuiteapp.gameCollectionMainMenu;

import android.content.Context;

import com.google.android.material.slider.Slider;
import com.google.android.material.slider.Slider.OnSliderTouchListener;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cardgamesuiteapp.R;

public class DisplaySettingsActivity extends AppCompatActivity {

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
        switch (getSharedPreferences("preferences", Context.MODE_PRIVATE).getString("cardStyle", "cardStyle not found")) {
            case "light_":
                findViewById(R.id.selectedLightBack).setVisibility(View.VISIBLE);
                break;
            case "dark_":
                findViewById(R.id.selectedDarkBack).setVisibility(View.VISIBLE);
                break;
        }
        findViewById(R.id.buttonLightStyle).setOnClickListener(v -> setLightStyle());
        findViewById(R.id.buttonBigStyle).setOnClickListener(v -> setBigStyle());
        findViewById(R.id.buttonDarkStyle).setOnClickListener(v -> setDarkStyle());
        findViewById(R.id.buttonBigDarkStyle).setOnClickListener(v -> setBigDarkStyle());
        findViewById(R.id.buttonDarkBack).setOnClickListener(v -> setDarkBack());
        findViewById(R.id.buttonLightBack).setOnClickListener(v -> setLightBack());

        Slider slider = findViewById(R.id.animationSpeedSlider);
        slider.setValue(getSharedPreferences("preferences", Context.MODE_PRIVATE).getInt("animationSpeed", 0));
        final OnSliderTouchListener touchListener = new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                setAnimationSpeedOnSlider((int) slider.getValue());
            }
        };
        slider.addOnSliderTouchListener(touchListener);
    }

    private void setLightBack() {
        findViewById(R.id.selectedDarkBack).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedLightBack).setVisibility(View.VISIBLE);
        setBackStyle("light_");
    }

    private void setDarkBack() {
        findViewById(R.id.selectedLightBack).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedDarkBack).setVisibility(View.VISIBLE);
        setBackStyle("dark_");
    }

    private void setLightStyle() {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedLight).setVisibility(View.VISIBLE);
        setDeckStyle("light_");
    }

    private void setBigStyle() {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedBig).setVisibility(View.VISIBLE);
        setDeckStyle("big_");
    }

    private void setDarkStyle() {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedDark).setVisibility(View.VISIBLE);
        setDeckStyle("dark_");
    }

    private void setBigDarkStyle() {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedBigDark).setVisibility(View.VISIBLE);
        setDeckStyle("bigdark_");
    }

    private void setAllSelectedCardStyleViewsToInvisible() {
        findViewById(R.id.selectedLight).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedBig).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedDark).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedBigDark).setVisibility(View.INVISIBLE);
    }

    private void setDeckStyle(String style) {
        getSharedPreferences("preferences", MODE_PRIVATE).edit().putString("cardStyle", style).apply();
    }

    private void setBackStyle(String style) {
        getSharedPreferences("preferences", MODE_PRIVATE).edit().putString("backStyle", style).apply();
    }

    private void setAnimationSpeedOnSlider(int speed) {
        getSharedPreferences("preferences", MODE_PRIVATE).edit().putInt("animationSpeed", speed).apply();
    }

}