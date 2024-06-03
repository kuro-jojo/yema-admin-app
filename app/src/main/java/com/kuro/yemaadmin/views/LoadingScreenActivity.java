package com.kuro.yemaadmin.views;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.kuro.yemaadmin.R;


public class LoadingScreenActivity extends AppCompatActivity {
    private static final int DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            finish();
        }, DELAY);

    }
}