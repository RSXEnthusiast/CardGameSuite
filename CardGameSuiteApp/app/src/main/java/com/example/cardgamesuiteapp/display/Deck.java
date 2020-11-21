package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.cardgamesuiteapp.Fives;
import com.example.cardgamesuiteapp.R;

public class Deck extends Card implements View.OnTouchListener {

    public Deck(Context context) {
        super(context);
        setBackgroundResource(R.drawable.card_back);
    }

    public Deck(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.card_back);
    }

    @Override
    public void updateImage(int imageNum) {
        super.updateImage(-1);
        super.setCardNum(imageNum);
    }
}
