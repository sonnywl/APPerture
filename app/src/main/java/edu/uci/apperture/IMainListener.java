package edu.uci.apperture;

/**
 * Interface to communicate between main activity and the fragments
 * Created by Sonny on 4/13/2015.
 */
public interface IMainListener {
    public enum APP_STATE {
        CAMERA,
        MAIN
    }

    /**
     * Notifies the activity which fragment or activity to move to
     * State machine equivalent
     *
     * @param moveTo
     */
    void notifyTransition(APP_STATE moveTo);

    /**
     * Notifies the activity the int data that is passed from
     *
     * @param fromState
     * @param data
     */
    void notifyDataChanged(APP_STATE fromState, int data);
}
