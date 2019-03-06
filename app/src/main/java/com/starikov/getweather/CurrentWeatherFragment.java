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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.starikov.getweather.weatherdata.CurrentWeather;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class CurrentWeatherFragment extends Fragment {

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 0;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

    private Handler handler;
    private MyLocation myLocation;
    private Activity activity;

    private boolean locationPermissionsGranted;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.weather_fragment, container, false);

        activity = getActivity();
        handler = new Handler();
        myLocation = new MyLocation(activity);

        locationPermissionsGranted =
                   ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        Typeface weatherFont = Typeface.createFromAsset(activity.getAssets(), "weather.ttf");

        cityField = view.findViewById(R.id.city_field);
        updatedField = view.findViewById(R.id.updated_fields);
        detailsField = view.findViewById(R.id.details_field);
        currentTemperatureField = view.findViewById(R.id.current_temperature_field);
        weatherIcon = view.findViewById(R.id.weather_icon);
        weatherIcon.setTypeface(weatherFont);

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
         switch (requestCode) {
             case REQUEST_CODE_LOCATION_PERMISSION:
                 if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                     myLocation.startUpdates();
                     requestWeather();
                     locationPermissionsGranted = true;
                 }
                 break;
         }
    }

    private void requestWeather() {
        if (myLocation.isEnable()) {
            newQuery(getQueryByLocation());
        } else {
            warningUnableDetermineLocation();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // Перестаем обновлять информацию о местоположении
        myLocation.stopUpdates();
    }

    private String getQueryByLocation() {
        Location location = myLocation.getLocation();
        return "lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Проверка разрешений
        if (locationPermissionsGranted) {
            myLocation.startUpdates();
            requestWeather();
        } else {
            locationPermissions();
            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
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
    }

    private void updateWeatherData(final String query) {
        new Thread() {
            public void run() {
                final JSONObject json = RemoteFetch.getJSON(activity, query);
                if (json != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            CurrentWeather currentWeather = new CurrentWeather(json);
                            renderWeather(currentWeather);
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

    private void renderWeather(CurrentWeather currentWeather) {
        String city = currentWeather.getCity().toUpperCase(Locale.US) + ", " + currentWeather.getCountry();
        cityField.setText(city);

        String details = currentWeather.getDescription().toUpperCase(Locale.US) + "\n"
                + "Humidity: " + currentWeather.getHumidity() + "%" + "\n"
                + "Pressure: " + currentWeather.getPressure() + " hPa";
        detailsField.setText(details);

        String currentTemperature = String.format(Locale.getDefault(), "%.2f", currentWeather.getTemp()) + " ℃";
        currentTemperatureField.setText(currentTemperature);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = "Last update: " + dateFormat.format(new Date(currentWeather.getForecastTime() * 1000));
        updatedField.setText(updateOn);

        setWeatherIcon(currentWeather.getConditionId(),
                currentWeather.getSunrise() * 1000,
                currentWeather.getSunset() * 1000);
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
