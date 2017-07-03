package com.example.joju.myapplication7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class RecentPlayedList extends AppCompatActivity {
    ArrayList<DataClass> datas = new ArrayList<>();
    ListView listview;
    JSONArray recent_array;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recent_played_list);

        SharedPreferences pref = getSharedPreferences("music_app_data", MODE_PRIVATE);
        String str = pref.getString("recent_array", "nonono");       //스트링이 들어가있어야 오류가 안나므로 초기값으로 "nonono"를 넣어놈.

        try {
            recent_array = new JSONArray(str);

            String musicID;
            String name;    //이름 저장
            String artist;      ////가수 이름
            String imgId;          ///이미지 변수

            if (!str.equals("nonono")) {

                for (int i = 0; i < recent_array.length(); i++) {
                    musicID = recent_array.getJSONObject(i).getString("musicID");
                    name = recent_array.getJSONObject(i).getString("name");
                    artist = recent_array.getJSONObject(i).getString("artist");
                    imgId = recent_array.getJSONObject(i).getString("imgId");

                    datas.add(0, new DataClass(musicID, name, artist, imgId));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(RecentPlayedList.this, "데이터 없음", Toast.LENGTH_SHORT).show();
        }
        listview = (ListView) findViewById(R.id.recent_listView);
        DataAdapter adapter = new DataAdapter(RecentPlayedList.this, datas);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(listener);
    }

    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            Intent intent1 = new Intent(RecentPlayedList.this, MusicPlayingActivity.class);
            intent1.putExtra("current_music", datas.get(position));
            intent1.putExtra("current_music_num", position);
            intent1.putExtra("current_datas", datas);
            startActivity(intent1);
        }
    };
}
