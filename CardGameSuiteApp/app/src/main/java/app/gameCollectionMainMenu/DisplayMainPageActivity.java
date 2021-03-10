package app.gameCollectionMainMenu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import app.MultiplayerConnection.MultiplayerOrSinglePlayerMenu;
import app.games.Fives;
import com.example.cardgamesuiteapp.R;
import app.games.Solitaire;
import app.singlePlayerMenus.FivesSinglePlayerMenu;

import java.io.Serializable;

public class DisplayMainPageActivity extends AppCompatActivity implements Serializable {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (sp.getString("highlightAssistEnabled", "default").equals("default")) {
            sp.edit().putString("highlightAssistEnabled", "On").apply();
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
        intent.putExtra("gameClass", Fives.class);
        intent.putExtra("gameName", "Fives");
        intent.putExtra("singlePlayerClass", FivesSinglePlayerMenu.class);
        startActivity(intent);
    }

    public void clickFivesRulesButton(View view) {
        Intent intent = new Intent(this, DisplayFivesRulesActivity.class);
        startActivity(intent);
    }

    public void clickFivesSettingsButton(View view) {
        Intent intent = new Intent(this, DisplayFivesSettingsActivity.class);
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

    public void clickSolitaireSettingsButton(View view) {
        Intent intent = new Intent(this, DisplaySolitaireSettingsActivity.class);
        startActivity(intent);
    }

}