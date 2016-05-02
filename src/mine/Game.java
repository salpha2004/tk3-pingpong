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
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.dyn4j.dynamics.World;
import org.dyn4j.geometry.Vector2;
import org.umundo.core.Message;

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
        
        
        Bar bar;
        HorWall bar1Wall, bar2Wall;
        int lifes = 3;
        Ball ball;
        
        Timer timer = new Timer(true);
        
        Mundo m;
        
        void waitForOthers (int num) {
            Message msg = new Message();
            int joined = 1;
            System.out.println(m.getId());
            try {
                Thread.sleep (1000);
            }
            catch (InterruptedException ex) {}
            /* game's coordinator has id=0. */
            if (m.getId() == 0) {
                // while not all players have joined...
                while (joined < num) {
                    if (msg.getMeta().containsKey("ready"))
                        joined++;
                }
                // after jumping out of the loop, all players have joined.
                msg.putMeta("start", "");
            }
            else {
                msg.putMeta("ready", "");
                // while "start" cmd not received from the coordinator...
                while (msg.getMeta().containsKey("start") == false) {
                    Thread.yield();
                }
            }
        }
        
	public Game (int numberOfPlayers) {
            super("TK3 - PingPong");
            
            // init uMundo
            m = Mundo.getInstance();
            
            waitForOthers (numberOfPlayers);
            
            // setup the JFrame
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // create the size of the window
            Dimension size = new Dimension(800, 600);

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
	}
        
	/*
	 * Creates game objects and adds them to the world.
	 */
    void initializeWorld() {
        // create the world
        this.world = new World();

        // create the ball
        ball = new Ball ();
        this.world.addBody(ball);


        // create the bars
        bar = new Bar(Color.BLUE, KeyEvent.VK_LEFT,
                KeyEvent.VK_RIGHT);
        bar.translate(0.0, 7.55);
        this.world.addBody(bar);

        // create the walls
        VerWall leftWall = new VerWall ();
        leftWall.translate (-8.9, 0.0);
        this.world.addBody(leftWall);

        VerWall rightWall = new VerWall ();
        rightWall.translate (8.87, 0.0);
        this.world.addBody(rightWall);

        bar1Wall = new HorWall ();
        bar1Wall.translate (0.0, 7.75);
        this.world.addBody(bar1Wall);

        bar2Wall = new HorWall ();
        bar2Wall.translate (0.0, -5.73);
        this.world.addBody(bar2Wall);
    }

        public void keyPressed(KeyEvent e) {
            bar.keyPressed(e.getKeyCode());
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
            ball.translateToOrigin();
            timer.schedule(new BallStarter(ball), 3000);
        }
        
        void checkBallCollision () {
            if (ball.isInContact(bar1Wall)) {
                lifes--;
                System.out.println("bar1");
                restart ();
            }
        }
        
	/* run the game and poll for bars' and the ball's positions.
         * update other players. */
        void gameLoop () {
            
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
		g.fillRect(-400, -300, 800, 600);
		
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
            String libPath = System.getProperty("user.dir") + "/libumundoNativeJava.so";
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
                
                // TODO: get number of players via GUI
                
                
		// create the example JFrame
		Game game = new Game(2);
                JFrame gameBoard = new JFrame("Game Board");
                JPanel board = new JPanel(new GridLayout(1, 3));
                JLabel time = new JLabel("3");
                JLabel p1Life = new JLabel ("1");
                
                JLabel p2Life = new JLabel ("2");
                board.add(p1Life);
                board.add(time);
                board.add(p2Life);
                gameBoard.getContentPane().add (board, BorderLayout.CENTER);
                gameBoard.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                gameBoard.pack();
                gameBoard.setVisible(true);
		
		// show it
		game.setVisible(true);
		
		// start it
		game.start();
	}
}
