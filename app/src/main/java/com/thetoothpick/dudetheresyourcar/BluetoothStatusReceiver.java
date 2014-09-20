package com.thetoothpick.dudetheresyourcar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by frank on 8/26/14.
 */
public class BluetoothStatusReceiver extends BroadcastReceiver {

    private final Finder finder;

    private final GoogleMap googleMap;

    public BluetoothStatusReceiver(Finder finder, GoogleMap googleMap) {
        this.finder = finder;
        this.googleMap = googleMap;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if (action == null){
            return;
        }
        if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
            finder.find();
            String title = "Dude there's your car?";
            googleMap.addMarker(new MarkerOptions()
                    .title(title)
                    .position(new LatLng(finder.cached().getLatitude(), finder.cached().getLongitude())));
        }
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            Toast.makeText(context, "bluetooth connected", Toast.LENGTH_SHORT).show();
        }

    }


    public void registerTo(FragmentActivity fragmentActivity) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            fragmentActivity.startActivityForResult(enableIntent, 1);
        }
        IntentFilter aclDisconnectIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        IntentFilter aclDisconnectRequestedIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter aclConnectedIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        fragmentActivity.registerReceiver(this, aclDisconnectIntent);
        fragmentActivity.registerReceiver(this, aclDisconnectRequestedIntent);
        fragmentActivity.registerReceiver(this, aclConnectedIntent);
    }

    //TODO: why does this still leak stuff when called?
    public void close(FragmentActivity fragmentActivity){
        fragmentActivity.unregisterReceiver(this);
    }
}
