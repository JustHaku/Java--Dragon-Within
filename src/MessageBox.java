
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

        int textSize = 12 * Game.SCALE / 2;
        text = new Text(message, endor, (textSize));
        text.setColor(Color.BLACK);

        this.x = x + StateMachine.xOffset;
        this.y = y + StateMachine.yOffset;

        background.setPosition(this.x, this.y);
        background.setScale(Game.SCALE / 1, Game.SCALE / 2);
        text.setPosition((background.getGlobalBounds().left + (Game.SCALE * 8)), (background.getGlobalBounds().top + background.getGlobalBounds().height / 2 - ((text.getGlobalBounds().height / 4) * 3))  );
        this.background = background;
        this.text = text;
    }
    
    public void editText(String s){
        text.setString(s);
    }
    
    public String getText(){
        return text.getString();
    }


    void draw(RenderWindow w) {
        w.draw(background);
        w.draw(text);
    }

    void hide() {
        hidden = true;
    }

    void showHide() {
        hidden = hidden != true;
    }

}
