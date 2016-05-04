package com.example.student.pingpong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.umundo.core.Publisher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.security.auth.callback.CallbackHandler;

public class GameSetup extends AppCompatActivity {

    private Button createGameButton;
    private ListView listView;
    private HashMap<String, String> pubs;
    private ArrayList<String> participants;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_setup);

        createGameButton = (Button) findViewById(R.id.buttonCreateGame);
        createGameButton.setVisibility(View.INVISIBLE);

        // load lib
        System.loadLibrary("umundoNativeJava_d");

        String creator = getIntent().getStringExtra("nickname");
        final Channel channelInstance = Channel.getInstance(this, creator);

        // list players
        participants = new ArrayList();
        pubs = channelInstance.getParticipants();

        System.out.println("PRINTING VALUES");
        System.out.println(pubs.values());

        String[] strings = pubs.values().toArray(new String[0]);

        listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                android.R.id.text1, strings);

        // assign adapter to listVIew
        listView.setAdapter(adapter);

        // listView item click listener


        // show join button only for original game creator
        System.out.println("ORIGINAL CREAOTR");
        System.out.println(channelInstance.getOriginalCreator());

        /*if(channelInstance.getOriginalCreator() == creator){
            createGameButton.setVisibility(View.VISIBLE);
            if(strings.length < 2){
                createGameButton.setEnabled(false);
            }
        }*/
        createGameButton.setVisibility(View.VISIBLE);
        if(strings.length < 2){
            createGameButton.setEnabled(false);
        }

    }
    
    private void createGameClickListener(){

    }
}

