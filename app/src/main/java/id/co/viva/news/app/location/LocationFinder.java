package id.co.viva.news.app.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Timer;

import id.co.viva.news.app.Constant;
import id.co.viva.news.app.interfaces.LocationResult;
import id.co.viva.news.app.interfaces.OnGPSListener;

/**
 * Created by reza on 23/02/15.
 */
public class LocationFinder {

    private Timer timer;
    private OnGPSListener listener;
    private LocationManager locationManager;
    private LocationResult locationResult;
    private boolean gps_enabled = false;
    private boolean network_enabled = false;

    public boolean getLocation(Context context, LocationResult result, OnGPSListener gpsListener) {
        locationResult = result;
        listener = gpsListener;

        //Set Location Manager
        if(locationManager == null) {
            locationManager = (LocationManager) context
                    .getSystemService(Context.LOCATION_SERVICE);
        }

        //Set GPS Provider
        try {
            gps_enabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.getMessage();
        }

        //Set Network Provider
        try {
            network_enabled=locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.getMessage();
        }

        if(!gps_enabled) {
            listener.onAlertGPS();
            return false;
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
        }

        if(!network_enabled) {
            return false;
        } else {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        }

        //Run Checking Process
        timer = new Timer();
        timer.schedule(new GetLastLocation(locationManager, locationListenerGps, locationListenerNetwork,
                gps_enabled, network_enabled, locationResult), Constant.MIN_TIME_BW_UPDATES);

        return true;
    }

    public void removeLocationListener() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListenerNetwork);
            locationManager.removeUpdates(locationListenerGps);
            locationManager = null;
        }
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            if(location != null) {
                locationResult.getLocation(location);
            }
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer.cancel();
            if(location != null) {
                locationResult.getLocation(location);
            }
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

}
