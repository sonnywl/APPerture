package edu.uci.apperture.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Main rhythm target view for users to detect when to press their button
 * <p/>
 * Created by Sonny on 4/18/2015.
 */
public class GameView extends SurfaceView implements Runnable {
    private static final String TAG = GameView.class.getSimpleName();
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
