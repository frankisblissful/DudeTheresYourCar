package com.thetoothpick.bluetoothpindrop;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by frank on 8/26/14.
 */
public class PinDropper {
    private final GoogleMap googleMap; // Might be null if Google Play services APK is not available.
    private final LocationManager locationManager;
    private final LocationClient locationClient;
    private volatile boolean locationClientConnected = false;
    private volatile Location lastBlueToothDisconnectLocation = null;


    public PinDropper(GoogleMap googleMap, LocationManager locationManager, LocationClient locationClient) {
        this.googleMap = googleMap;
        this.locationManager = locationManager;
        this.locationClient = locationClient;
    }

    public Location dropPinForOldBluetoothDisconnect(String title){
        if(lastBlueToothDisconnectLocation == null){
            return null;
        }
        return dropPinAt(lastBlueToothDisconnectLocation, title);
    }

    public void dropPinForBluetoothDisconnect(String title){
        this.setLastBlueToothDisconnectLocation(dropPin(title));
    }

    public Location dropPin(String title){
        if(locationClientConnected){
            return dropPinAtCurrentLocation(title);
        } else{
            return dropPinAtLastKnownLocation(title);
        }
    }

    public Location dropPinAtLastKnownLocation(String title) {
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        dropPinAt(lastKnownLocation, title);
        return lastKnownLocation;
    }

    public Location dropPinAtCurrentLocation(String title){
        dropPinAt(locationClient.getLastLocation(), title);
        return locationClient.getLastLocation();
    }

    private Location dropPinAt(Location location, String title){
        googleMap.addMarker(new MarkerOptions()
                .title(title)
                .position(new LatLng(location.getLatitude(), location.getLongitude())));
        return location;
    }

    public boolean isLocationClientConnected() {
        return locationClientConnected;
    }

    public void setLocationClientConnected(boolean locationClientConnected) {
        this.locationClientConnected = locationClientConnected;
    }

    public Location getLastBlueToothDisconnectLocation() {
        return lastBlueToothDisconnectLocation;
    }

    public void setLastBlueToothDisconnectLocation(Location lastBlueToothDisconnectLocation) {
        this.lastBlueToothDisconnectLocation = lastBlueToothDisconnectLocation;
    }
}
