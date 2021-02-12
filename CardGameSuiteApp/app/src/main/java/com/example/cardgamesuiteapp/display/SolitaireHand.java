package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class SolitaireHand extends Hand {

    public SolitaireHand(Context context) {
        this(context, null, 0);
    }

    public SolitaireHand(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SolitaireHand(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int count = getChildCount();
        int cardWidth = getMeasuredWidth();
        int cardHeight = getMeasuredHeight();

        for(int i = 0; i < count; i++){
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                return;
            }

            left = 0;
            top = i * 75;
            right = cardWidth;
            bottom = i * 75 + (cardHeight/5);

            child.layout(left, top, right, bottom);
        }

    }
}

