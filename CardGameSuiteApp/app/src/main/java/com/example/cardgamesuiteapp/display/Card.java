package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.decks.Standard;

import java.util.jar.Attributes;

public class Card extends View implements View.OnTouchListener {

    //    String that keeps track of the drawable to be displayed
    private String card = "";
    private Drawable toDraw;
    public Card(Context context) {
        super(context);
    }

    public Card(Context context, AttributeSet attrs){
        super(context, attrs);
    }

    public void setCard(int card){
        this.card = Standard.getCardImageFileName(card);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Resources resources = this.getContext().getResources();
        int resourceId = resources.getIdentifier(this.card, "drawable", this.getContext().getPackageName());
       setBackgroundResource(resourceId);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }
}
