package com.example.condiplant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class IntroSlider extends AppCompatActivity {
    ViewPager2 viewPager2;
    ArrayList<ViewPageItem> viewPageItemArrayList;
    ImageButton prevPage, nextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check SharedPreferences to see if the tutorial has been shown
        SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
        boolean isFirstLaunch = prefs.getBoolean("isFirstLaunch", true);
        Log.d("IntroSlider", "isFirstLaunch: " + isFirstLaunch);

        if (!isFirstLaunch) {
            // If not the first launch, go directly to MainActivity
            Log.d("IntroSlider", "Launching MainActivity directly.");
            startActivity(new Intent(this, MainActivity.class));
            finish();  // Close IntroSlider so it's not in the back stack
            return;  // Exit the onCreate method early
        }

        // Set the content view to the correct layout (IntroSlider layout)
        setContentView(R.layout.activity_intro_slider);

        // Find views by ID from the layout
        viewPager2 = findViewById(R.id.viewPager);
        prevPage = findViewById(R.id.prevPage);
        nextPage = findViewById(R.id.nextPage);

        // Create ViewPageItem objects
        int[] images = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};
        String[] title = {"Welcome to CondiPlant!", "Capturing and Uploading", "Viewing Prediction Result", "More Prediction Result Details"};
        String[] description = {"CondiPlant is an application that allows one to detect plant diseases by taking pictures of its leaves. Currently, the application focuses on Root Crops found in the area of Cabuyao City, Laguna.",
                "To start using the application, select either to capture or upload an image. The application will respectively open apps that are needed. Capture or Select the image that will be predicted by the application.",
                "If the capture or upload of an image is successful, the prediction result is shown after a few seconds later. In here, At most 3 predictions with their percentages are shown. Clicking the Read More button shows a detailed explanation of each prediction",
                "In this page, full details of the predicted disease, along with its scientific name, description, causes, and control & management is shown. "};

        viewPageItemArrayList = new ArrayList<>();
        for (int i = 0; i < images.length; i++) {
            ViewPageItem viewPageItem = new ViewPageItem(title[i], description[i], images[i]);
            viewPageItemArrayList.add(viewPageItem);
        }

        // Set up ViewPager with the adapter
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(viewPageItemArrayList);
        viewPager2.setAdapter(viewPageAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);

        // Navigation buttons setup
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                // Show/hide buttons based on position
                prevPage.setVisibility(position == 0 ? View.INVISIBLE : View.VISIBLE);
                nextPage.setVisibility(View.VISIBLE);  // Always keep nextPage visible

                // Set click listeners for the buttons
                prevPage.setOnClickListener(v -> {
                    if (position > 0) {
                        viewPager2.setCurrentItem(position - 1);
                    }
                });

                nextPage.setOnClickListener(v -> {
                    if (position < viewPageItemArrayList.size() - 1) {
                        viewPager2.setCurrentItem(position + 1);
                    } else {
                        // If it's the last page, finish the tutorial
                        Log.d("IntroSlider", "Finishing tutorial and launching MainActivity.");
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isFirstLaunch", false); // Set this to false after tutorial
                        editor.apply();

                        boolean check = prefs.getBoolean("isFirstLaunch", true);
                        Log.d("IntroSlider", "isFirstLaunch after update: " + check);

                        startActivity(new Intent(IntroSlider.this, MainActivity.class));
                        finish(); // Close IntroSlider
                    }
                });
            }
        });

        // Set up dots indicator
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager2);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d("IntroSlider", "Back pressed - closing IntroSlider.");
        finish();
    }
}
