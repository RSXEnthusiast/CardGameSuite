package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.cardgamesuiteapp.display.Card;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setUpFives(int playerNum, ArrayList<Integer>[] hands) {
        switch (playerNum) {
            case 2:
                setContentView(R.layout.two_players);
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
        if (playerNum >= 2) {
            setUp2Players(hands);
        }
        if (playerNum >= 3) {
            setUp3Players(hands);
        }
    }

    private void setUp3Players(ArrayList<Integer>[] hands) {
    }

    private void setUp2Players(ArrayList<Integer>[] hands) {
        LinearLayout player1 = findViewById(R.id.player1);
        LinearLayout columnsTop = new LinearLayout(this);
        columnsTop.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        columnsTop.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.weight = 1;
        columnsTop.setLayoutParams(layoutParams);
        LinearLayout columnsBottom = new LinearLayout(this);
        columnsBottom.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        columnsBottom.setOrientation(LinearLayout.HORIZONTAL);
        columnsBottom.setLayoutParams(layoutParams);
        player1.addView(columnsTop);
        player1.addView(columnsBottom);
        ArrayList<Card> cards = new ArrayList<Card>();
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        for (int i = 0; i < hands[0].size(); i++) {
            cards.add(new Card(this));
            cards.get(i).setLayoutParams(layoutParams);
            cards.get(i).updateImage(i);
        }
        columnsTop.addView(cards.get(0));
        columnsTop.addView(cards.get(1));
        columnsBottom.addView(cards.get(2));
        columnsBottom.addView(cards.get(3));
        for (int i = 0; i < cards.size(); i++) {
            cards.get(i).updateImage(i + 4);
        }

    }
}
