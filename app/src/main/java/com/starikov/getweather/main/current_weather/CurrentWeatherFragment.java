package com.starikov.getweather.main.current_weather;

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

import com.starikov.getweather.MyLocation;
import com.starikov.getweather.R;
import com.starikov.getweather.RemoteFetch;
import com.starikov.getweather.WeatherPreferences;
import com.starikov.getweather.main.MainActivity;
import com.starikov.getweather.model.CurrentWeather;

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
                    requestWeatherOnline();
                    locationPermissionsGranted = true;
                }
                break;
        }
    }

    private void requestWeatherOnline() {
        if (myLocation.isEnable()) {
            Location location = myLocation.getLocation();
            updateWeatherData(location.getLatitude(), location.getLongitude());
        } else {
            warningUnableDetermineLocation();
        }
    }

    private void requestWeatherOffline() {
        WeatherPreferences preferences = new WeatherPreferences(activity);
        CurrentWeather currentWeather = preferences.getCurrentWeather();
        if (currentWeather != null) {
            renderWeather(currentWeather);
        } else {
            warningNoSavedWeather();
        }
    }

    @Override
    public void onStop() {
        // Перестаем обновлять информацию о местоположении
        myLocation.stopUpdates();
        super.onStop();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Проверка разрешений
        if (locationPermissionsGranted) {
            myLocation.startUpdates();
            if (((MainActivity) activity).isNetworkAvailable()) {
                requestWeatherOnline();
            } else {
                requestWeatherOffline();
            }
        } else {
            warningGrantNecessaryLocationPermissions();
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    private void warningUnableDetermineLocation() {
        Toast.makeText(activity, getString(R.string.unable_determine_location), Toast.LENGTH_SHORT)
                .show();
    }

    private void warningNoSavedWeather() {
        Toast.makeText(activity, getString(R.string.no_saved_weather), Toast.LENGTH_SHORT)
                .show();
    }

    private void warningGrantNecessaryLocationPermissions() {
        Toast.makeText(activity, getString(R.string.location_permissions), Toast.LENGTH_SHORT)
                .show();
    }

    private void warningPlaceNotFound() {
        Toast.makeText(activity, getString(R.string.place_not_found), Toast.LENGTH_SHORT)
                .show();
    }

    private void updateWeatherData(final double lat, final double lon) {
        new Thread() {
            public void run() {
                final CurrentWeather currentWeather = RemoteFetch.getCurrentWeather(activity, lat, lon);
                if (currentWeather != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            WeatherPreferences preferences = new WeatherPreferences(activity);
                            preferences.setCurrentWeather(currentWeather);
                            renderWeather(currentWeather);
                        }
                    });
                } else {
                    warningPlaceNotFound();
                }
            }
        }.start();
    }

    private void renderWeather(CurrentWeather currentWeather) {
        String city = currentWeather.getName().toUpperCase(Locale.US) + ", " + currentWeather.getSys().getCountry();
        cityField.setText(city);

        String details = currentWeather.getWeather().get(0).getDescription().toUpperCase(Locale.US) + "\n"
                + "Humidity: " + currentWeather.getMain().getHumidity() + "%" + "\n"
                + "Pressure: " + currentWeather.getMain().getPressure() + " hPa";
        detailsField.setText(details);

        String currentTemperature = String.format(Locale.getDefault(), "%.2f", currentWeather.getMain().getTemp()) + " ℃";
        currentTemperatureField.setText(currentTemperature);

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String updateOn = "Last update: " + dateFormat.format(new Date(currentWeather.getDt() * 1000));
        updatedField.setText(updateOn);

        setWeatherIcon(currentWeather.getWeather().get(0).getId(),
                Long.parseLong(currentWeather.getSys().getSunrise()) * 1000,
                Long.parseLong(currentWeather.getSys().getSunset()) * 1000);
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
