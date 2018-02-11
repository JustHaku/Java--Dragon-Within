
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class Trader extends Actor {

    private String name;
    private MessageBox dialogue;
    private IntRect state;
    private Sprite img;
    private Game g;
    private Thread t1;
    private Runnable r;
    Trade trade;
    private final float ps = (float) 1;
    private Music m = new Music();
    private RenderWindow w;
    private Inventory playerInv;
    private Inventory traderInv;

    public Trader(String name, MessageBox dialogue, Texture imgTexture, int c1, int c2, Game g, RenderWindow w, Inventory playerInv, Inventory traderInv) {
        this.w = w;
        this.g = g;
        this.playerInv = playerInv;
        this.traderInv = traderInv;
        
        trade = new Trade(w, Game.SCALE, this.playerInv, this.traderInv);
        
        
        
        

        try {
            m.openFromFile(Paths.get("src/audio/ui/click1.ogg"));
        } catch (IOException ex) {
            Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
        }

        state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

        this.name = name;
        this.dialogue = dialogue;

        img = new Sprite(imgTexture, state);
        img.setScale(Game.SCALE / ps, Game.SCALE / ps);

        y = 0;
        y = 0;

        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;

        r = () -> {
            nextLock();

            t1 = new Thread(r);
        };

        t1 = new Thread(r);
    }

    @Override
    void calcMove(int minX, int minY, int maxX, int maxY) {

    }

    void nextLock() {
        dialogue.showHide();
        m.play();
        Game.player1.movementLock = true;
        try {
            Thread.sleep(350);
        } catch (InterruptedException ex) {
            Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
        }
        while (true) {
            if (Keyboard.isKeyPressed(Keyboard.Key.E)) {
                dialogue.showHide();
                m.play();
                Game.player1.movementLock = false;
                //StateMachine.states[4] = new Trade(w, Game.SCALE, g.playerInv, g.traderInv);

                try {
                    Thread.sleep(350);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
        
        synchronized(this){
            StateMachine.states[10] = trade;
            Game.state = 10;            
        }
        
        
        
        
    }

    boolean isHidden() {
        return dialogue.hidden;
    }

    void drawMessage(RenderWindow w) {
        dialogue.draw(w);
    }

    @Override
    synchronized void activate() {

        StateMachine.toggleLock();
        
        StateMachine.toggleLock();
        if (!t1.isAlive()) {
            t1.start();
        }
    }

    @Override
    boolean isInteractive() {
        return true;

    }

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean within(int px, int py) {
        return px > x - ((state.height * 1.2) * (Game.SCALE)) && px < x + ((state.height * 1.2) * (Game.SCALE))
                && py > y - ((state.height * 1.2) * (Game.SCALE)) && py < y + ((state.height * 1.2) * (Game.SCALE));
    }

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean withinInteractive(int px, int py) {
        return px > x - ((state.height * 1) * (Game.SCALE)) && px < x + ((state.height * 1) * (Game.SCALE))
                && py > y - ((state.height * 1) * (Game.SCALE)) && py < y + ((state.height * 1) * (Game.SCALE));
    }

    public void setTilePosition(int x, int y) {
        this.setPosition(x * Game.tileSize, y * Game.tileSize);
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
