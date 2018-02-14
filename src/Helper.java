
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderStates;
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
public class Helper {

    private boolean hidden = true;
    private ArrayList<Text> overlay = new ArrayList<>();

    public Helper() {
        Font endor = new Font();
        try {
            endor.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
        } catch (IOException ex) {
            Logger.getLogger(MessageBox.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < Game.gridWidth; i++) {
            for (int j = 0; j < Game.gridHeight; j++) {
                Text t = new Text(Integer.toString(i) + ", " + Integer.toString(j), endor, 4 * Game.SCALE);
                overlay.add(t);
                t.setColor(Color.MAGENTA);
                t.setPosition(i * Game.tileSize, j * Game.tileSize);

            }
        }

    }

    void toggleHidden() {
        if (hidden == false) {
            hidden = true;
        } else if (hidden == true) {
            hidden = false;
        }
    }

    void Draw(RenderWindow w) {
        if (hidden == false) {
            for (Text tx : overlay) {
                w.draw(tx);
            }
        }

    }

}
