package cards.games;

import androidx.appcompat.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import cards.R;

import cards.MultiplayerConnection.MultiPlayerConnector;
import cards.MultiplayerConnection.MultiplayerOrSinglePlayerMenu;
import cards.MultiplayerConnection.ServerConfig;
import cards.MultiplayerConnection.SocketIOEventArg;
import cards.deckMultiplayerManagement.DeckMultiplayerManager;
import cards.decks.Standard;
import cards.display.Card;
import cards.display.CardAnimation;
import cards.display.Hand;
import cards.gameCollectionMainMenu.DisplayMainPageActivity;
import cards.deckMultiplayerManagement.Operation;
import cards.singlePlayerMenus.FivesSinglePlayerMenu;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;

public class Fives extends MultiPlayerGame {
    static int numPlayers;//total number of players
    static int numOnlineOpponents;// Number of online opponents
    static int numAI;//number of AI players
    static Standard deck;// The Deck object
    static boolean multiplayer;
    MultiPlayerConnector _MultiPlayerConnector;
    public static final MultiPlayerGameInfo gameInfo = new MultiPlayerGameInfo(2, 6);

    public static MultiPlayerGameInfo getGameInfo() {
        return gameInfo;
    }

    Handler _UIHandler;
    ProgressDialog _LoadingDialog;
    //View object names will always be preceded by "view"
    //View objects used for every Fives game
    private static Hand[] viewPlayers;// The custom player views
    private static Card viewDiscard;// The discard view
    private static Card viewDeck;// The deck view
    private static TextView viewInstruction;// The instruction view
    private static Button viewConfirm;// The button the user will press once they've memorized their cards
    private static Button viewReturnToAppCollection;// The button the user will press to return to the main menu
    private static Button viewReturnToPlayerMenu;// The button the user will press to return to the player menu
    private static Button viewReturnToGameMainMenu;// The button the user will press to return to the game's main menu
    private static TextView[] viewPlayerNames;// The textViews of the player names
    private static TextView[] viewPlayerScores;// The textView of the player scores.
    private static View viewDiscardHighlight;// Simply the "highlight" of th discard, mainly used for setting the highlight to visible/invisible
    private static Card viewAnimatedCard1;
    private static CardAnimation viewAnimation1;
    private static Card viewAnimatedCard2;
    private static CardAnimation viewAnimation2;

    private static fivesStage stage;// The current stage of play
    private static int[] totalScores;// Keeps track of the cumulative score of the game
    private static ArrayList<Integer> winnerIndex;
    private static int loserIndex;
    private static boolean isAnimating;
    private static boolean preAnimation;
    private static int lastTouchedCardNum;
    private static int playersReadyToContinue;
    private static int colorOnSecondary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _UIHandler = new Handler();

        multiplayer = (boolean) getIntent().getSerializableExtra("multiplayer");

