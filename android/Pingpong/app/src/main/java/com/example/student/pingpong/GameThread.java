package com.example.student.pingpong;

import android.graphics.Canvas;
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
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
    }

    public void setRunning(Boolean bool){
        running = bool;
    }
}
