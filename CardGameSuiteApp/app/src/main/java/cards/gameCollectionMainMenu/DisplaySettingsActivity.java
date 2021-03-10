package app.gameCollectionMainMenu;

import android.content.Context;

import com.google.android.material.slider.Slider;
import com.google.android.material.slider.Slider.OnSliderTouchListener;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import app.R;

public class DisplaySettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_collection_settings);
        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        switch (sp.getString("cardStyle", "cardStyle not found")) {
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
        switch (sp.getString("backStyle", "backStyle not found")) {
            case "light_":
                findViewById(R.id.selectedLightBack).setVisibility(View.VISIBLE);
                break;
            case "dark_":
                findViewById(R.id.selectedDarkBack).setVisibility(View.VISIBLE);
                break;
        }
        ((EditText) findViewById(R.id.playerNameInput)).setText(sp.getString("name", "name not found"));
        Slider slider = findViewById(R.id.animationSpeedSlider);
        slider.setValue(sp.getInt("animationSpeed", 0));
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

    public void saveName(View view) {
        // 3/4 of these lines of code are to simply hide the keyboard. Why are you like this android?
        InputMethodManager imm = (InputMethodManager) this.getSystemService(this.INPUT_METHOD_SERVICE);
        view = this.getCurrentFocus();
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        editPreference("name", String.valueOf(((EditText) findViewById(R.id.playerNameInput)).getText()));
        ((Button) findViewById(R.id.saveName)).setText("SAVED!");
    }

    public void setLightBack(View view) {
        findViewById(R.id.selectedDarkBack).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedLightBack).setVisibility(View.VISIBLE);
        setBackStyle("light_");
    }

    public void setDarkBack(View view) {
        findViewById(R.id.selectedLightBack).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedDarkBack).setVisibility(View.VISIBLE);
        setBackStyle("dark_");
    }

    public void setLightStyle(View view) {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedLight).setVisibility(View.VISIBLE);
        setDeckStyle("light_");
    }

    public void setBigStyle(View view) {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedBig).setVisibility(View.VISIBLE);
        setDeckStyle("big_");
    }

    public void setDarkStyle(View view) {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedDark).setVisibility(View.VISIBLE);
        setDeckStyle("dark_");
    }

    public void setBigDarkStyle(View view) {
        setAllSelectedCardStyleViewsToInvisible();
        findViewById(R.id.selectedBigDark).setVisibility(View.VISIBLE);
        setDeckStyle("bigdark_");
    }

    private void setDeckStyle(String style) {
        editPreference("cardStyle", style);
    }

    private void setBackStyle(String style) {
        editPreference("backStyle", style);
    }

    private void setAnimationSpeedOnSlider(int speed) {
        editPreference("animationSpeed", speed);
    }

    private void editPreference(String name, String preference) {
        getSharedPreferences("preferences", MODE_PRIVATE).edit().putString(name, preference).apply();
    }

    private void editPreference(String name, int preference) {
        getSharedPreferences("preferences", MODE_PRIVATE).edit().putInt(name, preference).apply();
    }

    private void setAllSelectedCardStyleViewsToInvisible() {
        findViewById(R.id.selectedLight).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedBig).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedDark).setVisibility(View.INVISIBLE);
        findViewById(R.id.selectedBigDark).setVisibility(View.INVISIBLE);
    }
}
