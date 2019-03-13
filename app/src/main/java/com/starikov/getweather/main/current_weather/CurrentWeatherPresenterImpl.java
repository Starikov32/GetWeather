package com.starikov.getweather.main.current_weather;

import android.location.Location;

import static com.starikov.getweather.main.current_weather.CurrentWeatherContract.*;

public class CurrentWeatherPresenterImpl implements Presenter {

    private View view;

    private Model model;

    public CurrentWeatherPresenterImpl(View view) {

        this.view = view;
        this.model = new CurrentWeatherModelImpl();
    }

}
