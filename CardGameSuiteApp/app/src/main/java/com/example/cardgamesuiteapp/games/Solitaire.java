package com.example.cardgamesuiteapp.games;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.decks.Standard;
import com.example.cardgamesuiteapp.display.Card;
import com.example.cardgamesuiteapp.gameCollectionMainMenu.DisplayMainPageActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Solitaire extends AppCompatActivity {

    static Standard deck;// The Deck object
    static Card viewDiscard;// The discard view
    static Card viewDeck;// The deck view
    static Button viewReturnToAppCollection;// The button the user will press to return to the main menu
    static Button viewReturnToGameMainMenu;// The button the user will press to return to the game's main menu
    static TextView[] viewWinOrLose;// The textViews displays whether the user won or lost the game

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solitaire);
    }

    public void clickReturnToHome(View view) {
        Intent intent = new Intent(this, DisplayMainPageActivity.class);
        startActivity(intent);
    }

}
