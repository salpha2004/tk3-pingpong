package mine;

import org.umundo.core.Discovery;
import org.umundo.core.Greeter;
import org.umundo.core.Message;
import org.umundo.core.Node;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;
import org.umundo.core.Subscriber;
import org.umundo.core.SubscriberStub;

import java.util.HashMap;

/**
 * Created by Mohit on 01.05.2016.
 */
public class Mundo {
    private static Mundo _instance;

    private Discovery disc;
    private Node node;
    private Publisher pub;
    private Subscriber sub;

    private HashMap<String, Integer> participants;

    private int id;

    private Mundo() {
        participants = new HashMap<String, Integer>();
        disc = new Discovery(Discovery.DiscoveryType.MDNS);
        //long i = disc.list().size();
        node = new Node();
        disc.add(node);
        pub = new Publisher("batPos");
        pub.setGreeter(new Login());
        sub = new Subscriber("batPos");
        sub.setReceiver(new LoginReceiver());
        node.addPublisher(pub);
        node.addSubscriber(sub);
        int n = pub.waitForSubscribers(0);
        id = 0;

        participants.put(sub.getUUID(), id);
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

    public HashMap<String, Integer> getParticipants() {
        return participants;
    }

    public int getId() {
        return id;
    }

    class Login extends Greeter {
        @Override
        public void welcome(Publisher pub, SubscriberStub sub) {
            participants.put(sub.getUUID(), 0);
            Message greeting = Message.toSubscriber(sub.getUUID());
            greeting.putMeta("sender", Mundo.this.sub.getUUID());
            greeting.putMeta("subscriber", "" + id);
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
            if (msg.getMeta().containsKey("subscriber") && !participants.containsKey(msg.getMeta("sender"))) {
                int i = Integer.parseInt(msg.getMeta("subscriber"));
                if (i >= id) {
                    id = i + 1;
                    participants.put(sub.getUUID(), 0);
                }
                participants.put(msg.getMeta("sender"), 0);
            }
        }
    }
}
