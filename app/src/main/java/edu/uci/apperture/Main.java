package edu.uci.apperture;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import edu.uci.apperture.fragments.ChatFragment;
import edu.uci.apperture.service.MainService;

public class Main extends ActionBarActivity implements ServiceConnection {
    private static final String TAG = Main.class.getSimpleName();
    private IMainListener.APP_STATE appState = IMainListener.APP_STATE.MAIN;
    private MainService mService;
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case REQUEST_ENABLE_BT:
                if (requestCode != Activity.RESULT_OK) {
                    Toast.makeText(this,
                            getString(R.string.bluetooth_not_enabled),
                            Toast.LENGTH_SHORT).show();
                } else {
                    //Establish connections
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ChatFragment())
                    .commit();
        }
        startService(new Intent(this, MainService.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getApplicationContext().bindService(
                new Intent(this, MainService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mService != null) {
            getApplicationContext().unbindService(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (appState) {
            case MAIN:
                stopService(new Intent(this, MainService.class));
            default:
                super.onBackPressed();
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = ((MainService.MainBinder) iBinder).getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }


}
