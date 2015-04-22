package edu.uci.apperture;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import edu.uci.apperture.fragments.AboutDialogFragment;
import edu.uci.apperture.fragments.HSVColorPickerDialog;

/**
 * Main User Start Menu
 * Created by Sonny on 4/21/2015.
 */
public class Start extends ActionBarActivity implements View.OnClickListener, HSVColorPickerDialog.OnColorSelectedListener {
    private static final String TAG = Start.class.getSimpleName();
    private SharedPreferences mPreferences;
    private int playerSelection = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int p1Color = mPreferences.getInt("PlayerOneColor", 0xFF458B00);
        int p2Color = mPreferences.getInt("PlayerTwoColor", Color.BLUE);

        findViewById(R.id.btn_start_player1).setOnClickListener(this);
        findViewById(R.id.btn_start_player2).setOnClickListener(this);
        findViewById(R.id.btn_start_game).setOnClickListener(this);
        findViewById(R.id.btn_start_option).setOnClickListener(this);

        setButtonBgColor(R.id.btn_start_player1, p1Color);
        setButtonBgColor(R.id.btn_start_player2, p2Color);
        setButtonBgColor(R.id.btn_start_game, 0xff34a238);
        setButtonBgColor(R.id.btn_start_option, 0xfff6e213);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_player1:
                playerSelection = 0;
                showColorDialog(mPreferences.getInt("PlayerOneColor", Color.GREEN));
                break;
            case R.id.btn_start_player2:
                playerSelection = 1;
                showColorDialog(mPreferences.getInt("PlayerTwoColor", Color.BLUE));
                break;
            case R.id.btn_start_option:
                AboutDialogFragment dialog = new AboutDialogFragment();
                dialog.show(this.getSupportFragmentManager(), "About");
                break;
            case R.id.btn_start_game:
                startActivity(new Intent(this, Main.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_start, menu);
        // Get the menu item.
        MenuItem menuItem = menu.findItem(R.id.menu_share);
        // Get the provider and hold onto it to set/change the share intent.

        ShareActionProvider mShareActionProvider = new ShareActionProvider(this);
        MenuItemCompat.setActionProvider(menuItem, mShareActionProvider);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        mShareActionProvider.setShareIntent(sharingIntent());

        // This line chooses a custom shared history xml file. Omit the line if using
        // the default share history file is desired.
//        mShareActionProvider.setShareHistoryFileName("custom_share_history.xml");
        return super.onCreateOptionsMenu(menu);
    }

    private Intent sharingIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, "Vist our Facebook/Twitter/Weebly site!\n" +
                "Facebook: facebook.com/apperture2015\n" +
                "Twitter: twitter.com/APPerture2015\n" +
                "Weebly: apperture.weebly.com/about.html\n");

        return intent;
    }

    private void showColorDialog(int initColor) {
        AlertDialog dialog = new HSVColorPickerDialog(this, initColor, this);
        dialog.show();
    }

    private void setButtonBgColor(int resourceId, int color) {
        Drawable draw = getResources().getDrawable(R.drawable.btn_start);
        draw.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
        findViewById(resourceId).setBackground(draw);
    }

    @Override
    public void colorSelected(Integer color) {
        switch (playerSelection) {
            case 0:
                mPreferences.edit().putInt("PlayerOneColor", color).apply();
                setButtonBgColor(R.id.btn_start_player1, color);
                break;
            case 1:
                mPreferences.edit().putInt("PlayerTwoColor", color).apply();
                setButtonBgColor(R.id.btn_start_player2, color);
                break;
        }
    }
}
