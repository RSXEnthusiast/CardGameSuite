package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.cardgamesuiteapp.decks.Standard;
import com.example.cardgamesuiteapp.display.Hand;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int playerNum = 2;
        switch (playerNum) {
            case 2:
                setContentView(R.layout.two_players);
                setUp2Players();
                break;
            case 3:
                setContentView(R.layout.three_players);
                break;
            case 4:
                setContentView(R.layout.four_players);
                break;
            case 5:
                setContentView(R.layout.five_players);
                break;
            case 6:
                setContentView(R.layout.six_players);
                break;
        }
        ArrayList<Integer> first = new ArrayList<>();
        ArrayList<Integer> second = new ArrayList<>();
        ArrayList<Integer> third = new ArrayList<>();
        ArrayList<Integer> fourth = new ArrayList<>();
        ArrayList<Integer> fifth = new ArrayList<>();
        ArrayList<Integer> sixth = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            first.add(i);
        }
        for (int i = 4; i < 8; i++) {
            second.add(i);
        }
        for (int i = 8; i < 12; i++) {
            third.add(i);
        }
        for (int i = 12; i < 16; i++) {
            fourth.add(i);
        }
        for (int i = 16; i < 20; i++) {
            fifth.add(i);
        }
        for (int i = 20; i < 24; i++) {
            sixth.add(i);
        }
    }

    private void setUp2Players() {
        LinearLayout ll = findViewById(R.id.player1);
        LinearLayout columnsTop = new LinearLayout(this);
        columnsTop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        columnsTop.setOrientation(LinearLayout.HORIZONTAL);
        columnsTop.setWeightSum(1);
        LinearLayout columnsBottom = new LinearLayout(this);
        columnsBottom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        columnsBottom.setOrientation(LinearLayout.HORIZONTAL);
        columnsBottom.setWeightSum(1);
        ll.addView(columnsTop);
        ll.addView(columnsBottom);
        ImageView[] cards = new ImageView[4];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new ImageView(this);
            cards[i].setImageResource(getResources().getIdentifier(Standard.getCardImageFileName(i), "drawable", this.getPackageName()));
            cards[i].setLayoutParams(layoutParams);
        }
        columnsTop.addView(cards[0]);
        columnsTop.addView(cards[1]);
        columnsBottom.addView(cards[2]);
        columnsBottom.addView(cards[3]);
    }
}
