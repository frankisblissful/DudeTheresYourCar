package com.thetoothpick.dudetheresyourcar;

import android.bluetooth.BluetoothAdapter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.LocationClient;

import static android.location.Criteria.ACCURACY_HIGH;
import static android.location.Criteria.POWER_HIGH;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by frank on 9/14/14.
 */
public final class BluetoothFinder implements Finder {

    private final BluetoothAdapter bluetoothAdapter;

    private final LocationManager locationManager;

    private final LocationClient locationClient;

    private volatile Location lastBluetoothDisconnectLocation = null;

    public BluetoothFinder(BluetoothAdapter bluetoothAdapter, LocationManager locationManager, LocationClient locationClient) {
        this.bluetoothAdapter = checkNotNull(bluetoothAdapter);
        this.locationManager = checkNotNull(locationManager);
        this.locationClient = checkNotNull(locationClient);
    }


    public void bluetoothDisconnected(){
        setLastBluetoothDisconnectLocation(getCurrentLocation());
    }

    @Override
    public Location getCurrentLocation(){
        if (locationClient.isConnected()){
            return locationClient.getLastLocation();
        }
        return getLastKnownPosition();
    }

    public Location getLastKnownPosition() {
        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();
        criteria.setPowerRequirement(POWER_HIGH);
        criteria.setHorizontalAccuracy(ACCURACY_HIGH);
        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        return lastKnownLocation;
    }

    @Override
    public Location find() {
        if (lastBluetoothDisconnectLocation != null) {
            return lastBluetoothDisconnectLocation;
        }
        return getCurrentLocation();
    }

    @Override
    public Location cached() {
        return lastBluetoothDisconnectLocation;
    }

    public void setLastBluetoothDisconnectLocation(Location lastBluetoothDisconnectLocation) {
        this.lastBluetoothDisconnectLocation = lastBluetoothDisconnectLocation;
    }
}
