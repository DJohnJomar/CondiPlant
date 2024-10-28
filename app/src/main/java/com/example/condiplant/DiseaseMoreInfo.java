package com.example.condiplant;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class DiseaseMoreInfo extends AppCompatActivity {
    private ImageView imageView;
    private ImageButton btnBack, btnRemedy;
    private TextView txtRootCrop, txtDisease, txtDiseaseDesc, txtCausesDesc, txtBefore, txtDuring, txtAfter;
    private ArrayList<String> labelRootCrops, labelDiseases, labelDescription, labelCauses;
    private ArrayList<String> CBB, CBSp, CBSt, CMV, PYA, SPFW, SPLS, SPVD, TLB, TLS, TMV;
    private ArrayList<ArrayList<String>> listManagements;
    private ConstraintLayout layoutDiseaseDetails;
    private int index; // To store the indices
    private int drawableIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_more_info);
        labelRootCrops = new ArrayList<>();
        setUpLabels(labelRootCrops, "labelRootCrops.txt");
        labelDiseases = new ArrayList<>();
        setUpLabels(labelDiseases, "labelDiseases.txt");
        labelDescription = new ArrayList<>();
        setUpLabels(labelDescription, "labelDescription.txt");
        labelCauses = new ArrayList<>();
        setUpLabels(labelCauses, "labelCauses.txt");
        CBB = new ArrayList<>();
        setUpLabels(CBB, "CBB_Management.txt");
        CBSp = new ArrayList<>();
        setUpLabels(CBSp, "CBSp_Management.txt");
        CBSt = new ArrayList<>();
        setUpLabels(CBSt, "CBSt_Management.txt");
        CMV = new ArrayList<>();
        setUpLabels(CMV, "CMV_Management.txt");
        PYA = new ArrayList<>();
        setUpLabels(PYA, "PYA_Management.txt");
        SPFW = new ArrayList<>();
        setUpLabels(SPFW, "SPFW_Management.txt");
        SPLS = new ArrayList<>();
        setUpLabels(SPLS, "SPLS_Management.txt");
        SPVD = new ArrayList<>();
        setUpLabels(SPVD, "SPVD_Management.txt");
        TLB = new ArrayList<>();
        setUpLabels(TLB, "TLB_Management.txt");
        TLS = new ArrayList<>();
        setUpLabels(TLS, "TLS_Management.txt");
        TMV = new ArrayList<>();
        setUpLabels(TMV, "TMV_Management.txt");

        listManagements = new ArrayList<>();
        listManagements.add(CBB);
        listManagements.add(CBSp);
        listManagements.add(CBSt);
        listManagements.add(CMV);
        listManagements.add(PYA);
        listManagements.add(SPFW);
        listManagements.add(SPLS);
        listManagements.add(SPVD);
        listManagements.add(TLB);
        listManagements.add(TLS);
        listManagements.add(TMV);

        txtRootCrop = findViewById(R.id.txtRootCrop);
        txtDisease = findViewById(R.id.txtDisease);
        txtDiseaseDesc = findViewById(R.id.txtDiseaseDesc);
        txtCausesDesc = findViewById(R.id.txtCausesDesc);
        txtBefore = findViewById(R.id.txtBefore);
        txtDuring = findViewById(R.id.txtDuring);
        txtAfter = findViewById(R.id.txtAfter);
        imageView = findViewById(R.id.displayImageView);
        layoutDiseaseDetails = findViewById(R.id.layoutDiseaseDetails);

        //Indexes Received from previous classes (from the more diseases)
        index = getIntent().getIntExtra("index", -1);
        drawableIndex = getIntent().getIntExtra("drawableId", -1);

        // Display image from drawable if drawableId is set
        if (drawableIndex != -1) {
            imageView.setImageResource(drawableIndex);
        }


        btnBack = findViewById(R.id.backButton);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();// Finish current activity and go back to previous activity (MainActivity)
            }
        });

        //Changes text views from results

        txtRootCrop.setText(labelRootCrops.get(index));
        txtDisease.setText(labelDiseases.get(index));
        txtDiseaseDesc.setText("    " + labelDescription.get(index));

        if (txtDisease.getText().equals("Mechanical / Insect Damage")
                || txtDisease.getText().equals("Healthy")
                || txtDisease.getText().equals("Pest Damage")
                || txtDisease.getText().equals("Not Recognized")){
            layoutDiseaseDetails.setVisibility(View.GONE);
        } else {
            layoutDiseaseDetails.setVisibility(View.VISIBLE);
            txtCausesDesc.setText(labelCauses.get(index));
            getManagementDescription(index);
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

    public void getManagementDescription(int index){
        StringBuilder output = new StringBuilder();
        int indexManagement = 0;
        if (index == 0 || index == 1 || index == 2) {
            indexManagement = index;
        } else if (index == 5) {
            indexManagement = 3;
        } else if (index == 7) {
            indexManagement = 4;
        } else if (index == 11) {
            indexManagement = 5;
        } else if (index == 13) {
            indexManagement = 6;
        } else if (index == 14) {
            indexManagement = 7;
        } else if (index == 15) {
            indexManagement = 8;
        } else if (index == 18) {
            indexManagement = 9;
        } else if (index == 19) {
            indexManagement = 10;
        }

        StringBuilder beforePlanting = new StringBuilder();
        StringBuilder duringGrowth = new StringBuilder();
        StringBuilder afterHarvest = new StringBuilder();

        // Temporary variable to hold the current section
        String currentSection = "";

        // Iterate through the list to group the steps
        for (String step : listManagements.get(indexManagement)) {
            if (step.startsWith("Before Planting")) {
                currentSection = "Before Planting";
            } else if (step.startsWith("During Growth")) {
                currentSection = "During Growth";
            } else if (step.startsWith("After Harvest")) {
                currentSection = "After Harvest";
            } else {
                // Append to the current section
                switch (currentSection) {
                    case "Before Planting":
                        beforePlanting.append("• ").append(step).append("\n");
                        break;
                    case "During Growth":
                        duringGrowth.append("• ").append(step).append("\n");
                        break;
                    case "After Harvest":
                        afterHarvest.append("• ").append(step).append("\n");
                        break;
                }
            }
        }

        txtBefore.setText(beforePlanting.toString());
        txtDuring.setText(duringGrowth.toString());
        txtAfter.setText(afterHarvest.toString());
    }
}