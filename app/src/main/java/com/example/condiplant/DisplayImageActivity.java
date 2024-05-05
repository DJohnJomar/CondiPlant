package com.example.condiplant;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class DisplayImageActivity extends AppCompatActivity {

    ImageView imageView;
    Button btnBack, btnRemedy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        imageView = findViewById(R.id.displayImageView);

        //Receive the image bitmap from intent.
        Bitmap image = getIntent().getParcelableExtra("image");

        //Display the image in ImageView
        imageView.setImageBitmap(image);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });
    }
}