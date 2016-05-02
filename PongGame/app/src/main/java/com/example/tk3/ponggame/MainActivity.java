package com.example.tk3.ponggame;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import org.umundo.core.*;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock mcLock = wifi.createMulticastLock("mylock");
            mcLock.acquire();
            // mcLock.release();
        } else {
            Log.v("android-umundo", "Cannot get WifiManager");
        }
        System.loadLibrary("umundoNativeJava_d");

        Mundo.getInstance();

    }

    public void startGame(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
