package com.starikov.getweather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class WeatherPagerAdapter extends FragmentPagerAdapter {
    private int pageCount;

    private static final Fragment[] pagers = {
            new Fragment(),
            new CurrentWeatherFragment(),
            new Fragment()
    };

    public WeatherPagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.pageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        return pagers[position];
    }

    @Override
    public int getCount() {
        return pageCount;
    }
}
