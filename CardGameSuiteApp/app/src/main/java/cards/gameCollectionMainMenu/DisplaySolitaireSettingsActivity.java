package cards.gameCollectionMainMenu;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import cards.R;

public class DisplaySolitaireSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_solitaire);

        SharedPreferences sp = getSharedPreferences("preferences", Context.MODE_PRIVATE);

        ToggleButton highlightAssistToggle = (ToggleButton) findViewById(R.id.highlightAssistToggle);
        highlightAssistToggle.setChecked(sp.getString("highlightAssistEnabled", "valueNotFound").equals("On"));
        highlightAssistToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    sp.edit().putString("highlightAssistEnabled", "On").apply();
                else
                    sp.edit().putString("highlightAssistEnabled", "Off").apply();
            }
        });
    }

}
