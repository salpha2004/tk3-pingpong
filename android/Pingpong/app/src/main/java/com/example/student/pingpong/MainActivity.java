package com.example.student.pingpong;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.umundo.core.Discovery;
import org.umundo.core.Discovery.DiscoveryType;


public class MainActivity extends AppCompatActivity {

    private EditText nicknameText;
    private Button nicknameButton;
    private Context self;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.loadLibrary("umundoNativeJava");
        self = getApplicationContext();
        nicknameText = (EditText) findViewById(R.id.nicknameText);

        nicknameButton = (Button) findViewById(R.id.nicknameButton);
        nicknameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // go ahead only if nickname set
                String nickname = nicknameText.getText().toString();
                if(!nickname.isEmpty()){
                    Intent gameIntent = new Intent(self, GameActivity.class);
                    gameIntent.putExtra("nickname", nickname);
                    startActivity(gameIntent);
                }
            }
        });
    }
}
