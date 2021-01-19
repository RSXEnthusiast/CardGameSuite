package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.cardgamesuiteapp.games.Fives;
import com.example.cardgamesuiteapp.R;
import com.example.cardgamesuiteapp.decks.Standard;

public class Card extends View implements View.OnTouchListener {

    //    String that keeps track of the drawable to be displayed
    private int imageId;
    private int cardNum;
    private boolean isFaceUp = true;

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
        Fives.cardTouched(cardNum);
        return false;
    }

    /**
     * @return the imageId of the card.
     */
    public int getImageId() {
        return imageId;
    }

    public int getImageId(String imageName) {
        return getResources().getIdentifier(imageName, "drawable", this.getContext().getPackageName());
    }

    /**
     * Updates the card value, and the card image if the card is face up.
     *
     * @param imageNum the number representing the card
     */
    public void updateImage(int imageNum) {
        cardNum = imageNum;
        if (isFaceUp) {
            imageId = getImageId(Standard.getCardImageFileName(imageNum));
        }
        this.setOnTouchListener(this);
        this.invalidate();
    }

    /**
     * changes the cards drawable resource to the back of a card
     * or vice versa if the card is already flipped
     */
    public void flipCard() {
        if (isFaceUp) {
            imageId = R.drawable.card_back;
            this.isFaceUp = false;
        } else {
            imageId = getImageId(Standard.getCardImageFileName(cardNum));
            this.isFaceUp = true;
        }
        this.invalidate();
    }

    /**
     * @return the card value number
     */
    public int getCardNum() {
        return cardNum;
    }

    /**
     * Sets the cardnum value
     *
     * @param cardNum the card of which to set the cardnum value to
     */
    public void setCardNum(int cardNum) {
        this.cardNum = cardNum;
    }

    /**
     * @return true if the card is face up, false if not.
     */
    public boolean isFaceUp() {
        return isFaceUp;
    }

    /**
     * Sets the orientation of the card.
     *
     * @param b The orientation of which to set the card. True if face up, false if face down.
     */
    public void setFaceUp(boolean b) {
        isFaceUp = b;
        if (!isFaceUp) {
            imageId = R.drawable.card_back;
        } else {
            imageId = getImageId(Standard.getCardImageFileName(cardNum));
        }
        invalidate();
    }
}