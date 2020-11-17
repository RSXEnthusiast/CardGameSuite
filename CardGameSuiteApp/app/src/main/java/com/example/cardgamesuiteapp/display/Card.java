package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.cardgamesuiteapp.decks.Standard;

public class Card extends View implements View.OnTouchListener {

    //    String that keeps track of the drawable to be displayed
    private int imageId;

    public Card(Context context) {
        super(context);
    }

    public Card(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        setBackgroundResource(getImageId("ace_c"));
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public int getImageId() {
        return imageId;
    }

    public int getImageId(String imageName) {
        return getResources().getIdentifier(imageName, "drawable", this.getContext().getPackageName());
    }

    public void updateImage(int imageNum) {
        imageId = getImageId(Standard.getCardImageFileName(imageNum));
        this.invalidate();
    }
}