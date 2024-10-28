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

public class SweetPotatoDiseases extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnVirus;
    private ImageButton btnFusariumWilt;
    private ImageButton btnLeafSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sweet_potato_diseases);
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


        //Indexes to be passed to DiseaseMoreInfo Class
        int indexVirus = 14;
        int imgVirus = R.drawable.sp_virus_disease;
        btnVirus = findViewById(R.id.btnVirus);
        btnVirus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(SweetPotatoDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexVirus); //Pass disease Index
                intent.putExtra("drawableId", imgVirus); // Pass the image drawable id
                startActivity(intent);
            }
        });

        //Indexes to be passed to DiseaseMoreInfo Class
        int indexFusariumWilt = 11;
        int imgFusariumWilt = R.drawable.sp_fusarium;
        btnFusariumWilt = findViewById(R.id.btnFusariumWilt);
        btnFusariumWilt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(SweetPotatoDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexFusariumWilt); //Pass disease Index
                intent.putExtra("drawableId", imgFusariumWilt); // Pass the image drawable id
                startActivity(intent);
            }
        });

        //Indexes to be passed to DiseaseMoreInfo Class
        int indexLeafSpot = 13;
        int imgLeafSpot = R.drawable.sweetpotato_leafspot;
        btnLeafSpot = findViewById(R.id.btnLeafSpot);
        btnLeafSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(SweetPotatoDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexLeafSpot); //Pass disease Index
                intent.putExtra("drawableId", imgLeafSpot); // Pass the image drawable id
                startActivity(intent);
            }
        });
    }
}