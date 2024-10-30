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

public class TaroDiseases extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnLeafBlight;
    private ImageButton btnDasheenMosaic;
    private ImageButton btnTaroLeafSpot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_taro_diseases);
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
        int indexLeafBlight = 15;
        int imgLeafBlight = R.drawable.taro_blight;
        btnLeafBlight = findViewById(R.id.btnLeafBlight);
        btnLeafBlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(TaroDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexLeafBlight); //Pass disease Index
                intent.putExtra("drawableId", imgLeafBlight); // Pass the image drawable id
                startActivity(intent);
            }
        });

        //Indexes to be passed to DiseaseMoreInfo Class
        int indexDasheenMosaic = 19;
        int imgDasheeMosaic = R.drawable.taro_mosaic;
        btnDasheenMosaic = findViewById(R.id.btnDasheenMosaic);
        btnDasheenMosaic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(TaroDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexDasheenMosaic); //Pass disease Index
                intent.putExtra("drawableId", imgDasheeMosaic); // Pass the image drawable id
                startActivity(intent);
            }
        });

        //Indexes to be passed to DiseaseMoreInfo Class
        int indexTaroLeafSpot = 18;
        int imgTaroLeafSpot = R.drawable.taro_leaf_spot;
        btnTaroLeafSpot = findViewById(R.id.btnTaroLeafSpot);
        btnTaroLeafSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(TaroDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexTaroLeafSpot); //Pass disease Index
                intent.putExtra("drawableId", imgTaroLeafSpot); // Pass the image drawable id
                startActivity(intent);
            }
        });


    }
}