package id.co.viva.news.app.location;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.util.TimerTask;

import id.co.viva.news.app.interfaces.LocationResult;

/**
 * Created by reza on 24/02/15.
 */
public class GetLastLocation extends TimerTask {

    private LocationManager locationManager;
    private LocationListener listenerGPS;
    private LocationListener listenerProvider;
    private boolean isGPSEnabled;
    private boolean isProviderEnabled;
    private LocationResult result;

    public GetLastLocation(LocationManager locationManager, LocationListener listenerGPS, LocationListener listenerProvider,
                           boolean isGPSEnabled, boolean isProviderEnabled, LocationResult result) {
        this.locationManager = locationManager;
        this.listenerGPS = listenerGPS;
        this.listenerProvider = listenerProvider;
        this.isGPSEnabled = isGPSEnabled;
        this.isProviderEnabled = isProviderEnabled;
        this.result = result;
    }

    @Override
    public void run() {
        locationManager.removeUpdates(listenerGPS);
        locationManager.removeUpdates(listenerProvider);

        Location gps_location = null, provider_location = null;
        if(isProviderEnabled) {
            provider_location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        if(isGPSEnabled) {
            gps_location = locationManager
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }

        if(gps_location != null && provider_location != null) {
            if(gps_location.getTime() > provider_location.getTime())
                result.getLocation(gps_location);
            else
                result.getLocation(provider_location);
            return;
        }

        if(gps_location != null) {
            result.getLocation(gps_location);
            return;
        }
        if(provider_location != null) {
            result.getLocation(provider_location);
            return;
        }

        result.getLocation(null);
    }

}
