package com.example.student.pingpong;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class Panel extends AppCompatActivity  implements SurfaceHolder.Callback, SensorEventListener{

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;
    Canvas canvas;
    Background bg;
    Player player;
    GameThread gThread;

    private SensorManager sensorManager;
    private Sensor lAccel;
    float[] moveHistory = new float[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_panel);

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        gThread = new GameThread(surfaceHolder, this);

        // for linear acceleration
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        lAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, lAccel, SensorManager.SENSOR_DELAY_GAME);
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

    @Override
    public void onSensorChanged(SensorEvent event){
        float xChange, yChange;
        if(moveHistory.length <= 0 ){
            xChange = event.values[0];
            yChange = event.values[1];
        }else {
            xChange = moveHistory[0] - event.values[0];
            yChange = moveHistory[1] - event.values[1];
        }
        // reset history
        moveHistory[0] = event.values[0];
        moveHistory[1] = event.values[1];

        // infer direction and move player
        // anything less than 2 is just noise
        if(xChange > 2){
            System.out.println(event.values);
            showToaster("MOVEmENT" + event.values);
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy){
        // TOneverDO write code here
    }

    public void showToaster(String msg){
        Context context = getApplicationContext();
        CharSequence text = msg;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
