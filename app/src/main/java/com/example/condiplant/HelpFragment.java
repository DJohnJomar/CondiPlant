package com.example.condiplant;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.condiplant.databinding.ActivityMainBinding;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.ArrayList;

public class HelpFragment extends Fragment {
    ViewPager2 viewPager2;
    ArrayList<ViewPageItem> viewPageItemArrayList;
    ActivityMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                         Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(inflater);
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        viewPager2 = view.findViewById(R.id.viewPager);

        // Create ViewPageItem objects
        int[] images = {R.drawable.cassava_blight, R.drawable.cassava_blight, R.drawable.cassava_blight, R.drawable.cassava_blight};
        String[] title = {"Step 1: Testing", "Step 2: Testing", "Step 3: Testing", "Step 4: Testing"};
        String[] description = {getString(R.string.lorem_ipsum), getString(R.string.lorem_ipsum), getString(R.string.lorem_ipsum), getString(R.string.lorem_ipsum)};

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
        DotsIndicator dotsIndicator = view.findViewById(R.id.dots_indicator);
        dotsIndicator.setViewPager2(viewPager2);

        return view;
    }

}