package edu.uci.apperture.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;

import java.util.Set;

/**
 * Managing Chat data between bluetooth users
 * <p/>
 * 1. Text
 * 2. Images
 * 3. Sound
 * Created by Sonny on 4/13/2015.
 */
public class BluetoothController {
    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;

    public static BluetoothController getInstance(Context context, Handler handler) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Bluetooth is not supported
            return null;
        }
        return new BluetoothController(mBluetoothAdapter, handler);
    }

    public BluetoothController(BluetoothAdapter adapter, Handler handler) {
        mBluetoothAdapter = adapter;
        mHandler = handler;
    }

    public void getDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    }

}
