package edu.uci.apperture;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.util.concurrent.TimeUnit;

import edu.uci.apperture.fragments.GameFragment;
import edu.uci.apperture.fragments.SongDialogFragment;
import edu.uci.apperture.service.IMediaListener;
import edu.uci.apperture.service.MainService;

public class Main extends ActionBarActivity implements
        ServiceConnection,
        SongDialogFragment.SongDialogListener,
        IMediaListener {
    private static final String TAG = Main.class.getSimpleName();
    private IMainListener.APP_STATE appState = IMainListener.APP_STATE.MAIN;
    private MainService mService;
    private GameFragment gameFragment;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private String[] songs;
    /**
     * File URI for saving the taken image
     */
    private String mCurrentPhotoPath;
    private boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        songs = getResources().getStringArray(R.array.songs);
        if (savedInstanceState == null) {
            gameFragment = new GameFragment();
            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.container, new ChatFragment(), "Chat")
                    .add(R.id.container, gameFragment, "Game")
                    .commit();

            DialogFragment frag = SongDialogFragment.newInstance(
                    songs, getString(R.string.select_music));
            frag.show(getSupportFragmentManager(), "SongDialog");
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
            mService.removeMediaListener(this);
            getApplicationContext().unbindService(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int menuItem = 0; menuItem < menu.size(); menuItem++) {
            if (menu.getItem(menuItem).getItemId() == R.id.action_play) {
                if (isPlaying) {
                    menu.getItem(menuItem).setIcon(R.drawable.ic_action_pause);
                } else {
                    menu.getItem(menuItem).setIcon(R.drawable.ic_action_play_dark);
                }
            }
        }
        return true;
    }

    @Override
    public void notifyOnSelectMusic(int pos) {
        Log.i(TAG, songs[pos]);
        switch (pos) {
            case 0:
                mService.shutdown();
                mService.playSong(R.raw.little_lab);
                break;
            case 1:
                mService.shutdown();
                mService.playSong(R.raw.row_your_boat);
                break;
            case 2:
                mService.shutdown();
                mService.playSong(R.raw.rain_rain_go_away);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_play:
                if (mService != null) {
                    mService.togglePlay();
                    isPlaying = !isPlaying;
                }
            default:
        }
        invalidateOptionsMenu();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (appState) {
            case MAIN:
                mService.shutdown();
                stopService(new Intent(this, MainService.class));
                finish();
            default:
                super.onBackPressed();
        }
    }

    public MainService getService() {
        return mService;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = ((MainService.MainBinder) iBinder).getService();
        mService.registerMediaListener(this);
        if (gameFragment != null) {
            mService.setGameFragment(gameFragment);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }

    @Override
    public void start() {
        isPlaying = true;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void completed() {
        isPlaying = false;
        MediaPlayer mp = MediaPlayer.create(this, R.raw.yay);
        mp.start();
        try {
            TimeUnit.MILLISECONDS.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mp.stop();
        mp.release();
        invalidateOptionsMenu();
        DialogFragment frag = SongDialogFragment.newInstance(
                songs, getString(R.string.select_music));
        frag.show(getSupportFragmentManager(), "SongDialog");

        Log.i(TAG, "Completed notified");
    }
}
