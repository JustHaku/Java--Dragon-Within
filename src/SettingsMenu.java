
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
public class SettingsMenu extends Menu implements State {

    /*private RenderWindow window;
  private int scale;
  private Font text_font;*/
    private Text text;
    private boolean paused = false;
    /*private int screenHeight;
  private int screenWidth;*/
    public static int returnTo = 0;

    public SettingsMenu(RenderWindow window, int scale, int options_num) throws IOException {
        /*this.window = window;
    this.scale = scale;
    screenHeight = 160*scale;
    screenWidth = 288*scale;*/

        menuWindow(window, scale, options_num);

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        text = new Text("Settings Menu\nPlace Holder", text_font, screenHeight / 15);
        bounds = text.getLocalBounds();
        text.setOrigin(bounds.width / 2, bounds.height / 2);
        text.setPosition(screenWidth / 2, screenHeight / 2);
        text.setColor(Color.BLACK);
    }

    /*
  * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        paused = false;
        while (window.isOpen() && paused == false) {
            window.clear(Color.WHITE);
            window.draw(text);
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
        return returnTo;
    }
}
