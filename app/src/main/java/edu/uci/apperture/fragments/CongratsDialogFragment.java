package edu.uci.apperture.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Congratulations Dialog when game ends
 * Created by Sonny on 4/23/2015.
 */
public class CongratsDialogFragment extends DialogFragment {
    private static final String KEY = "MSG";

    public static CongratsDialogFragment newInstance(String msg) {
        Bundle bundle = new Bundle(1);
        bundle.putString(KEY, msg);
        CongratsDialogFragment frag = new CongratsDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString(KEY);
        return new AlertDialog.Builder(getActivity())
                .setTitle("Congrats")
                .setMessage(msg).create();
    }
}
