

import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

/**
 * Main class for creating players in the game.
 *
 * @author LBals
 */
public class Player extends Actor {

    private final Sprite img;
    private final IntRect state; // The Players current character model from the spritesheet.
    private final int c1;
    private final int c2;

    /**
     * Constructs the player. Gets Spritesheet and forms a rectangle from the
     * hard-coded value around the desired sprite.
     *
     * @param imgTexture Spritesheet for player texture.
     */
    public Player(Texture imgTexture) {
        c1 = 1; // Both c1 and c2 represent the hardcoded character sprite from the sheet.
        c2 = 6;

        state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16); // Creates the rectangle for the spritesheet.

        img = new Sprite(imgTexture, state);
        img.setScale(Game.SCALE / (float) 1.333, Game.SCALE / (float) 1.333); // Changes player scale to 2/3 of tile size.

        x = 1; // Default position.
        y = 1;

        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;
    }
    
    void setPosition(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean within(int px, int py) {
        return px > x - (state.width * (Game.SCALE / (float) 1.333)) && px < x + (state.width * (Game.SCALE / (float) 1.333))
                && py > y - (state.width * (Game.SCALE / (float) 1.333)) && py < y + (state.width * (Game.SCALE / (float) 1.333));
    }
    
    @Override
    boolean isPlayer(){
        return true;
    }

    @Override
    void calcMove(int minx, int miny, int maxx, int maxy) {
        // Do this if object hits window (stops out of bounds).
        if (x <= minx || x >= maxx) {
            if (x <= minx) {
                x += Game.spd;
            } else {
                x -= Game.spd;
            }
        }
        if (y <= miny || y >= maxy) {
            if (y <= minx) {
                y += Game.spd;
            } else {
                y -= Game.spd;
            }
        }
        // For detecting if collision with another actor.
        for (Actor a : Game.returnActors()) {
            if (a.obj != obj && a.within(x, y)) {
                System.out.println("Collision");
            }
        }
    }
}
