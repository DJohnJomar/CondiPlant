package com.example.condiplant;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

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

public class DisplayImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private ImageButton btnBack, btnRemedy;
    private TextView txtRootCrop, txtDisease, txtDiseaseDesc, txtRemedy, txtRemedyDesc;
    private String imagePath;
    private ArrayList<String> labelRootCrops, labelDiseases, labelDescription;
    private ArrayList<String> remedy1, remedy2, remedy4, remedy5, remedy6, remedy7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);
        labelRootCrops = new ArrayList<>();
        setUpLabels(labelRootCrops, "labelRootCrops.txt");
        labelDiseases = new ArrayList<>();
        setUpLabels(labelDiseases, "labelDiseases.txt");
        labelDescription = new ArrayList<>();
        setUpLabels(labelDescription, "labelDescription.txt");
        remedy1 = new ArrayList<>();
        setUpLabels(remedy1, "CLBRemedy.txt");
        remedy2 = new ArrayList<>();
        setUpLabels(remedy2, "CBSRemedy.txt");
        remedy4 = new ArrayList<>();
        setUpLabels(remedy4, "CMRemedy.txt");
        remedy5 = new ArrayList<>();
        setUpLabels(remedy5, "SPLSRemedy.txt");
        remedy6 = new ArrayList<>();
        setUpLabels(remedy6, "TLBRemedy.txt");
        remedy7 = new ArrayList<>();
        setUpLabels(remedy7, "TMRemedy.txt");

        txtRootCrop = findViewById(R.id.txtRootCrop);
        txtDisease = findViewById(R.id.txtDisease);
        txtDiseaseDesc = findViewById(R.id.txtDiseaseDesc);
        txtRemedy = findViewById(R.id.txtRemedy);
        txtRemedyDesc = findViewById(R.id.txtRemedyDesc);
        imageView = findViewById(R.id.displayImageView);

        /*
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
         */

        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });
    }

    /*
    public void predict(Bitmap bitmap){
        try {
            System.out.println("Predicting");
            EfficientNetB0 model = EfficientNetB0.newInstance(DisplayImageActivity.this);

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
            int predictedClass = getMax(confidence);
            float threshold = 0.70f;
            if (confidence[predictedClass] < threshold){
                predictedClass = 7;
            }

            //Changes text views from results

            txtRootCrop.setText(labelRootCrops.get(predictedClass));
            txtDisease.setText(labelDiseases.get(predictedClass));
            txtDiseaseDesc.setText("    " + labelDescription.get(predictedClass));

            if (predictedClass == 7 || predictedClass == 3){
                txtRemedy.setVisibility(View.INVISIBLE);
                txtRemedyDesc.setVisibility(View.INVISIBLE);
                txtRemedyDesc.setText("");
            } else {
                txtRemedy.setVisibility(View.VISIBLE);
                txtRemedyDesc.setVisibility(View.VISIBLE);
                txtRemedyDesc.setText(getRemedyDescription(predictedClass));
            }


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    */

    public int getMax(float[] array){
        int max = 0;
        for (int i = 0; i<array.length; i++){
            if(array[i] > array[max])
                max = i;
        }

        return max;
    }

    //Fills up the arraylist with the text contained in the selected text file named "fileName"
    public void setUpLabels(ArrayList<String> labels, String fileName){
        //For the classification labels
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader (getAssets().open(fileName)));
            String line = reader.readLine();
            while(line != null){
                labels.add(line);
                line = reader.readLine(); // Read the next line
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String getRemedyDescription(int index){
        StringBuilder remedyDescription = new StringBuilder();
        switch (index){
            case 0:
                for (String remedy : remedy1){
                    remedyDescription.append("• ").append(remedy).append("\n");
                }
                break;
            case 1:
                for (String remedy : remedy2){
                    remedyDescription.append("• ").append(remedy).append("\n");
                }
                break;
            case 3:
                for (String remedy : remedy4){
                    remedyDescription.append("• ").append(remedy).append("\n");
                }
                break;
            case 4:
                for (String remedy : remedy5){
                    remedyDescription.append("• ").append(remedy).append("\n");
                }
                break;
            case 5:
                for (String remedy : remedy6){
                    remedyDescription.append("• ").append(remedy).append("\n");
                }
                break;
            case 6:
                for (String remedy : remedy7){
                    remedyDescription.append("• ").append(remedy).append("\n");
                }
                break;
        }
        return remedyDescription.toString();
    }


}