package com.example.student.pingpong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.View;

import java.util.BitSet;

/**
 * Created by student on 24.04.16.
 */
public class Background{

    private Bitmap bGround;
    private int x, y;


    //public Background(Bitmap res){
    //    bGround = res;
    //}
    public Background(int x, int y, Bitmap res){
        this.x = x;
        this.y = y;
        //bGround = res;//Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
        bGround = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);
    }

    public void update(){

    }

    public void draw(Canvas canvas){
        if(canvas != null){
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            // background color
            canvas.drawARGB(0, 0, 0, 0);
            //canvas.drawColor(Color.BLUE);
            canvas.drawBitmap(bGround, x, y, null);
        }

    }
}
