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

    protected Card discradedCard;
    private Context context;

    public CardAnimation(Card discradedCard, Context context){
        this.discradedCard = discradedCard;
        this.discradedCard.setVisibility(View.VISIBLE);
        this.context = context;
    }

    @SuppressLint("WrongConstant")
    public void cardAnimate(){
        Animation slide = new TranslateAnimation(Animation.ABSOLUTE,Animation.ABSOLUTE,Animation.ABSOLUTE,800);
        this.discradedCard.setBackgroundResource(R.drawable.ace_d);
        slide.setDuration(2000);
        slide.setFillAfter(true);

        this.discradedCard.startAnimation(slide);

    }

    public void setVisibility(int visibility){
        Activity act = (Activity)context;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {

                if(visibility == 1) {
                    this.discradedCard.setVisibility(View.VISIBLE);
                }else if(visibility == 0){
                    this.discradedCard.setVisibility(View.INVISIBLE);
                }

            } });

    }

}
