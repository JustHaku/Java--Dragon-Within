
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

/**
 * Creates a portal which loads another map when the player is within() it.
 *
 * @author LBals
 */
public class Portal extends Actor {

    private final Sprite img;
    private final IntRect piece;
    private final Player player1;
    private final int p1;
    private final int p2;
    private final int w;
    private final ArrayList<Actor> actors;
    private Game m;
    private Music a = null;

    /**
     * Creates a portal which loads another map when the player is within() it.
     *
     * @param player1 The player which interacts with portal
     * @param imgTexture Spritesheet of the world
     * @param x X location of the portal
     * @param y Y location of the portal
     * @param c1 X Location of the spritesheet piece
     * @param c2 Y Location of the spritesheet piece
     * @param p1 X Location of where to set the player when it enter the portal
     * @param p2 Y location of where to set the player when it enters the portal
     * @param w Index of world number to port to
     * @param actors The arraylist of actors you want to act upon(The one
     * containing the player)
     */

    public Portal(Player player1, Texture imgTexture, int x, int y, int c1, int c2, int p1, int p2, int w, ArrayList<Actor> actors, Game m) {
        // Draws rectange around selected piece
        this.actors = actors;
        this.w = w;
        this.p1 = p1 * Game.tileSize;
        this.p2 = p2 * Game.tileSize;
        this.player1 = player1;
        this.m = m;
        piece = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

        img = new Sprite(imgTexture, piece);
        img.setScale(Game.SCALE, Game.SCALE); // Sets scale

        this.x = x * Game.tileSize;
        this.y = y * Game.tileSize;

        setPosition = img::setPosition;

    }

    public Portal(Player player1, Texture imgTexture, int x, int y, int c1, int c2, int p1, int p2, int w, ArrayList<Actor> actors, Game m, Music a) {
        // Draws rectange around selected piece
        this.actors = actors;
        this.w = w;
        this.p1 = p1 * Game.tileSize;
        this.p2 = p2 * Game.tileSize;
        this.player1 = player1;
        this.m = m;
        piece = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

        img = new Sprite(imgTexture, piece);
        img.setScale(Game.SCALE, Game.SCALE); // Sets scale

        this.x = x * Game.tileSize;
        this.y = y * Game.tileSize;

        setPosition = img::setPosition;

        this.a = a;

    }

    @Override
    void calcMove(int minx, int miny, int maxx, int maxy) {
        // Do this if actor hits this object.
        for (Actor a : actors) {
            if (a.obj != obj && a.within(x, y) && a.isPlayer()) {

                m.changeWorld(w);
                player1.setPosition(p1, p2);
                if (this.a != null) {
                    this.a.play();
                }
                try {
                    Thread.sleep(250);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Portal.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }
    }

    @Override
    void draw(RenderWindow w) {
        w.draw(img);
    }

}
