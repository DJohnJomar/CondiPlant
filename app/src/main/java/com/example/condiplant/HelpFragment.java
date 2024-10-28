package com.example.condiplant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.example.condiplant.databinding.ActivityMainBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;
import java.util.ArrayList;

public class HelpFragment extends Fragment {
    ViewPager2 viewPager2;
    ArrayList<ViewPageItem> viewPageItemArrayList;
    ActivityMainBinding binding;
    ImageButton prevPage, nextPage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(inflater);
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        viewPager2 = view.findViewById(R.id.viewPager);
        prevPage = view.findViewById(R.id.prevPage);
        nextPage = view.findViewById(R.id.nextPage);

        // Create ViewPageItem objects
        int[] images = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};
        String[] title = {"Welcome to CondiPlant!", "Capturing and Uploading", "Viewing Prediction Result", "More Prediction Result Details"};
        String[] description = {"CondiPlant is an application that allows one to detect plant diseases by taking pictures of its leaves. Currently, the application focuses on Root Crops found in the area of Cabuyao City, Laguna.",
                "To start using the application, select either to capture or upload an image. The application will respectively open apps that are needed. Capture or Select the image that will be predicted by the application.",
                "If the capture or upload of an image is successful, the prediction result is shown after a few seconds later. In here, At most 3 predictions with their percentages are shown. Clicking the Read More button shows a detailed explanation of each prediction",
                "In this page, full details of the predicted disease, along with its scientific name, description, causes, and control & management is shown. "};

        viewPageItemArrayList = new ArrayList<>();

        for (int i = 0; i < images.length; i++){
            ViewPageItem viewPageItem = new ViewPageItem(title[i], description[i], images[i]);
            viewPageItemArrayList.add(viewPageItem);
        }

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(viewPageItemArrayList);
        viewPager2.setAdapter(viewPageAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(2);
        viewPager2.getChildAt(0).setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);

                prevPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager2.setCurrentItem(position - 1);
                    }
                });
                nextPage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager2.setCurrentItem(position + 1);
                    }
                });

                if (position == 0){
                    prevPage.setVisibility(View.INVISIBLE);
                    nextPage.setVisibility(View.VISIBLE);
                } else if (position == viewPageItemArrayList.size() - 1){
                    prevPage.setVisibility(View.VISIBLE);
                    nextPage.setVisibility(View.INVISIBLE);
                } else {
                    prevPage.setVisibility(View.VISIBLE);
                    nextPage.setVisibility(View.VISIBLE);
                }
            }
        });
        DotsIndicator dotsIndicator = view.findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager2);

        return view;
    }
}