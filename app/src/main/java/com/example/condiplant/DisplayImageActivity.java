package com.example.condiplant;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.condiplant.ml.Model;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;

public class DisplayImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button btnBack, btnRemedy;
    private TextView txtPrediction;
    private String imagePath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        txtPrediction = findViewById(R.id.txtPrediction);
        imageView = findViewById(R.id.displayImageView);


        // Receive the image file path from the intent
        imagePath = getIntent().getStringExtra("image");

        // Load the image from the temporary file
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap);
                predict(bitmap);
            } else {
                // Handle the case where the bitmap cannot be decoded
                Log.e("DisplayImageActivity", "Failed to decode bitmap from file.");
            }
        } else {
            // Handle the case where no image file path is received
            Log.e("DisplayImageActivity", "No image file path received.");
        }
//        //Receive the image bitmap from intent.
//        Bitmap image = getIntent().getParcelableExtra("image");
//
//        //Display the image in ImageView
//        imageView.setImageBitmap(image);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });
    }

    public void predict(Bitmap bitmap){
        try {
            Model model = Model.newInstance(DisplayImageActivity.this);

            // Resize and normalize bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(bitmap);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            inputFeature0.loadBuffer(tensorImage.getBuffer());

            Log.d("Shape of input buffer", tensorImage.getBuffer() + "");

            // Runs model inference and gets result.
            Model.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Get max confidence index
            int predictedClass = getMax(outputFeature0.getFloatArray());
            txtPrediction.setText(predictedClass + "");

            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }

    }

    public int getMax(float[] array){
        int max = 0;
        for (int i = 0; i<array.length; i++){
            if(array[i] > array[max])
                max = i;
        }

        return max;
    }

    @Override
    //Destroys the temporary image when displayActivity is destroyed
    protected void onDestroy() {
        super.onDestroy();
        if (imagePath != null) {
            File tempFile = new File(imagePath);
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}