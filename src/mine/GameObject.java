/*
 * Simple workaround to implement "render" as an abstract
 * method without changing dyn4j source code.
 */

package mine;

import java.awt.Graphics2D;

import org.dyn4j.dynamics.Body;

public abstract class GameObject extends Body {
    public abstract void render (Graphics2D g);
}
