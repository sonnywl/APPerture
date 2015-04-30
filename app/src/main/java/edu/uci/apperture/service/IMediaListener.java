package edu.uci.apperture.service;

/**
 * Basic Media Playback interface
 * Created by Sonny on 4/20/2015.
 */
public interface IMediaListener {
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
