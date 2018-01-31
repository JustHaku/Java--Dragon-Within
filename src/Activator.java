
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;

/* this is a pointless comment
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LBals
 */
public class Activator extends Actor {
    
    protected Sprite img;
    protected IntRect thing;
    protected String text;
    protected boolean activated = false;
    
    
    public Activator(Texture imgTexture, String text, int x, int y){
        this.text = text;
        
        int c1 = 0;
        int c2 = 5;
        
        thing = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16); // Creates the rectangle for the spritesheet.

        img = new Sprite(imgTexture, thing);
        img.setScale(Game.SCALE, Game.SCALE);
        
        this.x = x * Game.tileSize;
        this.y = y * Game.tileSize;
        
        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;
        
    }
    
    @Override
    boolean isInteractive(){
        return true;
    }
    
    @Override
    boolean within(int px, int py){
        return px > x - ((thing.height*1.2) * (Game.SCALE)) && px < x + ((thing.height*1.2) * (Game.SCALE))
                && py > y - ((thing.height*1.2) * (Game.SCALE)) && py < y + ((thing.height*1.2) * (Game.SCALE));
    }
    
    @Override
    void activate(){
        if(activated == false){
            System.out.println(text);            
        }
               
        activated = true;
    }

    @Override
    void calcMove(int minX, int minY, int maxX, int maxY) {
    }
    
    
}
