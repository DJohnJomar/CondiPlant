package com.example.condiplant;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.condiplant.ml.Efficientnetb0MainModel;
import com.example.condiplant.ml.Efficientnetb0SubModel;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DisplayImageActivityInitial extends AppCompatActivity {

    private ConstraintLayout layout1;
    private ConstraintLayout layout2;
    private ConstraintLayout layout3;
    private ConstraintLayout layoutUnknown;
    private TextView txtPlant1;
    private TextView txtPrediction1;
    private TextView txtAccuracy1;
    private Button btnPrediction1More;
    private TextView txtPlant2;
    private TextView txtPrediction2;
    private TextView txtAccuracy2;
    private Button btnPrediction2More;
    private TextView txtPlant3;
    private TextView txtPrediction3;
    private TextView txtAccuracy3;
    private Button btnPrediction3More;
    private TextView txtUnknown;
    private TextView txtUnknownDetails;
    private TextView txtAccuracy4;
    private ImageButton btnBack;
    private ArrayList<String> labelDiseases;
    private ArrayList<String> labelPlants;
    private String imagePath;
    private ImageView imageView;
    private int[] topIndices;
    private float[] topConfidences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image_initial);

        labelDiseases = new ArrayList<>();
        setUpLabels(labelDiseases, "labelDiseases.txt");
        labelPlants = new ArrayList<>();
        setUpLabels(labelPlants, "labelRootCrops2.txt");

        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layoutUnknown = findViewById(R.id.layoutUnknown);
        txtPlant1 = findViewById(R.id.txtPlant1);
        txtPlant2 = findViewById(R.id.txtPlant2);
        txtPlant3 = findViewById(R.id.txtPlant3);
        txtUnknown = findViewById(R.id.txtUnknown);
        txtPrediction1 = findViewById(R.id.txtPrediction1);
        txtPrediction2 = findViewById(R.id.txtPrediction2);
        txtPrediction3 = findViewById(R.id.txtPrediction3);
        txtUnknownDetails = findViewById(R.id.txtUnknownDetails);
        txtAccuracy1 = findViewById(R.id.txtAccuracy1);
        txtAccuracy2 = findViewById(R.id.txtAccuracy2);
        txtAccuracy3 = findViewById(R.id.txtAccuracy3);
        txtAccuracy4 = findViewById(R.id.txtAccuracy4);
        btnPrediction1More = findViewById(R.id.btnPrediction1More);
        btnPrediction2More = findViewById(R.id.btnPrediction2More);
        btnPrediction3More = findViewById(R.id.btnPrediction3More);
        imageView = findViewById(R.id.displayImageView);
        topIndices = new int[3];
        topConfidences = new float[3];

        btnPrediction1More.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent to pass on data
                Intent intent = new Intent(DisplayImageActivityInitial.this, DisplayImageActivity.class);
                intent.putExtra("index", topIndices[0]); // Pass the top confidence - index 0
                intent.putExtra("imagePath", imagePath); // Pass the image path
                startActivity(intent);
            }
        });
        btnPrediction2More.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent to pass on data
                Intent intent = new Intent(DisplayImageActivityInitial.this, DisplayImageActivity.class);
                intent.putExtra("index", topIndices[1]); // Pass the 2nd to the top confidence - index 1
                intent.putExtra("imagePath", imagePath); // Pass the image path
                startActivity(intent);
            }
        });
        btnPrediction3More.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //Intent to pass on data
                Intent intent = new Intent(DisplayImageActivityInitial.this, DisplayImageActivity.class);
                intent.putExtra("index", topIndices[2]); // Pass the 3rd to the top confidence - index 2
                intent.putExtra("imagePath", imagePath); // Pass the image path
                startActivity(intent);
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
            Efficientnetb0MainModel mainModel = Efficientnetb0MainModel.newInstance(DisplayImageActivityInitial.this);
            Efficientnetb0SubModel subModel = Efficientnetb0SubModel.newInstance(DisplayImageActivityInitial.this);

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

            // Runs subModel inference and gets result.
            Efficientnetb0SubModel.Outputs outputs = subModel.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();
            // Get top three confidence indices and values
            float[] confidence = outputFeature0.getFloatArray();

            Map<Integer, Float> topThreeConfidence = getTopThreeConfidence(confidence);

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

            if (topIndices[0] == 20) {
                txtAccuracy4.setText(String.format("%.2f%%", 100.00));
                layoutUnknown.setVisibility(View.VISIBLE);
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.GONE);
                layout3.setVisibility(View.GONE);
            } else {
                if (checkForTopDisease(topIndices)) {
                    Efficientnetb0MainModel.Outputs result = mainModel.process(inputFeature0);
                    TensorBuffer outputFeature = result.getOutputFeature0AsTensorBuffer();
                    float[] subConfidence = outputFeature.getFloatArray();

                    Map<Integer, Float> topThreeSubConfidence = getTopThreeConfidence(subConfidence);

                    count = 0;
                    for (Map.Entry<Integer, Float> entry : topThreeSubConfidence.entrySet()) {
                        topIndices[count] = entry.getKey(); // Get the index of the predicted class
                        topConfidences[count] = entry.getValue(); // Get the confidence value
                        count++;
                    }

                    topIndices = convertIndices(topIndices);

                    // Debugging: Log the top indices and their corresponding confidence values
                    Log.d("Top Predictions", "Index 1: " + topIndices[0] + ", Confidence: " + topConfidences[0]);
                    Log.d("Top Predictions", "Index 2: " + topIndices[1] + ", Confidence: " + topConfidences[1]);
                    Log.d("Top Predictions", "Index 3: " + topIndices[2] + ", Confidence: " + topConfidences[2]);

                    // Add prediction to database for report
                    ReportItem reportItem;
                    try{
                        reportItem = new ReportItem(labelPlants.get(topIndices[0]), labelDiseases.get(topIndices[0]), getTodaysDate());
                        //Toast.makeText(DisplayImageActivityInitial.this, reportItem.toString(), Toast.LENGTH_SHORT).show();
                    }catch (Exception e){
                        //Toast.makeText(DisplayImageActivityInitial.this, "Error creating report", Toast.LENGTH_SHORT).show();
                        reportItem = new ReportItem("error", "Error", getTodaysDate());
                    }

                    DatabaseHelper databaseHelper = new DatabaseHelper(DisplayImageActivityInitial.this);
                    boolean success = databaseHelper.addOne(reportItem);

                    //Toast.makeText(DisplayImageActivityInitial.this, "Success = "+success, Toast.LENGTH_SHORT).show();

                }

                // Update the text views for the predictions and accuracy
                layoutUnknown.setVisibility(View.GONE);
                updatePredictionUI(layout1, txtPlant1, txtPrediction1, txtAccuracy1, btnPrediction1More, topIndices[0], topConfidences[0]);
                if (topIndices[1] != 20) {
                    updatePredictionUI(layout2, txtPlant2, txtPrediction2, txtAccuracy2, btnPrediction2More, topIndices[1], topConfidences[1]);
                } else {
                    updatePredictionUI(layout2, txtPlant2, txtPrediction2, txtAccuracy2, btnPrediction2More, topIndices[1], 0);
                }
                if (topIndices[2] != 20) {
                    updatePredictionUI(layout3, txtPlant3, txtPrediction3, txtAccuracy3, btnPrediction3More, topIndices[2], topConfidences[2]);
                } else {
                    updatePredictionUI(layout3, txtPlant3, txtPrediction3, txtAccuracy3, btnPrediction3More, topIndices[2], 0);
                }
            }



            // Releases subModel resources if no longer used.
            subModel.close();
            mainModel.close();
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
    //Destroys the temporary image when displayActivityInitial is destroyed
    protected void onDestroy() {
        super.onDestroy();
        if (imagePath != null) {
            File tempFile = new File(imagePath);
            if (tempFile.exists()) {
                tempFile.deleteOnExit();
            }
        }
        finish();
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
    //Removes unnecessary UI elements when confidence is 0%
    private void updatePredictionUI(ConstraintLayout layout, TextView plantText, TextView predictionText, TextView accuracyText, Button moreButton, int index, float confidence) {
        if (confidence >= 0.01) { // Set threshold for visibility to 1%
            plantText.setText(labelPlants.get(index));
            predictionText.setText(labelDiseases.get(index));
            accuracyText.setText(String.format("%.2f%%", confidence * 100));
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
    }

    private boolean checkForTopDisease(int[] topIndices) {
        int[] diseaseIndices = {0, 1, 2, 5, 7, 11, 13, 14, 15, 18, 19};
        for (int index : diseaseIndices){
            if (topIndices[0] == index){
                return true;
            }
        }
        return false;
    }

    private int[] convertIndices(int[] topIndices) {
        int[] diseaseIndices = {0, 1, 2, 5, 7, 11, 13, 14, 15, 18, 19};
        return new int[]{diseaseIndices[topIndices[0]], diseaseIndices[topIndices[1]], diseaseIndices[topIndices[2]]};
    }

    private String getTodaysDate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        month = month + 1; // Month is set to 0 (January is 0). This is to set it to 1 as january is in the calendar
        return makeDateString(year, month, day);
    }

    //yyyy-MM-DD format, standardized for the db (ISO 8601 standard)
    private String makeDateString(int year, int month, int day){
        return year+"-"+month+"-"+day;
    }

    private String getMonthFormat (int month){
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";
        //Default should never happen
        return "JAN";
    }
}
