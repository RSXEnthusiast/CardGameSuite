package app.gameCollectionMainMenu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.NumberPicker;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cardgamesuiteapp.R;

public class DisplayFivesSettingsActivity extends AppCompatActivity {

    private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fives);
        numberPicker = findViewById(R.id.gameSizePicker);
        numberPicker.setMinValue(2);
        numberPicker.setMaxValue(6);
        SharedPreferences sp = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        numberPicker.setValue(sp.getInt("numFivesRandomPlayers", -1));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                SharedPreferences sp = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
                sp.edit().putInt("numFivesRandomPlayers", newVal).apply();
            }
        });
    }
}
