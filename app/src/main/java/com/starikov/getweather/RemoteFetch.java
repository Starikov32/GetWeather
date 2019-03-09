package com.starikov.getweather;

import android.content.Context;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RemoteFetch {
    private static final String CURRENT_WEATHER_API =
            "http://api.openweathermap.org/data/2.5/weather?";
    private static final String WEATHER_FORECAST_API =
            "http://api.openweathermap.org/data/2.5/weather?";

    private static JSONObject getJSON(String url) throws Exception {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        JSONObject data = new JSONObject(response.body().string());
        return data;
    }

    public static JSONObject getCurrentWeatherJSON(final Context context, double lat, double lon) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(CURRENT_WEATHER_API).newBuilder();
            urlBuilder.addQueryParameter("lat", String.valueOf(lat));
            urlBuilder.addQueryParameter("lon", String.valueOf(lon));
            urlBuilder.addQueryParameter("units", "metric");
            urlBuilder.addQueryParameter("APPID", context.getString(R.string.open_weather_maps_app_id));

            String url = urlBuilder.build().toString();
            JSONObject data = getJSON(url);

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

    public static JSONObject getWeatherForecastJSON(final Context context, double lat, double lon) {
        try {
            HttpUrl.Builder urlBuilder = HttpUrl.parse(WEATHER_FORECAST_API).newBuilder();
            urlBuilder.addQueryParameter("lat", String.valueOf(lat));
            urlBuilder.addQueryParameter("lon", String.valueOf(lon));
            urlBuilder.addQueryParameter("units", "metric");
            urlBuilder.addQueryParameter("APPID", context.getString(R.string.open_weather_maps_app_id));

            String url = urlBuilder.build().toString();
            JSONObject data = getJSON(url);

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
