package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.example.cardgamesuiteapp.R;

public class Deck extends View implements View.OnTouchListener {

    public Deck(Context context) {
        super(context);
        setBackgroundResource(R.drawable.card_back);
    }

    public Deck(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setBackgroundResource(R.drawable.card_back);
    }

    public Deck(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setBackgroundResource(R.drawable.card_back);
    }

    public Deck(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setBackgroundResource(R.drawable.card_back);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
