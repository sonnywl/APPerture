package edu.uci.apperture.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import edu.uci.apperture.R;

/**
 * Main chat window interaction
 * Created by Sonny on 4/13/2015.
 */
public class ChatFragment extends Fragment implements ImageFragment {
    private ImageView mImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        mImageView = (ImageView) view.findViewById(R.id.main_image);

        return view;
    }

    @Override
    public ImageView getImageView() {
        return mImageView;
    }
}
