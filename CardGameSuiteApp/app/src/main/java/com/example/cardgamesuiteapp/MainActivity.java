package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridView;

import com.example.cardgamesuiteapp.display.Hand;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int playerNum = 3;
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
        if (playerNum >= 2) {
            GridView view1 = (GridView) findViewById(R.id.player1);
            view1.setAdapter(new Hand(this, first));
            GridView view2 = (GridView) findViewById(R.id.player2);
            view2.setAdapter(new Hand(this, second));
        }
        if (playerNum >= 3) {
            GridView view3 = (GridView) findViewById(R.id.player3);
            view3.setAdapter(new Hand(this, third));
        }
        if (playerNum >= 4) {
            GridView view4 = (GridView) findViewById(R.id.player4);
            view4.setAdapter(new Hand(this, fourth));
        }
        if (playerNum >= 5) {
            GridView view5 = (GridView) findViewById(R.id.player5);
            view5.setAdapter(new Hand(this, fifth));
        }
        if (playerNum >= 6) {
            GridView view6 = (GridView) findViewById(R.id.player6);
            view6.setAdapter(new Hand(this, sixth));
        }
    }

//        grid.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(MainActivity.this, ("You tapped on card " + cards.get(position)), Toast.LENGTH_SHORT));
}
