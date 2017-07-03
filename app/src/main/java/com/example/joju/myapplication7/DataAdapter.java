package com.example.joju.myapplication7;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class DataAdapter extends BaseAdapter {
    Activity activity;
    List<DataClass> datas;
    LayoutInflater inflater;

    public DataAdapter(Activity activity, List<DataClass> datas) {
        this.activity = activity;
        this.datas = datas;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return datas.size();
    }   //자식 뷰들의 개수를 리턴하는 메소드

    @Override
    public Object getItem(int i) {
        return i;
    }       //어댑터 객체가 갖는 항목들 중 하나를 리턴

    @Override
    public long getItemId(int i) {
        return i;
    }       //어댑터가 갖는 항목의 ID를 리턴

    @Override
    public View getView(final int i, View convertview, ViewGroup parent)    //자식 뷰들 중 하나를 리턴, 화면에 보여지는 리스트만 생성
    {                                                                       //리턴할 자식 뷰의 위치,리턴 할 자식 뷰, 리턴할 뷰의 부모 뷰(자식 뷰를 포함하는 어댑터 뷰)
        if (convertview == null) {
            convertview = inflater.inflate(R.layout.list_row, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertview.setLayoutParams(layoutParams);
        }

        ImageView imageView = (ImageView) convertview.findViewById(R.id.imageView);                      //리스트 아이템 안에 들어가는 데이터
        Bitmap albumImage = getAlbumImage(activity, Integer.parseInt((datas.get(i)).getImgId()), 170);   //앨범이미지, 노래제목, 가수이름들을 객체화, 설정
        imageView.setImageBitmap(albumImage);                                                            //

        TextView textView_music_1 = (TextView) convertview.findViewById(R.id.textView_music_1);          //
        textView_music_1.setText(datas.get(i).getname());                                                //

        TextView textView_music_artist = (TextView) convertview.findViewById(R.id.textView_music_artist);//
        textView_music_artist.setText(datas.get(i).getArtist());                                         //

        return convertview;
    }

    private static final BitmapFactory.Options options = new BitmapFactory.Options();         //앨범이미지 크기조절을 위한 객체

    private static Bitmap getAlbumImage(Context context, int album_id, int MAX_IMAGE_SIZE) {  //앨범이미지 반환

        ContentResolver res = context.getContentResolver();         //ContentResolver-어플리케이션 사이의 데이터 공유를 도와주는 객체
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);  //앨범이미지 주소

        if (uri != null) {                  //앨범이미지가 없는 경우
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r"); //앨범이미지를 열어 fd에 저장

                options.inJustDecodeBounds = true;     //디코더가 null(비트맵 없음)을 반환
                BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);       //앨범이미지 디코딩
                int scale = 0;

                if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {   //앨범이미지 크기가 제한크기를 넘을 경우
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE /
                            (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }

                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;         //1초과값- 원본이미지 서브샘플링 더 작은 이미지 반환

                Bitmap b = BitmapFactory.decodeFileDescriptor(fd.getFileDescriptor(), null, options);    //앨범이미지 디코딩후 b에 저장

                if (b != null) {                                                                         //b가 null이 아니고
                    if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {     //앨범이미지의 높이나 폭이 맥스가 아닌 경우
                        Bitmap tmp = Bitmap.createScaledBitmap(b, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true); //이미지 크기를 맥스사이즈에 재조정해서 tmp에 저장
                        b.recycle();
                        b = tmp;
                    }
                }
                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
