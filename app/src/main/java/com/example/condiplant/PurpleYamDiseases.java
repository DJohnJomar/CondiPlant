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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class PurpleYamDiseases extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageButton btnAnthracnose;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purple_yam_diseases);
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
        int indexAnthracnose = 7;
        int imgAntrhacnoseId = R.drawable.yam_anthracnose;
        btnAnthracnose = findViewById(R.id.btnAnthracnose);
        btnAnthracnose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to pass on data
                Intent intent = new Intent(PurpleYamDiseases.this, DiseaseMoreInfo.class);
                intent.putExtra("index", indexAnthracnose); //Pass disease Index
                intent.putExtra("drawableId", imgAntrhacnoseId); // Pass the image drawable id
                startActivity(intent);

            }
        });
    }
}
