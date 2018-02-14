
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the magic menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class MagicMenu extends Menu implements State {

    /*private RenderWindow window;
  private int scale;
  private Font text_font;*/
    private boolean paused = false;
    /*private int screenHeight;
  private int screenWidth;*/
    private RectangleShape menuRect;
    private RectangleShape playerRect;
    public static boolean returnTo = false;

    public MagicMenu(RenderWindow window, int scale, int options_num) throws IOException {
        menuWindow(window, scale, options_num);
        /*this.window = window;
    this.scale = scale;
    screenHeight = 160*scale;
    screenWidth = 288*scale;
    option = 1;
    
    text_font = new Font();
    text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
    
    menuRect = new RectangleShape(new Vector2f((screenWidth/4)*3, screenHeight - 10));
    menuRect.setFillColor(new Color(11,2,138));
    menuRect.setPosition(5,5);

    playerRect = new RectangleShape(new Vector2f((screenWidth/4) - 15, screenHeight - 10));
    playerRect.setFillColor(new Color(11,2,138));
    playerRect.setPosition(((screenWidth/4)*3)+10,5);
    
    text[0] = new Text("Magic", text_font, screenHeight / 15);
    bounds = text[0].getLocalBounds();
    text[0].setOrigin(bounds.width / 2, bounds.height / 2);
    text[0].setPosition((screenWidth / 8) * 7, screenHeight / 20);*/
    }

    /*
  * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        returnTo = true;
        paused = false;
        while (window.isOpen() && paused == false) {
            window.clear(Color.BLACK);
            window.draw(menuRect);
            window.draw(playerRect);
            showSelection(text, option);
            drawText(text);
            window.display();

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
        return 3;
    }
}
