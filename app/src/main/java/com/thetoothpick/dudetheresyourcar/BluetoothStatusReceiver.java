package com.thetoothpick.dudetheresyourcar;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by frank on 8/26/14.
 */
public class BluetoothStatusReceiver extends BroadcastReceiver{

    private PinDropper pinDropper;

    public BluetoothStatusReceiver(){
    }

    public BluetoothStatusReceiver(PinDropper pinDropper){
        this.pinDropper = pinDropper;
    }

    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        if (action == null){
            return;
        }
        if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
            pinDropper.dropPinForBluetoothDisconnect("Dude, there's your car?");
        }
        if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            Toast.makeText(context, "bluetooth connected", Toast.LENGTH_SHORT).show();
        }

    }

}
