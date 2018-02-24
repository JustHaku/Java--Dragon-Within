
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.system.Clock;
import org.jsfml.window.Keyboard;

/**
 * Class for creating main player of the game.
 *
 * @author LBals
 */
public class Player extends Character {

    ArrayList<WorldMap> m = new ArrayList<>();
    private Game g;
    private boolean moving = false;
    public boolean movementLock = false;
    private final Clock footstepsTimer = new Clock();

    /**
     * Constructs the player. Gets Spritesheet and forms a rectangle from the
     * hard-coded value around the desired sprite
     *
     * @param imgTexture Spritesheet for player texture.
     */
    public Player(Texture imgTexture, ArrayList<WorldMap> m, Game g) {
        c1 = 1; // Both c1 and c2 represent the hardcoded co-ordinates for character sprite from the sheet.
        c2 = 11;
        this.g = g;
        this.m = m;

        name = "Main Player";
        max_health = 100;
        max_mana = 100;
        health = max_health;
        mana = max_mana;
        attack = 10;
        defence = 10;
        speed = 10;
        exp = 0;
        level = 1;
        isFriendly = true;

        state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16); // Creates the rectangle for the spritesheet.

        //left off here
        img = new Sprite(imgTexture, state);
        img.setScale(Game.SCALE / ps, Game.SCALE / ps); // Changes player scale to 2/3 of tile size.

        x = 0; // Default position.
        y = 0;

        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;
    }

    boolean isMoving() {
        return moving;
    }

    void moveLeft() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            x -= (Game.spd * Game.SCALE);
            footstepsTimer.restart();
        }

        //}
    }

    void moveRight() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            x += (Game.spd * Game.SCALE);
            footstepsTimer.restart();
        }

        //}
    }

    void moveUp() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            y -= (Game.spd * Game.SCALE);
            footstepsTimer.restart();

        }

        //}
    }

    void moveDown() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            y += (Game.spd * Game.SCALE);
            footstepsTimer.restart();
        }

        //}
    }

    public void setTilePosition(int x, int y) {
        this.setPosition(x * Game.tileSize, y * Game.tileSize);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean within(int px, int py) {
        return px > x - (state.width * (Game.SCALE / (float) ps) * ps) && px < x + (state.width * (Game.SCALE / (float) ps))
                && py > y - (state.height * (Game.SCALE / (float) ps) * ps) && py < y + (state.height * (Game.SCALE / (float) ps));
    }

    @Override
    boolean isPlayer() {
        return true;
    }

    @Override
    void calcMove(int minx, int miny, int maxx, int maxy) {
        // Do this if object hits window (stops out of bounds).
        if (x <= minx || x >= (maxx - 16 * Game.SCALE)) {
            if (x <= minx) {
                x += Game.spd * Game.SCALE;
            } else {
                x -= Game.spd * Game.SCALE;
            }
        }
        if (y <= miny || y >= (maxy - 16 * Game.SCALE)) {
            if (y <= minx) {
                y += Game.spd * Game.SCALE;
            } else {
                y -= Game.spd * Game.SCALE;
            }
        }

        m.get(g.worldNum).getActor().stream().map((a) -> {
            if (a.obj != obj && a.within(x, y) && a.isInteractive() == false) {
                if (x > a.x) {
                    moveRight();
                }
                if (x < a.x) {
                    moveLeft();
                }

                if (y > a.y) {
                    moveDown();
                }
                if (y < a.y) {
                    moveUp();
                }
            } else if (a.obj != obj && a.withinInteractive(x, y) && a.isInteractive() == true) {
                if (x > a.x) {
                    moveRight();
                }
                if (x < a.x) {
                    moveLeft();
                }

                if (y > a.y) {
                    moveDown();
                }
                if (y < a.y) {
                    moveUp();
                }

            }
            return a;
        }).filter((a) -> (a.isInteractive() == true && a.within(x, y) && Keyboard.isKeyPressed(Keyboard.Key.E))).forEachOrdered((a) -> {
            a.activate();
        });
    }
}
