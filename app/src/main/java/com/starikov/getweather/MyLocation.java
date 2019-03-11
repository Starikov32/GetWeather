package com.starikov.getweather;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class MyLocation implements LocationListener {
    private static final long MIN_TIME = 1000 * 10; // Минимальное время обновления = 10 минут
    private static final long MIN_DISTANCE = 1000; // Минимальное расстояние = 1км

    private LocationManager locationManager;
    private Context context;
    private Location lastLocation;

    public MyLocation(Context context) {
        this.context = context;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public void startUpdates() throws SecurityException {
        // По GPS
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
        // По сети
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, MIN_TIME, MIN_DISTANCE, this);
    }

    public boolean isEnable() {
        return (isGpsEnable() || isNetworkEnable());
    }

    private boolean isGpsEnable() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private boolean isNetworkEnable() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void stopUpdates() {
        locationManager.removeUpdates(this);
    }

    public Location getLocation() throws SecurityException {
        if (isNetworkEnable()) {
            return locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        } else if (isGpsEnable()) {
            return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }
        return lastLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
