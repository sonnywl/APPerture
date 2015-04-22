package edu.uci.apperture.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import edu.uci.apperture.R;

/**
 * About Us Page
 * Created by Sonny on 4/21/2015.
 */
public class AboutDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        return new AlertDialog.Builder(getActivity())
                .setView(inflater.inflate(R.layout.about, null))
                .create();
    }
}
