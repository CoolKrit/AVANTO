package com.example.avanto.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.avanto.R;

public class SplashScreenActivity extends AppCompatActivity {
    // Длительность отображения SplashScreen в миллисекундах
    private static final int SPLASH_DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Запуск основной активности после задержки
                startActivity(new Intent(SplashScreenActivity.this, SignActivity.class));
                finish();
            }
        }, SPLASH_DELAY);
    }
}