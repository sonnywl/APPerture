package edu.uci.apperture.fragments;

/**
 * Interface to communicate between the application service and the game fragment
 * Created by Sonny on 4/19/2015.
 */
public interface IGameFragment {


    /**
     * Notifies the game ui the current progression
     *
     * @param currPos
     */
    void setCurrentProgress(int currPos);

    /**
     * Notifies the game ui the duration of the song
     *
     * @param duration
     */
    void setTotalDuration(int duration);

    /**
     * Sets the refresh rate of the ui to focus on click
     *
     * @param speed
     */
    void setFocusSpeed(int speed);

    /**
     * Sets the next color for the UI to show for the next beat
     *
     * @param color
     */
    void setNextColor(int color);

    /**
     * Start the Game to start drawing
     */
    void start();

    /**
     * Pause the Game from drawing
     */
    void pause();

    /**
     * Resume the Game to continue drawing
     */
    void resume();

    /**
     * Notify the music finished playing
     */
    void completed();
}
