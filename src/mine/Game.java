/*
 * Copyright (c) 2010-2015 William Bittle  http://www.dyn4j.org/
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Modified by Saeed Ehteshamifar (salpha.2004@gmail.com)
 */

package mine;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.dyn4j.collision.AxisAlignedBounds;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;

import org.umundo.core.Message;
import org.umundo.core.Publisher;
import org.umundo.core.Receiver;

class BallStarter extends TimerTask {
    Ball ball;
    final double MAX_FORCE = 90.0;
    final double MIN_FORCE = 30.0;

    double genForce () {
        double force = Math.random() * 100.0;
        while (force > MAX_FORCE || force < MIN_FORCE)
            force = Math.random() * 100.0;
        if (Math.random() < 0.5)
            force *= -1;
        return force;
    }
    
    public BallStarter (Ball b) {
        ball = b;
    }
    
    @Override
    public void run () {
        //ball.setLinearVelocity(genForce(), genForce());
        ball.applyForce(new Vector2(genForce(), genForce()));
    }
}

public class Game extends JFrame implements KeyListener {
	/** The scale 45 pixels per meter */
	public static final double SCALE = 45.0;
	
	/** The conversion factor from nano to base */
	public static final double NANO_TO_BASE = 1.0e9;

	/** The canvas to draw to */
	protected Canvas canvas;
	
	/** The dynamics engine */
	protected World world;
	
	/** Whether the example is stopped or not */
	protected boolean stopped;
	
	/** The time stamp for the last iteration */
	protected long last;
        
        
        Bat bat;
        Bat bat1, bat2, bat3, bat4;
        Wall myWall;
        Wall[] otherWalls; //other players' walls
        
        int lifes = 3;
        Ball ball;
        
        int startReceived;
        
        Timer timer = new Timer(true);
        
