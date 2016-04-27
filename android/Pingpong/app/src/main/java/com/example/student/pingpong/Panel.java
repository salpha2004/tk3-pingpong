package com.example.student.pingpong;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class Panel extends AppCompatActivity  implements SurfaceHolder.Callback{

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Canvas canvas;
    Background bg;
    Player player;
    GameThread gThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        gThread = new GameThread(surfaceHolder, this);
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        /*bg.draw(canvas);
        player.draw(canvas);
        System.out.println("SURAFACE CHaNGED");*/
    }

    public void surfaceCreated(SurfaceHolder holder) {
        bg = new Background(400, 300, BitmapFactory.decodeResource(getResources(), R.drawable.pitou));
        player = new Player(this, 330, 1270, 300, 50);
        this.surfaceHolder = holder;

        gThread.setRunning(true);
        gThread.start();

        /*Canvas c = holder.lockCanvas();
        canvas = c;
        synchronized (holder){
            c.drawARGB(255,215,255,0);
            Paint rPaint = new Paint();
            rPaint.setColor(Color.RED);
            c.drawCircle(100, 100, 30, rPaint);
            bg.draw(c);
        }*/
        //holder.unlockCanvasAndPost(c);

    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while(retry){
            try{
                gThread.setRunning(false);
                gThread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            retry = false;
        }
    }

    public void update(){
        player.update();
        bg.update();
    }

    public void draw(Canvas canvas){
        if(canvas != null){
            bg.draw(canvas);
            player.draw(canvas);
        }
        //super.draw(canvas);
    }

}
