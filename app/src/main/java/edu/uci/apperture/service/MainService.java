package edu.uci.apperture.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;

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
    private static MediaHandler mediaHandler;
    private IGameFragment gameFragment;

    private int currentPostion;
    private int songDuration;
    private HashSet<IMediaListener> listeners;

    @Override
    public void onCreate() {
        super.onCreate();
        listeners = new HashSet<IMediaListener>(2);
        dbManager = new DatabaseManager(this);
        mediaPlayer = new MediaPlayer();
        mediaHandler = new MediaHandler(this);
    }

    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

    public void setCurrentPostion(int pos) {
        currentPostion = pos;
        gameFragment.setCurrentProgress(currentPostion);
    }

    public int getCurrentDuration() {
        return currentPostion;
    }

    public int getSongDuration() {
        return songDuration;
    }

    // Plays the song that the user selected from the UI side
    public void playSong(int rawId) {
        mediaPlayer = MediaPlayer.create(this, rawId);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
        gameFragment.start();
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

    public void registerMediaListener(IMediaListener listener) {
        listeners.add(listener);
    }

    public void removeMediaListener(IMediaListener listener) {
        listeners.remove(listener);
    }

    public void togglePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            gameFragment.pause();
        } else {
            mediaPlayer.start();
            gameFragment.resume();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        for (IMediaListener listener : listeners) {
            listener.completed();
        }
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
    static class MediaHandler extends Handler {
        static final int CHECK = 0;

        private WeakReference<MainService> ref;

        public MediaHandler(MainService service) {
            ref = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CHECK:
                    Thread t = new Thread(new MediaRunnable(ref.get()));
                    t.start();
                    break;
            }
        }
    }

    static class MediaRunnable implements Runnable {
        final MainService mainService;

        public MediaRunnable(MainService mService) {
            this.mainService = mService;
        }

        @Override
        public void run() {
            try {
                mainService.setCurrentPostion(
                        mainService.getMediaPlayer().getCurrentPosition());
                if (mainService.getCurrentDuration() >= mainService.getSongDuration()) {
                    Log.i(TAG, "Removing CHECK messages");
                    mediaHandler.removeMessages(MediaHandler.CHECK);
                } else {
                    mediaHandler.sendEmptyMessageAtTime(MediaHandler.CHECK, 200);
                }
            } catch (Exception e) {
                // TODO shutdown mediaplayer and back out
            }
        }
    }

    public class MainBinder extends Binder {
        public MainService getService() {
            return MainService.this;
        }
    }
}
