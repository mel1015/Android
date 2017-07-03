package com.example.joju.myapplication7;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;


public class GameData
{

    ImageView imageView;
    Bitmap bitmap;
    float _x;
    float _y;

    Paint paint;

    GameData(Paint paint, Context context)
    {
        imageView = new ImageView(context);
        this.paint = paint;

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);    //리듬게임의 노트.
    }


    public void set_y(float y)
    {
        _y = y;
    }

    public void set_x(float x)
    {
        _x = x;
    }

    public synchronized  void drawOnCanvas(Canvas canvas)
    {
        canvas.drawBitmap(bitmap, _x, _y, null);
    }

}
