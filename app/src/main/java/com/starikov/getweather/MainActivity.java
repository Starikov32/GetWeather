package com.starikov.getweather;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private Typeface weatherFont;

    private TextView cityField;
    private TextView updatedField;
    private TextView detailsField;
    private TextView currentTemperatureField;
    private TextView weatherIcon;

    Handler handler;

    private MyLocation myLocation;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handler = new Handler();
        weatherFont = Typeface.createFromAsset(getAssets(), "weather.ttf");
        myLocation = new MyLocation(this);

        cityField = findViewById(R.id.city_field);
        updatedField = findViewById(R.id.updated_fields);
        detailsField = findViewById(R.id.details_field);
        currentTemperatureField = findViewById(R.id.current_temperature_field);
        weatherIcon = findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);

        updateWeatherData(new LastQueryPreferences(this).getLastQuery());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myLocation.isEnable()) {
                    try {
                        Location location = myLocation.getLocation();
                        String query = "lat=" + location.getLatitude() + "&lon=" + location.getLongitude();
                        newQuery(query);
                    } catch (SecurityException exc) {
                        locationPermissions();
                    }
                } else {
                    warningUnableDetermineLocation();
                }
            }
        });
    }

    private void warningUnableDetermineLocation() {
        Toast.makeText(this, getString(R.string.unable_determine_location), Toast.LENGTH_SHORT)
                .show();
    }

    private void locationPermissions() {
        Toast.makeText(this, getString(R.string.location_permissions), Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            myLocation.startUpdates();
        } catch (SecurityException exc) {
            locationPermissions();
        }

        // Проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Перестаем обновлять информацию о местоположении
        myLocation.stopUpdates();
    }

    private void updateWeatherData(final String query) {
        final MainActivity activity = this;
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

    private void newQuery(String query) {
        updateWeatherData(query);
        new LastQueryPreferences(this).setLastQuery(query);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_change_city) {
            showInputDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Change city");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newQuery("q=" + input.getText().toString());
            }
        });
        builder.show();
    }
}
