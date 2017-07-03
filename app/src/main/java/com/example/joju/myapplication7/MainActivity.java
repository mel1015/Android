package com.example.joju.myapplication7;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Animation animation_button2;

    boolean add_action_clicked = false;
    boolean remove_action_clicked = false;
    boolean removing = false;

    LinearLayout list_layout;
    LinearLayout add_music_layout;

    public static ArrayList<DataClass> datas;

    ListView listview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicchart);

        getMusicInfo();     //음악 리스트를 불러오는 메소드

        DataAdapter adapter = new DataAdapter(this, datas);      //어댑터

        listview = (ListView) findViewById(R.id.chart_listView);    //리스트뷰
        listview.setAdapter(adapter);   //리스트뷰를 어댑터에 연결
        listview.setOnItemClickListener(listener);  //리스트뷰의 아이템이 선택될시 실행될 리스너

        list_layout = (LinearLayout) findViewById(R.id.list_layout);    //음악 리스트 레이아웃
        add_music_layout = (LinearLayout) findViewById(R.id.add_music_layout);  //음악 추가 레이아웃

        Button button_mylist1 = (Button) findViewById(R.id.button_mylist1); //재생목록1 버튼
        Button button_mylist2 = (Button) findViewById(R.id.button_mylist2); //재생목록2 버튼
        Button button_recentlist = (Button) findViewById(R.id.button_recentlist);   //최근재생목록 버튼
        Button button_addmusic = (Button) findViewById(R.id.button_addmusic);   //음악 추가를 누를시 나타나는 버튼

        animation_button2 = AnimationUtils.loadAnimation(MainActivity.this, R.anim.animation_button2);    //버튼이 눌릴때마다 실행되는 애니메이션

        View.OnClickListener buttonlistener = new View.OnClickListener() {  //버튼 리스너
            @Override
            public void onClick(View view) {

                if (R.id.button_mylist1 == view.getId()) {  //재생목록1 버튼을 누르면
                    view.startAnimation(animation_button2); //버튼 애니메이션 시작
                    DataClass data = new DataClass(null, "null", "null", null); //Dataclass에 null값을 주고 객체화
                    Intent intent = new Intent(MainActivity.this, MyPlayList1.class); //인텐트를 통해 음악 리스트에서 재생목록1로 액티비티 변경
                    intent.putExtra("added_music", data);   //putExtra()를 통해 재생목록1에 데이터를 넘겨줌
                    startActivity(intent);  //재생목록1 액티비티 시작

                } else if (R.id.button_mylist2 == view.getId()) {
                    view.startAnimation(animation_button2);
                    DataClass data = new DataClass(null, "null", "null", null);
                    Intent intent = new Intent(MainActivity.this, MyPlayList2.class);
                    intent.putExtra("added_music", data);
                    startActivity(intent);

                } else if (R.id.button_recentlist == view.getId()) {
                    view.startAnimation(animation_button2);
                    DataClass data = new DataClass(null, "null", "null", null);
                    Intent intent = new Intent(MainActivity.this, RecentPlayedList.class);
                    intent.putExtra("added_music", data);
                    startActivity(intent);

                } else if (R.id.button_addmusic == view.getId()) {  //음악추가 메뉴를 선택후 음악추가 버튼을 클릭하면

                    view.startAnimation(animation_button2);

                    String[] proj = {MediaStore.Audio.Media._ID,    //저장되어있는 음악정보를 가져오기위해
                            MediaStore.Audio.Media.ALBUM_ID,
                            MediaStore.Audio.Media.TITLE,
                            MediaStore.Audio.Media.ARTIST};

                    Cursor musicCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj, null, null, null); //커서를 이용해 저장되어있는 음악목록을 proj배열에 저장

                    EditText editText_addingname = (EditText) findViewById(R.id.editText_adding_name);  //노래제목을 입력받을 텍스트 창
                    String str_name = editText_addingname.getText().toString(); //추가할 노래제목 스트링으로 저장

                    EditText editText_adding_artist = (EditText) findViewById(R.id.editText_adding_artist);
                    String str_artist = editText_adding_artist.getText().toString();    //가수이름 스트링으로 저장


                    if (musicCursor != null && musicCursor.moveToFirst()) { //저장된 음악이 없지않고 커서가 맨 첫 노래를 가르키면
                        String musicID;     //음악 정보를 저장
                        String albumID;
                        String musicTitle;
                        String singer;

                        int musicIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);    //DB의 음악 _ID 필드의 index 번호를 가져옴
                        int albumIDCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
                        int musicTitleCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                        int singerCol = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);

                        do {
                            musicID = musicCursor.getString(musicIDCol);    //musicID에 가져온 인덱스 값의 스트링을 저장
                            albumID = musicCursor.getString(albumIDCol);
                            musicTitle = musicCursor.getString(musicTitleCol);
                            singer = musicCursor.getString(singerCol);

                            if (str_artist.equals(singer) && str_name.equals(musicTitle)) { //음악 제목과 가수의 이름이 같은 음악이 있으면
                                Toast.makeText(MainActivity.this, "추가 되었습니다.", Toast.LENGTH_SHORT).show();
                                datas.add(0, new DataClass(musicID, musicTitle, singer, albumID));  //dataclass에 음악 저장
                                ((DataAdapter) listview.getAdapter()).notifyDataSetChanged();   //리스트뷰를 갱신
                                break;
                            } else if (musicCursor.isLast()) {  //저장된 음악의 마지막 목록까지 검색하고 없으면
                                Toast.makeText(MainActivity.this, "찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        } while (musicCursor.moveToNext()); //DB 커서의 행을 아래로 이동
                    }
                }
            }       //onclick
        };          //onclicklistener
        button_mylist1.setOnClickListener(buttonlistener);
        button_mylist2.setOnClickListener(buttonlistener);
        button_recentlist.setOnClickListener(buttonlistener);
        button_addmusic.setOnClickListener(buttonlistener);
    }

    public void getMusicInfo() {    //저장된 음악을 가져오는 함수
        datas = new ArrayList<>();
        String str = "/music/";
        String[] proj = {MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, proj,
                android.provider.MediaStore.Audio.Media.DATA + " like ? ", new String[]{"%" + str + "%"}, null);


        while (cursor.moveToNext()) {
            DataClass dataclass = new DataClass();
            dataclass.setId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID)));
            dataclass.setImgId(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));
            dataclass.setTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            dataclass.setArtist(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            datas.add(dataclass);
        }
        cursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.add_music_action) {

            if (add_action_clicked == false) {
                item.setTitle("취소");

                list_layout.setVisibility(View.GONE);
                add_music_layout.setVisibility(View.VISIBLE);

                add_action_clicked = true;

                return true;

            } else if (add_action_clicked == true) {
                item.setTitle("추가");

                list_layout.setVisibility(View.VISIBLE);
                add_music_layout.setVisibility(View.GONE);

                add_action_clicked = false;

                return true;
            }

        } else if (id == R.id.remove_music_action) {
            if (remove_action_clicked == false) {   //삭제버튼을 누르면
                removing = true;    //음악을 삭제하기위해 true 값으로 변경
                item.setTitle("취소");    //삭제버튼을 "취소"로 변경
                Toast.makeText(this, "삭제하세요", Toast.LENGTH_SHORT).show();

                list_layout.setVisibility(View.GONE);
                add_music_layout.setVisibility(View.GONE);

                remove_action_clicked = true;   //삭제버튼이 눌렸다고 체크
            } else if (remove_action_clicked == true) {
                removing = false;

                item.setTitle("삭제");

                list_layout.setVisibility(View.VISIBLE);
                add_music_layout.setVisibility(View.GONE);

                remove_action_clicked = false;
            }
        } else if (id == R.id.view_map) {

            Intent intent = new Intent(
                    getApplicationContext(),
                    MapViewer.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {  //음악 목록에서 아이템을 클릭하면
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            if (removing == false) { //삭제 버튼이 눌려있지 않으면
                Intent intent = new Intent(MainActivity.this, MusicPlayingActivity.class);    //음악재생 액티비티 인텐트
                intent.putExtra("current_music", datas.get(position));  //최근재생목록에 음악추가
                intent.putExtra("current_music_num", position);
                intent.putExtra("current_datas", datas);
                startActivity(intent);  //음악재생 액티비티 실행
            } else if (removing == true) {    //삭제 버튼이 눌려있다면

                for (int i = datas.size() - 1; i >= 0; i--) {
                    if (i == position) {
                        datas.remove(i);    //음악을 지우고
                        ((DataAdapter) parent.getAdapter()).notifyDataSetChanged(); //리스트를 갱신

                    }
                }
            }
        }
    };

    protected void onStart() {
        super.onStart();
//        Toast.makeText(this, "main_start", LENGTH_SHORT).show();
    }

    protected void onRestart() {
        super.onRestart();
//        Toast.makeText(this, "main_restart", LENGTH_SHORT).show();
    }

    protected void onPause() {
        super.onPause();
//        Toast.makeText(this, "main_pause", LENGTH_SHORT).show();
    }

    protected void onStop() {
        super.onStop();
//        Toast.makeText(this, "main_stop", LENGTH_SHORT).show();
    }

    protected void onResume() {

        super.onResume();
//        Toast.makeText(this, "main_resume", LENGTH_SHORT).show();
        listview = (ListView) findViewById(R.id.chart_listView);
        DataAdapter adapter = new DataAdapter(MainActivity.this, datas);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(listener);
    }

    protected void onDestroy() {
        super.onDestroy();
//        Toast.makeText(this, "main_destroy", LENGTH_SHORT).show();
    }
}
