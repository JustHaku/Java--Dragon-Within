
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
     private final Clock animationTimer = new Clock();
    private int c1 = 1; // Both c1 and c2 represent the hardcoded co-ordinates for character sprite from the sheet.
    private int c2 = 2;
    Texture playerTexture;
    Sprite img;
    IntRect state;
    /**
     * Constructs the player. Gets Spritesheet and forms a rectangle from the
     * hard-coded value around the desired sprite
     *
     * @param imgTexture Spritesheet for player texture.
     */
    public Player(Texture imgTexture, ArrayList<WorldMap> m, Game g) {
        this.g = g;
        this.m = m;
        
        playerTexture = imgTexture;

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
        x = 0; // Default position.
        y = 0;
        state = new IntRect(((c1 * 64) + c1), ((c2 * 64) + c2), 64, 64); // Creates the rectangle for the spritesheet.
        //left off here
        img = new Sprite(playerTexture, state);
        img.setScale((Game.SCALE / ps) / 4, (Game.SCALE / ps) / 4); // Changes player scale to 2/3 of tile size.
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
        if (animationTimer.getElapsedTime ().asMilliseconds() > 40){
            if(getC1() < 8){
                c1++;
            }
            else{
                c1 = 0;
            }
            animationTimer.restart();
        }
        this.updateImg(c1,9);
        //}
    }

    void moveRight() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            x += (Game.spd * Game.SCALE);
            footstepsTimer.restart();
        }
        if (animationTimer.getElapsedTime ().asMilliseconds() > 40){
            if(getC1() < 8){
                c1++;
            }
            else{
                c1 = 0;
            }
            animationTimer.restart();
        }
        this.updateImg(c1,11);
        //}
    }

    void moveUp() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            y -= (Game.spd * Game.SCALE);
            footstepsTimer.restart();
        }
        if (animationTimer.getElapsedTime ().asMilliseconds() > 40){
            if(getC1() < 8){
                c1++;
            }
            else{
                c1 = 0;
            }
            animationTimer.restart();
        }
        this.updateImg(c1,8);

        //}
    }

    void moveDown() {
        //if (footstepsTimer.getElapsedTime().asMilliseconds() > speed) {
        if (movementLock == false) {
            y += (Game.spd * Game.SCALE);
            footstepsTimer.restart();
        }
        if (animationTimer.getElapsedTime ().asMilliseconds() > 40){
            if(getC1() < 8){
                c1++;
            }
            else{
                c1 = 0;
            }
            animationTimer.restart();
        }
        this.updateImg(c1,10);
        //}
    }

    public void setTilePosition(int x, int y) {
        this.setPosition(x * Game.tileSize + StateMachine.xOffset, y * Game.tileSize + StateMachine.yOffset);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    public int getC1() {
        return c1;
    }
    
    public int getC2() {
        return c2;
    }
    
    
    public void updateImg(int i , int j){
        
        c1 = i;
        c2 = j;
        
        state = new IntRect(((c1 * 64) + c1), ((c2 * 64) + c2), 64, 64); // Creates the rectangle for the spritesheet.

        img.setTextureRect(state);
    }
    

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean within(int px, int py) {
        return px > x - (state.width * ((Game.SCALE / (float) ps) / 4) * ps) && px < x + (state.width * ((Game.SCALE / (float) ps) / 4))
                && py > y - (state.height * ((Game.SCALE / (float) ps) / 4) * ps) && py < y + (state.height * ((Game.SCALE / (float) ps) / 4));
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
        try {
            m.get(g.worldNum).getActor().stream().map((a) -> {
                if (a.obj != obj && a.within(x, y) && a.isInteractive() == false) {
                    if (x > a.x) {
                        x += (Game.spd * Game.SCALE);
                    }
                    if (x < a.x) {
                        x -= (Game.spd * Game.SCALE);
                    }

                    if (y > a.y) {
                        y += (Game.spd * Game.SCALE);
                    }
                    if (y < a.y) {
                        y -= (Game.spd * Game.SCALE);
                    }
                } else if (a.obj != obj && a.withinInteractive(x, y) && a.isInteractive() == true) {
                    if (x > a.x) {
                        x += (Game.spd * Game.SCALE);
                    }
                    if (x < a.x) {
                        x -= (Game.spd * Game.SCALE);
                    }

                    if (y > a.y) {
                        y += (Game.spd * Game.SCALE);
                    }
                    if (y < a.y) {
                        y -= (Game.spd * Game.SCALE);
                    }

                }
                return a;
            }).filter((a) -> (a.isInteractive() == true && a.within(x, y) && Keyboard.isKeyPressed(Keyboard.Key.E))).forEachOrdered((a) -> {
                if (movementLock == false) {
                    a.activate();
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

            });

        }catch(NullPointerException e){
            
        }

    }
}
