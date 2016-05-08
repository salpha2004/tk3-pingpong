package com.example.tk3.ponggame;

import org.umundo.core.Discovery;
import org.umundo.core.Greeter;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.StringArray;
import org.umundo.core.Subscriber;
import org.umundo.core.SubscriberStub;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Mohit on 01.05.2016.
 */
public class Mundo {
    private static Mundo _instance;

    private String name;

    private Discovery disc;
    private Node node;
    private Publisher pub;
    private Subscriber sub;

    //private HashMap<String, String> participants;
    private ArrayList<String> participants;
    private ArrayList<String> participantsUUID;
    
    private static Game game;

    private int id;

    private Mundo(String name) {
        this.name = name;
        //participants = new HashMap<String, String>();
        participants = new ArrayList<>();
        participantsUUID = new ArrayList<>();
        disc = new Discovery(Discovery.DiscoveryType.MDNS);
        //long i = disc.list().size();
        node = new Node();
        disc.add(node);
        pub = new Publisher("batPos");

        sub = new Subscriber("batPos");
        sub.setReceiver(new LoginReceiver());
        node.addPublisher(pub);
        node.addSubscriber(sub);
        pub.setGreeter(new Login(name));
        //int n = pub.waitForSubscribers(0);
        id = 0;
        //participants.put(sub.getUUID(), name);
        participants.add(name);
    }

    private Mundo() {
        this("NoName");
    }

    public static Mundo getInstance(String name, Game g) {
        game = g;
        if (_instance == null) {
            _instance = new Mundo(name);
        }
        return _instance;
    }
    
    public static Mundo getInstance(String name) {
        if (_instance == null) {
            _instance = new Mundo(name);
        }
        return _instance;
    }

    public static Mundo getInstance() {
        if (_instance == null) {
            _instance = new Mundo();
        }
        return _instance;
    }

    public Publisher getPub() {
        return pub;
    }

    public void setPub(Publisher pub) {
        this.pub = pub;
    }

    public Subscriber getSub() {
        return sub;
    }

    public void setSub(Subscriber sub) {
        this.sub = sub;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    /*public HashMap<String, String> getParticipants() {
        return participants;
    }*/

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public int getId() {
        return id;
    }

    class Login extends Greeter {
        private String userName;

        public Login(String name) {
            userName = name;
        }

        @Override
        public void welcome(Publisher pub, SubscriberStub sub) {
            //participants.put(sub.getUUID(), userName);
            //participants.add(userName);
            participantsUUID.add(sub.getUUID());
            Message greeting = Message.toSubscriber(sub.getUUID());
            //greeting.putMeta("subscriber", Mundo.this.sub.getUUID());
            greeting.putMeta("name", userName);
            greeting.putMeta("senderUUID", Mundo.this.sub.getUUID());
            greeting.putMeta("senderId", "" + id);
            pub.send(greeting);
        }

        @Override
        public void farewell(Publisher arg0, SubscriberStub arg1) {
            //super.farewell(arg0, arg1);
            return;
        }
    }

    public class LoginReceiver extends Receiver {
        public void receive(Message msg) {
            //if (msg.getMeta().containsKey("subscriber") && !participants.containsKey(msg.getMeta("subscriber"))) {
            if (msg.getMeta().containsKey("name")) {
                if (!participantsUUID.contains(msg.getMeta("senderUUID"))) {
                    int i = Integer.parseInt(msg.getMeta("senderId"));
                    if (i >= id) {
                        id = i + 1;
                        //participants.put(sub.getUUID(), this);
                    }
                }
                //participants.put(msg.getMeta("subscriber"), msg.getMeta("name"));
                if (!participants.contains(msg.getMeta("name"))) {
                    participants.add(msg.getMeta("name"));
                }
                if (game != null) {
                    game.updatePlayerList (msg.getMeta("name"));
                }
            }
        }
    }
}
