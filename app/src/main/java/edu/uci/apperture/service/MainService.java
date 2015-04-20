package edu.uci.apperture.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import edu.uci.apperture.R;
import edu.uci.apperture.database.DatabaseManager;
import edu.uci.apperture.fragments.IGameFragment;

/**
 * Application service for running background task
 * Created by Sonny on 4/12/2015.
 */
public class MainService extends Service implements MediaPlayer.OnCompletionListener {
    private static final String TAG = MainService.class.getSimpleName();
    private Binder mBinder = new MainBinder();
    private DatabaseManager dbManager;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    private IGameFragment gameFragment;

    @Override
    public void onCreate() {
        super.onCreate();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        dbManager = new DatabaseManager(this);
        mediaPlayer = new MediaPlayer();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    // Plays the song that the user selected from the UI side
    public void playSong(String song) {

    }

    // Decisions to the user's interaction here
    public void notifyOnClick(int btnGame) {
        if (gameFragment != null) {
            Log.i(TAG, "Hello button " + btnGame);
            // TODO either pause or notify the UI to pause
            // Need to check against the music and the beat time
            switch (btnGame) {
                case R.id.btn_game_bottom:
                    gameFragment.setNextColor(Color.GREEN);
                    break;
                case R.id.btn_game_top:
                    gameFragment.setNextColor(Color.BLUE);
                    break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        gameFragment.completed();
    }

    public void shutdown() {
        mediaPlayer.release();
    }

    public void setGameFragment(IGameFragment frag) {
        gameFragment = frag;
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
}
