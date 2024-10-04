package com.example.condiplant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

        if (!isFirstLaunch) {
            // If not the first launch, go directly to MainActivity
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
        int[] images = {R.drawable.cassava_blight, R.drawable.cassava_blight, R.drawable.cassava_blight, R.drawable.cassava_blight};
        String[] title = {"Step 1: Testing", "Step 2: Testing", "Step 3: Testing", "Step 4: Testing"};
        String[] description = {getString(R.string.lorem_ipsum), getString(R.string.lorem_ipsum), getString(R.string.lorem_ipsum), getString(R.string.lorem_ipsum)};

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
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean("isFirstLaunch", false);  // Set this to false after tutorial
                        editor.apply();

                        startActivity(new Intent(IntroSlider.this, MainActivity.class));
                        finish();  // Close IntroSlider
                    }
                });

                // Show/hide buttons based on position
                if (position == 0) {
                    prevPage.setVisibility(View.INVISIBLE);
                    nextPage.setVisibility(View.VISIBLE);
                } else if (position == viewPageItemArrayList.size() - 1) {
                    prevPage.setVisibility(View.VISIBLE);
                    nextPage.setVisibility(View.INVISIBLE);
                } else {
                    prevPage.setVisibility(View.VISIBLE);
                    nextPage.setVisibility(View.VISIBLE);
                }
            }
        });

        // Set up dots indicator
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager2);
    }
}
