package com.example.joju.myapplication7;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

/**
 * Created by sks10 on 2017-05-17.
 */
public class ScoreDB extends SQLiteOpenHelper {

    public ScoreDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션);
        db.execSQL("CREATE TABLE ScoreTBL( _id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, score INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public void insert(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String getData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("SELECT * FROM ScoreTBL ORDER BY score DESC LIMIT 0,10", null);
        while (cursor.moveToNext()) {
            str += "이름 : "
                    + cursor.getString(1)
                    + "    점수 = "
                    + cursor.getInt(2)
                    + "\n";
        }

        return str;
    }

    public Integer getMax() {
        SQLiteDatabase db = getReadableDatabase();
        int score = 0;

        Cursor cursor = db.rawQuery("SELECT * FROM ScoreTBL ORDER BY score DESC", null);
        cursor.moveToFirst();

        if(cursor.getCount() > 0) {
            score = cursor.getInt(2);
        }

        return score;
    }
}
