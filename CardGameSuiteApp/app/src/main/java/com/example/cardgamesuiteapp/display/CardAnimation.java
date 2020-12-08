package com.example.cardgamesuiteapp.display;

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

public class CardAnimation extends View {

    private Card player1;

    public CardAnimation(Context context) {

        super(context);
        init(context);
    }

    public CardAnimation(Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);
        init(context);
    }

    private void init(Context context){
        player1 = new Card(context);
    }

    public void cardAnimate(ImageView theview){
        Animation slide = new TranslateAnimation(Animation.ABSOLUTE,Animation.ABSOLUTE,Animation.ABSOLUTE,800);
        player1.setBackgroundResource(R.drawable.ace_d);
        this.invalidate();
        slide.setDuration(2000);
        slide.setFillAfter(true);

        theview.startAnimation(slide);

    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        this.setBackgroundColor(Color.TRANSPARENT);
        player1.setBackgroundColor(Color.TRANSPARENT);
    }

}
