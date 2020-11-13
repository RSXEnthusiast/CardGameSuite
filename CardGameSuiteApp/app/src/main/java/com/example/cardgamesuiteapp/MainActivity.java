package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.example.cardgamesuiteapp.display.Card;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.two_players);
//        int playerNum = 2;
//        switch (playerNum) {
//            case 2:
//                break;
//            case 3:
//                setContentView(R.layout.three_players);
//                break;
//            case 4:
//                setContentView(R.layout.four_players);
//                break;
//            case 5:
//                setContentView(R.layout.five_players);
//                break;
//            case 6:
//                setContentView(R.layout.six_players);
//                break;
//        }
    }

    private void setUp2Players() {
        LinearLayout ll = findViewById(R.id.player1);
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
        ll.addView(columnsTop);
        ll.addView(columnsBottom);
        Card[] cards = new Card[4];
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new Card(this);
            cards[i].updateImage(i);
            cards[i].setLayoutParams(layoutParams);
        }
        columnsTop.addView(cards[0]);
        columnsTop.addView(cards[1]);
        columnsBottom.addView(cards[2]);
        columnsBottom.addView(cards[3]);
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < cards.length; i++) {
            cards[i].updateImage(i + 4);
        }
    }
}
