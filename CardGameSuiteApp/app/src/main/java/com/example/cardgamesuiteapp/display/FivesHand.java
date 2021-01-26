package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class FivesHand extends Hand {

    public FivesHand(Context context) {
        this(context, null, 0);
    }

    public FivesHand(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FivesHand(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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
}
