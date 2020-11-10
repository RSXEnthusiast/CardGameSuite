package com.example.cardgamesuiteapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.cardgamesuiteapp.display.Hand;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    LinkedList<Integer> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GridView grid = (GridView) findViewById(R.id.gridview);
        grid.setAdapter(new Hand(this));
        grid.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(MainActivity.this, ("You tapped on card " + cards.get(position)), Toast.LENGTH_SHORT));
    }
}