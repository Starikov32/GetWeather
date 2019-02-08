package com.starikov.getweather;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreferences {
    private static final String CITY_KEY = "city";

    private SharedPreferences preferences;

    public CityPreferences(Activity activity) {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getCity() {
        return preferences.getString(CITY_KEY, "Dushanbe, TJ");
    }

    void setCity(String city) {
        preferences.edit().putString(CITY_KEY, city).apply();
    }
}
