package com.thetoothpick.dudetheresyourcar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {

    private GoogleMap googleMap; // Might be null if Google Play services APK is not available.

    private static final String TAG = "MapsActivity";

    private BluetoothFinder bluetoothFinder = null;

    private BluetoothStatusReceiver bluetoothStatusReceiver = null;

    private LocationManager locationManager;
    private LocationClient locationClient;


    private static final String lastBluetoothDisconnectLocationKey = "lastBluetoothDisconnectLocationKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded(savedInstanceState);
        bluetoothStatusReceiver = new BluetoothStatusReceiver(bluetoothFinder, googleMap);
        bluetoothStatusReceiver.registerTo(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #googleMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     *
     * @param savedInstanceState
     */
    private void setUpMapIfNeeded(Bundle savedInstanceState) {
        // Do a null check to confirm that we have not already instantiated the map.

        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            googleMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        }
        if (locationClient == null) {
            locationClient = new LocationClient(this, this, this);
        }
        locationClient.connect();
        // Check if we were successful in obtaining the map.

        if (bluetoothFinder == null){
            bluetoothFinder = new BluetoothFinder(BluetoothAdapter.getDefaultAdapter(), locationManager, locationClient);
            if (savedInstanceState != null){
                bluetoothFinder.setLastBluetoothDisconnectLocation((Location) savedInstanceState.getParcelable(lastBluetoothDisconnectLocationKey));
            }
        }
        if (googleMap != null) {
            googleMap.setMyLocationEnabled(true);
            setUpMap();
        }
    }




    /**
     * This is where we can add markers or lines, add listeners or move the camera.
     * <p/>
     * This should only be called once and when we are sure that {@link #googleMap} is not null.
     */
    private void setUpMap() {
        float zoom = 15;
        Location initialPin = bluetoothFinder.cached();
        if (bluetoothFinder.cached() != null) {
            dropPinAt(initialPin, "Car Location?");//TODO: turn into resource?
        } else{
            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();
            // Getting the name of the best provider
            String provider = LocationManager.GPS_PROVIDER;
            // Getting Current Location
            initialPin = locationManager.getLastKnownLocation(provider);
            dropPinAt(locationManager.getLastKnownLocation(provider), "Start Location"); //TODO: turn into resource?
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(getLatLngForLocation(initialPin), zoom);
        googleMap.animateCamera(cameraUpdate);
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putParcelable(lastBluetoothDisconnectLocationKey, bluetoothFinder.cached());
    }

    @Override
    protected void onStop() {
        locationClient.disconnect();
        bluetoothStatusReceiver.close(this);
        super.onStop();
    }

    @Override
    public void onConnected(Bundle dataBundle) {
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected from google maps.",
                Toast.LENGTH_SHORT).show();
    }

    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        Toast.makeText(this, "Connection failed. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    private Location dropPinAt(Location location, String title){
        googleMap.addMarker(new MarkerOptions()
                .title(title)
                .position(new LatLng(location.getLatitude(), location.getLongitude())));
        return location;
    }

    private static LatLng getLatLngForLocation(Location location) {
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
}
