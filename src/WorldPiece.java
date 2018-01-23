
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

/**
 * Selects a world piece from a Spritesheet.
 *
 * @author LBals
 */
public class WorldPiece {

    private final Sprite img;
    private final IntRect piece;

    /**
     * Selects a world piece from a Spritesheet.
     *
     * @param imgTexture Spritesheet from which to select piece.
     * @param x X coordinate of location on render window.
     * @param y Y coordinate of location on render window.
     * @param c1 X coordinate of piece on Spritesheet.
     * @param c2 Y coordinate of piece on Spritesheet.
     */
    public WorldPiece(Texture imgTexture, int x, int y, int c1, int c2) {
        // Draws rectange around selected piece
        piece = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

        img = new Sprite(imgTexture, piece);
        img.setPosition(x * Game.tileSize, y * Game.tileSize); // Sets position
        img.setScale(Game.SCALE, Game.SCALE); // Sets scale
    }

    void draw(RenderWindow w) {
        w.draw(img);
    }
}
