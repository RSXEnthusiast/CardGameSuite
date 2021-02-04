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
    static int player = 1; //only one player in solitaire
    static Card viewDiscard;// The discard view
    static View viewDiscardHighlight;// Simply the "highlight" of th discard, mainly used for setting the highlight to visible/invisible
    static Card viewDeck;// The deck view
    static Button viewReturnToAppCollection;// The button the user will press to return to the main menu
    static Button viewReturnToGameMainMenu;// The button the user will press to return to the game's main menu
    static TextView viewWinOrLose;// The textViews displays whether the user won or lost the game
    static solitaireStage stage;// The current stage of play
    static boolean isAnimating;
    static boolean preAnimation;
    static int lastTouchedCardNum;
    static boolean backButtonEnabled;
    static int textColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solitaire);
        initSolitaire();
    }

    private void initSolitaire() {
        deck = new Standard(true, player);
        viewDiscard = findViewById(R.id.discard);
        viewDiscard.bringToFront();
        viewDiscardHighlight = findViewById(R.id.highlightDiscard);
        viewDiscardHighlight.setVisibility(View.INVISIBLE);
        viewDeck = findViewById(R.id.deck);
        viewWinOrLose = findViewById(R.id.winOrLose);
    }

    public void clickReturnToHome(View view) {
        Intent intent = new Intent(this, DisplayMainPageActivity.class);
        startActivity(intent);
    }

    /**
     * Walks through the stages of a players turn and switches the value of the enum and calls the relevant game method during each phase
     *
     * @param cardNum the value of the card that was tapped on
     */
    public static void cardTouched(int cardNum) {
        if (!deck.isMyTurn() || isAnimating) {
            return;
        }
        lastTouchedCardNum = cardNum;
        switch (stage) {
            case deal:
                stageDeal();
                break;
            case cardSelected:
                stageCardSelected(cardNum);
                break;
            case movingCard:
                stageMovingCard(cardNum);
                break;
        }
    }

    /**
     * This method is called when the deck is touched and the current stage is deal
     */
    private static void stageDeal() {
        stage = solitaireStage.deal;
        viewDeck.flipCard();
        viewDiscardHighlight.setVisibility(View.VISIBLE);
    }

    /**
     * Called every time that there is a new game to reset the table
     */
    private void newGame() {
        deck.shuffleDiscardIntoDeck();
        try {
            deck.deal(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewDeck.setFaceUp(false);
    }

    /**
     * This method updates every single dynamic item on the screen.
     */
    private static void updateEntireScreen() {
        viewDiscard.updateImage(deck.peekTopDiscard());
        viewDeck.updateImage(deck.peekTopDraw());
        viewDeck.setFaceUp(false);
    }

    //TODO METHODS
    /**
     * @return returns win or lose string depending on how the game ends
     */
    private static String getWinOrLose() {
        String display = "";
        if(stage.equals(solitaireStage.gameOver)) {
            //only aces left, no cards in deck TODO if statement
            display = "Game Over! You Win!";

            //more than aces left, no cards in deck TODO if statement
            display = "Game Over! You Lose";
        }
        return display;
    }

    /**
     * A simple method to check whether there are any remaining moves or not
     *
     * @return true if the round is over, false if not.
     */
    private static boolean movesRemaining() {

        //if no moves remaining TODO if statement
        stage = stage.deal;
        return true;
    }

    /**
     * This method is called when a card is touched and the current stage is cardSelected
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageCardSelected(int cardNum) {

    }

    /**
     * This method is called when a card is touched and the current stage is cardMoved
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageMovingCard(int cardNum) {

    }

    private static boolean hasWon() {
        return false;
    }

    private static boolean hasLost() {
        return false;
    }

    /**
     * The stages of the fives game turn
     */
    private enum solitaireStage {
        deal, cardSelected, movingCard, gameOver
    }

}
