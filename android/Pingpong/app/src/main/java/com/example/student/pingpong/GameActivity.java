package com.example.student.pingpong;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class GameActivity extends AppCompatActivity {

    private SensorManager msManager;
    private Sensor mSensor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_game);
        msManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // set to full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //setContentView(new Player(this, 90, 170, 300, 50));
        //setContentView(new GamePanel(this));
        Intent intent = new Intent(this, Panel.class);
        startActivity(intent);
    }
}
