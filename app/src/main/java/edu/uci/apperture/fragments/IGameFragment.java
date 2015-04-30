package edu.uci.apperture.fragments;

import edu.uci.apperture.service.IMediaListener;

/**
 * Interface to communicate between the application service and the game fragment
 * Created by Sonny on 4/19/2015.
 */
public interface IGameFragment extends IMediaListener {
    /**
     * Sets the next color for the UI to show for the next beat
     *
     * @param color
     */
    void setNextColor(int color, int circle);

    /**
     * Get Game View
     * @return
     */
    GameFragment.GameView getGameView();
}