        if (multiplayer) {
            _MultiPlayerConnector = MultiPlayerConnector.get_Instance();
            _MultiPlayerConnector.addObserver(_MultiPlayerConnectorObserver);

            _LoadingDialog = ProgressDialog.show(Fives.this, "",
                    "Initializing. Please wait...", true);

            _MultiPlayerConnector.emitEvent(ServerConfig.playerReadyForInitialGameData);
        } else {
            initFives();
        }
    }

    @Override
    public void onBackPressed() {
    }

    private void initFives() {
        numAI = (int) getIntent().getSerializableExtra("numAI");
        TypedArray ta = getTheme().obtainStyledAttributes(R.styleable.ViewStyle);
        colorOnSecondary = ta.getColor(R.styleable.ViewStyle_colorOnSecondary, 0);
        if (!multiplayer) numOnlineOpponents = 0;
        if (multiplayer) numAI = 0;
        numPlayers = numAI + numOnlineOpponents + 1;
        setContentView();
        viewPlayers = new Hand[numPlayers];
        viewPlayerNames = new TextView[numPlayers];
        viewPlayerScores = new TextView[numPlayers];
        SharedPreferences fivesGameInfo = getSharedPreferences("fivesGameInfo", MODE_PRIVATE);
        int playerNum = fivesGameInfo.getInt("myNumber", -1);
        initViewPlayers(playerNum);
        SharedPreferences settings = getSharedPreferences("preferences", MODE_PRIVATE);
        viewPlayerNames[playerNum].setText(settings.getString("name", "nameNotFound"));
        if (multiplayer) addOnlinePlayerNamesToView(_OnlinePlayerList);
        for (int i = 1 + numOnlineOpponents; i < numPlayers; i++) {
            viewPlayerNames[i].setText("CPU" + i);
        }
        totalScores = new int[numPlayers];
        deck = new Standard(true, numPlayers, playerNum);
        viewDiscard = findViewById(R.id.discard);
        viewDiscard.bringToFront();
        viewDiscardHighlight = findViewById(R.id.highlightDiscard);
        viewDiscardHighlight.setVisibility(View.INVISIBLE);
        viewDeck = findViewById(R.id.deck);
        viewInstruction = findViewById(R.id.instruction);

        viewConfirm = findViewById(R.id.confirmButton);
        viewConfirm.setOnClickListener(v -> confirmButtonTapped());

        viewReturnToAppCollection = findViewById(R.id.returnToGameCollectionButton);
        viewReturnToAppCollection.setOnClickListener(v -> returnToGameCollection());

        viewReturnToPlayerMenu = findViewById(R.id.returnToPlayerMenuButton);
        if (multiplayer) viewReturnToPlayerMenu.setVisibility(View.INVISIBLE);
        else viewReturnToPlayerMenu.setOnClickListener(v -> returnToPlayerMenu());

        viewReturnToGameMainMenu = findViewById(R.id.returnToGameMainMenuButton);
        viewReturnToGameMainMenu.setText("Fives Home");
        viewReturnToGameMainMenu.setOnClickListener(v -> returnToGameMainMenu());

        winnerIndex = new ArrayList<Integer>();
        viewAnimatedCard1 = findViewById(R.id.animatedCard1);
        viewAnimatedCard1.bringToFront();
        viewAnimation1 = new CardAnimation(viewAnimatedCard1, true, this);
        viewAnimatedCard2 = findViewById(R.id.animatedCard2);
        viewAnimatedCard2.bringToFront();
        viewAnimation2 = new CardAnimation(viewAnimatedCard2, false, this);
        isAnimating = false;
        preAnimation = true;
        handlingIncomingData = false;
        playersReadyToContinue = 0;
        newGame();
    }

    private void addOnlinePlayerNamesToView(ArrayList<Player> playerList) {
        for (Player player : playerList) {
            viewPlayerNames[player.playerNumber].setText(player.playerName);
        }
    }

    private void returnToPlayerMenu() {
        alertBuilder("Return to player Menu", FivesSinglePlayerMenu.class);
    }

    private void returnToGameMainMenu() {
        alertBuilder("Home", MultiplayerOrSinglePlayerMenu.class);
    }

    private void returnToGameCollection() {
        alertBuilder("Return to Main Menu", DisplayMainPageActivity.class);
    }

    private void alertBuilder(String title, Class c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage("All current game progress will be lost.\n\nAre you sure?");
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Fives.this, c);
                intent.putExtra("gameClass", Fives.class); // this is where we are coming from
                intent.putExtra("gameName", "Fives");
                intent.putExtra("singlePlayerClass", FivesSinglePlayerMenu.class);
                startActivity(intent);
                if (multiplayer) endMultiPlayerGame();
                finish();
            }
        });
        builder.setNegativeButton("Cancel", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setContentView() {
        switch (numPlayers) {
            case 2:
                setContentView(R.layout.fives_two_players);
                break;
            case 3:
                setContentView(R.layout.fives_three_players);
                break;
            case 4:
                setContentView(R.layout.fives_four_players);
                break;
            case 5:
                setContentView(R.layout.fives_five_players);
                break;
            case 6:
                setContentView(R.layout.fives_six_players);
                break;
        }
    }

    private void initViewPlayers(int playerNum) {
        for (int i = 0; i < numPlayers; i++) {
            int temp = (playerNum * (numPlayers - 1) + i) % numPlayers + 1;
            viewPlayers[i] = findViewById(getResources().getIdentifier("player" + temp, "id", getPackageName()));
            viewPlayerNames[i] = findViewById(getResources().getIdentifier("player" + temp + "name", "id", getPackageName()));
            viewPlayerScores[i] = findViewById(getResources().getIdentifier("player" + temp + "score", "id", getPackageName()));
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
                if (multiplayer) {
                    for (int i = 0; i < numAI + 1; i++) {
                        playersReadyToContinue++;
                    }
                    DeckMultiplayerManager.readyToContinue();
                    if (playersReadyToContinue >= numPlayers - numAI) {
                        playersReadyToContinue = 0;
                        stage = fivesStage.memCards;
                        newRound();
                        viewConfirm.setText("Memorized");
                        DeckMultiplayerManager.initialize(deck);
                    } else {
                        viewConfirm.setVisibility(View.INVISIBLE);
                    }
                } else {
                    stage = fivesStage.memCards;
                    newRound();
                    viewConfirm.setText("Memorized");
                }
                updateViewInstruction();
                break;
            case "New Game":
                if (multiplayer) {
                    for (int i = 0; i < numAI + 1; i++) {
                        playersReadyToContinue++;
                    }
                    DeckMultiplayerManager.readyToContinue();
                    if (playersReadyToContinue >= numPlayers - numAI) {
                        playersReadyToContinue = 0;
                        stage = fivesStage.memCards;
                        newGame();
                        viewConfirm.setText("Memorized");
                        DeckMultiplayerManager.initialize(deck);
                    } else {
                        viewConfirm.setVisibility(View.INVISIBLE);
                    }
                } else {
                    stage = fivesStage.memCards;
                    newGame();
                    viewConfirm.setText("Memorized");
                }
                updateViewInstruction();
                break;
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
        if (!deck.isMyTurn() || isAnimating) {
            return;
        }
        lastTouchedCardNum = cardNum;
        switch (stage) {
            case draw:
                stageDraw(cardNum);
                break;
            case drewFromDeck:
                stageDrewFromDeck(cardNum);
                break;
            case discardedFromDeck:
                stageDiscardFromDraw(cardNum);
                break;
            case drewFromDiscard:
                stageDrewFromDiscard(cardNum);
                break;
        }
    }

    public static void postAnimation() {
        isAnimating = false;
        if (!preAnimation) {
            cardTouched(lastTouchedCardNum);
        } else {
            postAnimationAI();
        }
    }

    /**
     * This method is called when a card is touched and the current stage is draw
     *
     * @param cardNum the value of the card that was tapped on
     */
    private static void stageDraw(int cardNum) {
        if (cardNum == deck.peekTopDraw()) {
            stage = fivesStage.drewFromDeck;
            viewDeck.flipCard();
            viewDiscardHighlight.setVisibility(View.VISIBLE);
            viewInstruction.setText(getCurInstruction());
            if (multiplayer) {
                DeckMultiplayerManager.flipDeck();
            }
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
    private static void stageDrewFromDeck(int cardNum) {
        boolean endTurn = false;
        handlingIncomingData = true;
        if (deck.peekTopDiscard() == cardNum) {
            if (preAnimation) {
                //Logic for discarded from draw
                viewAnimatedCard1.updateImage(deck.peekTopDraw());
                isAnimating = true;
                preAnimation = false;
                viewDeck.flipCard();
                viewAnimation1.cardAnimate(viewDeck.getX(), viewDiscard.getX(), viewDeck.getY(), viewDiscard.getY());
                if (multiplayer) {
                    DeckMultiplayerManager.discardedFromDeck();
                }
            } else {
                preAnimation = true;
                stage = fivesStage.discardedFromDeck;
                try {
                    deck.discardFromDeck();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewDiscard.updateImage(deck.peekTopDiscard());
                viewDeck.updateImage(deck.peekTopDraw());
                viewDiscardHighlight.setVisibility(View.INVISIBLE);
                updateViewInstruction();
            }
        } else if (isValidTapOnCardInHand(cardNum)) {
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            if (preAnimation) {
                //Logic for swapped with hand
                isAnimating = true;
                preAnimation = false;
                viewAnimatedCard1.updateImage(deck.peekTopDraw());
                viewAnimation1.cardAnimate(viewDeck.getX(), getCardInHandX(cardNum), viewDeck.getY(), getCardInHandY(cardNum));
                viewPlayers[deck.getMyPlayerNum()].swapVisibility(cardLocation);
                viewAnimatedCard2.updateImage(cardNum);
                viewAnimation2.cardAnimate(getCardInHandX(cardNum), viewDiscard.getX(), getCardInHandY(cardNum), viewDiscard.getY());
                viewDeck.flipCard();
                if (multiplayer) {
                    DeckMultiplayerManager.drewFromDeck(cardLocation);
                }
            } else {
                stage = fivesStage.draw;//reset stage, turn over.
                endTurn = true;
                preAnimation = true;
                viewPlayers[deck.getMyPlayerNum()].swapVisibility(cardLocation);
                viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.peekTopDraw());
                viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
                try {
                    deck.discardByValue(deck.getMyPlayerNum(), cardNum);
                    deck.draw(deck.getMyPlayerNum(), cardLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewDeck.updateImage(deck.peekTopDraw());
                viewDiscard.updateImage(deck.peekTopDiscard());
                viewDiscardHighlight.setVisibility(View.INVISIBLE);
                updateViewInstruction();
            }
        }
        if (endTurn) {
            if (roundOver()) {
                new Fives().scoreRound();
            }
            deck.nextPlayer();
            updateViewInstruction();
            handleNextData();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numOnlineOpponents + 1) {
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
            if (multiplayer) {
                DeckMultiplayerManager.flipCardInHand(cardNum);
            }
        }
        if (endTurn) {
            if (roundOver()) {
                new Fives().scoreRound();
            }
            deck.nextPlayer();
            handleNextData();
            updateViewInstruction();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numOnlineOpponents + 1) {
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
        handlingIncomingData = true;
        if (isValidTapOnCardInHand(cardNum)) {
            int cardLocation = deck.getCardLocation(deck.getMyPlayerNum(), cardNum);
            if (preAnimation) {
                viewAnimatedCard1.updateImage(deck.peekTopDiscard());
                isAnimating = true;
                preAnimation = false;
                viewAnimation1.cardAnimate(viewDiscard.getX(), getCardInHandX(cardNum), viewDiscard.getY(), getCardInHandY(cardNum));
                viewPlayers[deck.getMyPlayerNum()].swapVisibility(cardLocation);
                if (deck.getDiscardedCard(1) == -1) {
                    viewDiscard.setVisibility(View.INVISIBLE);
                } else {
                    viewDiscard.updateImage(deck.getDiscardedCard(1));
                }
                viewAnimatedCard2.updateImage(cardNum);
                viewAnimation2.cardAnimate(getCardInHandX(cardNum), viewDiscard.getX(), getCardInHandY(cardNum), viewDiscard.getY());
                if (multiplayer) {
                    DeckMultiplayerManager.drewFromDiscard(cardLocation);
                }
            } else {
                endTurn = true;
                stage = fivesStage.draw;//reset stage, turn over.
                preAnimation = true;
                viewDiscard.setVisibility(View.VISIBLE);
                viewDiscard.updateImage(viewAnimatedCard2.getCardNum());
                viewPlayers[deck.getMyPlayerNum()].swapVisibility(cardLocation);
                try {
                    deck.drawFromDiscard(deck.getMyPlayerNum(), cardLocation);
                    deck.discardByValue(deck.getMyPlayerNum(), cardNum);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                viewPlayers[deck.getMyPlayerNum()].updateCard(cardLocation, deck.getHand(deck.getMyPlayerNum()).get(cardLocation));
                viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(cardLocation);
                updateViewInstruction();
            }
        }
        if (endTurn) {
            if (roundOver()) {
                new Fives().scoreRound();
            }
            deck.nextPlayer();
            handleNextData();
            updateViewInstruction();
            if (numAI > 0 && deck.getCurPlayersTurn() >= numOnlineOpponents + 1) {
                runAITurns();
            }
        }
    }

    private static float getCardInHandX(int cardNum) {
        return viewPlayers[deck.getCurPlayersTurn()].getX() + viewPlayers[deck.getCurPlayersTurn()].getCard(cardNum).getX();
    }

    private static float getCardInHandY(int cardNum) {
        return viewPlayers[deck.getCurPlayersTurn()].getY() + viewPlayers[deck.getCurPlayersTurn()].getCard(cardNum).getY();
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
            viewPlayerNames[i].setTextColor(colorOnSecondary);
            viewPlayerScores[i].setTextColor(colorOnSecondary);
        }
        newRound();
    }

    /**
     * Called every time that there is a new round, to reset the table
     */
    public void newRound() {
        try {
            deck.deal(4);
            deck.discardFromDeck();
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage = fivesStage.memCards;
        setConfirmButtonVisible();
        updateEntireScreen();
    }

    /**
     * This method updates every single dynamic item on the screen.
     */
    private static void updateEntireScreen() {
        viewDiscard.updateImage(deck.peekTopDiscard());
        viewDeck.setFaceUp(false);
        viewDeck.updateImage(deck.peekTopDraw());
        for (int i = 0; i < viewPlayers.length; i++) {
            viewPlayers[i].initHand(deck.getHand(i));
            viewPlayers[i].allCardsFaceDown();
        }
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(2);
        viewPlayers[deck.getMyPlayerNum()].flipCardByIndex(3);
        updateViewInstruction();
        updateViewScores();
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
                case drewFromDeck:
                    return "Discard the new card, or swap it with a face down card";
                case discardedFromDeck:
                    return "Select a face down card to flip over";
                case drewFromDiscard:
                    return "Select a face down card to swap the new card with";
            }
        }
        switch (stage) {
            case roundOver:
                if (playersReadyToContinue > 0) {
                    return "Waiting on other players";
                } else {
                    return "Press continue";
                }
            case gameOver:
                if (playersReadyToContinue == 0) {
                    String winners = "";
                    for (int i = 0; i < winnerIndex.size(); i++) {
                        winners += viewPlayerNames[winnerIndex.get(i)].getText();
                        if (winnerIndex.size() > i + 1) {
                            winners += " and ";
                        }
                    }
                    winners += " won! ";
                    winners += viewPlayerNames[loserIndex].getText() + " lost!";
                    return winners;
                } else if (playersReadyToContinue > 0) {
                    return "Waiting on other players";
                } else {
                    return "Press continue";
                }
        }
        return viewPlayerNames[deck.getCurPlayersTurn()].getText() + "'s Turn";
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
        updateViewInstruction();
        return true;
    }

    /**
     * This method is used to score the game after each round.
     */
    public void scoreRound() {
        for (int i = 0; i < deck.getNumPlayers(); i++) {
            // Duplicates
            ArrayList<Integer> toRemove = new ArrayList<Integer>();
            for (int j = 0; j < deck.getHand(i).size() - 1; j++) {
                for (int k = j + 1; k < deck.getHand(i).size(); k++) {
                    // If it isn't a 5(worth -5) or a K(worth 0) remove it.
                    if (deck.compareNumericalValues(deck.getHand(i).get(j), deck.getHand(i).get(k))
                            && Standard.getNumericalValueAceOne(deck.getHand(i).get(j)) != 5) {
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
                totalScores[i] += getFivesValue(Standard.getNumericalValueAceOne(deck.discardByIndex(i, 0)));
            }
        }
        updateViewScores();
        if (hasWon()) {
            stage = fivesStage.gameOver;
            updateViewInstruction();
            for (int i : winnerIndex) {
                viewPlayerNames[i].setTextColor(Color.GREEN);
                viewPlayerScores[i].setTextColor((Color.GREEN));
            }
            viewPlayerNames[loserIndex].setTextColor(Color.RED);
            viewPlayerScores[loserIndex].setTextColor((Color.RED));
            viewConfirm.setText("New Game");
        } else {
            viewConfirm.setText("Continue");
        }
        updateViewInstruction();
        setConfirmButtonVisible();
        if (multiplayer) {
            deck.nextPlayer();
        }
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
        int maxScore = -100;
        int minScore = 100;
        winnerIndex.clear();
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] > maxScore) {
                maxScore = totalScores[i];
            }
            if (totalScores[i] < minScore) {
                minScore = totalScores[i];
            }
            viewPlayerNames[i].setTextColor(colorOnSecondary);
            viewPlayerScores[i].setTextColor(colorOnSecondary);
        }
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] <= minScore) {
                winnerIndex.add(i);
                viewPlayerNames[i].setTextColor(Color.GREEN);
                viewPlayerScores[i].setTextColor(Color.GREEN);
            }
            if (totalScores[i] >= maxScore) {
                viewPlayerNames[i].setTextColor(Color.RED);
                viewPlayerScores[i].setTextColor(Color.RED);
            }
        }
        if (maxScore < 50) {
            return false;
        }
        int numLosers = 0;
        loserIndex = 0;
        for (int i = 0; i < totalScores.length; i++) {
            if (totalScores[i] == maxScore) {
                numLosers++;
                loserIndex = i;
            }
        }
        if (numLosers > 1) {
            return false;
        }
        return true;
    }

    private static fivesStage AIStage = fivesStage.draw;
    private static boolean beforeAnimationAI = true;
    private static int lastLocation;

    /**
     * Called when it's the AI turns
     */
    private static void runAITurns() {
        beforeAnimationAI = true;
        AIStage = fivesStage.draw;
        int drawFromDiscard = getAIDrawFromDiscard();
        if (drawFromDiscard != -1) {
            AIStage = fivesStage.drewFromDiscard;
            AIDrewFromDiscard(drawFromDiscard);
        } else {
            AIStage = fivesStage.drewFromDeck;
            AIDrawFromPile();
            int drawFromPile = getAIKeepDrawSelection();
            if (drawFromPile != -1) {
                AIStage = fivesStage.drewFromDeck;
                AIKeptDraw(drawFromPile);
            } else {
                AIStage = fivesStage.discardedFromDeck;
                AIDiscardedDraw();
            }
        }
    }

    private static void postAnimationAI() {
        switch (AIStage) {
            case drewFromDiscard:
                AIDrewFromDiscard(lastLocation);
                deck.nextPlayer();
                handleNextData();
                break;
            case drewFromDeck:
                AIKeptDraw(lastLocation);
                deck.nextPlayer();
                handleNextData();
                break;
            case discardedFromDeck:
                AIDiscardedDraw();
                if (deck.getCurPlayersTurn() >= numOnlineOpponents + 1) {
                    AIFlippedCardByIndex(getAIFlipLocation());
                }
                break;
        }
        updateViewInstruction();
        if (!deck.isMyTurn() && numAI > 0) {
            runAITurns();
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
        if (beforeAnimationAI) {
            int cardNum = deck.getHand(deck.getCurPlayersTurn()).get(location);
            beforeAnimationAI = false;
            lastLocation = location;
            viewAnimatedCard1.updateImage(deck.peekTopDraw());
            viewAnimation1.cardAnimate(viewDeck.getX(), getCardInHandX(cardNum), viewDeck.getY(), getCardInHandY(cardNum));
            viewPlayers[deck.getCurPlayersTurn()].swapVisibility(location);
            viewDeck.flipCard();
            viewAnimatedCard2.updateImage(cardNum);
            viewAnimation2.cardAnimate(getCardInHandX(cardNum), viewDiscard.getX(), getCardInHandY(cardNum), viewDiscard.getY());
        } else {
            beforeAnimationAI = true;
            viewPlayers[deck.getCurPlayersTurn()].swapVisibility(lastLocation);
            viewPlayers[deck.getCurPlayersTurn()].updateCard(location, deck.peekTopDraw());
            viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
            try {
                deck.discardByIndex(deck.getCurPlayersTurn(), location);
                deck.draw(deck.getCurPlayersTurn(), location);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDeck.updateImage(deck.peekTopDraw());
            viewDiscard.updateImage(deck.peekTopDiscard());
            AIStage = fivesStage.draw;
        }
        if (roundOver()) {
            new Fives().scoreRound();
        }
    }

    /**
     * This method is called to update the screen after the AI has decided to discard from the draw pile
     */
    private static void AIDiscardedDraw() {
        //logic for flipping over card in hand.
        if (beforeAnimationAI) {
            beforeAnimationAI = false;
            viewAnimatedCard1.updateImage(deck.peekTopDraw());
            viewDeck.flipCard();
            viewAnimation1.cardAnimate(viewDeck.getX(), viewDiscard.getX(), viewDeck.getY(), viewDiscard.getY());
        } else {
            beforeAnimationAI = true;
            try {
                deck.discardFromDeck();
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDeck.updateImage(deck.peekTopDraw());
            viewDiscard.updateImage(deck.peekTopDiscard());
            handleNextData();
        }
    }

    private static void AIFlippedCardByIndex(int location) {
        viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
        AIStage = fivesStage.draw;
        deck.nextPlayer();
        if (roundOver()) {
            new Fives().scoreRound();
        }
        updateViewInstruction();
        handleNextData();
    }

    private static void AIFlippedCardByCardNum(int cardNum) {
        viewPlayers[deck.getCurPlayersTurn()].flipCardByNum(cardNum);
        AIStage = fivesStage.draw;
        deck.nextPlayer();
        if (roundOver()) {
            new Fives().scoreRound();
        }
        updateViewInstruction();
        handleNextData();
    }

    /**
     * This method is called to update the screen and deck after the AI has decided to draw from the discard.
     *
     * @param location where to put the card that the AI drew from this discard.
     */
    private static void AIDrewFromDiscard(int location) {
        if (beforeAnimationAI) {
            int cardNum = deck.getHand(deck.getCurPlayersTurn()).get(location);
            beforeAnimationAI = false;
            lastLocation = location;
            viewAnimatedCard1.updateImage(deck.peekTopDiscard());
            viewAnimation1.cardAnimate(viewDiscard.getX(), getCardInHandX(cardNum), viewDiscard.getY(), getCardInHandY(cardNum));
            viewPlayers[deck.getCurPlayersTurn()].swapVisibility(location);
            viewAnimatedCard2.updateImage(cardNum);
            viewAnimation2.cardAnimate(getCardInHandX(cardNum), viewDiscard.getX(), getCardInHandY(cardNum), viewDiscard.getY());
            if (deck.getDiscardedCard(1) == -1) {
                viewDiscard.setVisibility(View.INVISIBLE);
            } else {
                viewDiscard.updateImage(deck.getDiscardedCard(1));
            }
        } else {
            beforeAnimationAI = true;
            viewDiscard.setVisibility(View.VISIBLE);
            viewDiscard.updateImage(viewAnimatedCard2.getCardNum());
            viewPlayers[deck.getCurPlayersTurn()].swapVisibility(location);
            viewPlayers[deck.getCurPlayersTurn()].updateCard(location, deck.peekTopDiscard());
            viewPlayers[deck.getCurPlayersTurn()].flipCardByIndex(location);
            try {
                deck.drawFromDiscard(deck.getCurPlayersTurn(), location);
                deck.discardByIndex(deck.getCurPlayersTurn(), location + 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            viewDiscard.updateImage(deck.peekTopDiscard());
            AIStage = fivesStage.draw;
        }
        if (roundOver()) {
            new Fives().scoreRound();
        }
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
        if (lowestNewScore - curScore < 0 || (lowestNewScore - curScore <= 3 && Standard.getNumericalValueAceOne(newCard) <= 3)) {
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
                        && Standard.getNumericalValueAceOne(hand.get(i)) != 5) {
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
            totalCount += getFivesValue(Standard.getNumericalValueAceOne(hand.remove(0)));
        }
        return totalCount;
    }

    /**
     * The stages of the fives game turn
     */
    private enum fivesStage {
        memCards, draw, drewFromDeck, discardedFromDeck, drewFromDiscard, roundOver, gameOver
    }

    private static Queue<JSONObject> incomingData = new LinkedList<>();
    static boolean handlingIncomingData;

    private Observer _MultiPlayerConnectorObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            SocketIOEventArg socketIOEventArg = (SocketIOEventArg) arg;
            if (socketIOEventArg._EventName.equals(ServerConfig.gameData)) {
                incomingData.add(socketIOEventArg._JsonObject);
                if (!handlingIncomingData) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            handleIncomingData(incomingData.poll());
                        }
                    });
                }
            }

            if (socketIOEventArg._EventName.equals(ServerConfig.playerNumber)) { // once player number is received that signals game is ready to start

                int myPlayerNumber = (int) socketIOEventArg._JsonObject.opt("playerNumber");
                numOnlineOpponents = (int) socketIOEventArg._JsonObject.opt("numberOfPlayersInRoom") - 1;

                getSharedPreferences("fivesGameInfo", MODE_PRIVATE).edit().putInt("myNumber", myPlayerNumber).apply();


            }


            if (socketIOEventArg._EventName.equals(ServerConfig.playerNumbers)) { // once player number is received that signals game is ready to start

                JSONArray playerNumbers = ((JSONArray) socketIOEventArg._JsonObject.opt("playerNumbers"));
                setPlayerNames(playerNumbers);

                _UIHandler.post(() -> {
                    initFives();
                    if (deck.getMyPlayerNum() == 0) {
                        DeckMultiplayerManager.initialize(deck);
                        _LoadingDialog.dismiss();
                    }
                });

            }

            if (socketIOEventArg._EventName.equals(ServerConfig.playerDisconnected)) {// this means a player has left
                alertThatPlayerHasLeft();
            }
        }
    };

    private class Player {
        public int playerNumber;
        public String playerName;

        public Player(String playerName, int playerNumber) {
            this.playerName = playerName;
            this.playerNumber = playerNumber;
        }
    }

    ArrayList<Player> _OnlinePlayerList;

    private void setPlayerNames(JSONArray playerNames) {
        _OnlinePlayerList = new ArrayList<>();
        for (int i = 0; i < playerNames.length(); i++) {
            JSONObject player = null;
            try {
                player = playerNames.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String playerName = (String) (player.opt("playerName"));
            int playerNumber = (int) player.opt("playerNumber");
            _OnlinePlayerList.add(new Player(playerName, playerNumber));
        }


    }

    private void endMultiPlayerGame() {

        _MultiPlayerConnector.deleteObserver(_MultiPlayerConnectorObserver);
        _MultiPlayerConnector.Close();

    }

    private void alertThatPlayerHasLeft() {
        _MultiPlayerConnector.deleteObserver(_MultiPlayerConnectorObserver);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Player Left Game");
        builder.setMessage("Sorry a player has left the game. The game cannot continue.");
        builder.setPositiveButton("Return to menu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Fives.this, MultiplayerOrSinglePlayerMenu.class);
                intent.putExtra("gameClass", Fives.class); // this is where we are coming from
                intent.putExtra("gameName", "Fives");
                intent.putExtra("singlePlayerClass", FivesSinglePlayerMenu.class);
                startActivity(intent);
                endMultiPlayerGame();
                finish();
            }
        });

        _UIHandler.post(() -> {
            AlertDialog dialog = builder.create();
            dialog.show();
        });

    }

    private static void handleNextData() {
        if (incomingData.isEmpty()) {
            handlingIncomingData = false;
        } else {
            new Fives().handleIncomingData(incomingData.poll());
        }
    }

    public void handleIncomingData(JSONObject data) {
        handlingIncomingData = true;
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        System.out.println(gson.fromJson((String) data.opt("operation"), Operation.class));
        switch (gson.fromJson((String) data.opt("operation"), Operation.class)) {
            case discardFromDeck:
                discardFromDeckReceived();
                break;
            case initialize:
                initializeReceived(data);
                if (_LoadingDialog != null) {
                    _LoadingDialog.dismiss();
                }
                break;
            case drawIntoIndex:
                playerDrawIntoIndexReceived(data);
                break;
            case drawIntoIndexFromDiscard:
                playerDrawIntoIndexFromDiscardReceived(data);
                break;
            case recover:
                recoverReceived(data);
                break;
            case flipCardInHand:
                flipCardReceived(data);
                break;
            case flipDeck:
                viewDeck.flipCard();
                handleNextData();
                break;
            case readyToContinue:
                playersReadyToContinue++;
                if (playersReadyToContinue >= numPlayers - numAI) {
                    playersReadyToContinue = 0;
                    stage = fivesStage.memCards;
                    boolean newGame = false;
                    if (viewConfirm.getText().equals("New Game")) {
                        newGame = true;
                    }
                    viewConfirm.setText("Memorized");
                    setConfirmButtonVisible();
                    if (newGame) {
                        newGame();
                    } else {
                        newRound();
                    }
                }
                handleNextData();
                break;
        }
    }

    private void setConfirmButtonVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewConfirm.setVisibility(View.VISIBLE);
            }
        });
    }

    private static void flipCardReceived(JSONObject data) {
        AIFlippedCardByCardNum((int) data.opt("cardNum"));
    }


    private static void initializeReceived(JSONObject deck) {
        Fives.deck.initializeFromPeer(deck);
        updateEntireScreen();
        handleNextData();
    }

    private static void recoverReceived(JSONObject deck) {
        handleNextData();
    }

    private static void playerDrawIntoIndexFromDiscardReceived(JSONObject data) {
        AIStage = fivesStage.drewFromDiscard;
        AIDrewFromDiscard((Integer) data.opt("location"));
    }

    private static void playerDrawIntoIndexReceived(JSONObject data) {
        AIStage = fivesStage.drewFromDeck;
        AIKeptDraw((Integer) data.opt("location"));
    }

    private static void discardFromDeckReceived() {
        AIStage = fivesStage.discardedFromDeck;
        AIDiscardedDraw();
    }
}