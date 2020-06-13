package com.example.srmobile;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class RectResolution extends AppCompatActivity {
    ImageGenerator generator = MainActivity.singletone.generator;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rect_resolution);
    }
}
