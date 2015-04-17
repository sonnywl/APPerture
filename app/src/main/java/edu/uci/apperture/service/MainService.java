package edu.uci.apperture.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import edu.uci.apperture.bluetooth.BluetoothController;
import edu.uci.apperture.database.DatabaseManager;

/**
 * Application service for running background task
 * Created by Sonny on 4/12/2015.
 */
public class MainService extends Service {
    private Binder mBinder = new MainBinder();
    private BluetoothHandler mHandler = new BluetoothHandler();
    private BluetoothController mBluetoothController;
    private DatabaseManager dbManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mBluetoothController = BluetoothController.getInstance(this, mHandler);
        dbManager = new DatabaseManager(this);
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class MainBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }

    /**
     * Class that interacts with the Bluetooth Controller and the UI side
     */
    class BluetoothHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }
}
