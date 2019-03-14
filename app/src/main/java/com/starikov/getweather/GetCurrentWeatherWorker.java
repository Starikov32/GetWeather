package com.starikov.getweather;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;

import com.starikov.getweather.main.MainActivity;
import com.starikov.getweather.model.CurrentWeather;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class GetCurrentWeatherWorker extends Worker {
    private MainActivity activity;
    private MyLocation myLocation;

    public GetCurrentWeatherWorker(MainActivity activity, @NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.activity = activity;
    }

    @NonNull
    @Override
    public Result doWork() {
        myLocation = new MyLocation(activity);
        myLocation.startUpdates();

        Location location;
        if (myLocation.isEnable()) {
            location = myLocation.getLocation();
            CurrentWeather currentWeather =
                    RemoteFetch.getCurrentWeather(activity, location.getLatitude(), location.getLongitude());

            WeatherPreferences preferences = new WeatherPreferences(activity);
            preferences.setCurrentWeather(currentWeather);
        } else {
            Log.d("GetCurrentWeatherWorker", "Location is not enable!");
            return Result.retry();
        }

        myLocation.stopUpdates();
        Log.i("GetCurrentWeatherWorker", "AllOk");
        return Result.success();
    }

    @Override
    public void onStopped() {
        super.onStopped();
        if (myLocation != null) {
            myLocation.startUpdates();
        }
        Log.i("GetCurrentWeatherWorker", "WorkerIsStopped");
    }
}
