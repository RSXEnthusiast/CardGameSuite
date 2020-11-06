package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;

public class Card extends View implements View.OnTouchListener {

//    String that keeps track of the drawable to be displayed
    private String card = "";

    public Card(Context context) {
        super(context);
    }

//  will determine which card needs to be displayed based on input by the standard class
    public void determineCard(String cardToDisplay){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
