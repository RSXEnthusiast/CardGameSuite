package com.example.cardgamesuiteapp.display;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.example.cardgamesuiteapp.R;

public class CardAnimation {

    protected Card discardedCard;
    private Context context;

    public CardAnimation(Card discardedCard, Context context) {
        this.discardedCard = discardedCard;
        this.discardedCard.setVisibility(View.VISIBLE);
        this.context = context;
    }

    @SuppressLint("WrongConstant")
    public void cardAnimate() {
        Animation slide = new TranslateAnimation(Animation.ABSOLUTE, Animation.ABSOLUTE, Animation.ABSOLUTE, 800);
        discardedCard.setBackgroundResource(R.drawable.ace_d);
        slide.setDuration(2000);
        slide.setFillAfter(true);
        discardedCard.startAnimation(slide);
    }

    public void setVisibility(int visibility) {
        Activity act = (Activity) context;
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (visibility == 1) {
                    discardedCard.setVisibility(View.VISIBLE);
                } else if (visibility == 0) {
                    discardedCard.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}
