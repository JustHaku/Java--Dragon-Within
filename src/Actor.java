
import java.util.function.BiConsumer;
import java.util.function.IntConsumer;
import org.jsfml.graphics.Drawable;
import org.jsfml.graphics.RenderWindow;

/**
 * Abstract class to be extended for adding collision into the game.
 *
 * @author LBals
 */
public abstract class Actor {

    Drawable obj; //The object to set for collision.
    IntConsumer rotate;
    BiConsumer<Float, Float> setPosition;

    int x = 0;	// Current X-coordinate.
    int y = 0;	// Current Y-coordinate.
    int r = 0;	// Change in rotation per cycle.

    // Work out where object should be for next frame.
    abstract void calcMove(int minX, int minY, int maxX, int maxY);

    // Method for detecting if it is inside another actor.
    boolean within(int x, int y) {
        return false;
    }

    // Reposition the object.
    void performMove() {
        //rotate.accept(r);
        setPosition.accept((float) x, (float) y);
    }
    
    boolean isPlayer(){
        return false;
    }

    // Render the object at its new position.
    void draw(RenderWindow w) {
        w.draw(obj);
    }
}
