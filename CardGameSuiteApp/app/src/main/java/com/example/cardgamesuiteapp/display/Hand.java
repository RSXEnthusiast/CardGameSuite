package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.view.ViewGroup;

import java.util.LinkedList;

public class Hand extends ViewGroup {
    LinkedList<Card> cards;

    public Hand(Context context ){
        super(context);

    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {

    }
}
