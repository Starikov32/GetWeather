package com.starikov.getweather;

import android.app.Activity;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.starikov.getweather.model.CurrentWeather;
import com.starikov.getweather.weatherdata.WeatherForecast5Days;

public class WeatherPreferences {
    private static final String CURRENT_WEATHER_KEY = "current_weather";
    private static final String WEATHER_FORECAST_KEY = "weather_forecast";

    private SharedPreferences preferences;
    private Gson gson;

    public WeatherPreferences(Activity activity) {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
        gson = new Gson();
    }

    public CurrentWeather getCurrentWeather() {
        String json = preferences.getString(CURRENT_WEATHER_KEY, "");
        return gson.fromJson(json, CurrentWeather.class);
    }

    public void setCurrentWeather(CurrentWeather currentWeather) {
        String json = gson.toJson(currentWeather);
        preferences.edit().putString(CURRENT_WEATHER_KEY, json).apply();
    }

    public WeatherForecast5Days getWeatherForecast() {
        String json = preferences.getString(WEATHER_FORECAST_KEY, "");
        return gson.fromJson(json, WeatherForecast5Days.class);
    }

    public void setWeatherForecast(WeatherForecast5Days weatherForecast) {
        String json = gson.toJson(weatherForecast);
        preferences.edit().putString(WEATHER_FORECAST_KEY, json).apply();
    }
}
