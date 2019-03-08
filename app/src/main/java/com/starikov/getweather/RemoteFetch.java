package com.starikov.getweather;

import android.content.Context;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONObject;

public class RemoteFetch {
    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?";

    public static JSONObject getJSON(final Context context, String query) {
        try {
            OkHttpClient client = new OkHttpClient();

            // TODO: тут нужно сделать query чтобы не напрямую вставлялся а методами
            HttpUrl.Builder urlBuilder = HttpUrl.parse(OPEN_WEATHER_MAP_API + query).newBuilder();
            urlBuilder.addQueryParameter("units", "metric");
            urlBuilder.addQueryParameter("APPID", context.getString(R.string.open_weather_maps_app_id));

            String url = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url)
                    .build();

            Response response = client.newCall(request).execute();
            JSONObject data = new JSONObject(response.body().string());

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
