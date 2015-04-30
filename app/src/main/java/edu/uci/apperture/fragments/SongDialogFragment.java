package edu.uci.apperture.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * List dialog to show a list of available music selection
 * Created by Sonny on 4/19/2015.
 */
public class SongDialogFragment extends DialogFragment {
    private static final String KEY = "SONGS";
    private static final String TITLE = "TITLE";

    public static SongDialogFragment newInstance(String[] songs, String title) {
        SongDialogFragment frag = new SongDialogFragment();
        Bundle bundle = new Bundle(2);
        bundle.putStringArray(KEY, songs);
        bundle.putString(TITLE, title);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] songs = getArguments().getStringArray(KEY);
        final String title = getArguments().getString(TITLE);
        Dialog dl = new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setItems(songs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int pos) {
                        if (getActivity() instanceof SongDialogListener) {
                            ((SongDialogListener) getActivity()).notifyOnSelectMusic(pos);
                        }
                    }
                })
                .create();
        dl.setCancelable(false);
        dl.setCanceledOnTouchOutside(false);
        return dl;
    }

    public interface SongDialogListener {
        void notifyOnSelectMusic(int pos);
    }
}
