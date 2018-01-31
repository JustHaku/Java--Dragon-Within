import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard;

/**
 * Main class for creating players in the game.
 *
 * @author LBals
 */
public class Player extends Actor {

    protected Sprite img;
    protected IntRect state; // The Players current character model from the spritesheet.
    protected int[] held_items = new int[4];
    protected int c1, c2, level, exp, health, mana, speed, attack, defence;
    protected final int max_health, max_mana;

    protected final float ps = (float)1;

    /**
     * Constructs the player. Gets Spritesheet and forms a rectangle from the
     * hard-coded value around the desired sprite.
     *
     * @param imgTexture Spritesheet for player texture.
     */
    public Player(Texture imgTexture) {
        c1 = 1; // Both c1 and c2 represent the hardcoded character sprite from the sheet.
        c2 = 6;

        max_health = 100;
        max_mana = 100;

        state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16); // Creates the rectangle for the spritesheet.

        img = new Sprite(imgTexture, state);
        img.setScale(Game.SCALE / ps, Game.SCALE / ps); // Changes player scale to 2/3 of tile size.

        x = 0; // Default position.
        y = 0;

        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;
    }

    void heal(int heal){
        if(health + heal >= max_health ){
            health = max_health;
        }else{
            health += heal;
        }
    }

    void regen(int regen){
        if(mana + regen >= max_mana ){
            mana = max_mana;
        }else{
            mana += regen;
        }
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    void moveLeft() {
        x -= Game.spd;
    }

    void moveRight() {
        x += Game.spd;
    }

    void moveUp() {
        y -= Game.spd;
    }

    void moveDown() {
        y += Game.spd;
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

        for (Actor a : Game.maps.get(Game.worldNum).getActor()) {
                if (a.obj != obj && a.within(x, y) && a.isInteractive() == false) {
                    if(x > a.x){
                        moveRight();
                    }
                    if(x < a.x){
                        moveLeft();
                    }

                    if(y > a.y){
                        moveDown();
                    }
                    if(y < a.y){
                        moveUp();
                    }
                }

                if(a.isInteractive() == true && a.within(x,y) && Keyboard.isKeyPressed(Keyboard.Key.E)){
                    try {
                        a.activate();
                        Thread.sleep(200);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
        }
    }
}
