package com.example.condiplant;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CassavaDiseases extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnBrownSpot;
    private ImageButton btnMosaicVirus;
    private ImageButton btnBacterialBlight;
    private ImageButton btnBrownStreak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cassava_diseases);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });


        int indexBrownSpot = 1;
        int imgBrownSpot = R.drawable.cassava_brown_leafspot;
        btnBrownSpot = findViewById(R.id.btnBrownSpot);
        btnBrownSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to pass on data
                Intent intent = new Intent(CassavaDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexBrownSpot); //Pass disease Index
                intent.putExtra("drawableId", imgBrownSpot); // Pass the image drawable id
                startActivity(intent);
            }
        });


        int indexMosaicVirus = 5;
        int imgMosaicVirus = R.drawable.cassava_mosaic;
        btnMosaicVirus = findViewById(R.id.btnMosaicVirus);
        btnMosaicVirus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to pass on data
                Intent intent = new Intent(CassavaDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexMosaicVirus); //Pass disease Index
                intent.putExtra("drawableId", imgMosaicVirus); // Pass the image drawable id
                startActivity(intent);            }
        });

        int indexBacterialBlight = 0;
        int imgBacterialBlight = R.drawable.cassava_blight;
        btnBacterialBlight = findViewById(R.id.btnBacterialBlight);
        btnBacterialBlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to pass on data
                Intent intent = new Intent(CassavaDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexBacterialBlight); //Pass disease Index
                intent.putExtra("drawableId", imgBacterialBlight);// Pass the image drawable id
                startActivity(intent);            }
        });

        int indexBrownStreak = 2;
        int imgBrownStreak = R.drawable.brown_streak;
        btnBrownStreak = findViewById(R.id.btnBrownStreak);
        btnBrownStreak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent to pass on data
                Intent intent = new Intent(CassavaDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexBrownStreak);//Pass disease Index
                intent.putExtra("drawableId", imgBrownStreak);// Pass the image drawable id
                startActivity(intent);            }
        });
    }
}