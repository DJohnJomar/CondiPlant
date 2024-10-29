package com.example.condiplant;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
private ImageView splashImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setSupportActionBar(null);

        splashImageView = findViewById(R.id.splashImageView);
        splashImageView.setImageResource(R.drawable.splash_image);


        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, IntroSlider.class);
            startActivity(intent);
            finish();
        }, 3000);
    }
}