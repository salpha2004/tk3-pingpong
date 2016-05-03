package com.example.student.pingpong;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import org.umundo.core.Discovery;
import org.umundo.core.Discovery.DiscoveryType;
import org.umundo.core.Greeter;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;
import org.umundo.core.SubscriberStub;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by student on 01.05.16.
 */
public class Channel {

    private static Channel _instance;
    private WifiManager wifi;
    private Discovery disc;
    private Node node;
    private Context context;
    private Publisher pongPub;
    private Subscriber pongSub;
    private ArrayList<Publisher> publishers = new ArrayList();
    public HashMap<String, String> participants;
    private String name;
    private String originalCreator;

    private Channel(Context context, String name){
        this.context = context;
        this.name = name;

        disc = new Discovery(DiscoveryType.MDNS);
        node = new Node();
        disc.add(node);

        pongPub = new Publisher("pingpong");
        pongPub.setGreeter(new PongGreeter(name));

        this.publishers.add(pongPub);

        pongSub = new Subscriber("pingpong");
        pongSub.setReceiver(new PongReceiver());

        node.addPublisher(pongPub);
        node.addSubscriber(pongSub);

        // add subscriber
        participants = new HashMap();
        participants.put(pongSub.getUUID(), this.name);
//        if(participants.size() == 1)
//            originalCreator = this.name;

        //pongPub.waitForSubscribers(0);

        System.out.println("PRINTING publishers");
        System.out.println(node.getPublishers());
    }

    public static Channel getInstance(Context context, String name){
        if (_instance == null) {
            _instance = new Channel(context, name);
        }
        return _instance;
    }

    public HashMap<String, String> getParticipants(){
        //return node.getPublishers();
        return this.participants;
    }

    protected void refreshSetup(){
        Intent intent = new Intent(context , GameSetup.class);
        this.context.startActivity(intent);
    }

    public String getOriginalCreator(){
        return this.participants.values().toArray(new String[0])[0];
        //return originalCreator;
    }

    /*public ArrayList<Publisher> getPublishers(){
        return this.publishers;
    }*/

    /*public void joinGame(String name){
        //GameCreator game = new GameCreator(name);
        pongPub.setGreeter(new PongGreeter(name));
        System.out.println("pRINTING pongSUB");
        System.out.println(pongPub.toString());
    }*/

    /*private void initWifi(){
        WifiManager wifi = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            WifiManager.MulticastLock mcLock = wifi.createMulticastLock("mylock");
            mcLock.acquire();
            // mcLock.release();
        } else {
            Log.v("android-umundo", "Cannot get WifiManager");
        }
    }*/

    public class PongReceiver extends Receiver{

        public void receive(Message msg){
           /* for (String key : msg.getMeta().keySet()) {
                Log.i("umundo", key + ": " + msg.getMeta(key));
            }*/
            System.out.println("RECEIVING");
            if (msg.getMeta().containsKey("participant")) {
                Channel.this.participants.put(msg.getMeta("subscriber"), msg.getMeta("participant"));
                System.out.println(msg.getMeta("participant") + " joined the chat");

                refreshSetup();
            } else {
                System.out.println(msg.getMeta("userName") + ": "
                        + msg.getMeta("chatMsg"));
            }
        }
    }

    public class PongGreeter extends Greeter{

        public String userName;

        public PongGreeter(String userName) {
            this.userName = userName;
        }


        public void welcome(Publisher pub, SubscriberStub subStub) {
            System.out.println("putting name for sub, username");
            System.out.println(userName);
            participants.put(subStub.getUUID(), userName);

            Message greeting = Message.toSubscriber(subStub.getUUID());
            greeting.putMeta("participant", userName);
            greeting.putMeta("subscriber", Channel.this.pongSub.getUUID());
            System.out.println("GREETING");
            System.out.println(greeting);

            pub.send(greeting);
        }

        public void farewell(Publisher pub, SubscriberStub subStub) {
            /*if (Channel.this.participants.containsKey(subStub.getUUID())) {
                System.out.println(Channel.this.participants.get(subStub.getUUID()) + " left the game");
            } else {
                System.out.println("An unknown user left the chat: " + subStub.getUUID());
            }*/
            return;
        }
    }

    /*public static class GameCreator{

        private static ArrayList<String> gameNames = new ArrayList();

        public GameCreator(String gameName){
            gameNames.add(gameName);
        }

        public ArrayList<String> getGameNames(){
            return gameNames;
        }
    }*/

}