        Mundo mundo;
        int mundoId;
        Publisher pub;
        // TODO: readyPlayers are those in the black screen (pressed the button),
        // numPlayers are those in the main screen (before pressing the button)
        int numPlayers, readyPlayers;
    
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
                double curPos = Double.parseDouble(msg.getMeta("pos"));
                switch (id) {
                    case 0:
                    case 1:
                        bat2.translate(1.0, 1.0);
                        break;
                    case 2:
                    case 3:
                        
                        break;
                }
            }
            if (msg.getMeta().containsKey("ballX")) {
                //int i = java.nio.ByteBuffer.wrap(msg.getData()).getInt();
                double x = Double.parseDouble(msg.getMeta("ballX"));
                double y = Double.parseDouble(msg.getMeta("ballY"));
            }
        }
    }
        
        private void waitForOthers () {
            Message msg = new Message();
            
            /* game's coordinator has id=0. */
            if (mundoId == 0) {
                mundo.getSub().setReceiver(new Recv());
            }
            else {
                mundo.getSub().setReceiver(new Recv());
                msg.putMeta("ready", "");
                mundo.getPub().send (msg);
            }
            /* special case for boring single-player game. */
            if (mundoId == 0 && numPlayers == 1)
                startReceived = 1;
        }
        
	public Game (int numberOfPlayers) {
            super("TK3 - PingPong");
            startReceived = 0;
            // current player (this) is counted and is ready, so init to 1!
            numPlayers = 1;
            readyPlayers = 1;
            // init uMundo

            System.out.println("Enter your name");
            Scanner dump = new Scanner (System.in);
            
            mundo = Mundo.getInstance(dump.nextLine());
            // wait until IDs are set.
            try {
                Thread.sleep (2000);
            }
            catch (InterruptedException ex) {}
            
            mundoId = mundo.getId();
            pub = mundo.getPub();
            
            System.out.println("mundo id: " + mundoId);
            
            System.out.println("Press Enter when ready...");
            dump.nextLine();

            numPlayers = mundo.getParticipants().size();
            System.out.println("num players: " + numPlayers);

            waitForOthers ();
            // poll for start:
            // for the coordinator (id=0), start is set when it sends start to others,
            // for others, it is set when they actually receive start.
            while (startReceived == 0) {
                try {
                    Thread.sleep(500);
                    System.out.print(".");
                }
                catch (InterruptedException ex) {}
            }
            System.out.println("");
            // after jumping out of the loop, "start" command has been received.
            init ();
	}

    void init () {
        // setup the JFrame
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create the size of the window
        Dimension size = new Dimension(Globals.WORLD_WIDTH, Globals.WORLD_HEIGHT);

        // create a canvas to paint to 
        this.canvas = new Canvas();
        this.canvas.setPreferredSize(size);
        this.canvas.setMinimumSize(size);
        this.canvas.setMaximumSize(size);

        // add the canvas to the JFrame
        this.add(this.canvas);

        // make the JFrame not resizable
        // (this way I dont have to worry about resize events)
        this.setResizable(false);

        this.addKeyListener (this);
        this.setFocusable(true);

        // size everything
        this.pack();

        // make sure we are not stopped
        this.stopped = false;

        // setup the world
        this.initializeWorld();
        // must be called after the world is initialized.
        restart();
        // show it
        this.setVisible(true);
		
        // start it
        this.start();

    }
        
    void createBat () {
        // create the bat
        bat1 = new Bat(Bat.BAT_HORIZONTAL, Color.BLUE,
                KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        bat1.translate(0.0, -5.55);
        bat2 = new Bat(Bat.BAT_HORIZONTAL, Color.RED,
                KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT);
        bat2.translate(0.0, 7.55);
        bat3 = new Bat(Bat.BAT_VERTICAL, Color.GREEN,
                KeyEvent.VK_DOWN, KeyEvent.VK_UP);
        bat3.translate(-8.76, 0.0);
        bat4 = new Bat(Bat.BAT_VERTICAL, Color.YELLOW,
                KeyEvent.VK_DOWN, KeyEvent.VK_UP);
        bat4.translate(8.76, 0.0);
        switch (mundoId) {
            case 0:
                bat = bat1;
                break;
            case 1:
                bat = bat2;
                break;
            case 2:
                bat = bat3;
                break;
            case 3:
                bat = bat4;
                break;
        }
        switch (numPlayers) {
            case 1:
                this.world.addBody(bat1);
                break;
            case 2:
                this.world.addBody(bat1);
                this.world.addBody(bat2);
                break;
            case 3:
                this.world.addBody(bat1);
                this.world.addBody(bat2);
                this.world.addBody(bat3);
                break;
            case 4:
                this.world.addBody(bat1);
                this.world.addBody(bat2);
                this.world.addBody(bat3);
                this.world.addBody(bat4);
                break;
        }
    }

    void createWalls () {
        Wall leftWall = new Wall (Wall.WALL_VERTICAL);
        leftWall.translate (-8.96, 0.0);
        this.world.addBody(leftWall);

        Wall rightWall = new Wall (Wall.WALL_VERTICAL);
        rightWall.translate (8.93, 0.0);
        this.world.addBody(rightWall);

        Wall topWall = new Wall (Wall.WALL_HORIZONTAL);
        topWall.translate (0.0, 7.75);
        this.world.addBody(topWall);

        Wall bottomWall = new Wall (Wall.WALL_HORIZONTAL);
        bottomWall.translate (0.0, -5.73);
        this.world.addBody(bottomWall);

        otherWalls = new Wall[numPlayers-1]; // exclude myself (this player).
        switch (mundoId) {
            case 0:
                myWall = bottomWall;
                switch (numPlayers) {
                    case 2:
                        otherWalls[0] = topWall;
                        break;
                    case 3:
                        otherWalls[0] = topWall;
                        otherWalls[1] = leftWall;
                        break;
                    case 4:
                        otherWalls[0] = topWall;
                        otherWalls[1] = leftWall;
                        otherWalls[2] = rightWall;
                        break;
                }
                break;
            case 1:
                myWall = topWall;
                switch (numPlayers) {
                    case 2:
                        otherWalls[0] = bottomWall;
                        break;
                    case 3:
                        otherWalls[0] = bottomWall;
                        otherWalls[1] = leftWall;
                        break;
                    case 4:
                        otherWalls[0] = bottomWall;
                        otherWalls[1] = leftWall;
                        otherWalls[2] = rightWall;
                        break;
                }
                break;
            case 2:
                myWall = leftWall;
                switch (numPlayers) {
                    case 3:
                        otherWalls[0] = bottomWall;
                        otherWalls[1] = topWall;
                        break;
                    case 4:
                        otherWalls[0] = bottomWall;
                        otherWalls[1] = topWall;
                        otherWalls[2] = rightWall;
                        break;
                }
                break;
            case 3:
                myWall = rightWall;
                switch (numPlayers) {
                    case 4:
                        otherWalls[0] = bottomWall;
                        otherWalls[1] = topWall;
                        otherWalls[2] = leftWall;
                        break;
                }
                break;
            default:
                break;
        }
    }

    /*
     * Creates game objects and adds them to the world.
     */
    void initializeWorld() {
        // create the world
        this.world = new World(new AxisAlignedBounds(Globals.WORLD_WIDTH_CM,
                Globals.WORLD_HEIGHT_CM));

        // create the ball
        ball = new Ball ();
        this.world.addBody(ball);

        createBat ();

        // create the walls
        createWalls();
    }
    public double denormX (double x) {
        return x * (7.66 + 7.63);
    }
    public double denormY (double y) {
        return y * (4.43 + 6.45);
    }
    public double normX (double x) {
        // x changes from -7.66 to 7.63
        return (x + 6.86) / (7.66 + 7.63);
    }
    public double normY (double y) {
        // y changes from -4.43 to 6.45
        return (-1 * y + 5.23) / (4.43 + 6.45);
    }

    public void keyPressed(KeyEvent e) {
        bat.keyPressed(e.getKeyCode());
        Message msg = new Message();
        msg.putMeta("id", String.valueOf(mundoId));
        switch (mundoId) {
            case 0:
            case 1:
                msg.putMeta("pos", String.valueOf(normX(bat.getWorldCenter().x)));
                break;
            case 2:
            case 3:
                msg.putMeta("pos", String.valueOf(normY(bat.getWorldCenter().x)));
                break;
        }
        pub.send(msg);
    }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
            
        }
	/**
	 * Start active rendering the example.
	 * This should be called after the JFrame has been shown.
	 */
	public void start() {
		// initialize the last update time
		this.last = System.nanoTime();
		// don't allow AWT to paint the canvas since we are
		this.canvas.setIgnoreRepaint(true);
		// enable double buffering (the JFrame has to be
		// visible before this can be done)
		this.canvas.createBufferStrategy(2);
		// run a separate thread to do active rendering
		// because we don't want to do it on the EDT
		Thread thread = new Thread() {
                        @Override
			public void run() {
                            // perform an infinite loop stopped
                            // render as fast as possible
                            while (!isStopped()) {
                                checkBallCollision();
                                renderLoop();
                                gameLoop ();
                                // you could add a Thread.yield(); or
                                // Thread.sleep(long) here to give the
                                // CPU some breathing room
                            }
			}
		};
		// set the game loop thread to a daemon thread so that
		// it cannot stop the JVM from exiting
		thread.setDaemon(true);
		// start the game loop
		thread.start();
	}
	        
        
        void restart () {
            ball.setLinearVelocity(0.0, 0.0);
            ball.clearAccumulatedForce();
            ball.clearAccumulatedTorque();
            ball.clearForce();
            ball.clearTorque();
            //ball.translateToOrigin();
            ball.translate(-7.3, 4.3);
            timer.schedule(new BallStarter(ball), 3000);
        }
        
        void checkBallCollision () {
            if (ball.isInContact(myWall)) {
                lifes--;
                System.out.println("collision with my wall");
                restart ();
            }
            for (Wall w : otherWalls) {
                if (ball.isInContact(w))
                    restart ();
            }
        }
        
	/* run the game and poll for bats' and the ball's positions.
         * update other players. */
        void gameLoop () {
            if (mundoId == 0) {
                Message msg = new Message();
                msg.putMeta("ballX", String.valueOf(normX(ball.getWorldCenter().x)));
                msg.putMeta("ballY", String.valueOf(normY(ball.getWorldCenter().y)));
                pub.send(msg);
            }
        }
        
        /**
	 * The method calling the necessary methods to update
	 * the game, graphics, and poll for input.
	 */
	protected void renderLoop() {
            
            // get the graphics object to render to
            Graphics2D g = (Graphics2D)this.canvas.getBufferStrategy().getDrawGraphics();

            // before we render everything im going to flip the y axis and move the
            // origin to the center (instead of it being in the top left corner)
            AffineTransform yFlip = AffineTransform.getScaleInstance(1, -1);
            AffineTransform move = AffineTransform.getTranslateInstance(400, -300);
            g.transform(yFlip);
            g.transform(move);

            // now (0, 0) is in the center of the screen with the positive x axis
            // pointing right and the positive y axis pointing up

            // render anything about the Example (will render the World objects)
            this.render(g);

            // dispose of the graphics object
            g.dispose();

            // blit/flip the buffer
            BufferStrategy strategy = this.canvas.getBufferStrategy();
            if (!strategy.contentsLost()) {
                    strategy.show();
            }

            // Sync the display on some systems.
            // (on Linux, this fixes event queue problems)
            Toolkit.getDefaultToolkit().sync();
        
            // update the World

            // get the current time
            long time = System.nanoTime();
            // get the elapsed time from the last iteration
            long diff = time - this.last;
            // set the last time
            this.last = time;
            // convert from nanoseconds to seconds
            double elapsedTime = diff / NANO_TO_BASE;
            // update the world with the elapsed time
            this.world.update(elapsedTime);
	}

	/**
	 * Renders the example.
	 * @param g the graphics object to render to
	 */
	protected void render(Graphics2D g) {
		// lets draw over everything with a white background
		g.setColor(Color.WHITE);
		g.fillRect(-400, -300, Globals.WORLD_WIDTH, Globals.WORLD_HEIGHT);
		
		// lets move the view up some
		g.translate(0.0, -1.0 * SCALE);
		
		// draw all the objects in the world
		for (int i = 0; i < this.world.getBodyCount(); i++) {
			// get the object
			GameObject go = (GameObject)this.world.getBody(i);
			// draw the object
			go.render(g);
		}
	}
	
	/**
	 * Stops the example.
	 */
	public synchronized void stop() {
		this.stopped = true;
	}
	
	/**
	 * Returns true if the example is stopped.
	 * @return boolean true if stopped
	 */
	public synchronized boolean isStopped() {
		return this.stopped;
	}

	public static void main(String[] args) {
            String arch = System.getProperty("os.arch");
            String libPath = "";
            if ("i386".equals(arch))
                libPath = System.getProperty("user.dir") + "/libumundoNativeJava.so";
            else if ("amd64".equals(arch))
                libPath = System.getProperty("user.dir") + "/libumundoNativeJava64.so";
            System.load(libPath);
		// set the look and feel to the system look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
                
		// create the example JFrame
		Game game = new Game(2);

	}
}

