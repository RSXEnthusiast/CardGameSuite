package com.example.cardgamesuiteapp.display;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.GridView;

import com.example.cardgamesuiteapp.R;

import java.util.ArrayList;

public class Hands extends View {
    private GridView[] hands;
    private ArrayList<Integer>[] handsRaw;

    public Hands(Context context) {
        super(context);
    }

    public Hands(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void initHands(ArrayList<Integer>[] hands) {
        this.handsRaw = hands;
    }

    private void init2Players(ArrayList<Integer>[] hands) {
        this.hands = new GridView[2];
        this.hands[0] = (GridView) findViewById(R.id.gridview);
        this.hands[0].setAdapter(new Hand(getContext(), hands[0], 0, 0));
        this.hands[1] = (GridView) findViewById(R.id.gridview);
        this.hands[1].setAdapter(new Hand(getContext(), hands[1], 50, 50));
    }

    private void init3Players(ArrayList<Integer>[] hands) {
    }

    private void init4Players(ArrayList<Integer>[] hands) {
    }

    private void init5Players(ArrayList<Integer>[] hands) {
    }

    private void init6Players(ArrayList<Integer>[] hands) {
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();

        switch (handsRaw.length) {
            case 2:
                init2Players(handsRaw);
                break;
            case 3:
                init3Players(handsRaw);
                break;
            case 4:
                init4Players(handsRaw);
                break;
            case 5:
                init5Players(handsRaw);
                break;
            case 6:
                init6Players(handsRaw);
                break;
        }
    }
}
