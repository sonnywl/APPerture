package edu.uci.apperture.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import edu.uci.apperture.Main;
import edu.uci.apperture.R;

/**
 * Main Game View
 * Created by Sonny on 4/19/2015.
 */
public class GameFragment extends Fragment implements
        View.OnClickListener,
        IGameFragment,
        Animation.AnimationListener {
    private GameView gameView;
    private int player1Color, player2Color, centerColor;

    private LinearLayout topHands;
    private LinearLayout bottomHands;
    private Animation anim;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        player1Color = mPreferences.getInt("PlayerOneColor", 0xFF458B00);
        player2Color = mPreferences.getInt("PlayerTwoColor", Color.BLUE);
        centerColor = 0xff34a238;

        gameView = new GameView(getActivity());
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.game_view);
        frameLayout.addView(gameView);
        gameView.setZOrderOnTop(true);

        topHands = (LinearLayout) view.findViewById(R.id.hands_top);
        bottomHands = (LinearLayout) view.findViewById(R.id.hands_bottom);
        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fade_out);
        anim.setAnimationListener(this);
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

    SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    int soundIds[] = new int[10];

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_game_bottom:
                ((Main) getActivity()).getService().notifyOnClick(R.id.btn_game_bottom);
                sp.play(soundIds[0], 1, 1, 1, 0, 1);
                gameView.setPlayerAlphaPressed(2);
                break;
            case R.id.btn_game_top:
                ((Main) getActivity()).getService().notifyOnClick(R.id.btn_game_top);
                sp.play(soundIds[1], 1, 1, 1, 0, 1);
                gameView.setPlayerAlphaPressed(1);
                break;
        }
    }

    public GameView getGameView() {
        return gameView;
    }

    @Override
    public void setNextColor(int color, int circle) {
        gameView.setColor(color, circle);
    }

    // Interactions from the media player from the MainService
    @Override
    public void start() {
        topHands.startAnimation(anim);
        bottomHands.startAnimation(anim);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void completed() {
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        topHands.setVisibility(View.GONE);
        bottomHands.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    public class GameView extends SurfaceView implements Runnable {
        private Thread gameThread;
        private volatile boolean isRunning = true;
        private SurfaceHolder surfaceHolder;
        private Paint mPaint, mPaint1, mPaint2, mPaint1Press, mPaint2Press;

        // Flashes
        private Shader shader1, shader2;
        // Color Alpha for three center circles
        private Paint mPaintBg, mPaint1Bg, mPaint2Bg;

        int paintStroke = 20;
        int radius = 120;
        int radius2 = 90;
        int radiusBg = 10;
        int maxAlpha = 100;
        int alphaDecreaseRate = 5;

        public GameView(Context context) {
            super(context);
            init();
        }

        private void init() {
            surfaceHolder = getHolder();
            surfaceHolder.setFormat(PixelFormat.TRANSLUCENT);

            mPaintBg = newPaint(centerColor, paintStroke);
            mPaint1Bg = newPaint(player1Color, paintStroke);
            mPaint2Bg = newPaint(player2Color, paintStroke);

            mPaint = newPaint(Color.GRAY, paintStroke);
            mPaint1 = newPaint(Color.GRAY, paintStroke);
            mPaint2 = newPaint(Color.GRAY, paintStroke);

            mPaint1Press = newPaint(player1Color, paintStroke);
            mPaint2Press = newPaint(player2Color, paintStroke);

            mPaint.setAlpha(120);
            mPaint1.setAlpha(120);
            mPaint2.setAlpha(120);

            mPaintBg.setAlpha(maxAlpha);
            mPaint1Bg.setAlpha(maxAlpha);
            mPaint2Bg.setAlpha(maxAlpha);
        }

        @Override
        public void run() {
            soundIds[0] = sp.load(this.getContext(), R.raw.bam, 1);
            soundIds[1] = sp.load(this.getContext(), R.raw.drum2, 1);

            // Initialize with canvas width and height ready
            while (true) {
                if (surfaceHolder.getSurface().isValid()) {
                    Canvas local = surfaceHolder.lockCanvas();
                    shader1 = new RadialGradient(getWidth() / 2, 0, getWidth(), player1Color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
                    shader2 = new RadialGradient(getWidth() / 2, getHeight(), getWidth(), player2Color, Color.TRANSPARENT, Shader.TileMode.CLAMP);
                    surfaceHolder.unlockCanvasAndPost(local);
                    break;
                }
            }
            while (isRunning) {
                if (!surfaceHolder.getSurface().isValid()) {
                    continue;
                }
                Canvas canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        mPaint1Press.setAlpha(updateAlpha(mPaint1Press.getAlpha(), alphaDecreaseRate));
                        mPaint2Press.setAlpha(updateAlpha(mPaint2Press.getAlpha(), alphaDecreaseRate));

                        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

                        canvas.drawCircle(getWidth() / 2, 0, getWidth() / 2, mPaint1Press);
                        canvas.drawCircle(getWidth() / 2, getHeight(), getWidth() / 2, mPaint2Press);

                        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius + radiusBg, mPaintBg);
                        canvas.drawCircle(getWidth() / 2, getHeight() / 4, radius2 + radiusBg, mPaint1Bg);
                        canvas.drawCircle(getWidth() / 2, getHeight() * 3 / 4, radius2 + radiusBg, mPaint2Bg);

                        canvas.drawCircle(getWidth() / 2, getHeight() / 2, radius, mPaint);
                        canvas.drawCircle(getWidth() / 2, getHeight() / 4, radius2, mPaint1);
                        canvas.drawCircle(getWidth() / 2, getHeight() * 3 / 4, radius2, mPaint2);
                    }

                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }

        public int updateAlpha(int alphaValue, int decreaseRate) {
            if (alphaValue > 0) {
                return alphaValue - decreaseRate;
            }
            return alphaValue;
        }

        public void setPlayerAlphaPressed(int player) {
            switch (player) {
                case 1:
                    mPaint1Press.setAlpha(maxAlpha);
                    break;
                case 2:
                    mPaint2Press.setAlpha(maxAlpha);
                    break;
            }
        }

        public void setColor(int color, int circle) {
            switch (circle) {
                case 1:
                    mPaint1.setColor(color);
                    break;
                case 2:
                    mPaint2.setColor(color);
                    break;
                default:
                    mPaint.setColor(color);
            }
        }

        public int getColor(int circle) {
            switch (circle) {
                case 1:
                    return mPaint1.getColor();
                case 2:
                    return mPaint2.getColor();
                default:
                    return mPaint.getColor();
            }
        }

        private Paint newPaint(int color, int stroke) {
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setStrokeWidth(stroke);
            p.setColor(color);
            return p;
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
