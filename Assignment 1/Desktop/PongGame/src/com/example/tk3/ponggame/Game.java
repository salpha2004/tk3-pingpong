package com.example.tk3.ponggame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;


import org.umundo.core.Message;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;

/**
 *
 * @author Saeed
 */
public class Game extends JFrame {
    int startReceived;

    Mundo mundo;
    int mundoId;
    Publisher pub;
    int numPlayers, readyPlayers;
    
    JLabel[] playerNames;
    int playerIndex;

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
                int id = Integer.parseInt(msg.getMeta("id"));
                float f = Float.parseFloat(msg.getMeta("pos"));
                gameState.moveOpponentBat(id, f);
            }
            if (msg.getMeta().containsKey("ballX")) {
                float x = Float.parseFloat(msg.getMeta("ballX"));
                float y = Float.parseFloat(msg.getMeta("ballY"));
                gameState.setBall(x, y);
            }
        }
    }

    private void startGame () {
        mundo.getSub().setReceiver(new Recv());
        if (mundoId != 0) {
            Message msg = new Message();
            msg.putMeta("ready", "");
            mundo.getPub().send (msg);
        }

        gameState = new GameState(Globals.WORLD_WIDTH, Globals.WORLD_HEIGHT,
                    Globals.SCALE, numPlayers);
        // setup the main JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.add(gameState);
        // create the size of the window. it's 100 pxs more than the game board rect size.
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
    
    public void initMundo (String username) {
        mundo = Mundo.getInstance(username, this);
        try {
            Thread.sleep (2000);
        }
        catch (InterruptedException ex) {};

        mundoId = mundo.getId();
        System.out.println("mundo id: " + mundoId);
    }

    public Game () {
        super("TK3 Ping Pong");
        readyPlayers = 1;
        numPlayers = 1;
        startReceived = 0;
        playerIndex = 0;
        initLoginForm();
    }
    
    synchronized public void updatePlayerList (String newPlayer) {
        playerNames[playerIndex].setText(newPlayer);
        playerIndex++;
    }
    
    // initialize and display the login form.
    private void initLoginForm() {
        JFrame loginFrame = new JFrame("Login Screen");
        JPanel loginPanel = new JPanel();
        loginPanel.setBackground(Color.BLACK);
        loginPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridLayout panelLayout = new GridLayout(9, 3);
        panelLayout.setVgap(5);
        loginPanel.setLayout(panelLayout);
        loginFrame.setContentPane(loginPanel);
        loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        loginFrame.setResizable(false);
        loginFrame.setFocusable(true);
        JLabel umundoPong = new JLabel("uMundo Pong");
        umundoPong.setHorizontalAlignment(SwingConstants.CENTER);
        umundoPong.setFont (new Font("Droid Sans Mono", Font.PLAIN, Globals.BIG_FONT_SIZE));
        umundoPong.setBackground(Color.BLACK);
        umundoPong.setForeground(Color.WHITE);
        loginFrame.add(umundoPong);
        JLabel usernameFieldText = new JLabel("Username:");
        usernameFieldText.setFont (new Font("Droid Sans Mono", Font.PLAIN, Globals.NORMAL_FONT_SIZE));
        usernameFieldText.setBackground(Color.BLACK);
        usernameFieldText.setForeground(Color.WHITE);
        loginFrame.add(usernameFieldText);
        final JTextField username = new JTextField(20);
        username.setFont (new Font("Droid Sans Mono", Font.PLAIN, Globals.NORMAL_FONT_SIZE));
        username.setMargin(new Insets(1, 15, 1, 15));
        username.setBackground(Color.BLACK);
        username.setForeground(Color.WHITE);
        loginFrame.add(username);
        playerNames = new JLabel[4];
        for (int i=0; i<4; i++) {
            playerNames[i] = new JLabel();
            playerNames[i].setHorizontalAlignment(SwingConstants.CENTER);
            playerNames[i].setForeground(Color.WHITE);
            playerNames[i].setFont (new Font("Droid Sans Mono", Font.PLAIN, Globals.NORMAL_FONT_SIZE));
            loginFrame.add (playerNames[i]);
        }
        final JButton createUserBtn = new JButton("Create User");
        createUserBtn.setFont (new Font("Droid Sans Mono", Font.PLAIN, Globals.NORMAL_FONT_SIZE));
        createUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (createUserBtn.getText().equals(e.getActionCommand())) {
                    String name = username.getText();
                    if (!"".equals(name)) {
                        updatePlayerList(name);
                        initMundo(name);
                    }
                }
            }
         });
        final JButton startBtn = new JButton ("Start Game");
        startBtn.setFont (new Font("Droid Sans Mono", Font.PLAIN, Globals.NORMAL_FONT_SIZE));
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                numPlayers = mundo.getParticipants().size();
                System.out.println("num players: " + numPlayers);
                if (mundoId == 0 && numPlayers == 1)
                    startReceived = 1;
                startGame();
            }
        });
        createUserBtn.setBackground(Color.BLACK);
        createUserBtn.setBorder(new LineBorder(Color.WHITE));
        createUserBtn.setForeground(Color.WHITE);
        createUserBtn.setOpaque(true);
        startBtn.setBackground(Color.BLACK);
        startBtn.setBorder(new LineBorder(Color.WHITE));
        startBtn.setForeground(Color.WHITE);
        startBtn.setOpaque(true);
        loginFrame.add(createUserBtn);
        loginFrame.add(startBtn);
        loginFrame.pack();
        loginFrame.setVisible(true);
    }

    public static void main(String[] args) {
        String arch = System.getProperty("os.arch");
        String os = System.getProperty("os.name");
        String libPath = "";
        // load proper umundo library based on the underlying os and arch.
        if (os.indexOf("win") >= 0 || os.indexOf("Win") >= 0) {
            if ("i386".equals(arch))
                libPath = System.getProperty("user.dir") + "\\..\\lib\\umundoNativeJava.dll";
            else if ("amd64".equals(arch))
                libPath = System.getProperty("user.dir") + "\\..\\lib\\umundoNativeJava64.dll";
        }
        if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") >= 0) {
            if ("i386".equals(arch))
                libPath = System.getProperty("user.dir") + "/../lib/libumundoNativeJava.so";
            else if ("amd64".equals(arch))
                libPath = System.getProperty("user.dir") + "/../lib/libumundoNativeJava64.so";
        }
        System.load(libPath);

        Game game = new Game();
    }
}

