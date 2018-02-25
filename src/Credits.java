
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the settings menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class Credits implements State {

    private RenderWindow window;
    private int scale;
    private Font text_font;
    private Text[] text = new Text[6];
    private int screenHeight;
    private int screenWidth;
    private boolean paused = false;
    public static int returnTo = 0;
    private int offset = 0;

    public Credits(RenderWindow window, int scale) throws IOException {
      this.window = window;
      this.scale = scale;
      screenHeight = 160*scale;
      screenWidth = 288*scale;

      text_font = new Font();
      text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

      text[0] = new Text("Credits\n", text_font, screenHeight / 15);
      text[1] = new Text("Georgi Valchanov", text_font, screenHeight / 15);
      text[2] = new Text("Kirk Sparnenn", text_font, screenHeight / 15);
      text[3] = new Text("Luke Balshaw", text_font, screenHeight / 15);
      text[4] = new Text("Nathaniel Vanderpuye", text_font, screenHeight / 15);
      text[5] = new Text("Petros Soutzis", text_font, screenHeight / 15);
      
      
      for (int i=0; i < text.length; i++)
      {
        FloatRect bounds = text[i].getLocalBounds();
        text[i].setOrigin(bounds.width / 2, bounds.height / 2);
        text[i].setPosition(screenWidth / 2 + StateMachine.xOffset, (screenHeight) * (i+1) + StateMachine.yOffset);
        text[i].setColor(Color.BLACK);
      }
      text[0].setColor(Color.WHITE);
    }

    /*
  * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        paused = false;
        offset = 0;
        while (window.isOpen() && paused == false) {
            window.clear(new Color(104, 89, 183));
            for (int i=0; i < text.length; i++)
            {
              text[i].setPosition(screenWidth / 2 + StateMachine.xOffset, ((screenHeight / 10) * (i+1)) + offset + StateMachine.yOffset);   
              window.draw(text[i]);
            }
            window.display();           
            
            if (0-((text.length+1)*(screenHeight / 10)) > offset)
            {
              paused = true;              
            } else {
              offset--;
            }
            

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close();
                } else if (event.type == Event.Type.KEY_PRESSED) {
                    if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        paused = true;
                    }
                }
            }
        }
        return returnTo;
    }
}
