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
    private int cardNum;
    private boolean isFlipped = false;


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
        this.flipCard();
        return false;
    }

    public int getImageId() {
        return imageId;
    }

    public int getImageId(String imageName) {
        return getResources().getIdentifier(imageName, "drawable", this.getContext().getPackageName());
    }

    public void updateImage(int imageNum) {
        cardNum = imageNum;
        imageId = getImageId(Standard.getCardImageFileName(imageNum));
        this.setOnTouchListener(this);
        this.invalidate();
    }

    //changes the cards drawable resource to the back of a card
    ///or vice versa if the card is already flipped
    public void flipCard(){
        if(!isFlipped){
            imageId = getImageId(Standard.getCardImageFileName(-1));
            this.isFlipped = true;
        }else{
            imageId = getImageId(Standard.getCardImageFileName(cardNum));
            this.isFlipped = false;
        }
        this.invalidate();
    }

    public int getCardNum() {
        return cardNum;
    }
}