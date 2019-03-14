package com.starikov.getweather.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.starikov.getweather.GetCurrentWeatherWorker;
import com.starikov.getweather.main.current_weather.CurrentWeatherFragment;
import com.starikov.getweather.R;
import com.starikov.getweather.WeatherForecastFragment;
import com.starikov.getweather.adapters.WeatherPagerAdapter;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity {
    public static final String GET_CURRENT_WEATHER_WORKER_TAG = "get_current_weather_worker_tag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = findViewById(R.id.view_pager);
        WeatherPagerAdapter pagerAdapter = new WeatherPagerAdapter(
                Arrays.asList(new CurrentWeatherFragment(), new WeatherForecastFragment()),
                getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        // if (WorkManager.getInstance().getWorkInfosByTag(GET_CURRENT_WEATHER_WORKER_TAG).isCancelled()) {
            Log.i("GetCurrentWeatherWorker", "WorkerIsCancelled");

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest getCurrentWeatherRequest =
                    new PeriodicWorkRequest.Builder(GetCurrentWeatherWorker.class,
                            1, TimeUnit.HOURS,
                            30, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .addTag(GET_CURRENT_WEATHER_WORKER_TAG)
                            .build();

            WorkManager.getInstance().enqueue(getCurrentWeatherRequest);

            Log.i("GetCurrentWeatherWorker", "WorkerIsStarted");
        // }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                // TODO: открытие настроек
                break;
        }
        return true;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager manager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
