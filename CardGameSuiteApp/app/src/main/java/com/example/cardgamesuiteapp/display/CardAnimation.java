package com.example.cardgamesuiteapp.display;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

import com.example.cardgamesuiteapp.games.Fives;
import com.example.cardgamesuiteapp.R;

public class CardAnimation {

    final Card cardToAnimate;
    final boolean callPostAnimation;
    private Context context;

    public CardAnimation(Card card, boolean callPostAnimation, Context context) {
        this.cardToAnimate = card;
        this.callPostAnimation = callPostAnimation;
        this.cardToAnimate.animate().alpha(0f);
        this.cardToAnimate.setVisibility(View.VISIBLE);
        this.context = context;
    }

    @SuppressLint("WrongConstant")
    public void cardAnimate(float xStart, float xEnd, float yStart, float yEnd) {
        cardToAnimate.setX(0);
        cardToAnimate.setY(0);
        Animation slide = new TranslateAnimation(xStart, xEnd, yStart, yEnd);
        this.cardToAnimate.setBackgroundResource(R.drawable.ace_d);
        slide.setDuration(2000);
        slide.setFillAfter(true);
        this.cardToAnimate.startAnimation(slide);
        slide.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation a) {
                cardToAnimate.animate().alpha(1f);
            }

            public void onAnimationRepeat(Animation a) {
            }

            public void onAnimationEnd(Animation a) {
                cardToAnimate.animate().alpha(0f).setDuration(0);
                if (callPostAnimation) {
                    Fives.postAnimation();
                }
            }
        });
    }

    public Card getCard() {
        return this.cardToAnimate;
    }
}
