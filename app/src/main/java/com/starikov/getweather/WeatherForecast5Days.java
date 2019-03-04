package com.starikov.getweather;

import java.util.ArrayList;

public class WeatherForecast5Days {
    // location
    private double longitude;
    private double latitude;
    private String country;
    private String name;

    private ArrayList<WeatherForecast3Hour> list;

    private class WeatherForecast3Hour {
        private long forecastTime;

        // main
        private double temp;
        private double minTemp;
        private double maxTemp;
        private double pressure;
        private int humidity;

        // additional
        private int clouds;
        private int rainVolume3h;
        private int snowVolume3h;

        // wind
        private double speed;
        private double direction;

        // weather
        private int id;
        private String main;
        private String description;
    }
}
