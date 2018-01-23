
import java.io.IOException;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

/**
 * Class for adding a graphics barrier to the game.
 *
 * @author LBals
 */
public class Barrier extends Actor {

    private final Sprite img;

    /**
     * Creates a barrier in game. Stops actors from moving past this barrier.
     *
     * @param barrierTexture Texture of barrier.
     * @param x X tile coordinate of desired barrier.
     * @param y Y tile coordinate of desired barrier.
     * @throws IOException
     */
    public Barrier(Texture barrierTexture, int x, int y) throws IOException {
        img = new Sprite(barrierTexture); // New barrier object.

        // Changes coordinates so that they are tile-bound and not pixel bound.
        this.x = x * Game.tileSize;
        this.y = y * Game.tileSize;

        obj = img; //Makes img the object
        setPosition = img::setPosition;
    }

    @Override
    void calcMove(int minx, int miny, int maxx, int maxy) {
        // Do this if actor hits this object.
        for (Actor a : Game.returnActors()) {
            if (a.obj != obj && a.within(x, y)) {
                if (a.x <= x) {
                    a.x -= Game.spd;
                } else if (a.x >= x) {
                    a.x += Game.spd;
                }
                if (a.y <= y) {
                    a.y -= Game.spd;
                } else if (a.y >= y) {
                    a.y += Game.spd;
                }
            }
        }
    }
}
