
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Text;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;

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
    int x;
    int y;
    boolean hidden = true;
    
    public MessageBox(int x, int y, String message, Color color) {
        Font endor = new Font();
        try {
            endor.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
        } catch (IOException ex) {
            Logger.getLogger(MessageBox.class.getName()).log(Level.SEVERE, null, ex);
        }

        text = new Text(message, endor, 4*Game.SCALE);
        text.setColor(Color.BLACK);
        
        
        
        this.x = x;
        this.y = y;
        
        text.setPosition(x, y);
        
        this.text = text;   
    }
    
    void draw(RenderWindow w) {
        
        w.draw(text);
    }
    
    void showHide(){
        hidden = hidden != true;
    }

}
