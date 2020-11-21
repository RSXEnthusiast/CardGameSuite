package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;

public class Hand extends ViewGroup {
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

    private void init(Context context) {
        final Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point deviceDisplay = new Point();
        display.getSize(deviceDisplay);
        deviceWidth = deviceDisplay.x;
    }

    public void initHand(ArrayList<Integer> hand) {
        for (int card : hand) {
            Card cardView = new Card(getContext());
            cardView.updateImage(card);
            this.addView(cardView);
            cards.add(cardView);
        }
    }

    public void removeAllCards(){
        for (Card card : cards){
            this.removeView(card);
            cards.remove(card);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        int cardWidth = getMeasuredWidth() / 2;
        int cardHeight = getMeasuredHeight() / 2;

        for (int i = 0; i < count; i++) {
            View child = getChildAt(i);

            if (child.getVisibility() == GONE) {
                return;
            }
            left = cardWidth * (i % 2);
            top = cardHeight * (i / 2);
            right = cardWidth * (i % 2 + 1);
            bottom = cardHeight * (i / 2 + 1);
            child.layout(left, top, right, bottom);
        }
    }

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

    public void updateCard(int i, int card) {
        cards.get(i).updateImage(card);
    }

    public void flipCard(int i){cards.get(i).flipCard();}

    public void removeCard(int card) {
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).getCardNum() == card) {
                this.removeView(cards.get(i));
                cards.remove(i);
            }
        }
    }

    public void addCard(int card) {
        Card cardView = new Card(getContext());
        cardView.updateImage(card);
        this.addView(cardView);
        cards.add(cardView);
    }
}