
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Text;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Text;
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
public class MessageBox {
    private Text text;
    private Sprite background;
    int x;
    int y;
    boolean hidden = true;
    private Music m = new Music();
    
    public MessageBox(int x, int y, String message, Color color) {
        
                
        Font endor = new Font();
        try {
            endor.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
        } catch (IOException ex) {
            Logger.getLogger(MessageBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Texture t = new Texture();
        try {
            t.loadFromFile(Paths.get("src/graphics/ui/spritesheet/messageBox.png"));
        } catch (IOException ex) {
            Logger.getLogger(MessageBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        background = new Sprite(t);
        
        text = new Text(message, endor, (12*Game.SCALE));
        text.setColor(Color.BLACK);
        
        
        
        this.x = x;
        this.y = y;
        
        
        
        text.setPosition(x + 8*(Game.SCALE) , y + (Game.SCALE));
        background.setPosition(x, y);
        background.setScale(Game.SCALE/1, Game.SCALE/2);
        this.background = background;
        this.text = text;   
    }
    
    void draw(RenderWindow w) {
        w.draw(background);
        w.draw(text);
    }
    
    void hide(){
        hidden = true;
    }
    
    void showHide(){
        hidden = hidden != true;
    }

}
