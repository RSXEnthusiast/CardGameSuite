package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.cardgamesuiteapp.R;

import java.util.ArrayList;

abstract public class Hand extends ViewGroup {
    int deviceWidth;
    ArrayList<Card> cards = new ArrayList<Card>();

    public Hand(Context context) {
        this(context, null, 0);
    }

    public Hand(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hand(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;
    }

    /**
     * Add all the cards in a hand from an ArrayList to the local array as Cards
     * Also adds the cards to the viewgroup
     *
     * @param hand the hand to be put into the array
     */
    public void initHand(ArrayList<Integer> hand) {
        if (cards.size() == 0) {
            for (int card : hand) {
                Card cardView = new Card(getContext());
                cardView.updateImage(card);
                this.addView(cardView);
                cards.add(cardView);
            }
        }
    }

    /**
     * clears the card array and removes all views from the viewgroup
     */
    public void removeAllCards() {
        int tempLength = cards.size();
        for (int i = 0; i < tempLength; i++) {
            this.removeView(cards.get(0));
            cards.remove(0);
        }
    }

    @Override
    abstract protected void onLayout(boolean changed, int left, int top, int right, int bottom);

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int count = getChildCount();
        // Measurement will ultimately be computing these values.
        int maxHeight = 0;
        int maxWidth = 0;
        int childState = 0;
        int mLeftWidth = 0;
        int rowCount = 0;

        // Iterate through all children, measuring them and computing our dimensions
        // from their size.
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            if (child.getVisibility() == GONE)
                continue;

            // Measure the child.
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            maxWidth += Math.max(maxWidth, child.getMeasuredWidth());
            mLeftWidth += child.getMeasuredWidth();

            if ((mLeftWidth / deviceWidth) > rowCount) {
                maxHeight += child.getMeasuredHeight();
                rowCount++;
            } else {
                maxHeight = Math.max(maxHeight, child.getMeasuredHeight());
            }
            childState = combineMeasuredStates(childState, child.getMeasuredState());
        }

        // Check against our minimum height and width
        maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());

        // Report our final dimensions.
        setMeasuredDimension(resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
                resolveSizeAndState(maxHeight, heightMeasureSpec, childState << MEASURED_HEIGHT_STATE_SHIFT));
    }

    /**
     * Updates a card based on the integer value
     *
     * @param i    The index at which the card to update is
     * @param card The value of the card to update to.
     */
    public void updateCard(int i, int card) {
        cards.get(i).updateImage(card);
    }

    /**
     * Flips over a card by index
     *
     * @param i The index at which to flip the card.
     */
    public void flipCardByIndex(int i) {
        cards.get(i).flipCard();
    }

    /**
     * Flips all cards in the hand
     */
    public void flipAllCards() {
        for (Card card : cards) {
            card.flipCard();
        }
    }

    /**
     * Removes a card from the viewgroup and array
     *
     * @param card the card to be removed
     */
    public void removeCard(int card) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCardNum() == card) {
                this.removeView(cards.get(i));
                cards.remove(i);
            }
        }
    }

    /**
     * adds a card to the array and viewgroup
     *
     * @param card the card to be added
     */
    public void addCard(int card) {
        Card cardView = new Card(getContext());
        cardView.updateImage(card);
        this.addView(cardView);
        cards.add(cardView);
    }

    /**
     * Flips a card by the card value
     *
     * @param cardNum The card to be flipped over.
     */
    public void flipCardByNum(int cardNum) {
        for (Card card : cards) {
            if (card.getCardNum() == cardNum) {
                card.flipCard();
            }
        }
    }

    /**
     * Checks whether a card is flipped up or not.
     *
     * @param index the index at which to check
     * @return true if card is face up, false if not.
     */
    public boolean isCardFaceUp(int index) {
        return cards.get(index).isFaceUp();
    }

    /**
     * @return The number of cards in the array and viewgroup
     */
    public int getNumCards() {
        return cards.size();
    }

    public Card getCard(int cardValue) {
        for (Card card : cards) {
            if (card.getCardNum() == cardValue) {
                return card;
            }
        }
        return null;
    }

    public ArrayList<Card> getHand() {
        return cards;
    }

    public void swapVisibility(int location) {
        if (cards.get(location).getVisibility() == VISIBLE) {
            cards.get(location).setVisibility(INVISIBLE);
        } else {
            cards.get(location).setVisibility(VISIBLE);
        }
    }

    public void allCardsFaceDown() {
        for (Card card : cards) {
            card.setFaceUp(false);
        }
    }
}