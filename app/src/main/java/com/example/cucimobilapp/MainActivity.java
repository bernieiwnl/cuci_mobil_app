package com.example.cucimobilapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private TextView textViewPlatNomor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewPlatNomor = (TextView) findViewById(R.id.textPlatNomor);
        textViewPlatNomor.setText(getIntent().getStringExtra("data_plat_nomor"));

    }
}