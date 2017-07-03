package com.example.joju.myapplication7;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RythmGameActivity extends AppCompatActivity {

    ScoreDB scoreDB;
    int score;

    Clap clap;
    Check check;
    View.OnClickListener buttonlistener;

    Bitmap bitmap;

    GameView gameview;

    LinearLayout liner;

    int button_speed = 200;

    MenuItem item1;
    int max = 0;

    String userName;
    CustomDialog dlg;

    double lastLevel = 0;
    int bufferSize;
    AudioRecord audio;

    Animation animation_image;
    Animation animation_line1;
    Animation animation_line2;
    Animation animation_line3;
    Animation animation_line4;

    Button button1;
    Button button2;
    Button button3;
    Button button4;

    TextView linetext1;
    TextView linetext2;
    TextView linetext3;
    TextView linetext4;

    EditText nameEditText;

    ImageView imageView1;
    ImageView imageView2;
    ImageView imageView3;
    ImageView imageView4;

    FrameLayout frame;

    boolean clapmode = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_rythm_game);

        Intent intent = getIntent();
        int musicid = Integer.parseInt(((DataClass) intent.getExtras().get("game_music")).getMusicID());
        final Uri musicURI = Uri.withAppendedPath(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicid);

        scoreDB = new ScoreDB(getApplicationContext(), "RESULT.db", null, 1);
        max = scoreDB.getMax();

        gameview = new GameView(RythmGameActivity.this, this, musicURI);

        frame = (FrameLayout) findViewById(R.id.gameview_frame);
        frame.addView(gameview, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        gameview.setZOrderOnTop(false);

        linetext1 = (TextView) findViewById(R.id.linetext1);
        linetext2 = (TextView) findViewById(R.id.linetext2);
        linetext3 = (TextView) findViewById(R.id.linetext3);
        linetext4 = (TextView) findViewById(R.id.linetext4);

        //점수를 입력받을 EditText창 만들기
        nameEditText = (EditText) findViewById(R.id.nameEditText);

        linetext1.setBackgroundColor(Color.argb(255, 130, 255, 255));
        linetext2.setBackgroundColor(Color.argb(255, 130, 255, 255));
        linetext3.setBackgroundColor(Color.argb(255, 130, 255, 255));
        linetext4.setBackgroundColor(Color.argb(255, 130, 255, 255));

        linetext1.setAlpha(0);
        linetext2.setAlpha(0);
        linetext3.setAlpha(0);
        linetext4.setAlpha(0);

        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button1.setAlpha(0);
        button2.setAlpha(0);
        button3.setAlpha(0);
        button4.setAlpha(0);

        bitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher);

        imageView1 = (ImageView) findViewById(R.id.imageView1);
        imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView3 = (ImageView) findViewById(R.id.imageView3);
        imageView4 = (ImageView) findViewById(R.id.imageView4);

        imageView1.setImageBitmap(bitmap);
        imageView2.setImageBitmap(bitmap);
        imageView3.setImageBitmap(bitmap);
        imageView4.setImageBitmap(bitmap);

        liner = (LinearLayout) findViewById(R.id.liner);

        buttonThread bth = new buttonThread();
        bth.start();

        buttonlistener = new View.OnClickListener() {
            @Override
            public synchronized void onClick(View view) {
                if (R.id.button1 == view.getId()) {
                    gameview.clicked_buttonNum = 1;
                    line_color_on(gameview.clicked_buttonNum);
                } else if (R.id.button2 == view.getId()) {
                    gameview.clicked_buttonNum = 2;
                    line_color_on(gameview.clicked_buttonNum);
                } else if (R.id.button3 == view.getId()) {
                    gameview.clicked_buttonNum = 3;
                    line_color_on(gameview.clicked_buttonNum);
                } else if (R.id.button4 == view.getId()) {
                    gameview.clicked_buttonNum = 4;
                    line_color_on(gameview.clicked_buttonNum);
                }

                gameview.line_top = liner.getTop();
                gameview.line_bottom = liner.getBottom();
            }
        };

        button1.setOnClickListener(buttonlistener);
        button2.setOnClickListener(buttonlistener);
        button3.setOnClickListener(buttonlistener);
        button4.setOnClickListener(buttonlistener);

        animation_image = AnimationUtils.loadAnimation(RythmGameActivity.this, R.anim.animation_bitmap);

        animation_line1 = AnimationUtils.loadAnimation(RythmGameActivity.this, R.anim.animation_linetext);
        animation_line2 = AnimationUtils.loadAnimation(RythmGameActivity.this, R.anim.animation_linetext);
        animation_line3 = AnimationUtils.loadAnimation(RythmGameActivity.this, R.anim.animation_linetext);
        animation_line4 = AnimationUtils.loadAnimation(RythmGameActivity.this, R.anim.animation_linetext);

        int sampleRate = 8000;
        try {
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        } catch (Exception e) {
            android.util.Log.e("TrackingFlow", "Exception", e);
        }
        audio.startRecording();

        check = new Check();
        check.isplaying = true;
        check.start();

        clap = new Clap();
        clap.isplaying = true;
        clap.start();
