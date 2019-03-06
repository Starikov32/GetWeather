package com.starikov.getweather.weatherdata;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CurrentWeather {
    // location
    private double longitude;
    private double latitude;
    private String country;
    private String city;

    // additional
    private int sunrise;
    private int sunset;
    private int forecastTime;
    private int cloudiness;

    // weather
    private int conditionId;
    private String parameters;
    private String description;

    // wind
    private double windSpeed;
    private double windDirection;

    // main
    private double temp;
    private double pressure;
    private int humidity;
    private double maxTemp;
    private double minTemp;

    public CurrentWeather(JSONObject jsonObject) {
        try {
            JSONObject coordinates = jsonObject.getJSONObject("coord");
            longitude = (double) coordinates.get("lon");
            latitude = (double) coordinates.get("lat");

            JSONObject weather = jsonObject.getJSONArray("weather").getJSONObject(0);
            conditionId = (int) weather.get("id");
            parameters = (String) weather.get("main");
            description = (String) weather.get("description");

            JSONObject main = jsonObject.getJSONObject("main");
            temp = (double) main.get("temp");
            pressure = (double) main.get("pressure");
            humidity = (int) main.get("humidity");
            minTemp = (double) main.get("temp_min");
            maxTemp = (double) main.get("temp_max");

            JSONObject wind = jsonObject.getJSONObject("wind");
            windSpeed = (double) wind.get("speed");
            windDirection = (double) wind.get("deg");

            JSONObject clouds = jsonObject.getJSONObject("clouds");
            cloudiness = (int) clouds.get("all");

            JSONObject sys = jsonObject.getJSONObject("sys");
            country = (String) sys.get("country");
            sunrise = (int) sys.get("sunrise");
            sunset = (int) sys.get("sunset");

            forecastTime = (int) jsonObject.get("dt");
            city = (String) jsonObject.get("name");
        } catch (JSONException exc) {
            Log.e("JSON", "One or more fields not found in the JSON data");
        }
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }

    public long getSunrise() {
        return sunrise;
    }

    public long getSunset() {
        return sunset;
    }

    public long getForecastTime() {
        return forecastTime;
    }

    public int getCloudiness() {
        return cloudiness;
    }

    public int getConditionId() {
        return conditionId;
    }

    public String getParameters() {
        return parameters;
    }

    public String getDescription() {
        return description;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public double getTemp() {
        return temp;
    }

    public double getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }

    public double getMaxTemp() {
        return maxTemp;
    }

    public double getMinTemp() {
        return minTemp;
    }
}
