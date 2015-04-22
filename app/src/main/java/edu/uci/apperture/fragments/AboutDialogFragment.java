package edu.uci.apperture.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import edu.uci.apperture.R;

/**
 * About Us Page
 * Created by Sonny on 4/21/2015.
 */
public class AboutDialogFragment extends DialogFragment implements View.OnClickListener {
    private static final String TAG = AboutDialogFragment.class.getSimpleName();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.about, null);
        view.findViewById(R.id.about_share_fb).setOnClickListener(this);
        view.findViewById(R.id.about_share_twitter).setOnClickListener(this);
        view.findViewById(R.id.about_share_weebly).setOnClickListener(this);
        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .create();
    }

    @Override
    public void onClick(View view) {
        String url = "http://apperture.weebly.com/about.html";
        switch (view.getId()) {
            case R.id.about_share_fb:
                url = "http://facebook.com/apperture2015";
                break;
            case R.id.about_share_twitter:
                url = "http://twitter.com/APPerture2015";
                break;
            default:
            case R.id.about_share_weebly:
                break;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
