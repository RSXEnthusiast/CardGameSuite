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
        boolean bigFontCard = getCardType();
        int count = getChildCount();
        int cardWidth = getMeasuredWidth();
        int cardHeight = getMeasuredHeight();
        int layeredCardDistance = 90;
        int lastShrunk = 0;

        if(bigFontCard)
            layeredCardDistance = 220;

        for(int i = 0; i < count; i++){
            View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                return;
            }

            left = 0;
            right = cardWidth;

            if(bigFontCard && count-i>3) {
                top = i * 40;
                bottom = (int) (i * 40 + (cardHeight / 5.5));
                lastShrunk = i;
            }
            else if(bigFontCard && count-i<=3) {
                top = ((i - lastShrunk) * layeredCardDistance) + lastShrunk*40;
                bottom = (int) (((i - lastShrunk) * layeredCardDistance) + lastShrunk*40 + (cardHeight / 5.5));
            }
            else {
                top = i * layeredCardDistance;
                bottom = i * layeredCardDistance + (cardHeight / 5);
            }

            child.layout(left, top, right, bottom);
        }
    }

    public boolean getCardType () {
        if(getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE).getString("cardStyle", "cardStyle not found").contains("big"))
            return true;
        else
            return false;
    }

    public Card removeFinalCard(){
        Card finalCard = cards.get(cards.size()-1);
        this.removeCard(cards.get(cards.size()-1).getCardNum());
        return finalCard;
    }

    /** Testing method please remove**/
    public Card finalCard(){
        return cards.get(cards.size()-1);
    }

}

