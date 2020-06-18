package com.example.srmobile.mainBanner;

import com.example.srmobile.R;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public void onBindImageSlide(int position, ImageSlideViewHolder viewHolder) {
        switch (position) {
            case 0:
                viewHolder.bindImageSlide(R.drawable.banner_instant);
                break;
            case 1:
                viewHolder.bindImageSlide(R.drawable.banner_material_android_hive);
                break;
            case 2:
                viewHolder.bindImageSlide(R.drawable.banner_material_design);
                break;
        }
    }
}