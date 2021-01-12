package com.example.cardgamesuiteapp.display;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.cardgamesuiteapp.R;

public class CardAnimation {

    private Card discardedCard;
    private Context context;

    public CardAnimation(Card discardedCard, Context context){
        this.discardedCard = discardedCard;
        this.discardedCard.setVisibility(View.VISIBLE);
        this.context = context;
    }

    @SuppressLint("WrongConstant")
    public void cardAnimate(){
        Animation slide = new TranslateAnimation(Animation.ABSOLUTE,Animation.ABSOLUTE,Animation.ABSOLUTE,800);
        this.discardedCard.setBackgroundResource(R.drawable.ace_d);
        slide.setDuration(2000);
        slide.setFillAfter(true);
        slide.setAnimationListener(new Animation.AnimationListener(){
            public void onAnimationStart(Animation a){}
            public void onAnimationRepeat(Animation a){}
            public void onAnimationEnd(Animation a){
            }
        });
        this.discardedCard.startAnimation(slide);
    }

    public Card getCard(){
        return this.discardedCard;
    }

    public void setVisibility(int visibility){

        if (visibility == 0){
            this.discardedCard.animate().alpha(0f).setDuration(3000);
        }
        if(visibility == 1){
            this.discardedCard.animate().alpha(1f);
        }
    }

}
