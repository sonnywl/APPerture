package edu.uci.apperture;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uci.apperture.fragments.GameFragment;
import edu.uci.apperture.fragments.ImageFragment;
import edu.uci.apperture.fragments.SongDialogFragment;
import edu.uci.apperture.service.MainService;

public class Main extends ActionBarActivity implements
        ServiceConnection,
        SongDialogFragment.SongDialogListener {
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE:
                if (resultCode == Activity.RESULT_OK && mCurrentPhotoPath != null) {

                    ImageFragment frag = (ImageFragment) getSupportFragmentManager().findFragmentByTag("Chat");
                    int width = frag.getImageView().getWidth();
                    int height = frag.getImageView().getHeight();

                    BitmapFactory.Options factoryOptions = new BitmapFactory.Options();

                    factoryOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(mCurrentPhotoPath, factoryOptions);

                    int imageWidth = factoryOptions.outWidth;
                    int imageHeight = factoryOptions.outHeight;

                    int scaleFactor = Math.min(imageWidth / width, imageHeight / height);

                    factoryOptions.inJustDecodeBounds = false;
                    factoryOptions.inSampleSize = scaleFactor;
                    factoryOptions.inPurgeable = true;
                    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, factoryOptions);
                    frag.getImageView().setImageBitmap(bitmap);
                    mCurrentPhotoPath = null;
                }
                break;

            default:
                Log.w(TAG, "Unknown Intent Completed");
        }
    }

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
    public void notifyOnSelectMusic(int pos) {
        Log.i(TAG, songs[pos]);
        switch (pos) {
            case 0:
                mService.playSong(R.raw.little_lab);
            case 1:
                mService.playSong(R.raw.row_your_boat);
            case 2:
                mService.playSong(R.raw.rain_rain_go_away);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                break;
            case R.id.action_camera:
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                // Ensure that there's a camera activity to handle the intent
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    // Create the File where the photo should go
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                        mCurrentPhotoPath = photoFile.getPath();
                    } catch (IOException ex) {
                        // Error occurred while creating the File
                    }

                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(photoFile));
                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    }
                }
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (appState) {
            case MAIN:
                mService.shutdown();
                stopService(new Intent(this, MainService.class));
            default:
                super.onBackPressed();
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "apperture_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public MainService getService() {
        return mService;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mService = ((MainService.MainBinder) iBinder).getService();
        if (gameFragment != null) {
            mService.setGameFragment(gameFragment);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        mService = null;
    }


}
