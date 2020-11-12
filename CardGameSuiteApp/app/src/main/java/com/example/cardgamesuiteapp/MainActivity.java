package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.cardgamesuiteapp.display.Hands;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Hands hands = new Hands(this);
        ArrayList<Integer>[] test = new ArrayList[2];
        for (int i = 0; i < test.length; i++) {
            test[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < 8; i++) {
            test[i / 4].add(i % 4);
        }
        hands.initHands(test);
        setContentView(R.layout.activity_main);
//        grid.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(MainActivity.this, ("You tapped on card " + cards.get(position)), Toast.LENGTH_SHORT));
    }
}