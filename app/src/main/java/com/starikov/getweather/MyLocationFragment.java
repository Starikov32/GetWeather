package com.starikov.getweather;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MyLocationFragment extends Fragment {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 0;

    private Typeface weatherFont;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

    private Handler handler;
    private MyLocation myLocation;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);

        activity = getActivity();
        handler = new Handler();
        myLocation = new MyLocation(activity);
        Location location = myLocation.getLocation();

        if (myLocation.isEnable()) {
            try {
                String query = "lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
                newQuery(query);
            } catch (SecurityException exc) {
                locationPermissions();
            }
        } else {
            warningUnableDetermineLocation();
        }

        weatherFont = Typeface.createFromAsset(activity.getAssets(), "weather.ttf");

        cityField = view.findViewById(R.id.city_field);
        updatedField = view.findViewById(R.id.updated_fields);
        detailsField = view.findViewById(R.id.details_field);
        currentTemperatureField = view.findViewById(R.id.current_temperature_field);
        weatherIcon = view.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        String query = "lat=" + location.getLatitude() + "&lon=" + location.getLongitude();

        updateWeatherData(new LastQueryPreferences(activity).getLastQuery());
        newQuery(query);

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        // Перестаем обновлять информацию о местоположении
        myLocation.stopUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            myLocation.startUpdates();
        } catch (SecurityException exc) {
            locationPermissions();
        }

        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(activity,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    private void warningUnableDetermineLocation() {
        Toast.makeText(activity, getString(R.string.unable_determine_location), Toast.LENGTH_SHORT)
                .show();
    }

    private void locationPermissions() {
        Toast.makeText(activity, getString(R.string.location_permissions), Toast.LENGTH_SHORT)
                .show();
    }

    private void newQuery(String query) {
        updateWeatherData(query);
        new LastQueryPreferences(activity).setLastQuery(query);
    }

    private void updateWeatherData(final String query) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(activity, query);
                if (json != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            renderWeather(json);
                        }
                    });
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,
                                    getString(R.string.place_not_found),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }.start();
    }

    private void renderWeather(JSONObject json) {
        try {
            String city = json.getString("name").toUpperCase(Locale.US) + ", " + json.getJSONObject("sys").getString("country");
            cityField.setText(city);

            JSONObject detailsJson = json.getJSONArray("weather").getJSONObject(0);
            JSONObject mainJson = json.getJSONObject("main");
            String details = detailsJson.getString("description").toUpperCase(Locale.US) + "\n"
                    + "Humidity: " + mainJson.getString("humidity") + "%" + "\n"
                    + "Pressure: " + mainJson.getString("pressure") + " hPa";
            detailsField.setText(details);

            String currentTemperature = String.format(Locale.getDefault(), "%.2f", mainJson.getDouble("temp")) + " ℃";
            currentTemperatureField.setText(currentTemperature);

            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            String updateOn = dateFormat.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last update: " + updateOn);

            setWeatherIcon(detailsJson.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);
        } catch (Exception exc) {
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset) {
        int id = actualId / 100;
        String icon = "";
        if (actualId == 800) {
            long currentTime = new Date().getTime();
            if (currentTime >= sunrise && currentTime < sunset) {
                icon = getString(R.string.weather_sunny);
            } else {
                icon = getString(R.string.weather_clear_night);
            }
        } else {
            switch (id) {
                case 2:
                    icon = getString(R.string.weather_thunder);
                    break;
                case 3:
                    icon = getString(R.string.weather_drizzle);
                    break;
                case 5:
                    icon = getString(R.string.weather_rainy);
                    break;
                case 6:
                    icon = getString(R.string.weather_snowy);
                    break;
                case 7:
                    icon = getString(R.string.weather_foggy);
                    break;
                case 8:
                    icon = getString(R.string.weather_cloudy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
}