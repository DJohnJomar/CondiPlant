package com.example.condiplant;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class DisplayImageActivityInitial extends AppCompatActivity {

    private TextView txtPrediction1;
    private TextView txtAccuracy1;
    private Button btnPrediction1More;
    private TextView txtPrediction2;
    private TextView txtAccuracy2;
    private Button btnPrediction2More;
    private TextView txtPrediction3;
    private TextView txtAccuracy3;
    private Button btnPrediction3More;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        txtPrediction1 = findViewById(R.id.txtPrediction1);
        txtPrediction2 = findViewById(R.id.txtPrediction2);
        txtPrediction3 = findViewById(R.id.txtPrediction3);
        txtAccuracy1 = findViewById(R.id.txtAccuracy1);
        txtAccuracy2 = findViewById(R.id.txtAccuracy2);
        txtAccuracy3 = findViewById(R.id.txtAccuracy3);
        btnPrediction1More = findViewById(R.id.btnPrediction1More);
        btnPrediction2More = findViewById(R.id.btnPrediction2More);
        btnPrediction3More = findViewById(R.id.btnPrediction3More);

        btnPrediction1More.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        btnPrediction2More.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
        btnPrediction3More.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });


    }
}
