
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
    int dx = 0;
    int dy = 0;
    int xv = 0;
    int yv = 0;


    // Method for detecting if it is inside another actor.
    boolean within(int x, int y) {
        return false;
    }

    void setValid(int x,int y){
        xv = x;
        yv = y;
    }

    // Work out where object should be for next frame.
    abstract void calcMove(int minx, int miny, int maxx, int maxy);

    //
    boolean isPlayer()
    {
        return false;
    }

    // Reposition the object.
    void performMove() {
        //rotate.accept(r);
        setPosition.accept((float) x, (float) y);
    }

    // Render the object at its new position.
    void draw(RenderWindow w) {
        w.draw(obj);
    }
}
