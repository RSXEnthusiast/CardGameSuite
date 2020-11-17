package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cardgamesuiteapp.decks.Standard;

import java.util.ArrayList;
import java.util.LinkedList;

public class Hand extends ViewGroup {
    public Card card1;
    public Card card2;
    public Card card3;
    public Card card4;

    public Hand(Context context) {
        super(context);
    }

    public Hand(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Hand(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public Hand(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init() {
        card1 = new Card(this.getContext());
        card2 = new Card(this.getContext());
        card3 = new Card(this.getContext());
        card4 = new Card(this.getContext());
        card1.updateImage(0);
        card2.updateImage(1);
        card3.updateImage(2);
        card4.updateImage(3);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }
}