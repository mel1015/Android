package com.example.joju.myapplication7;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class MusicPlayingActivity extends AppCompatActivity {
    Animation animation_button;

    Bitmap bitmap;
    int position;
    ArrayList<DataClass> datas = new ArrayList<>();
    Intent intent;

    JSONArray recent_array;
    JSONObject jObject;

    Uri musicURI;

    String current_song_name;
    String current_artist;

    MediaPlayer mp; // 음악 재생을 위한 객체
    int pos; // 재생 멈춘 시점
    SeekBar sb; // 음악 재생위치를 나타내는 시크바
    boolean isPlaying = false; // 재생중인지 확인할 변수

    class MyThread extends Thread {
        @Override
        public void run() { // 쓰레드가 시작되면 콜백되는 메서드
            // 씨크바 막대기 조금씩 움직이기 (노래 끝날 때까지 반복)
            while (isPlaying) {
                sb.setProgress(mp.getCurrentPosition());
            }
        }
    }

    private Button button_back;
    private Button bPause;
    private Button button_next;

    CustomDialog dlg;
    Add_to_list_Dialog list_dialog;

    Intent game_intent;

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
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);
            setContentView(R.layout.dialog);

            Button button_ok = (Button) findViewById(R.id.button_ok);
            Button button_cancel = (Button) findViewById(R.id.button_can);

            button_ok.setOnClickListener(oklistenr);
            button_cancel.setOnClickListener(cancelListener);
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.musicplay_activity);

        sb = (SeekBar) findViewById(R.id.seekBar);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (seekBar.getMax() == progress) {
                    bPause.setVisibility(View.VISIBLE);
                    isPlaying = false;
                    mp.stop();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isPlaying = false;
                mp.pause();
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                isPlaying = true;
                int ttt = seekBar.getProgress(); // 사용자가 움직여놓은 위치
                mp.seekTo(ttt);
                mp.start();
                new MyThread().start();
            }
        });

        button_back = (Button) findViewById(R.id.button_back);
        bPause = (Button) findViewById(R.id.button_pause);
        button_next = (Button) findViewById(R.id.button_next);

        TextView textView_musicname = (TextView) findViewById(R.id.textView_musicname);
        TextView textView_music_artist = (TextView) findViewById(R.id.textView_music_artist);
        final ImageView imageview_music = (ImageView) findViewById(R.id.imageView_music);

        animation_button = AnimationUtils.loadAnimation(MusicPlayingActivity.this, R.anim.animation_button);

        intent = getIntent();
        game_intent = intent;

        saveinlist("recent_array");

        datas = (ArrayList<DataClass>) intent.getExtras().get("current_datas");
        position = intent.getIntExtra("current_music_num", 1);
        Toast.makeText(MusicPlayingActivity.this, ((DataClass) intent.getExtras().get("current_music")).getname() + " 재생 중", Toast.LENGTH_SHORT).show();


        current_song_name = ((DataClass) intent.getExtras().get("current_music")).getname();
        current_artist = ((DataClass) intent.getExtras().get("current_music")).getArtist();

        textView_musicname.setText(current_song_name);
        textView_musicname.setSelected(true);
        textView_music_artist.setText(current_artist);

        bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(((DataClass) intent.getExtras().get("current_music")).getImgId()), getApplication()));
        imageview_music.setImageBitmap(bitmap);

        final Animation animation = AnimationUtils.loadAnimation(MusicPlayingActivity.this, R.anim.animation_rotate);
        imageview_music.startAnimation(animation);

        int musicid = Integer.parseInt(((DataClass) intent.getExtras().get("current_music")).getMusicID());
        musicURI = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, "" + musicid);

        try {
            mp = MediaPlayer.create(MusicPlayingActivity.this, musicURI);
            mp.setLooping(false); // true:무한반복
            mp.start(); // 노래 재생 시작

            int a = mp.getDuration(); // 노래의 재생시간(miliSecond)
            sb.setMax(a);// 씨크바의 최대 범위를 노래의 재생시간으로 설정
            new MyThread().start(); // 씨크바 그려줄 쓰레드 시작
            isPlaying = true; // 씨크바 쓰레드 반복 하도록
        } catch (Exception e) {
            Toast.makeText(MusicPlayingActivity.this, "파일로드 실패", Toast.LENGTH_SHORT).show();
        }

        View.OnClickListener buttonlistener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (view.getId() == R.id.button_pause) {
                    view.startAnimation(animation_button);
                    if (isPlaying) {
                        pos = mp.getCurrentPosition();
                        mp.pause(); // 일시중지
                        imageview_music.clearAnimation();
                        isPlaying = false; // 쓰레드 정지
                        bPause.setText("시작");
                    } else {
                        mp.seekTo(pos); // 일시정지 시점으로 이동
                        mp.start(); // 시작
                        isPlaying = true; // 재생하도록 flag 변경
                        imageview_music.startAnimation(animation);
                        new MyThread().start(); // 쓰레드 시작
                        bPause.setText("일시 정지");
                    }
                } else if (view.getId() == R.id.button_back) {
                    view.startAnimation(animation_button);
                    datas = (ArrayList<DataClass>) intent.getExtras().get("current_datas");

                    if (position <= 0)
                        Toast.makeText(MusicPlayingActivity.this, "이전 곡이 없습니다.", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent1 = new Intent(MusicPlayingActivity.this, MusicPlayingActivity.class);
                        intent1.putExtra("current_music_num", position - 1);
                        intent1.putExtra("current_music", datas.get(position - 1));
                        intent1.putExtra("current_datas", datas);
                        startActivity(intent1);

                        overridePendingTransition(R.anim.animation_left, R.anim.animation_hold);

                        finish();
                    }
                } else if (view.getId() == R.id.button_next) {
                    view.startAnimation(animation_button);
                    datas = (ArrayList<DataClass>) intent.getExtras().get("current_datas");

                    if (position >= datas.size() - 1)
                        Toast.makeText(MusicPlayingActivity.this, "다음 곡이 없습니다.", Toast.LENGTH_SHORT).show();
                    else {
                        Intent intent1 = new Intent(MusicPlayingActivity.this, MusicPlayingActivity.class);
                        intent1.putExtra("current_music_num", position + 1);
                        intent1.putExtra("current_music", datas.get(position + 1));
                        intent1.putExtra("current_datas", datas);
                        startActivity(intent1);

                        overridePendingTransition(R.anim.animation_slide, R.anim.animation_hold);

                        finish();
                    }
                }
            }

        };
        bPause.setOnClickListener(buttonlistener);
        button_back.setOnClickListener(buttonlistener);
        button_next.setOnClickListener(buttonlistener);
    }

    public void saveinlist(String m_str) {
        SharedPreferences pref = getSharedPreferences("music_app_data", MODE_PRIVATE);
        SharedPreferences.Editor editer = pref.edit();
        String str = pref.getString(m_str, "default");


        jObject = new JSONObject();
        recent_array = new JSONArray();
        try {

            if (!str.equals("default"))
                recent_array = new JSONArray(str);

            jObject.put("name", ((DataClass) intent.getExtras().get("current_music")).getname());
            jObject.put("artist", ((DataClass) intent.getExtras().get("current_music")).getArtist());
            jObject.put("musicID", ((DataClass) intent.getExtras().get("current_music")).getMusicID());
            jObject.put("imgId", ((DataClass) intent.getExtras().get("current_music")).getImgId());                   //int 화

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MusicPlayingActivity.this, "데이터 접근 실패", Toast.LENGTH_SHORT).show();
        }
        recent_array.put(jObject);
        editer.putString(m_str, recent_array.toString());
        Toast.makeText(MusicPlayingActivity.this, m_str + "에 저장", Toast.LENGTH_SHORT).show();
        editer.commit();
    }

    public void saveinlist(String m_str, String feeling) {
        SharedPreferences pref = getSharedPreferences("music_app_data", MODE_PRIVATE);
        SharedPreferences.Editor editer = pref.edit();
        String str = pref.getString(m_str, "default");


        jObject = new JSONObject();
        recent_array = new JSONArray();
        try {

            if (!str.equals("default"))
                recent_array = new JSONArray(str);

            jObject.put("name", ((DataClass) intent.getExtras().get("current_music")).getname());
            jObject.put("Feeling", feeling);
            jObject.put("musicID", ((DataClass) intent.getExtras().get("current_music")).getMusicID());
            jObject.put("imgId", ((DataClass) intent.getExtras().get("current_music")).getImgId());                   //int 화

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MusicPlayingActivity.this, "데이터 접근 실패", Toast.LENGTH_SHORT).show();
        }
        recent_array.put(jObject);
        editer.putString(m_str, recent_array.toString());
        Toast.makeText(MusicPlayingActivity.this, m_str + "에 저장", Toast.LENGTH_SHORT).show();
        editer.commit();
    }

    public class Add_to_list_Dialog extends Dialog {

        View.OnClickListener oklistenr;
        View.OnClickListener cancelListener;

        protected Add_to_list_Dialog(Context context, View.OnClickListener oklistenr, View.OnClickListener cancelListener) {
            super(context, android.R.style.Theme_Translucent_NoTitleBar);
            this.oklistenr = oklistenr;
            this.cancelListener = cancelListener;
        }

        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
            lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            lpWindow.dimAmount = 0.8f;
            getWindow().setAttributes(lpWindow);
            setContentView(R.layout.dialog_addlist);

            Button button_ok = (Button) findViewById(R.id.button_list1);
            Button button_cancel = (Button) findViewById(R.id.button_list2);

            button_ok.setOnClickListener(oklistenr);
            button_cancel.setOnClickListener(cancelListener);
        }
    }

    View.OnClickListener okListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.button_ok) {
                Intent intent1 = new Intent(MusicPlayingActivity.this, RythmGameActivity.class);
                intent1.putExtra("game_music", (DataClass) game_intent.getExtras().get("current_music"));
                startActivity(intent1);
                overridePendingTransition(R.anim.animation_up, R.anim.animation_hold);
                mp.stop();
                dlg.dismiss();
                finish();
            } else if (v.getId() == R.id.button_list1) {
                saveinlist("playlist_1");
                list_dialog.dismiss();
            }
        }
    };

    View.OnClickListener cancelListener = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.button_list2) {
                saveinlist("playlist_2");
                list_dialog.dismiss();
            } else if (v.getId() == R.id.button_can) {
                dlg.dismiss();
            }
        }
    };

    public void onclick(View view) {
        view.startAnimation(animation_button);
        list_dialog = new Add_to_list_Dialog(MusicPlayingActivity.this, okListener, cancelListener);
        list_dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.going_internet) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            Uri u = Uri.parse("http://music.naver.com/search/search.nhn?query=" + current_artist + "+" + current_song_name);
            i.setData(u);
            startActivity(i);
        } else if (id == R.id.feeling) {
            AlertDialog.Builder feeldlg = new AlertDialog.Builder(MusicPlayingActivity.this);
            feeldlg.setTitle("한줄 감상평 등록");

            final EditText feelEdit = new EditText(MusicPlayingActivity.this);
            feeldlg.setView(feelEdit);

            feeldlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String feeling = feelEdit.getText().toString();
                    saveinlist("feeling",feeling);
                    try {
                        FileOutputStream fos = openFileOutput("feeling.txt",
                                Context.MODE_APPEND);
                        PrintWriter out = new PrintWriter(fos);
                        out.println(feeling);
                        out.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(MusicPlayingActivity.this, FeelingActivity.class);
                    intent.putExtra("feel", feeling);
                    startActivity(intent);
                }
            });

            feeldlg.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            feeldlg.show();
        } else if (id == R.id.rythm_game) {
            dlg = new CustomDialog(MusicPlayingActivity.this, okListener, cancelListener);
            dlg.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    protected void onPause() {
        super.onPause();
        isPlaying = false;
        mp.stop();
    }
}
