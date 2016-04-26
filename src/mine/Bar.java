package mine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Vector2;

public class Bar extends GameObject {
        final double WIDTH = 2.5;
        final double HEIGHT = 0.3;
        final double SPEED_UP = 300.0;
        final double SLOW_DOWN = 5.0;
	
        Color color;
        Rectangle rect;
        int leftKey, rightKey;
        
        
        public Bar (Color c, int left, int right) {
            leftKey = left;
            rightKey = right;
            color = c;
            rect = new Rectangle (WIDTH, HEIGHT);
            this.addFixture(new BodyFixture(rect));
            // allow linear velocity to change the position via arrow keys.
            this.setMass(MassType.INFINITE);
            this.setGravityScale(0.0); // not affected by gravity.
            // lose linear velocity over the time.
            this.setLinearDamping(SLOW_DOWN);
        }
        
        public void keyPressed (int key) {
            if (key == leftKey) {
                //this.applyForce(new Vector2(-SPEED_UP, 0.0));
                this.shift(new Vector2(-0.5, 0.0));
            }
            if (key == rightKey) {
                this.applyForce(new Vector2(SPEED_UP, 0.0));
                this.shift(new Vector2(0.5, 0.0));
            }
        }
        
        public void keyReleased (int key) {
        }
        
	/**
	 * Draws the body.
	 * <p>
	 * Only coded for polygons and circles.
	 * @param g the graphics object to render to
	 */
        @Override
	public void render(Graphics2D g) {
            // save the original transform
            AffineTransform ot = g.getTransform();

            // transform the coordinate system from world coordinates to local coordinates
            AffineTransform lt = new AffineTransform();
            lt.translate(this.transform.getTranslationX() * Globals.SCALE,
                    this.transform.getTranslationY() * Globals.SCALE);
            lt.rotate(this.transform.getRotation());

            // apply the transform
            g.transform(lt);

            // loop over all the body fixtures for this body
            for (BodyFixture fixture : this.fixtures) {
                    // get the shape on the fixture
                    Convex convex = fixture.getShape();
                    Graphics2DRenderer.render(g, convex,
                            Globals.SCALE, color);
            }

            // set the original transform
            g.setTransform(ot);
	}
}
