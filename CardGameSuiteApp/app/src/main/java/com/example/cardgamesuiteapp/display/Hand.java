package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.cardgamesuiteapp.R;

import java.util.LinkedList;

public class Hand extends BaseAdapter {
    private Context context;
    private Integer[] cards = {R.drawable.ace_c, R.drawable.ace_d, R.drawable.ace_h, R.drawable.ace_s};

    public Hand(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return cards.length;
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
            grid.setLayoutParams(new ViewGroup.LayoutParams(250, 350));
            grid.setScaleType(ImageView.ScaleType.CENTER_CROP);
            grid.setPadding(1, 2, 1, 2);
        } else {
            grid = (ImageView) convertView;
        }
        grid.setImageResource(cards[position]);
        return grid;
    }
}