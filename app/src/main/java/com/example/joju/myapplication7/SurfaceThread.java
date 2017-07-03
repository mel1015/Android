package com.example.joju.myapplication7;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

public class SurfaceThread extends Thread {
    int score = 0;

    public void toss_score(int score) {
        this.score = score;
    }

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private SurfaceHolder msurfaceHolder;
    private GameView mgameView;
    private boolean isrunning = false;
    RythmGameActivity rythmGameActivity;

    boolean[] matched = {false, false, false, false};

    public SurfaceThread(SurfaceHolder surfaceHolder, GameView gameView, RythmGameActivity rythmGameActivity) {
        this.rythmGameActivity = rythmGameActivity;
        msurfaceHolder = surfaceHolder;
        mgameView = gameView;

        paint.setColor(Color.BLUE);
        paint.setTextSize(50);
    }

    public void setrunning(boolean isrunning) {
        this.isrunning = isrunning;
    }

    public void run() {
        Canvas _canvas;

        while (isrunning) {
            _canvas = null;
            try {
                _canvas = msurfaceHolder.lockCanvas(null);
                synchronized (msurfaceHolder) {
                    if (_canvas != null) {
                        _canvas.drawColor(Color.WHITE);
                        _canvas.drawText("" + score, 340, 200, paint);

                        if (mgameView.dataQueue != null) {
                            for (int i = 0; i < mgameView.dataQueue.size(); i++)
                                mgameView.dataQueue.get(i).drawOnCanvas(_canvas);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (_canvas != null) {
                    msurfaceHolder.unlockCanvasAndPost(_canvas);
                }
            }
        }
    }
}
