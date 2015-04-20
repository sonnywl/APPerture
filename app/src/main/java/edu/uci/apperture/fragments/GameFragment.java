package edu.uci.apperture.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import edu.uci.apperture.Main;
import edu.uci.apperture.R;
import edu.uci.apperture.service.MainService;

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
        gameView.setColor(color);
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

    public class GameView extends SurfaceView implements Runnable {
        private static final int refreshRate = 100;
        private Thread gameThread;
        private volatile boolean isRunning = true;
        private SurfaceHolder surfaceHolder;
        private Paint mPaint;

        public GameView(Context context) {
            super(context);
            init();
        }

        private void init() {
            surfaceHolder = getHolder();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(Color.BLUE);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(20);
        }

        @Override
        public void run() {
            boolean increasing = false;
            int minRadius = 50;
            int maxRadius = 220;
            int radius = 120;

            while (isRunning) {
                if (!surfaceHolder.getSurface().isValid()) {
                    continue;
                }
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        canvas.drawColor(Color.WHITE);
                        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
                    }

                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
                radius = increasing ? (radius + 1) : (radius - 1);
                if (radius <= minRadius) {
                    increasing = true;
                } else if (radius >= maxRadius) {
                    increasing = false;
                }
            }
        }

        public void setColor(int color) {
            mPaint.setColor(color);
        }


        public void resume() {
            isRunning = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        public void pause() {
            boolean retry = true;
            isRunning = false;
            while (retry) {
                try {
                    gameThread.join();
                    retry = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
