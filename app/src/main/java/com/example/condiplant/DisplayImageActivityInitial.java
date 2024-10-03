package com.example.condiplant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.condiplant.ml.EfficientNetB0;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
    private ImageButton btnBack;
    private ArrayList<String> labelRootCrops;
    private String imagePath;
    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image_initial);

        labelRootCrops = new ArrayList<>();
        setUpLabels(labelRootCrops, "labelRootCrops.txt");

        txtPrediction1 = findViewById(R.id.txtPrediction1);
        txtPrediction2 = findViewById(R.id.txtPrediction2);
        txtPrediction3 = findViewById(R.id.txtPrediction3);
        txtAccuracy1 = findViewById(R.id.txtAccuracy1);
        txtAccuracy2 = findViewById(R.id.txtAccuracy2);
        txtAccuracy3 = findViewById(R.id.txtAccuracy3);
        btnPrediction1More = findViewById(R.id.btnPrediction1More);
        btnPrediction2More = findViewById(R.id.btnPrediction2More);
        btnPrediction3More = findViewById(R.id.btnPrediction3More);
        imageView = findViewById(R.id.displayImageView);

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

        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });

    }

    public void predict(Bitmap bitmap){
        try {
            System.out.println("Predicting");
            EfficientNetB0 model = EfficientNetB0.newInstance(DisplayImageActivityInitial.this);

            // Resize and normalize bitmap
            bitmap = Bitmap.createScaledBitmap(bitmap, 224, 224, true);

            TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
            tensorImage.load(bitmap);

            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3);
            byteBuffer.order(ByteOrder.nativeOrder());
            int[] intValues = new int[224 * 224];
            bitmap.getPixels(intValues, 0 ,bitmap.getWidth(),0,0,bitmap.getWidth(), bitmap.getHeight());
            int pixel = 0;

            for (int i = 0; i < 224; i++){
                for (int j = 0; j < 224; j++){
                    int val = intValues[pixel++];
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f/255));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f/255));
                    byteBuffer.putFloat((val & 0xFF) * (1.f/255));
                }
            }
            inputFeature0.loadBuffer(byteBuffer);

            Log.d("Shape of input buffer", tensorImage.getBuffer() + "");

            // Runs model inference and gets result.
            EfficientNetB0.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            // Get max confidence index
            float[] confidence = outputFeature0.getFloatArray();
            Map<Integer,Float> topThreeConfidence = getTopThreeConfidence(confidence);

            // Get the top 3 indices and confidence values
            int[] topIndices = new int[3];
            float[] topConfidences = new float[3];

            int count = 0;
            for (Map.Entry<Integer, Float> entry : topThreeConfidence.entrySet()) {
                topIndices[count] = entry.getKey(); // Get the index of the predicted class
                topConfidences[count] = entry.getValue(); // Get the confidence value
                count++;
            }

            // Debugging: Log the top indices and their corresponding confidence values
            Log.d("Top Predictions", "Index 1: " + topIndices[0] + ", Confidence: " + topConfidences[0]);
            Log.d("Top Predictions", "Index 2: " + topIndices[1] + ", Confidence: " + topConfidences[1]);
            Log.d("Top Predictions", "Index 3: " + topIndices[2] + ", Confidence: " + topConfidences[2]);

            // Update the text views for the predictions and accuracy
            updatePredictionUI(txtPrediction1, txtAccuracy1, btnPrediction1More, topIndices[0], topConfidences[0]);
            updatePredictionUI(txtPrediction2, txtAccuracy2, btnPrediction2More, topIndices[1], topConfidences[1]);
            updatePredictionUI(txtPrediction3, txtAccuracy3, btnPrediction3More, topIndices[2], topConfidences[2]);


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Function to get the top 3 confidence values and their indices (class predictions)
    public static Map<Integer, Float> getTopThreeConfidence(float[] confidence) {
        Map<Integer, Float> topThreeConfidence = new HashMap<>();

        float first = Float.NEGATIVE_INFINITY, second = Float.NEGATIVE_INFINITY, third = Float.NEGATIVE_INFINITY;
        int firstIndex = -1, secondIndex = -1, thirdIndex = -1;

        // Iterate through the array to find the top three confidence values and their indices
        for (int i = 0; i < confidence.length; i++) {
            float num = confidence[i];
            if (num > first) {
                // Update the indices and values accordingly
                third = second;
                thirdIndex = secondIndex;
                second = first;
                secondIndex = firstIndex;
                first = num;
                firstIndex = i;
            } else if (num > second) {
                third = second;
                thirdIndex = secondIndex;
                second = num;
                secondIndex = i;
            } else if (num > third) {
                third = num;
                thirdIndex = i;
            }
        }

        // Store the top three confidence values with their respective indices in the map
        topThreeConfidence.put(firstIndex, first);
        topThreeConfidence.put(secondIndex, second);
        topThreeConfidence.put(thirdIndex, third);

        // Create a list from the map entries and sort it by confidence values in descending order
        List<Map.Entry<Integer, Float>> sortedEntries = new ArrayList<>(topThreeConfidence.entrySet());
        sortedEntries.sort((entry1, entry2) -> Float.compare(entry2.getValue(), entry1.getValue()));

        // Create a new LinkedHashMap to maintain the order of entries
        Map<Integer, Float> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer, Float> entry : sortedEntries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
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

    //Fills up the arraylist with the text contained in the selected text file named "fileName"
    public void setUpLabels(ArrayList<String> labels, String fileName){
        //For the classification labels
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(fileName)));
            String line = reader.readLine();
            while(line != null){
                labels.add(line);
                line = reader.readLine(); // Read the next line
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // Helper method to update UI elements based on confidence level
    private void updatePredictionUI(TextView predictionText, TextView accuracyText, Button moreButton, int index, float confidence) {
        if (confidence >= 0.01) { // Set threshold for visibility to 1%
            predictionText.setText(labelRootCrops.get(index));
            accuracyText.setText(String.format("%.2f%%", confidence * 100));
            predictionText.setVisibility(View.VISIBLE);
            accuracyText.setVisibility(View.VISIBLE);
            moreButton.setVisibility(View.VISIBLE);
        } else {
            predictionText.setVisibility(View.GONE);
            accuracyText.setVisibility(View.GONE);
            moreButton.setVisibility(View.GONE);
        }
    }
}