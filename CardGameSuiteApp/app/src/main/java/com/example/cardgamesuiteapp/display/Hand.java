package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cardgamesuiteapp.decks.Standard;

import java.util.ArrayList;
import java.util.LinkedList;

public class Hand extends BaseAdapter {

    private Context context;
    private ArrayList<String> cards = new ArrayList<String>();

    public Hand(Context context, ArrayList<Integer> hand) {
        this.context = context;
        for (int card : hand) {
            cards.add(Standard.getCardImageFileName(card));
        }
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
            int width = 35 * 5;
            int height = 35 * 7;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            grid.setLayoutParams(layoutParams);
            grid.setScaleType(ImageView.ScaleType.CENTER_CROP);
            grid.setPadding(1, 2, 1, 2);
        } else {
            grid = (ImageView) convertView;
        }
        grid.setImageResource(context.getResources().getIdentifier(cards.get(position), "drawable", context.getPackageName()));
        return grid;
    }
}