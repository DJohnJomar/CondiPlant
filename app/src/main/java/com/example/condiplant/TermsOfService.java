package com.example.condiplant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TermsOfService extends AppCompatActivity {

    private Button btnAccept;
    private Button btnDecline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Check if this is the first launch
        SharedPreferences preferences = getSharedPreferences("TermsPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = preferences.getBoolean("isFirstLaunch", true);
        // If it's not the first launch, navigate to the IntroSlider activity
        if (!isFirstLaunch) {
            startActivity(new Intent(this, IntroSlider.class));
            finish(); // Close this activity
            return; // Exit onCreate
        }

        setContentView(R.layout.activity_terms_of_service);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnAccept = findViewById(R.id.btnAccept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save that the user has accepted the terms
                preferences.edit().putBoolean("isFirstLaunch", false).apply();
                // Navigate to the IntroSlider activity
                startActivity(new Intent(TermsOfService.this, IntroSlider.class));
                finish(); // Close this activity
            }
        });

        btnDecline = findViewById(R.id.btnDecline);
        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the app
                finish();
            }
        });
    }
}
