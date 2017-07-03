package com.example.joju.myapplication7;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 감상평 작성 및 리스트뷰 구현
 */
public class FeelingActivity extends AppCompatActivity {
    ArrayList<DataClass> datas = new ArrayList<>();
    ListView listview;
    TextView read_feeling;
    EditText editBattery;
    LinearLayout feel_layout;
    JSONArray recent_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeling);

        feel_layout = (LinearLayout) findViewById(R.id.feel_layout);

        read_feeling = (TextView) findViewById(R.id.read_feeling);      //감상평 파일 내용 보여줄 텍스트뷰
        read_feeling.setMovementMethod(new ScrollingMovementMethod());   //스크롤기능 추가
        editBattery = (EditText) findViewById(R.id.edtBattery);         //배터리 잔량 보여주기 위해

        //텍스트 파일 불러와서 한줄씩 텍스트뷰에 추가
        try {
            StringBuffer data = new StringBuffer();
            FileInputStream fis = openFileInput("feeling.txt");
            BufferedReader buffer = new BufferedReader(new InputStreamReader(fis));

            String string = buffer.readLine();

            while (string != null) {
                data.append(string + "\n");
                string = buffer.readLine();
            }
            read_feeling.setText(data);
            buffer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //쉐어드프리퍼런스를 이용하여 MusicPlayingActivity 에서 넘겨준 음악정보와 감상평 받아오기
        SharedPreferences pref = getSharedPreferences("music_app_data", MODE_PRIVATE);
        String str = pref.getString("feeling", "nonono");

        try {
            recent_array = new JSONArray(str);

            String musicID;
            String name;        //이름 저장
            String feeling;
            String imgId;       //이미지 변수

            if (!str.equals("nonono")) {
                for (int i = 0; i < recent_array.length(); i++) {
                    musicID = recent_array.getJSONObject(i).getString("musicID");
                    name = recent_array.getJSONObject(i).getString("name");
                    feeling = recent_array.getJSONObject(i).getString("feeling");
                    imgId = recent_array.getJSONObject(i).getString("imgId");

                    datas.add(0, new DataClass(musicID, name, feeling, imgId));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(FeelingActivity.this, "데이터 없음", Toast.LENGTH_SHORT).show();
        }
        listview = (ListView) findViewById(R.id.feeling_listview);
        DataAdapter adapter = new DataAdapter(FeelingActivity.this, datas);
        listview.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_feeling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.showAll:
                listview.setVisibility(View.GONE);
                feel_layout.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }

    //브로드캐스트 리시버를 활용하여 배터리 잔량을 표시
    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                int remain = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                editBattery.setText("현재 충전량 : " + remain + "%\n");

                int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
                switch (plug) {
                    case 0:
                        editBattery.append("전원 연결 : 안됨");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_AC:
                        editBattery.append("전원 연결 : 어댑터 연결됨");
                        break;
                    case BatteryManager.BATTERY_PLUGGED_USB:
                        editBattery.append("전원 연결 : USB 연결됨");
                        break;
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(br, intentFilter);
    }
}