//        Toast.makeText(getApplicationContext(), "oncreate", Toast.LENGTH_SHORT).show();
    }


    View.OnClickListener okListener = new View.OnClickListener() {
        public void onClick(View v) {
//            Toast.makeText(getApplicationContext(), "OK버튼 클릭", Toast.LENGTH_SHORT).show();
            dlg.dismiss();
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
//            Toast.makeText(getApplicationContext(), "cancel버튼 클릭", Toast.LENGTH_SHORT).show();
            dlg.dismiss();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_score, menu);
        item1 = menu.findItem(R.id.current_score);
        item1.setTitle("최고점 : " + max);
//        Toast.makeText(this, "createoption", LENGTH_SHORT).show();
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.current_score) {
            dlg = new CustomDialog(RythmGameActivity.this, okListener, cancelListener);
            dlg.show();
        } else if (id == R.id.clap) {
            if (!clapmode) {
                item.setTitle("취소");
                clapmode = true;
                button_speed = 500;
//                Toast.makeText(this, "박수 모드 ON", LENGTH_SHORT).show();
                return true;
            } else if (clapmode) {
                item.setTitle("박수");
                clapmode = false;
//                Toast.makeText(this, "박수 모드 OFF", LENGTH_SHORT).show();
                return true;
            }
        } else if(id == R.id.save_score) {
            AlertDialog.Builder nameDialog = new AlertDialog.Builder(RythmGameActivity.this);

            nameDialog.setTitle("점수를 등록하시겠습니까?");
            nameDialog.setMessage("이름을 적어주세요.");

            final EditText nameEdit = new EditText(RythmGameActivity.this);
            nameDialog.setView(nameEdit);


            nameDialog.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    userName = nameEdit.getText().toString();
                    score = gameview.getscore();
                    scoreDB.insert("insert into ScoreTBL values(null, '"+userName+"', "+score+");");
                    dialog.dismiss();
                }
            });

            nameDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            nameDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

    public class CustomDialog extends Dialog {
        View.OnClickListener oklistenr;
        View.OnClickListener cancelListener;

        protected CustomDialog(Context context, View.OnClickListener oklistenr, View.OnClickListener cancelListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.oklistenr = oklistenr;
            this.cancelListener = cancelListener;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;    //배경 액티비티를 어둡게
            lpWindow.dimAmount = 0.8f;  //어둡게 하는 정도
            getWindow().setAttributes(lpWindow);    //속성적용
            setContentView(R.layout.score_chart_dialog);    //스코어 차트 다이얼로그를 뷰에 띄움

            Button button_ok = (Button) findViewById(R.id.button_ok);

            button_ok.setOnClickListener(oklistenr);

            TextView maintext = (TextView) findViewById(R.id.maintext);

            TextView tvResult = (TextView) findViewById(R.id.tvResult);

            maintext.setText("역대 스코어");
            tvResult.setText(scoreDB.getData());

        }
    }

    class Clap extends Thread {

        boolean isplaying = false;
        Handler han = new Handler(Looper.getMainLooper()) {
            public synchronized void handleMessage(Message msg) {
                if (clapmode == true && lastLevel > 30) {
//                    Toast.makeText(getApplicationContext(), "assss", Toast.LENGTH_SHORT).show();
                    buttonlistener.onClick(button1);
                    buttonlistener.onClick(button2);
                    buttonlistener.onClick(button3);
                    buttonlistener.onClick(button4);
                }
            }
        };

        public void run() {
            while (isplaying) {
//                Toast.makeText(getApplicationContext(), "aaaaa", Toast.LENGTH_SHORT).show();
                readAudioBuffer();
                han.sendEmptyMessage(0);
            }
        }
    }

    class Check extends Thread {
        boolean isplaying = false;
        Handler han = new Handler(Looper.getMainLooper()) {
            public synchronized void handleMessage(Message msg) {
                if (animation_line1.hasEnded())
                    linetext1.setAlpha(0);

                if (animation_line2.hasEnded())
                    linetext2.setAlpha(0);

                if (animation_line3.hasEnded())
                    linetext3.setAlpha(0);

                if (animation_line4.hasEnded())
                    linetext4.setAlpha(0);
            }
        };

        public void run() {
            while (isplaying) {
//                readAudioBuffer();
                han.sendEmptyMessage(0);
            }
        }
    }

    private void readAudioBuffer() {    //박수 인식 모드에서 사용할 소리 크기 인식

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null) {
// Sense the voice…
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) {
                    sumLevel += buffer[i];
                }
                lastLevel = Math.abs((sumLevel / bufferReadResult));    //인식될 최소 소리 크기
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void line_color_on(int _clicked_buttonNum) {
        if (_clicked_buttonNum == 1) {
            linetext1.setAlpha((float) 0.6);
            linetext1.startAnimation(animation_line1);
        }
        if (_clicked_buttonNum == 2) {
            linetext2.setAlpha((float) 0.6);
            linetext2.startAnimation(animation_line2);
        }
        if (_clicked_buttonNum == 3) {
            linetext3.setAlpha((float) 0.6);
            linetext3.startAnimation(animation_line3);
        }
        if (_clicked_buttonNum == 4) {
            linetext4.setAlpha((float) 0.6);
            linetext4.startAnimation(animation_line4);
        }
    }

    public class buttonThread extends Thread {
        public synchronized void run() {

            while (true) {
                if (gameview.clicked_buttonNum != -1) {
                    try {
                        Thread.sleep(button_speed);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    gameview.clicked_buttonNum = -1;
                }
            }
        }
    }

    protected void onPause() {
        super.onPause();

//        Toast.makeText(this, "main_pause", LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();

        check.isplaying = false;
        clap.isplaying = false;
//        mPlayer.stop();
//        mPlayer2.stop();
//        Toast.makeText(this, "main_stop", LENGTH_SHORT).show();
    }

    protected void onDestroy() {

        super.onDestroy();
        // mPlayer.pause();
        //  mPlayer.release();
//        Toast.makeText(this, "rythmgame_destroy", LENGTH_SHORT).show();
    }
}
