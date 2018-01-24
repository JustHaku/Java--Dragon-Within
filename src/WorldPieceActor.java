
import java.util.ArrayList;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class WorldPieceActor extends Actor {

    private final Sprite img;
    private final IntRect piece;
    private final ArrayList<Actor> actors;

    /**
     * Selects a world piece from a Spritesheet.
     *
     * @param imgTexture Spritesheet from which to select piece.
     * @param x X coordinate of location on render window.
     * @param y Y coordinate of location on render window.
     * @param c1 X coordinate of piece on Spritesheet.
     * @param c2 Y coordinate of piece on Spritesheet.
     */
    public WorldPieceActor(Texture imgTexture, int x, int y, int c1, int c2, ArrayList<Actor> actors) {
        this.actors = actors;
        // Draws rectange around selected piece
        piece = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

        img = new Sprite(imgTexture, piece);
        img.setScale(Game.SCALE, Game.SCALE); // Sets scale

        this.x = x * Game.tileSize;
        this.y = y * Game.tileSize;

        setPosition = img::setPosition;

    }

    @Override
    void calcMove(int minx, int miny, int maxx, int maxy) {
        // Do this if actor hits this object.
        for (Actor a : actors) {
            if (a.obj != obj && a.within(x, y)) {
                System.out.println("Collision!");
                if (a.x <= x) {
                    a.x -= Game.spd;
                }
                else if (a.x >= x) {
                    a.x += Game.spd;
                }
                if (a.y <= y) {
                    a.y -= Game.spd;
                }
                else if (a.y >= y) {
                    a.y += Game.spd;
                }
            }
        }
    }

    @Override
    void draw(RenderWindow w) {
        w.draw(img);
    }
}