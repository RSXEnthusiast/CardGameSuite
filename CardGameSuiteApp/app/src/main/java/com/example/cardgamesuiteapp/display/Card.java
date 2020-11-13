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
        setBackgroundResource(imageId);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    public void getImageId(String imageName) {
        imageId = getResources().getIdentifier(imageName, "drawable", this.getContext().getPackageName());
    }

    public void updateImage(int imageNum) {
        getImageId(Standard.getCardImageFileName(imageNum));
        this.invalidate();
    }
}
