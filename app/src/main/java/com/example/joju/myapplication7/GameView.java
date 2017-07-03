package com.example.joju.myapplication7;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import static java.lang.Math.abs;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    int score = 0;
    int subcount = 0;
    int note_speed = 70;
    int clicked_buttonNum = -1;
    RythmGameActivity rythmGameActivity;

    static public ArrayList<GameData> dataQueue = new ArrayList<>();

    private SurfaceThread surfacethread;

    MediaPlayer mPlayer;
    MediaPlayer mPlayer2;

    Visualizer mVisualizer;

    float line_bottom;
    float line_top;

    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GameView(final Context context, RythmGameActivity rythmGameActivity, Uri _musicuri) {
        super(context);

        this.rythmGameActivity = rythmGameActivity;
        paint.setColor(Color.BLACK);
        paint.setTextSize(30);

        mPlayer = MediaPlayer.create(context, _musicuri);     //미리 재생해서 파형 분석용 플레이어
        mPlayer2 = MediaPlayer.create(context, _musicuri);    //실제 재생되는 음악

        mPlayer.setVolume((float) 0.01, (float) 0.01);
        mPlayer.start();

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(context,"asdfasdfasdfasdf",Toast.LENGTH_SHORT).show();
                mPlayer2.setVolume(10, 10);
                mPlayer2.start();
            }
        });

        //재생되는 오디오의 파형을 분석해서 음역대 별로 노트를 나눔
        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {      //파형 캡처
                int postheight = -128;
                int count = 0;

                for (int i = 0; i < 1024; i++) {
                    if (abs(bytes[i] - postheight) > 100)
                        count++;
                    postheight = bytes[i];
                }
                if (abs(count - subcount) > 20) {
                    if (count > 80) {
                        node thnode = new node(1);
                        thnode.start();
                    } else if (count > 50) {
                        node thnode = new node(2);
                        thnode.start();
                    } else if (count > 20) {
                        node thnode = new node(3);
                        thnode.start();
                    }
                    if (count > 10) {
                        node thnode = new node(4);
                        thnode.start();
                    }
                }
                subcount = count;
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {   //주파수 캡처
            }
        };

        mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(captureListener, Visualizer.getMaxCaptureRate() / 4, true, false);
        mVisualizer.setEnabled(true);

        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        getHolder().addCallback(this);
        surfacethread = new SurfaceThread(getHolder(), this, rythmGameActivity);
        setFocusable(true);
    }

    class node extends Thread {
        int x = 0;
        float y = 10;
        boolean isrunning = false;

        GameData gameData = new GameData(paint, getContext());

        public node(int xx) {

            isrunning = true;
            x = xx;

            if (x == 1)
                gameData.set_x((float) 40);
            else if (x == 2)
                gameData.set_x((float) 220);
            else if (x == 3)
                gameData.set_x((float) 400);
            else if (x == 4)
                gameData.set_x((float) 580);

            gameData.set_y(y);

            dataQueue.add(gameData);
        }

        public synchronized void run() {
            try {
                while (isrunning) {
                    y = y + 20;     //노드가 내려와야 하므로 y값 증가
                    gameData.set_y(y);

                    if (y > 950) {  //노트가 사라질 위치
                        isrunning = false;
                        dataQueue.remove(dataQueue.indexOf(gameData));
                    }

                    if (x == clicked_buttonNum && y > line_top) {   //지정한 위치에서 버튼이 눌리면

                        score = score + 10;     //점수 추가
                        surfacethread.toss_score(score);    //화면에서 계속 바뀌게될 쓰레드에 점수 갱신
                        isrunning = false;  //노드를 사라지게 하기위해
                        surfacethread.matched[clicked_buttonNum - 1] = true;

                        dataQueue.remove(dataQueue.indexOf(gameData));
                    }
                    try {
                        Thread.sleep(note_speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
//                Toast.makeText(MainActivity.this, "스레드 종료", LENGTH_SHORT).show();
            }
        }
    }

    public int getscore() {
        return score;
    }

    public void onDraw(Canvas canvas) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        surfacethread.setrunning(true);
        surfacethread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        boolean retry = true;
        surfacethread.setrunning(false);

        mPlayer.stop();
        mPlayer2.stop();

        while (retry) {
            try {
                surfacethread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
