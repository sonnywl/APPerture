package edu.uci.apperture.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    private HashSet<IMediaListener> listeners;

    @Override
    public void onCreate() {
        super.onCreate();
        listeners = new HashSet<>(2);
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

    // Plays the song that the user selected from the UI side
    public void playSong(int rawId) {
        mediaPlayer = MediaPlayer.create(this, rawId);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.start();
        gameFragment.start();
        mediaHandler.sendEmptyMessage(MediaHandler.CHECK);
        Log.i(TAG, "Playing song " + rawId);

    }

    int flag1 = 0;
    int flag2 = 0;
    int flag = 0;

    // Decisions to the user's interaction here
    public void notifyOnClick(int btnGame) {

        if (gameFragment != null) {
            // TODO either pause or notify the UI to pause
            // Need to check against the music and the beat time

            switch (btnGame) {
                case R.id.btn_game_bottom:
                    if (gameFragment.getGameView().getColor(2) != Color.GRAY) {
                        flag1 = 0;
                        gameFragment.setNextColor(Color.GRAY, 2);
                        if (!this.getMediaPlayer().isPlaying() &&
                                this.getMediaPlayer().getCurrentPosition() < this.getMediaPlayer().getDuration() - 1300)
                            this.togglePlay();
                    }
                    if (gameFragment.getGameView().getColor(0) != Color.GRAY) {
                        flag1 = 1;
                        flag = flag1 + flag2;
                        if (flag > 1) {
                            flag = 0;
                            gameFragment.setNextColor(Color.GRAY, 0);
                            if (!this.getMediaPlayer().isPlaying() &&
                                    this.getMediaPlayer().getCurrentPosition() < this.getMediaPlayer().getDuration() - 1300)
                                this.togglePlay();

                        }
                    }
                    break;
                case R.id.btn_game_top:
                    if (gameFragment.getGameView().getColor(1) != Color.GRAY) {
                        flag2 = 0;
                        gameFragment.setNextColor(Color.GRAY, 1);
                        if (!this.getMediaPlayer().isPlaying() &&
                                this.getMediaPlayer().getCurrentPosition() < this.getMediaPlayer().getDuration() - 1300)
                            this.togglePlay();
                    }
                    if (gameFragment.getGameView().getColor(0) != Color.GRAY) {
                        flag2 = 1;
                        flag = flag1 + flag2;
                        if (flag > 1) {
                            flag = 0;
                            gameFragment.setNextColor(Color.GRAY, 0);
                            if (!this.getMediaPlayer().isPlaying() &&
                                    this.getMediaPlayer().getCurrentPosition() < this.getMediaPlayer().getDuration() - 1300)
                                this.togglePlay();
                        }
                    }
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
      /*  AlertDialog.Builder builder1 = new AlertDialog.Builder(this.getApplicationContext());
        builder1.setMessage("Yay you did it!!!");
        builder1.setCancelable(true);
        builder1.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();*/
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
    class MediaHandler extends Handler {
        static final int CHECK = 0;

        private WeakReference<MainService> ref;
        private Thread messageThread;

        public MediaHandler(MainService service) {
            ref = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(ref.get());
            int p1Color = mPreferences.getInt("PlayerOneColor", 0xFF458B00);
            int p2Color = mPreferences.getInt("PlayerTwoColor", Color.BLUE);
            switch (msg.what) {
                case CHECK:
                    Log.i(TAG, "Thread started");
                    messageThread = new Thread(new MediaRunnable(ref.get(), p1Color, p2Color));
                    messageThread.start();
                    break;
            }
        }
    }

    class MediaRunnable implements Runnable {
        final MainService mainService;
        final int colorP1, colorP2;
        Random r = new Random();
        int i = 0;

        public MediaRunnable(MainService mService, int color1, int color2) {
            this.mainService = mService;
            this.colorP1 = color1;
            this.colorP2 = color2;
        }

        @Override
        public void run() {
            try {
                while (mainService.getMediaPlayer().isPlaying()) {
                    i = r.nextInt(3) + 1;
                    switch (i) {
                        case 1:
                            mainService.gameFragment.setNextColor(colorP1, 1);
                            mainService.gameFragment.setNextColor(Color.GRAY, 2);
                            mainService.gameFragment.setNextColor(Color.GRAY, 0);
                            break;
                        case 2:
                            mainService.gameFragment.setNextColor(colorP2, 2);
                            mainService.gameFragment.setNextColor(Color.GRAY, 0);
                            mainService.gameFragment.setNextColor(Color.GRAY, 1);

                            break;
                        case 3:
                            mainService.gameFragment.setNextColor(0xff34a238, 0);
                            mainService.gameFragment.setNextColor(Color.GRAY, 1);
                            mainService.gameFragment.setNextColor(Color.GRAY, 2);
                            break;
                        default:

                    }
                    try {
                        TimeUnit.MILLISECONDS.sleep(1300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (gameFragment.getGameView().getColor(1) != Color.GRAY || gameFragment.getGameView().getColor(2) != Color.GRAY || gameFragment.getGameView().getColor(0) != Color.GRAY) {
                        if (mainService.getMediaPlayer().getCurrentPosition() < mainService.getMediaPlayer().getDuration() - 1300)
                            this.mainService.togglePlay();
                    }

                }
                mediaHandler.sendEmptyMessageAtTime(MediaHandler.CHECK, 600);

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
