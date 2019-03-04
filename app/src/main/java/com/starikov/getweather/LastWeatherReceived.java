package com.starikov.getweather;

public class LastWeatherReceived {
    // location
    private double longitude;
    private double latitude;
    private String country;
    private String name;

    // additional
    private long sunrise;
    private long sunset;
    private long forecastTime;
    private double rainVolume3h;
    private double snowVolume3h;
    private int clouds;

    // weather
    private int id;
    private String main;
    private String description;

    // wind
    private double speed;
    private double direction;

    // main
    private double temp;
    private double pressure;
    private int humidity;
    private double maxTemp;
    private double minTemp;
}
