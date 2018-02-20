
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.*;

/**
 * Abstract class to be extended for adding collision into the game.
 *
 * @author LBals
 */
public abstract class Actor {

    protected int c1, c2;
    protected Sprite img;
    protected IntRect state;  // The Players current character model from the spritesheet.
    protected Drawable obj; //The object to set for collision.
    protected IntConsumer rotate;
    protected BiConsumer<Float, Float> setPosition;

    protected final float ps = (float) 1;

    int x = 0;	// Current X-coordinate.
    int y = 0;	// Current Y-coordinate.
    int r = 0;	// Change in rotation per cycle.
    int dx = 0;
    int dy = 0;
    int xv = 0;
    int yv = 0;

    // Work out where object should be for next frame.
    abstract void calcMove(int minX, int minY, int maxX, int maxY);

    // Method for detecting if it is inside another actor.
    boolean within(int x, int y) {
        return false;
    }

    boolean withinInteractive(int x, int y) {
        return false;
    }

    void setValid(int x, int y) {
        xv = x;
        yv = y;
    }

    /**
     * @param actor is the actor to be checked
     * @return Determines if the actor can be interacted with or returns false
     * if actor is redundant
     */
    boolean isInteractive() {
        //assume actor is redundant
        return false;
    }

    void activate() {

    }

    /**
     * @return Indicates if actor is the main player
     */
    boolean isPlayer() {
        return false;
    }

    // Reposition the object.
    void performMove() {
        setPosition.accept((float) x, (float) y);
    }

    // Render the object at its new position.
    void draw(RenderWindow w) {
        w.draw(obj);
    }
}
