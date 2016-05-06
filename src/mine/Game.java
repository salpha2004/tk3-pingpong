package mine;

import java.awt.Dimension;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


import org.umundo.core.Message;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;

public class Game extends JFrame {
    int startReceived;

    Mundo mundo;
    int mundoId;
    Publisher pub;
    // TODO: readyPlayers are those in the black screen (pressed the button),
    // numPlayers are those in the main screen (before pressing the button)
    int numPlayers, readyPlayers;

    GameState gameState;
    public class Recv extends Receiver {
        public void receive(Message msg) {
            if (msg.getMeta().containsKey("start")) {
                // signal game's start.
                startReceived = 1;
                System.out.println ("\nstart\n");
            }
            if (mundoId == 0 && msg.getMeta().containsKey("ready")) {
                readyPlayers++;
                if (readyPlayers == numPlayers) {
                    try {
                        Message start = new Message();
                        start.putMeta("start", "");
                        mundo.getPub().send(start);
                        Thread.sleep(1000);
                        startReceived = 1;
                        System.out.println ("\nstart\n");
                    } catch (InterruptedException e) {}
                }
            }
            if (msg.getMeta().containsKey("pos")) {
                //int i = java.nio.ByteBuffer.wrap(msg.getData()).getInt();
                int id = Integer.parseInt(msg.getMeta("id"));
                float f = Float.parseFloat(msg.getMeta("pos"));
                gameState.moveOpponentBat(id, f);
            }
            if (msg.getMeta().containsKey("ballX")) {
                //int i = java.nio.ByteBuffer.wrap(msg.getData()).getInt();
                float x = Float.parseFloat(msg.getMeta("ballX"));
                float y = Float.parseFloat(msg.getMeta("ballY"));
                gameState.setBall(x, y);
            }
        }
    }
        
    public void initMundo () {
        System.out.println("Enter your name:");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        mundo = Mundo.getInstance(name);
        try {
            Thread.sleep (2000);
        }
        catch (InterruptedException ex) {};

        mundoId = mundo.getId();
        System.out.println("mundo id: " + mundoId);

        System.out.println("Press Enter when ready...");
        scanner.nextLine();

        numPlayers = mundo.getParticipants().size();
        System.out.println("num players: " + numPlayers);

        mundo.getSub().setReceiver(new Recv());
        if (mundoId != 0) {
            Message msg = new Message();
            msg.putMeta("ready", "");
            mundo.getPub().send (msg);
        }
        if (mundoId == 0 && numPlayers == 1)
            startReceived = 1;

    }
    public Game () {
        super("TK3 Ping Pong");
        readyPlayers = 1;
        numPlayers = 1;
        startReceived = 0;
        initMundo ();
        gameState = new GameState(Globals.WORLD_WIDTH, Globals.WORLD_HEIGHT,
                    Globals.SCALE, numPlayers);
        // setup the JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.add(gameState);
        // create the size of the window
        Dimension size = new Dimension(Globals.WORLD_WIDTH + 100, Globals.WORLD_HEIGHT + 100);
        this.setSize(size);

        this.addKeyListener (gameState);
        this.setFocusable(true);

        while (startReceived == 0) {
            try {
                Thread.sleep (500);
                System.out.print(".");
            }
            catch (InterruptedException ex) {}
        }
        this.setVisible(true);
        Thread t = new Thread() {
            @Override
            public void interrupt() {
                super.interrupt();
            }

            @Override
            public void run() {
                while (true) {
                    gameState.update();
                    try {
                        Thread.sleep(Globals.REFRESH_DELAY);
                    }
                    catch (InterruptedException ex) {}
                }
            }
        };
        t.start();
    }

    public static void main(String[] args) {
        String arch = System.getProperty("os.arch");
        String os = System.getProperty("os.name");
        String libPath = "";
        if (os.indexOf("win") >= 0) {
            if ("i386".equals(arch))
                libPath = System.getProperty("user.dir") + "\\umundoNativeJava.dll";
            else if ("amd64".equals(arch))
                libPath = System.getProperty("user.dir") + "\\umundoNativeJava64.dll";
        }
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0) {
            if ("i386".equals(arch))
                libPath = System.getProperty("user.dir") + "/libumundoNativeJava.so";
            else if ("amd64".equals(arch))
                libPath = System.getProperty("user.dir") + "/libumundoNativeJava64.so";
        }
        System.load(libPath);
        // create the example JFrame
        Game game = new Game();

    }
}

