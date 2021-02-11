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
        View child = getChildAt(0);
        int cardWidth = getMeasuredWidth();
        int cardHeight = getMeasuredHeight();

        if (child.getVisibility() == GONE) {
            return;
        }

        left = 0;
        top = 0;
        right = cardWidth;
        bottom = cardHeight/5;

        child.layout(left, top, right, bottom);
    }
}

