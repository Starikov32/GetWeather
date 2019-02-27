package com.starikov.getweather;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
    private static final int PAGER_COUNT = 3;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.action_current_weather);
        bottomNavigation.setOnNavigationItemSelectedListener(bottomNavigationListener);

        viewPager = findViewById(R.id.view_pager);
        WeatherPagerAdapter pagerAdapter = new WeatherPagerAdapter(getSupportFragmentManager(), PAGER_COUNT);
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(1);
    }

    ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int position) {
            bottomNavigation.setSelectedItemId(position);
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    OnNavigationItemSelectedListener bottomNavigationListener = new OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.action_current_weather:
                    viewPager.setCurrentItem(0, true);
                    break;
                case R.id.action_weather_forecast:
                    viewPager.setCurrentItem(1, true);
                    break;
            }
            return true;
        }
    };
}
