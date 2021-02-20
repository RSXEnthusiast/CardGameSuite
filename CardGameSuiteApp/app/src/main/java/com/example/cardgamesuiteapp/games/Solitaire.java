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
import com.example.cardgamesuiteapp.display.CardAnimation;
import com.example.cardgamesuiteapp.display.SolitaireHand;
import com.example.cardgamesuiteapp.gameCollectionMainMenu.DisplayMainPageActivity;

import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class Solitaire extends AppCompatActivity {
    static SolitaireHand[] viewColumns;
    static Standard deck;
    final static int columns = 4;
    static Card viewDiscard;
    static View viewDeckBack;
    static View viewDeckHighlight;
    static View viewDiscardHighlight;
    static View viewColumnHighlight0;
    static View viewColumnHighlight1;
    static View viewColumnHighlight2;
    static View viewColumnHighlight3;
    static TextView viewWinOrLose;
    static boolean isAnimating;
    static Card viewAnimatedCard1;
    static CardAnimation viewAnimation1;
    static int lastTouchedCardNum;
    static boolean backButtonEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.solitaire);
        initSolitaire();
    }


    /******************** INITIALIZING METHODS **********************/

    /**
     * Sets all solitaire values to corresponding xml source, starts the game.
     */
    private void initSolitaire() {
        viewColumns = new SolitaireHand[columns];
        initViewColumns();
        deck = new Standard(false, columns);

        viewDiscard = findViewById(R.id.discard);
        viewDiscard.bringToFront();
        viewDeckBack = findViewById(R.id.deck);
        viewDeckBack.bringToFront();
        setBackStyleToImageButton(viewDeckBack);

        viewDeckHighlight = findViewById(R.id.highlightDeck);
        viewDeckHighlight.setVisibility(View.INVISIBLE);
        viewDiscardHighlight = findViewById(R.id.highlightDiscard);
        viewDiscardHighlight.setVisibility(View.INVISIBLE);
        viewColumnHighlight0 = findViewById(R.id.highlightColumn0);
        viewColumnHighlight0.setVisibility(View.INVISIBLE);
        viewColumnHighlight1 = findViewById(R.id.highlightColumn1);
        viewColumnHighlight1.setVisibility(View.INVISIBLE);
        viewColumnHighlight2 = findViewById(R.id.highlightColumn2);
        viewColumnHighlight2.setVisibility(View.INVISIBLE);
        viewColumnHighlight3 = findViewById(R.id.highlightColumn3);
        viewColumnHighlight3.setVisibility(View.INVISIBLE);
        viewWinOrLose = findViewById(R.id.winOrLose);

        viewAnimatedCard1 = findViewById(R.id.animatedCard1);
        viewAnimatedCard1.bringToFront();
        viewAnimation1 = new CardAnimation(viewAnimatedCard1, true, this);
        backButtonEnabled = false;
        isAnimating = false;
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
            viewColumns[i].initHand(deck.getHand(i));
    }


    /******************** GAME PLAY METHODS **********************/

    /**
     * Calls relevant animation method based on which card was selected.
     *
     * @param cardNum the value of the card that was tapped on
     */
    public static void cardTouched(int cardNum) {
        if (isAnimating)
            return;

        int columnToMoveCardFrom = findColumnOfSelectedCard(cardNum);

        if(columnToMoveCardFrom != -1) {
            cardNum = getLastCardInColumn(columnToMoveCardFrom).getCardNum();
            lastTouchedCardNum = cardNum;
            selectPlayingCard(cardNum);
        }
        else if(deck.discardIsEmpty() || deck.peekTopDiscard() == cardNum)
            moveCardToDiscard(columnToMoveCardFrom, cardNum);
        else
            moveCardToEmptyColumn(cardNum);

        updateViewInstruction();
    }

    /**
     * A simple method to check whether there are any remaining moves or not
     *
     * @return true if the round is over, false if not.
     */
    private static boolean movesRemaining() {
        boolean remainingMoves = false;

        //fill a string array with all the suits from the cards at the bottom of each column
        String [] suits = new String[4];
        for(int i = 0; i < columns; i++) {
            suits[i] = Standard.getCardSuit(getLastCardInColumn(i).getCardNum());
        }

        //compare each suit to make sure none match
        if(suits[0].equals(suits[1]) || suits[1].equals(suits[2]) || suits[2].equals(suits[3]) || suits[1].equals(suits[3]) || suits[2].equals(suits[0]))
            remainingMoves = true;

        if(!remainingMoves)
            viewDeckHighlight.setVisibility(View.VISIBLE);

        return remainingMoves;
    }


    /******************** END OF GAME METHODS **********************/

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
        return check;
    }

    /**
     * Updates view win or lose string depending on how the game ends
     */
    private static void updateViewInstruction() {
        String display = "";
        if(onlyAcesRemaining() && !movesRemaining() && deck.deckIsEmpty())
            display = "Game Over! \nYou Win!";
        else if(!onlyAcesRemaining() && !movesRemaining() && deck.deckIsEmpty())
            display = "Game Over! \nYou Lose";

        backButtonEnabled = true;
        viewWinOrLose.setText(display);
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

        viewDeckHighlight.setVisibility(View.INVISIBLE);
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

    /************************ CARD ANIMATION ************************/

    /**
     * This method is called when a card on the playing table is touched
     *
     * @param cardNum the column number of the card selected
     */
    private static void selectPlayingCard(int cardNum) {
        viewAnimatedCard1.updateImage(cardNum);

        //TODO grab first card and wait for location to be selected

        highlightAvailableChoices();
    }

    /**
     * This method is called when a card on the playing table is selected and the discard pile is touched
     *
     * @param cardNum the value of the card that was tapped on
     * @param columnToMoveCardFrom the column where the card were moving resides
     */
    private static void moveCardToDiscard(int columnToMoveCardFrom, int cardNum) {
        System.out.println("I MADE IT TO DISCARD");
        isAnimating = true;
        removeHighlightedChoices();

        viewAnimation1.cardAnimate(getCardInHandX(cardNum), viewDiscard.getX(), getCardInHandY(cardNum), viewDiscard.getY());

        try {
            deck.discardByValue(columnToMoveCardFrom, lastTouchedCardNum);
            viewColumns[columnToMoveCardFrom].removeCard(lastTouchedCardNum);
        } catch (Exception e) {
            e.printStackTrace();
        }

        viewDiscard.updateImage(deck.peekTopDiscard());
        isAnimating = false;
    }

    /**
     * This method is called when a card on the playing table is selected and an empty column is touched
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void moveCardToEmptyColumn(int cardNum) {
        isAnimating = true;
        removeHighlightedChoices();

        isAnimating = false;
    }


    /******************** CARD ANIMATION HELPERS ********************/

    /**
     * The visual x position of the card view
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static float getCardInHandX(int cardNum) {
        return viewColumns[deck.getCurPlayersTurn()].getX() + viewColumns[deck.getCurPlayersTurn()].getCard(cardNum).getX();
    }

    /**
     * The visual y position of the card view
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static float getCardInHandY(int cardNum) {
        return viewColumns[deck.getCurPlayersTurn()].getY() + viewColumns[deck.getCurPlayersTurn()].getCard(cardNum).getY();
    }

    /**
     * Returns true if the card on top of a stack in any column has been selected
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static int findColumnOfSelectedCard(int cardNum) {
        int column = -1;

        for(int i = 0; i<viewColumns.length; i++) {
            for(int j = 0; j<viewColumns[i].getHand().size(); j++) {
                if (viewColumns[i].getHand().get(j).getCardNum() == cardNum)
                    column = i;
            }
        }

        return column;
    }

    /**
     * This method is called when a card on the playing table is touched. Highlights discard pile on first use and any empty columns
     */
    private static void highlightAvailableChoices() {
        int emptyColumn = -1;

        if(!movesRemaining())
            return ;

        if(deck.discardIsEmpty())
            viewDiscardHighlight.setVisibility(View.VISIBLE);

        for(int i = 0; i<viewColumns.length; i++) {
            if (viewColumns[i].getHand().isEmpty())
                emptyColumn = i;
        }

        if(emptyColumn == 0)
            viewColumnHighlight0.setVisibility(View.VISIBLE);
        else if(emptyColumn == 1)
            viewColumnHighlight1.setVisibility(View.VISIBLE);
        else if(emptyColumn == 2)
            viewColumnHighlight2.setVisibility(View.VISIBLE);
        else if(emptyColumn == 3)
            viewColumnHighlight3.setVisibility(View.VISIBLE);
    }

    /**
     * This method is called when a card is moved to its destination and highlights are no longer needed.
     */
    private static void removeHighlightedChoices() {
        viewDiscardHighlight.setVisibility(View.INVISIBLE);
        viewColumnHighlight0.setVisibility(View.INVISIBLE);
        viewColumnHighlight1.setVisibility(View.INVISIBLE);
        viewColumnHighlight2.setVisibility(View.INVISIBLE);
        viewColumnHighlight3.setVisibility(View.INVISIBLE);
    }

    /**
     * Returns last card in column
     *
     * @param columnNum to be used to find last card
     */
    private static Card getLastCardInColumn(int columnNum) {
        return viewColumns[columnNum].getHand().get(viewColumns[columnNum].getHand().size()-1);
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