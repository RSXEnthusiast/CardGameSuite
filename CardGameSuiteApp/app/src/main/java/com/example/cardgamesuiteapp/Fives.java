package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.cardgamesuiteapp.decks.Standard;
import com.example.cardgamesuiteapp.display.Card;
import com.example.cardgamesuiteapp.display.Hand;

import java.util.ArrayList;

public class Fives extends AppCompatActivity {
    static int numHumans;
    static int numAI;
    static Standard deck;
    static int[] totalScores;// Keeps track of the cumulative score of the game
    static Hand[] viewPlayers;// The custom player views
    static Card viewDiscard;// The discard view
    static Card viewDeck;// The deck view
    static TextView viewInstruction;// The instruction view
    static Button viewConfirm;// The button the user will press once they've memorized their cards
    static Button viewReturnToMainMenu;
    static TextView[] viewPlayerNames;// The textViews of the player names
    static TextView[] viewPlayerScores;// The textView of the player scores.
    static View viewDiscardHighlight;
    static View[] viewAINumButtons;
    static fivesStage stage;// The current stage of play
    static int winnerIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_player_menu);
        initAISelectionMenu();
    }

    private void initAISelectionMenu() {
        viewAINumButtons = new View[5];
        viewAINumButtons[0] = findViewById(R.id.oneAI);
        viewAINumButtons[0].setOnClickListener(v -> selectedOneAI());
        viewAINumButtons[1] = findViewById(R.id.twoAI);
        viewAINumButtons[1].setOnClickListener(v -> selectedTwoAI());
        viewAINumButtons[2] = findViewById(R.id.threeAI);
        viewAINumButtons[2].setOnClickListener(v -> selectedThreeAI());
        viewAINumButtons[3] = findViewById(R.id.fourAI);
        viewAINumButtons[3].setOnClickListener(v -> selectedFourAI());
        viewAINumButtons[4] = findViewById(R.id.fiveAI);
        viewAINumButtons[4].setOnClickListener(v -> selectedFiveAI());
    }

    private void selectedOneAI() {
        numAISelected(1);
    }

    private void selectedTwoAI() {
        numAISelected(2);
    }

    private void selectedThreeAI() {
        numAISelected(3);
    }

    private void selectedFourAI() {
        numAISelected(4);
    }

    private void selectedFiveAI() {
        numAISelected(5);
    }

    private void numAISelected(int numAI) {
        numHumans = 1;
        this.numAI = numAI;
        initFives();
    }

    private void initFives() {
        setContentView();
        viewPlayers = new Hand[numAI + numHumans];
        viewPlayerNames = new TextView[numHumans + numAI];
        viewPlayerScores = new TextView[numHumans + numAI];
        initViewPlayers();
        //Temp - placeholder for player name
        viewPlayerNames[0].setText("Human");
        for (int i = numHumans; i <= numAI; i++) {
            viewPlayerNames[i].setText("Computer " + i);
        }
        totalScores = new int[numHumans + numAI];
        deck = new Standard(true, numHumans + numAI);
        viewDiscard = findViewById(R.id.discard);
        viewDiscard.bringToFront();
        viewDiscardHighlight = findViewById(R.id.highlightDiscard);
        viewDiscardHighlight.setVisibility(View.INVISIBLE);
        viewDeck = findViewById(R.id.deck);
        viewInstruction = findViewById(R.id.instruction);
        viewConfirm = findViewById(R.id.confirmButton);
        viewConfirm.setOnClickListener(v -> confirmButtonTapped());//call confirmButtonTapped when that button is tapped
        viewReturnToMainMenu = findViewById(R.id.returnToMainMenuButton);
        viewReturnToMainMenu.setOnClickListener(v -> returnToMainMenu());
        newGame();
    }

    private void returnToMainMenu() {
        setContentView(R.layout.single_player_menu);
        initAISelectionMenu();
    }

    private void setContentView() {
        switch (numHumans + numAI) {
            case 2:
                setContentView(R.layout.two_players);
                break;
            case 3:
                setContentView(R.layout.three_players);
                break;
            case 4:
                setContentView(R.layout.four_players);
                break;
            case 5:
                setContentView(R.layout.five_players);
                break;
            case 6:
                setContentView(R.layout.six_players);
                break;
        }
    }

    private void initViewPlayers() {
        viewPlayers[0] = findViewById(R.id.player1);
        viewPlayerNames[0] = findViewById(R.id.player1name);
        viewPlayerScores[0] = findViewById(R.id.player1score);
        viewPlayers[1] = findViewById(R.id.player2);
        viewPlayerNames[1] = findViewById(R.id.player2name);
        viewPlayerScores[1] = findViewById(R.id.player2score);
        int numPlayers = numHumans + numAI;
        if (numPlayers >= 3) {
            viewPlayers[2] = findViewById(R.id.player3);
            viewPlayerNames[2] = findViewById(R.id.player3name);
            viewPlayerScores[2] = findViewById(R.id.player3score);
        }
        if (numPlayers >= 4) {
            viewPlayers[3] = findViewById(R.id.player4);
            viewPlayerNames[3] = findViewById(R.id.player4name);
            viewPlayerScores[3] = findViewById(R.id.player4score);
        }
        if (numPlayers >= 5) {
            viewPlayers[4] = findViewById(R.id.player5);
            viewPlayerNames[4] = findViewById(R.id.player5name);
            viewPlayerScores[4] = findViewById(R.id.player5score);
        }
        if (numPlayers >= 6) {
            viewPlayers[5] = findViewById(R.id.player6);
            viewPlayerNames[5] = findViewById(R.id.player6name);
            viewPlayerScores[5] = findViewById(R.id.player6score);
        }
    }


    /**
     * When confirmMemorizedButton is tapped, this method is called.
     */
    private void confirmButtonTapped() {
        switch ((String) viewConfirm.getText()) {
            case "Memorized":
                stage = fivesStage.draw;
                updateViewInstruction();
                viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
                viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
                viewConfirm.setVisibility(View.INVISIBLE);
                break;
            case "Continue":
                stage = fivesStage.memCards;
                viewConfirm.setText("Memorized");
                newRound();
                break;
            case "New Game":
                stage = fivesStage.memCards;
                viewConfirm.setText("Memorized");
                viewReturnToMainMenu.setVisibility(View.INVISIBLE);
                newGame();
        }
    }

    /**
     * Updates view instruction based upon current stage
     */
    private static void updateViewInstruction() {
        viewInstruction.setText(getCurInstruction());
    }

    private static void updateViewScores() {
        for (int i = 0; i < viewPlayerScores.length; i++) {
            viewPlayerScores[i].setText("" + totalScores[i]);
        }
    }

    /**
     * Walks through the stages of a players turn and switches the value of the enum and calls the relevant game method during each phase
     *
     * @param cardNum the value of the card that was tapped on
     */
    public static void cardTouched(int cardNum) {
        if (!deck.isMyTurn()) {
            return;//Not your turn
        }
        switch (stage) {
            case draw:
                stageDraw(cardNum);
                break;
            case drewFromDraw:
                stageDrewFromDraw(cardNum);
                break;
            case discardedFromDraw:
                stageDiscardFromDraw(cardNum);
                break;
            case drewFromDiscard:
                stageDrewFromDiscard(cardNum);
                break;
        }
    }

    /**
     * This method is called when a card is touched and the current stage is draw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDraw(int cardNum) {
        if (cardNum == deck.peekTopDraw()) {
            stage = fivesStage.drewFromDraw;
            viewDeck.flipCard();
            viewDiscardHighlight.setVisibility(View.VISIBLE);
            viewInstruction.setText(getCurInstruction());
        } else if (cardNum == deck.peekTopDiscard()) {
            stage = fivesStage.drewFromDiscard;
            viewInstruction.setText(getCurInstruction());
        }
    }

    /**
     * This method is called when a card is touched and the current stage is drewFromDraw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDrewFromDraw(int cardNum) {
        boolean endTurn = false;
        if (deck.peekTopDiscard() == cardNum) {
            stage = fivesStage.discardedFromDraw;
            //Logic for discarded from draw
            try {
                deck.discardFromDeck();
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDiscard.updateImage(deck.peekTopDiscard());
            viewDeck.flipCard();
            viewDeck.updateImage(deck.peekTopDraw());
            viewDiscardHighlight.setVisibility(View.INVISIBLE);
            updateViewInstruction();
        } else if (isValidTapOnCardInHand(cardNum)) {
            endTurn = true;
            stage = fivesStage.draw;//reset stage, turn over.
            //Logic for swapped with hand
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.peekTopDraw());
            viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
            try {
                deck.discardByValue(deck.getMyPlayerNum(), cardNum);
                deck.draw(deck.getMyPlayerNum(), cardLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDeck.flipCard();
            viewDeck.updateImage(deck.peekTopDraw());
            viewDiscard.updateImage(deck.peekTopDiscard());
            viewDiscardHighlight.setVisibility(View.INVISIBLE);
            updateViewInstruction();
        }
        if (endTurn) {
            if (roundOver()) {
                scoreRound();
            }
            deck.nextPlayer();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numHumans) {
                runAITurns();
            }
        }
    }


    /**
     * This method is called when a card is touched and the current stage is discardedFromDraw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDiscardFromDraw(int cardNum) {
        boolean endTurn = false;
        if (isValidTapOnCardInHand(cardNum)) {
            endTurn = true;
            stage = fivesStage.draw; //reset stage, turn over.
            //logic for flipping over card in hand.
            viewPlayers[deck.getMyPlayerNum()].flipCardByNum(cardNum);
            updateViewInstruction();
        }
        if (endTurn) {
            if (roundOver()) {
                scoreRound();
            }
            deck.nextPlayer();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numHumans) {
                runAITurns();
            }
        }
    }

    /**
     * This method is called when a card is touched and the current stage is drewFromDiscard
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDrewFromDiscard(int cardNum) {
        boolean endTurn = false;
        if (isValidTapOnCardInHand(cardNum)) {
            endTurn = true;
            stage = fivesStage.draw; //reset stage, turn over.
            //logic for drawn from discard
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.peekTopDiscard());
            viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
            try {
                deck.drawFromDiscard(deck.getMyPlayerNum(), cardLocation);
                deck.discardByValue(deck.getMyPlayerNum(), cardNum);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDiscard.updateImage(deck.peekTopDiscard());
            updateViewInstruction();
        }
        if (endTurn) {
            if (roundOver()) {
                scoreRound();
            }
            deck.nextPlayer();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numHumans) {
                runAITurns();
            }
        }
    }


    /**
     * @param cardNum the value of the card that was tapped on
     * @return true if the card is in the hand and is face down
     */
    private static boolean isValidTapOnCardInHand(int cardNum) {
        ArrayList<Integer> hand = deck.getHand(deck.getMyPlayerNum());
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i) == cardNum && !viewPlayers[deck.getMyPlayerNum()].isCardFaceUp(i)) {
                return true;
            }
        }
        return false;
    }

    private void newGame() {
        deck.shuffleDiscardIntoDeck();
        for (int i = 0; i < totalScores.length; i++) {
            totalScores[i] = 0;
        }
        updateViewScores();
        newRound();
    }


    /**
     * Called every time that there is a new round, to reset the table
     */
    public static void newRound() {
        try {
            deck.deal(4);
            deck.discardFromDeck();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Flipping up discard card
        stage = fivesStage.memCards;
        viewConfirm.setVisibility(View.VISIBLE);
        updateEntireScreen();
    }

    /**
     * This method updates every single dynamic item on the screen.
     */
    private static void updateEntireScreen() {
        viewDiscard.updateImage(deck.peekTopDiscard());
        viewDeck.updateImage(deck.peekTopDraw());
        viewDeck.setFaceUp(false);
        for (int i = 0; i < viewPlayers.length; i++) {
            viewPlayers[i].initHand(deck.getHand(i));
            viewPlayers[i].flipAllCards();
        }
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
        viewInstruction.setText(getCurInstruction());
    }

    /**
     * @return the instruction text based on the current stage, or which player's turn it is
     */
    private static String getCurInstruction() {
        if (deck.isMyTurn()) {
            switch (stage) {
                case memCards:
                    return "Memorize your bottom two cards";
                case draw:
                    return "Draw a card";
                case drewFromDraw:
                    return "Discard the new card, or swap it with a face down card";
                case discardedFromDraw:
                    return "Select a face down card to flip over";
                case drewFromDiscard:
                    return "Select a face down card to swap the new card with";
                case roundOver:
                    return "Press continue";
                case gameOver:
                    return viewPlayerNames[winnerIndex].getText() + " won!";
            }
        }
        return "Player " + deck.getCurPlayersTurn() + "'s Turn";
    }

    /**
     * A simple method to check whether the round is complete or not.
     *
     * @return true if the round is over, false if not.
     */
    private static boolean roundOver() {
        for (int i = 0; i < viewPlayers.length; i++) {
            for (int j = 0; j < viewPlayers[i].getNumCards(); j++) {
                if (!viewPlayers[i].isCardFaceUp(j)) {
                    return false;
                }
            }
        }
        stage = fivesStage.roundOver;
        return true;
    }

    /**
     * This method is used to score the game after each round.
     */
    public static void scoreRound() {
        for (int i = 0; i < deck.getNumPlayers(); i++) {
            // Duplicates
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            for (int j = 0; j < deck.getHand(i).size() - 1; j++) {
                for (int k = j + 1; k < deck.getHand(i).size(); k++) {
                    // If it isn't a 5(worth -5) or a K(worth 0) remove it.
                    if (deck.compareNumericalValues(deck.getHand(i).get(j), deck.getHand(i).get(k))
                            && Standard.getNumericalValue(deck.getHand(i).get(j)) != 5) {
                        toRemove.add(deck.getHand(i).get(j));
                    }
                }
            }
            //Remove all instances of the duplicate numbers
            for (int num : toRemove) {
                while (deck.discardByNumericalValue(i, num)) {
                    //Empty while
                }
            }
            // Adding current round values to score
            while (!deck.getHand(i).isEmpty()) {
                totalScores[i] += getFivesValue(Standard.getNumericalValue(deck.discardByIndex(i, 0)));
            }
        }
        updateViewScores();
        if (hasWon()) {
            stage = fivesStage.gameOver;
            updateViewInstruction();
            viewConfirm.setText("New Game");
            viewReturnToMainMenu.setVisibility(View.VISIBLE);
        } else {
            viewConfirm.setText("Continue");
        }
        viewConfirm.setVisibility(View.VISIBLE);
    }

    /**
     * @param i the card value
     * @return the numerical value of the card in fives
     */
    private static int getFivesValue(int i) {
        if (i == 5) {
            return -5;
        }
        if (i == 13) {
            return 0;
        }
        if (i > 10) {
            return 10;
        }
        return i;
    }

    /**
     * @return true if a player has won
     */
    private static boolean hasWon() {
        int maxScore = 0;
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] > maxScore) {
                maxScore = totalScores[i];
            }
        }
        if (maxScore < 50) {
            return false;
        }
        int numWinners = 0;
        winnerIndex = 0;
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] == maxScore) {
                numWinners++;
                winnerIndex = i;
            }
        }
        if (numWinners > 1) {
            return false;
        }
        return true;
    }

    /**
     * Called when it's the AI turns
     */
    private static void runAITurns() {
        for (int i = numHumans; i < numAI + numHumans; i++) {
            int drawFromDiscard = getAIDrawFromDiscard();
            if (drawFromDiscard != -1) {
                AIDrewFromDiscard(drawFromDiscard);
            } else {
                AIDrawFromPile();
                int drawFromPile = getAIKeepDrawSelection();
                if (drawFromPile != -1) {
                    AIKeptDraw(drawFromPile);
                } else {
                    AIDiscardedDraw(getAIFlipLocation());
                }
            }
            deck.nextPlayer();
        }
        if (roundOver()) {
            scoreRound();
        }
    }

    /**
     * This updates the screen after the AI has decided to draw from the draw pile.
     */
    private static void AIDrawFromPile() {
        viewDeck.flipCard();
    }

    /**
     * This is the logic for updating the screen after the AI has kept a drawn card
     *
     * @param location where the card should be swapped to
     */
    private static void AIKeptDraw(int location) {
        //Logic for swapped with hand
        viewPlayers[deck.getCurPlayersTurn()].updateCard(location, deck.peekTopDraw());
        viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
        try {
            deck.discardByIndex(deck.getCurPlayersTurn(), location);
            deck.draw(deck.getCurPlayersTurn(), location);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewDeck.flipCard();
        viewDeck.updateImage(deck.peekTopDraw());
        viewDiscard.updateImage(deck.peekTopDiscard());
    }

    /**
     * This method is called to update the screen after the AI has decided to discard from the draw pile
     *
     * @param location which card will be flipped up.
     */
    private static void AIDiscardedDraw(int location) {
        //logic for flipping over card in hand.
        viewDeck.flipCard();
        try {
            deck.discardFromDeck();
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewDeck.updateImage(deck.peekTopDraw());
        viewDiscard.updateImage(deck.peekTopDiscard());
        viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
    }

    /**
     * This method is called to update the screen and deck after the AI has decided to draw from the discard.
     *
     * @param location where to put the card that the AI drew from this discard.
     */
    private static void AIDrewFromDiscard(int location) {
        viewPlayers[deck.getCurPlayersTurn()].updateCard(location, deck.peekTopDiscard());
        viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
        try {
            deck.drawFromDiscard(deck.getCurPlayersTurn(), location);
            deck.discardByIndex(deck.getCurPlayersTurn(), location + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        viewDiscard.updateImage(deck.peekTopDiscard());
    }

    /**
     * The AI will decide whether to pickup the discard or not.
     *
     * @return -1 if the AI doesn't want to draw from discard, or the index at which the AI wants to place the discard.
     */
    private static int getAIDrawFromDiscard() {
        return keepCard(deck.peekTopDiscard());
    }

    /**
     * If the AI drew from the pile they will decide whether to keep the card or not
     *
     * @return -1 if the AI discards the drawn card, or the index at which the AI would like to put the drawn card.
     */
    private static int getAIKeepDrawSelection() {
        return keepCard(deck.peekTopDraw());
    }

    /**
     * @param newCard the card to test in the hand
     * @return -1 if the AI doesn't want the card, or the index at which the AI would like to put the card.
     */
    private static int keepCard(int newCard) {
        ArrayList<Integer> curHand = deck.getHand(deck.getCurPlayersTurn());
        int curScore = getAIKnownHandWorth((ArrayList<Integer>) curHand.clone(), -1);
        int lowestNewScore = 100;
        int lowestNewScoreMoveIndex = -1;
        for (int i = 0; i < 4; i++) {
            if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(i)) {
                ArrayList<Integer> testingNewHand = (ArrayList<Integer>) curHand.clone();
                testingNewHand.remove(i);
                testingNewHand.add(i, newCard);
                int testingNewHandWorth = getAIKnownHandWorth(testingNewHand, i);
                if (testingNewHandWorth < lowestNewScore) {
                    lowestNewScore = testingNewHandWorth;
                    lowestNewScoreMoveIndex = i;
                }
            }
        }
        if (lowestNewScore - curScore < 0 || (lowestNewScore - curScore <= 3 && Standard.getNumericalValue(newCard) <= 3)) {
            return lowestNewScoreMoveIndex;
        }
        return -1;
    }

    /**
     * The AI will decide which card to flip up or swap
     *
     * @return the location of the card to flip up or swap
     */
    private static int getAIFlipLocation() {
        int bestIndex = 2;
        ArrayList<Integer> curHand = deck.getHand(deck.getCurPlayersTurn());
        int curHandWorth = getAICurHandWorth((ArrayList<Integer>) curHand.clone());
        int bestNewHandVal = 1000;
        if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(2)) {
            bestNewHandVal = getAIKnownHandWorth((ArrayList<Integer>) curHand.clone(), 2);
        }
        int newHandValTemp = 1000;
        if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(3)) {
            newHandValTemp = getAIKnownHandWorth((ArrayList<Integer>) curHand.clone(), 3);
        }
        if (newHandValTemp < bestNewHandVal) {
            bestNewHandVal = newHandValTemp;
            bestIndex = 3;
        }
        if (bestNewHandVal - curHandWorth <= 3) {
            return bestIndex;
        }
        if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(0)) {
            return 0;
        }
        if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(1)) {
            return 1;
        }
        return bestIndex;
    }

    /**
     * @param hand            the hand that the method will evaluate
     * @param testingLocation the location that is currently flipped down, but needs to be evaluated as it's the potential new card location.
     * @return the value of the face up cards and the card at the testing location
     */
    private static int getAIKnownHandWorth(ArrayList<Integer> hand, int testingLocation) {
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        //Removing cards that are unknown
        for (int i = 0; i < 2; i++) {
            if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(i) && i != testingLocation) {
                toRemove.add(hand.get(i));
            }
        }
        while (!toRemove.isEmpty()) {
            hand.remove((Object) toRemove.remove(0));
        }
        return scoreAIHand(hand);
    }


    /**
     * @param hand the hand the the method will evaluate
     * @return the value of the face up cards in the hand
     */
    private static int getAICurHandWorth(ArrayList<Integer> hand) {
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        //Removing cards that are unknown
        for (int i = 0; i < 4; i++) {
            if (!viewPlayers[deck.getCurPlayersTurn()].isCardFaceUp(i)) {
                toRemove.add(hand.get(i));
            }
        }
        while (!toRemove.isEmpty()) {
            hand.remove((Object) toRemove.remove(0));
        }
        return scoreAIHand(hand);
    }

    /**
     * @param hand The hand that the method will evaluate
     * @return the value of the hand
     */
    private static int scoreAIHand(ArrayList<Integer> hand) {
        ArrayList<Integer> toRemove = new ArrayList<Integer>();
        //Removing duplicates
        for (int i = 0; i < hand.size() - 1; i++) {
            for (int j = i + 1; j < hand.size(); j++) {
                // If it isn't a 5(worth -5) or a K(worth 0) remove it.
                if (deck.compareNumericalValues(hand.get(i), hand.get(j))
                        && Standard.getNumericalValue(hand.get(i)) != 5) {
                    toRemove.add(hand.get(i));
                }
            }
        }
        //Remove all instances of the duplicate numbers
        for (int num : toRemove) {
            while (hand.remove((Object) num)) {
                //Empty while
            }
        }
        int totalCount = 0;
        // Adding current round values to score
        while (!hand.isEmpty()) {
            totalCount += getFivesValue(Standard.getNumericalValue(hand.remove(0)));
        }
        return totalCount;
    }

    /**
     * The stages of the fives game turn
     */
    private enum fivesStage {
        memCards, draw, drewFromDraw, discardedFromDraw, drewFromDiscard, roundOver, gameOver
    }
}