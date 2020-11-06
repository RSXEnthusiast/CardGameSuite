package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

import com.example.cardgamesuiteapp.decks.Standard;

public class Card extends View implements View.OnTouchListener {

    //    String that keeps track of the drawable to be displayed
    private String card = "";

    public Card(Context context, int card) {
        super(context);
        this.card = Standard.getCardImageFileName(card);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        getResources().getDrawable(getResources().getIdentifier(card, "drawable", getClass().getPackage().getName()),null).draw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
