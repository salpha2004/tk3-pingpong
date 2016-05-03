package com.example.student.pingpong;

import android.content.Context;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.SurfaceHolder;

/**
 * Created by student on 27.04.16.
 */
public class GameThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private Panel panel;
    private boolean running;
    public static Canvas canvas;

    public GameThread(SurfaceHolder holder, Panel panel){
        super();
        surfaceHolder = holder;
        this.panel = panel;


    }

    public void run(){
        while(running){
            canvas = null;

            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    this.panel.update();
                    this.panel.draw(canvas);
                }

            } catch (RuntimeException e) {
                // check for exceptions
                System.err.println(e);
                return;
            }finally {
                if(canvas != null){
                    // once done with drawing with the canvas
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void setRunning(Boolean bool){
        running = bool;
    }

    public boolean isRunning(){
        return running;
    }

    public void redrawPlayer(Player player){
        if(canvas != null){
            player.draw(canvas);
        }
    }
}