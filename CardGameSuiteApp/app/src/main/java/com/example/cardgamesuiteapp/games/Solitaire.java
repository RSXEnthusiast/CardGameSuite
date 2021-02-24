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

import android.widget.TextView;

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
        else if((deck.discardIsEmpty() || deck.peekTopDiscard() == cardNum) && isCardValidToMoveToDiscard(lastTouchedCardNum))
            moveCardToDiscard(lastTouchedCardNum);
        else
            moveCardToEmptyColumn(lastTouchedCardNum);

        updateViewInstruction();
    }

    /**
     * A method to check whether there are any remaining moves or not
     *
     * @return true if the round is over, false if not.
     */
    private static boolean movesRemaining() {
        String [] suits = getSuits();
        boolean areThereRemainingMoves = doAnySuitsMatch(suits);

        for (int j = 0; j < columns; j++) {
            if(suits[j].equals("No Card"))
                areThereRemainingMoves = true;
        }

        if(!areThereRemainingMoves)
            viewDeckHighlight.setVisibility(View.VISIBLE);

        return areThereRemainingMoves;
    }

    /**
     * A method that checks whether card is valid to move or not
     *
     * @return true if the card can move or false if it cannot
     */
    private static boolean isCardValidToMoveToDiscard(int passedCardNum) {
        boolean isCardValidToMove = false;

        Card [] lastFourCards = new Card[4];

        for (int i = 0; i < columns; i++) {
            if (viewColumns[i].getHand().isEmpty()) {
                lastFourCards[i] = null;
            }
            else
                lastFourCards[i] = getLastCardInColumn(i);
        }

        //Game Logic: Card must be of the same suit and lesser value than another card at the bottom of a column
        for (int i = 0; i < lastFourCards.length - 1; i++) {
            for (int j = i + 1; j < lastFourCards.length; j++) {
                String passedCardSuit = Standard.getCardSuit(passedCardNum);
                String suit1 = Standard.getCardSuit(lastFourCards[i].getCardNum());
                String suit2 = Standard.getCardSuit(lastFourCards[j].getCardNum());
                int cardNum1 = lastFourCards[i].getCardNum();
                int cardNum2 = lastFourCards[j].getCardNum();

                if(suit1.equals(suit2) && suit1.equals(passedCardSuit))
                    if((cardNum1 < cardNum2 && cardNum1 == passedCardNum) || (cardNum2 < cardNum1 && cardNum2 == passedCardNum))
                        isCardValidToMove = true;
            }
        }

        return isCardValidToMove;
    }

    /**
     * A simple method to check whether any suits in the string array match
     *
     * @return true if if suits match, false if not
     */
    private static boolean doAnySuitsMatch(String [] suits) {
        boolean doAnySuitsMatch = false;

        for (int i = 0; i < suits.length - 1; i++){
            for (int j = i + 1; j < suits.length; j++){
                if (suits[i].equals(suits[j])){
                    doAnySuitsMatch = true;
                    j = suits.length;
                    i = suits.length;
                }
            }
        }

        return doAnySuitsMatch;
    }

    /**
     * Fill a string array with all the suits from the cards at the bottom of each column
     *
     * @return this string array
     */
    private static String [] getSuits() {
        String [] suits = new String[4];

        for (int i = 0; i < columns; i++) {
            if (viewColumns[i].getHand().isEmpty()) {
                suits[i] = "No Card";
            }
            else
                suits[i] = Standard.getCardSuit(getLastCardInColumn(i).getCardNum());
        }

        return suits;
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
            for (int i = 0; i < viewColumns.length; i++){
                int card = deck.peekTopDraw();
                deck.draw(i);
                viewColumns[i].addCard(card);
            }

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

    /**
     * Makes the discard stack a clickable object even when empty and calls card touched. Passes a negative number to indicating that the click was a destination.
     *
     * @param view is the deck ImageButton
     */
    public void clickDiscardHighlight(View view) {
        cardTouched(-1);
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
     */
    private static void moveCardToDiscard(int cardNum) {
        int columnToMoveCardFrom = findColumnOfSelectedCard(cardNum);
        isAnimating = true;
        removeHighlightedChoices();

        System.out.println(columnToMoveCardFrom);
        System.out.println(lastTouchedCardNum);

        try {
            boolean test = deck.discardByValue(columnToMoveCardFrom, lastTouchedCardNum);
            System.out.println(test);
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

        if(cardNum == -1)
            return -1;

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
        if(emptyColumn == 1)
            viewColumnHighlight1.setVisibility(View.VISIBLE);
        if(emptyColumn == 2)
            viewColumnHighlight2.setVisibility(View.VISIBLE);
        if(emptyColumn == 3)
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