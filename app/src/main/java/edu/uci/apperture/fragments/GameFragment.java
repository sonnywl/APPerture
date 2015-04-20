package edu.uci.apperture.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import edu.uci.apperture.Main;
import edu.uci.apperture.R;
import edu.uci.apperture.service.MainService;
import edu.uci.apperture.view.GameView;

/**
 * Main Game View
 * Created by Sonny on 4/19/2015.
 */
public class GameFragment extends Fragment implements View.OnClickListener, IGameFragment {
    private GameView gameView;
    private MainService mSerivce;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        gameView = new GameView(getActivity());
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.game_view);
        frameLayout.addView(gameView);
        view.findViewById(R.id.game_controller).bringToFront();
        view.findViewById(R.id.btn_game_bottom).setOnClickListener(this);
        view.findViewById(R.id.btn_game_top).setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        gameView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_game_bottom:
                ((Main) getActivity()).getService().notifyOnClick(R.id.btn_game_bottom);
                break;
            case R.id.btn_game_top:
                ((Main) getActivity()).getService().notifyOnClick(R.id.btn_game_top);
                break;
        }
    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public void setFocusSpeed(int speed) {

    }

    @Override
    public void setNextColor(int color) {

    }

    // Interactions from the media player from the MainService
    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void completed() {

    }

}
