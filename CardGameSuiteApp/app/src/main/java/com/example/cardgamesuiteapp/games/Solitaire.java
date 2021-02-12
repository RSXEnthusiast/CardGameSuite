package com.example.cardgamesuiteapp.games;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.decks.Standard;
import com.example.cardgamesuiteapp.display.Card;
import com.example.cardgamesuiteapp.display.SolitaireHand;
import com.example.cardgamesuiteapp.gameCollectionMainMenu.DisplayMainPageActivity;
import com.example.cardgamesuiteapp.gameCollectionMainMenu.DisplaySettingsActivity;

import android.widget.Button;
import android.widget.TextView;

public class Solitaire extends AppCompatActivity {
    static SolitaireHand[] viewColumns;
    static Standard deck;// The Deck object
    final static int player = 4; //only one player in solitaire
    final static int columns = 4; //number of columns on the playing table
    static Card viewDiscard;// The discard view
    static View viewDeckBack;//Deck back
    static View viewDeckHighlight;// Simply the "highlight" of th discard, mainly used for setting the highlight to visible/invisible
    static Button viewReturnToAppCollection;// The button the user will press to return to the main menu
    static Button viewReturnToGameMainMenu;// The button the user will press to return to the game's main menu
    static TextView viewWinOrLose;// The textViews displays whether the user won or lost the game
    static boolean isAnimating;
    static boolean preAnimation;
    static int lastTouchedCardNum;
    static boolean backButtonEnabled;
    static int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solitaire);
        backButtonEnabled = true;
        initSolitaire();
    }


    /******************** INITIALIZING METHODS **********************/

    /**
     * Sets all solitaire values to corresponding xml source, starts the game.
     */
    private void initSolitaire() {
        viewColumns = new SolitaireHand[columns];
        initViewColumns();
        deck = new Standard(false, player);
        viewDiscard = findViewById(R.id.discard);
        viewDiscard.bringToFront();
        viewDeckBack = findViewById(R.id.deck);
        setBackStyleToImageButton(viewDeckBack);
        viewDeckHighlight = findViewById(R.id.highlightDiscard);
        viewDeckHighlight.setVisibility(View.INVISIBLE);
        viewWinOrLose = findViewById(R.id.winOrLose);
        backButtonEnabled = false;
        newGame();
    }

    /**
     * Assigns each column view to it's xml source
     */
    private void initViewColumns() {
        for (int i = 0; i < columns; i++) {
            viewColumns[i] = findViewById(getResources().getIdentifier(("column" + i), "id", getPackageName()));
        }
    }


    /******************** GAME PLAY METHODS **********************/

    /**
     * @param cardNum the value of the card that was tapped on
     */
    public static void cardTouched(int cardNum) {
        if (isAnimating)
            return;

        lastTouchedCardNum = cardNum;
        //TODO highlight selected card here
        //TODO allow card movement here

        updateViewInstruction();
    }


    /******************** START GAME METHODS **********************/

    /**
     * Called every time that there is a new game to reset the table
     */
    private void newGame() {
        deck.shuffleDiscardIntoDeck();
        try {
            deck.deal(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateEntireScreen();
    }

    /**
     * This method updates every single dynamic item on the screen.
     */
    private static void updateEntireScreen() {
        for (int i = 0; i < viewColumns.length; i++)
            viewColumns[i].initHand(deck.getHand(i)); //this get hand is probably wrong - TODO refactor for Solitaire
    }


    /******************** END OF GAME METHODS **********************/

    /**
     * A simple method to check whether there are any remaining moves or not
     *
     * @return true if the round is over, false if not.
     */
    private static boolean movesRemaining() {
        //TODO method

        return false;
    }

    /**
     * A simple method to check whether each hand has only one card left and if that card is an ace
     *
     * @return true if only aces are on the board, false if not
     */
    private static boolean onlyAcesRemaining() {
        boolean check = true;

        for(int i = 0; i<viewColumns.length; i++) {
            if (viewColumns[i].getHand().size() != 1 || viewColumns[i].getHand().get(0).getCardNum()%13 != 1)
                check = false;
        }
        System.out.println("onlyAcesRemaining: " + check);
        return check;
    }

    /**
     * @return returns win or lose string depending on how the game ends
     */
    private static String getWinOrLoseText() {
        String display = "";
        if(onlyAcesRemaining() && !movesRemaining() && deck.deckIsEmpty())
            display = "Game Over! \nYou Win!";
        else if(!onlyAcesRemaining() && !movesRemaining() && deck.deckIsEmpty())
            display = "Game Over! \nYou Lose";

        backButtonEnabled = true;
        return display;
    }

    /**
     * Updates view instruction based upon current stage
     */
    private static void updateViewInstruction() {
        viewWinOrLose.setText(getWinOrLoseText());
    }


    /******************** CLICK BUTTON METHODS **********************/

    /**
     * Deals cards when deck is selected, makes the deck invisible when deck is empty
     *
     * @param view is the deck ImageButton
     */
    public void clickDeckToDeal(View view) {
        try {
            deck.deal(1);
            for (int i = 0; i < viewColumns.length; i++)
                viewColumns[i].initHand(deck.getHand(i));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(deck.deckIsEmpty())
            viewDeckBack.setVisibility(View.INVISIBLE);
    }

    /**
     * Moves user back to home screen if Home button is clicked. Prompts them that game progress will be lost and confirms action.
     */
    public void clickReturnToHome(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Return To Main Menu");
        builder.setMessage("All current game progress will be lost.\n\nAre you sure?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getBaseContext(), DisplayMainPageActivity.class);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do Nothing
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    /*************** SETTINGS AND PREFERENCES METHODS ***************/

    /**
     * Sets the background image of an ImageButton based on shared preferences
     *
     * @param view is the ImageButton
     */
    public void setBackStyleToImageButton(View view) {
        String stringBuilder = "";
        stringBuilder += getSharedPreferences("preferences", Context.MODE_PRIVATE).getString("backStyle", "cardStyle not found");
        stringBuilder += "back";
        view.setBackgroundResource(getResources().getIdentifier(stringBuilder, "drawable", this.getPackageName()));
    }

    @Override
    public void onBackPressed() {
        if (backButtonEnabled) {
            super.onBackPressed();
        } else {
            //do nothing
        }
    }
}