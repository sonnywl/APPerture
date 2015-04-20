package edu.uci.apperture.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

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
    private MediaHandler mediaHandler;
    private IGameFragment gameFragment;

    private int currentPostion;
    private int songDuration;

    @Override
    public void onCreate() {
        super.onCreate();
        dbManager = new DatabaseManager(this);
        mediaPlayer = new MediaPlayer();
        mediaHandler = new MediaHandler();
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    // Plays the song that the user selected from the UI side
    public void playSong(int rawId) {
        mediaPlayer = MediaPlayer.create(this, rawId);
        mediaPlayer.start();
        songDuration = mediaPlayer.getDuration();
        mediaHandler.sendEmptyMessage(MediaHandler.CHECK);
        Log.i(TAG, "Playing song " + rawId);
    }

    // Decisions to the user's interaction here
    public void notifyOnClick(int btnGame) {
        if (gameFragment != null) {
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

    public void togglePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else {
            mediaPlayer.start();
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


    // Handler class to check for media player's progress
    class MediaHandler extends Handler {
        static final int CHECK = 0;

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK:
                    Thread t = new Thread(new MediaRunnable());
                    t.start();
                    break;
            }
        }
    }

    class MediaRunnable implements Runnable {

        @Override
        public void run() {
            currentPostion = mediaPlayer.getCurrentPosition();
            gameFragment.setCurrentProgress(currentPostion);
            mediaHandler.sendEmptyMessageAtTime(MediaHandler.CHECK, 200);
            if (currentPostion >= songDuration) {
                Log.i(TAG, "Removing CHECK messages");
                mediaHandler.removeMessages(MediaHandler.CHECK);
            }
        }
    }

    public class MainBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }
}
