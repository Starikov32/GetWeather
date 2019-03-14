package com.starikov.getweather;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.starikov.getweather.model.CurrentWeather;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RemoteFetch {

    private static final String TAG = RemoteFetch.class.getName();

    private static final String CURRENT_WEATHER_API =
            "http://api.openweathermap.org/data/2.5/weather?";
    private static final String WEATHER_FORECAST_API =
            "http://api.openweathermap.org/data/2.5/weather?";

    private static String getBody(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Log.d(TAG, "url: " + url);
        Response response = client.newCall(request).execute();
        String data = response.body().string();
        return data;
    }

    public static CurrentWeather getCurrentWeather(final Context context, double lat, double lon) {
        CurrentWeather currentWeather = null;
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(CURRENT_WEATHER_API).newBuilder();
            urlBuilder.addQueryParameter("lat", String.valueOf(lat));
            urlBuilder.addQueryParameter("lon", String.valueOf(lon));
            urlBuilder.addQueryParameter("units", "metric");
            urlBuilder.addQueryParameter("APPID", context.getString(R.string.open_weather_maps_app_id));

            String url = urlBuilder.build().toString();
            String data = getBody(url);

            currentWeather = new Gson().fromJson(data, CurrentWeather.class);

            // This value will be 404 if the request was not
            // successful; 200 = success
            if(currentWeather.getCod() != 200) {
                return null;
            }
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        return currentWeather;
    }

    public static JSONObject getWeatherForecastJSON(final Context context, double lat, double lon) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(WEATHER_FORECAST_API).newBuilder();
            urlBuilder.addQueryParameter("lat", String.valueOf(lat));
            urlBuilder.addQueryParameter("lon", String.valueOf(lon));
            urlBuilder.addQueryParameter("units", "metric");
            urlBuilder.addQueryParameter("APPID", context.getString(R.string.open_weather_maps_app_id));

            String url = urlBuilder.build().toString();
            JSONObject data = new JSONObject(getBody(url));

            // This value will be 404 if the request was not
            // successful; 200 = success
            if(data.getInt("cod") != 200) {
                return null;
            }

            return data;
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }
}
