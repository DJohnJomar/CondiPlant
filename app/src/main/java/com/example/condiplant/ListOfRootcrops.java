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

public class ListOfRootcrops extends AppCompatActivity {

    private ImageButton btnBack, btnCassava, btnTaro, btnSweetPotato, btnPurpleYam;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_list_of_rootcrops);
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

        btnCassava = findViewById(R.id.btnCassava);
        btnCassava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOfRootcrops.this, CassavaDiseases.class);
                startActivity(intent);
            }
        });

        btnTaro = findViewById(R.id.btnTaro);
        btnTaro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOfRootcrops.this, TaroDiseases.class);
                startActivity(intent);
            }
        });

        btnSweetPotato = findViewById(R.id.btnSweetPotato);
        btnSweetPotato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOfRootcrops.this, SweetPotatoDiseases.class);
                startActivity(intent);
            }
        });

        btnPurpleYam = findViewById(R.id.btnPurpleYam);
        btnPurpleYam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOfRootcrops.this, PurpleYamDiseases.class);
                startActivity(intent);
            }
        });
    }
}