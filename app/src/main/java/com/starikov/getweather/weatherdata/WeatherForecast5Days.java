package com.starikov.getweather.weatherdata;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class WeatherForecast5Days {
    // location
    private double longitude;
    private double latitude;
    private String country;
    private String city;

    private ArrayList<WeatherForecast3Hour> list;

    public WeatherForecast5Days(JSONObject json) {
        try {
            JSONObject city = json.getJSONObject("city");
            this.city = (String) city.get("name");

            JSONObject coordinates = json.getJSONObject("coord");
            longitude = (double) coordinates.get("lon");
            latitude = (double) coordinates.get("lat");

            JSONArray list = json.getJSONArray("list");
            this.list = new ArrayList<>(list.length());
            for (int i = 0; i < list.length(); ++i) {
                this.list.add(new WeatherForecast3Hour(list.getJSONObject(i)));
            }

            country = (String) json.get("country");
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

    public ArrayList<WeatherForecast3Hour> getList() {
        return list;
    }

    private class WeatherForecast3Hour {
        private long forecastTime;

        // main
        private double temp;
        private double minTemp;
        private double maxTemp;
        private double pressure;
        private int humidity;

        // cloud
        private int cloudiness;

        // wind
        private double windSpeed;
        private double windDirection;

        // weather
        private int conditionId;
        private String parameters;
        private String description;

        private WeatherForecast3Hour(JSONObject json) {
            try {
                JSONObject main = json.getJSONObject("main");
                temp = (double) main.get("temp");
                minTemp = (double) main.get("temp_min");
                maxTemp = (double) main.get("temp_max");
                pressure = (int) main.get("pressure");
                humidity = (int) main.get("humidity");

                JSONObject weather = json.getJSONArray("weather").getJSONObject(0);
                conditionId = (int) weather.get("id");
                parameters = (String) weather.get("main");
                description = (String) weather.get("description");

                JSONObject wind = json.getJSONObject("wind");
                windSpeed = (double) wind.get("speed");
                windDirection = (int) wind.get("deg");

                JSONObject clouds = json.getJSONObject("clouds");
                cloudiness = (int) clouds.get("all");

                forecastTime = (int) json.get("dt");
            } catch (JSONException exc) {
                Log.e("JSON", "One or more fields not found in the JSON data");
            }
        }

        public long getForecastTime() {
            return forecastTime;
        }

        public double getTemp() {
            return temp;
        }

        public double getMinTemp() {
            return minTemp;
        }

        public double getMaxTemp() {
            return maxTemp;
        }

        public double getPressure() {
            return pressure;
        }

        public int getHumidity() {
            return humidity;
        }

        public int getCloudiness() {
            return cloudiness;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public double getWindDirection() {
            return windDirection;
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
    }
}
