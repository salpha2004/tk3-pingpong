package mine;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Color;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.MassType;
import org.dyn4j.geometry.Rectangle;

/**
 *
 * @author saeed
 */
public class Wall extends GameObject {
    public static final int WALL_HORIZONTAL = 0;
    public static final int WALL_VERTICAL = 1;
    
    public Wall (int type) {
        Rectangle wall = null;
        if (type == WALL_HORIZONTAL)
            wall = new Rectangle (20.0, 0.1);
        else if (type == WALL_VERTICAL)
            wall = new Rectangle (0.1, 20.0);
        this.addFixture(new BodyFixture(wall));
        this.setMass(MassType.INFINITE);
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
                       Globals.SCALE, Color.BLACK);
       }

       // set the original transform
       g.setTransform(ot);
    }
}
