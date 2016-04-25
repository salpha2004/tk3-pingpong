package com.example.student.pingpong;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.webkit.WebIconDatabase;

/**
 * Created by student on 24.04.16.
 */
public class GamePanel extends SurfaceView implements SurfaceHolder.Callback{

    public static final int WIDTH = 500;
    public static final int HEIGHT = 400;
    private MainThread thread;
    private Background bg;
    private Player player;

    public GamePanel(Context context){
        super(context);

        // get surfaceHolder to handle surfaceObject and
        // add callback to the surfaceholder to intercept events
        // i.e. SurfaceHolder.Callback
        getHolder().addCallback(this);


        // in order to draw the surface canvas from within my thread
        thread = new MainThread(getHolder(), this);

        // make game panel focusable as it can handle events
        setFocusable(true);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder){
        boolean retry = true;
        while(retry){
            try{
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            retry = false;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder){
        bg = new Background(200,300, BitmapFactory.decodeResource(getResources(), R.drawable.pitou));
        player = new Player(getContext(), 190, 100, 300, 50);

        thread.setRunning(true);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        return super.onTouchEvent(event);
    }

    public void update(){
        player.update();
        bg.update();
    }

    @Override
    public void draw(Canvas canvas){
        if(canvas != null){
            bg.draw(canvas);
            player.draw(canvas);
        }

        super.draw(canvas);
    }

}
