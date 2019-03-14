package com.starikov.getweather.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.List;

public class WeatherPagerAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragments;

    public WeatherPagerAdapter(List<Fragment> fragments, FragmentManager fragmentManager) {
        super(fragmentManager);
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
