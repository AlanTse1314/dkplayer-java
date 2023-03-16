package com.alan.banner.listener;

import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;


import com.alan.banner.bean.BannerBean;

import java.util.List;

public class OnBannerChangeListener implements ViewPager.OnPageChangeListener {
    public TextView textView;
    public List<BannerBean> onBannerChangeListener;

    public OnBannerChangeListener(TextView textView, List<BannerBean> list) {
        this.onBannerChangeListener = list;
        this.textView = textView;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
    }

    @Override
    public void onPageScrolled(int i, float f, int i2) {
    }

    @Override
    public void onPageSelected(int i) {
        if (i >= this.onBannerChangeListener.size() || this.onBannerChangeListener.get(i) == null) {
            this.textView.setText("");
        } else {
            this.textView.setText(this.onBannerChangeListener.get(i).getTitle());
        }
    }
}
