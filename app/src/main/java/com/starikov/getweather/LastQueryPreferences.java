package com.starikov.getweather;

import android.app.Activity;
import android.content.SharedPreferences;

public class LastQueryPreferences {
    private static final String LAST_QUERY_KEY = "last_query";

    private SharedPreferences preferences;

    public LastQueryPreferences(Activity activity) {
        preferences = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    String getLastQuery() {
        return preferences.getString(LAST_QUERY_KEY, "q=Dushanbe, TJ");
    }

    void setLastQuery(String query) {
        preferences.edit().putString(LAST_QUERY_KEY, query).apply();
    }
}
