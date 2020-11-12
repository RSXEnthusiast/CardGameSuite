package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cardgamesuiteapp.decks.Standard;

import java.util.ArrayList;

public class Hand extends BaseAdapter {
    private Context context;
    private ArrayList<String> cards = new ArrayList<String>();
    private int marginStart;
    private int marginEnd;

    public Hand(Context context, ArrayList<Integer> hand, int marginStart, int marginEnd) {
        this.context = context;
        for (int card : hand) {
            cards.add(Standard.getCardImageFileName(card));
        }
        this.marginStart = marginStart;
        this.marginEnd = marginEnd;
    }

    public void updateCards(ArrayList<Integer> hand) {
        cards.clear();
        for (int card : hand) {
            cards.add(Standard.getCardImageFileName(card));
        }
    }

    @Override
    public int getCount() {
        return cards.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView grid;
        if (convertView == null) {
            grid = new ImageView(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(250, 350);
            layoutParams.setMarginStart(marginStart);
            grid.setLayoutParams(layoutParams);
            grid.setScaleType(ImageView.ScaleType.CENTER);
            grid.setPadding(1, 2, 1, 2);
        } else {
            grid = (ImageView) convertView;
        }
        grid.setImageResource(context.getResources().getIdentifier(cards.get(position), "drawable", context.getPackageName()));
        return grid;
    }
}