package mine;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;

public class Ball extends GameObject {
    
    final double BALL_RAD = 0.3;
    
    Circle circ;
    
    public Ball () {
        circ = new Circle (BALL_RAD);
        BodyFixture c = new BodyFixture(circ);
        c.setRestitution(1.02);
        c.setFriction(0.0);
        this.addFixture(c);
        this.setMass (MassType.NORMAL);
        this.setGravityScale(0.0);
        
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
                        Globals.SCALE, Color.RED);
        }

        // set the original transform
        g.setTransform(ot);
    }
}
