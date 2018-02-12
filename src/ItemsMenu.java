
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;
import java.util.ArrayList;

/**
 * The class that provides the items menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class ItemsMenu extends Menu implements State {

    private RenderWindow window;
    private int scale;
    private Font text_font;
    private boolean paused = false;
    private int screenHeight;
    private int screenWidth;
    private RectangleShape menuRect;
    private RectangleShape playerRect;
    private ArrayList items;
    private ArrayList<Text> itemText = new ArrayList<>();
    private ArrayList<RectangleShape> itemRect = new ArrayList<>();
    public static boolean returnTo = false;
    public Game g;

    public ItemsMenu(RenderWindow window, int scale, int options_num, Game g) throws IOException {
        menuWindow(window, scale, 288, 160, options_num);
        this.window = window;
        this.scale = scale;
        screenHeight = 160 * scale;
        screenWidth = 288 * scale;
        this.g = g;

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/Stay_Wildy.ttf"));

        soundBuffer = new SoundBuffer();
        soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));

        menuSound = new Sound();
        menuSound.setBuffer(soundBuffer);

        menuRect = new RectangleShape(new Vector2f((screenWidth / 4) * 3, screenHeight - 10));
        menuRect.setFillColor(new Color(11, 2, 138));
        menuRect.setPosition(5, 5);

        playerRect = new RectangleShape(new Vector2f((screenWidth / 4) - 15, screenHeight - 10));
        playerRect.setFillColor(new Color(11, 2, 138));
        playerRect.setPosition(((screenWidth / 4) * 3) + 10, 5);

        text[0] = new Text("consumables", text_font, screenHeight / 10);
        bounds = text[0].getLocalBounds();
        text[0].setOrigin(bounds.width / 2, bounds.height / 2);
        text[0].setPosition((screenWidth / 8) * 7, screenHeight / 20);

        text[1] = new Text("weapons", text_font, screenHeight / 10);
        bounds = text[1].getLocalBounds();
        text[1].setOrigin(bounds.width / 2, bounds.height / 2);
        text[1].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 2 + ((screenHeight / 20) / 2));

        text[2] = new Text("trinkets", text_font, screenHeight / 10);
        bounds = text[2].getLocalBounds();
        text[2].setOrigin(bounds.width / 2, bounds.height / 2);
        text[2].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 5);

        text[3] = new Text("keyItems", text_font, screenHeight / 10);
        bounds = text[3].getLocalBounds();
        text[3].setOrigin(bounds.width / 2, bounds.height / 2);
        text[3].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 7);

    }

    /*
  * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        returnTo = true;
        paused = false;
        option = 1;
        showSelection(text, option);
        items = g.playerInv.getConsumables(); // gets first inventory.

        // sets up rectangles and text for items.
        for (int i = 0; i < items.size(); i++) {
            itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
            (itemRect.get(i)).setPosition(10, 10 + ((screenHeight / 10 - 2) * i));

            itemText.add(new Text(((Item) items.get(i)).name, text_font, screenHeight / 15));
            (itemText.get(i)).setPosition(15, 5 + ((screenHeight / 10 - 2) * i));
        }

        while (window.isOpen() && paused == false) {
            window.clear(Color.BLACK);
            window.draw(menuRect);
            window.draw(playerRect);
            drawText(text);

            for (int i = 0; i < items.size(); i++) {
                window.draw(itemRect.get(i));
                window.draw(itemText.get(i));
            }
            window.display();

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close();
                } else if (event.type == Event.Type.KEY_PRESSED) {
                    if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                        // resets the array lists.
                        itemRect = new ArrayList<>();
                        itemText = new ArrayList<>();

                        menuSound.play();
                        option++;
                        if (option >= options_num) {
                            option = options_num;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                        itemRect = new ArrayList<>();
                        itemText = new ArrayList<>();

                        menuSound.play();
                        option--;
                        if (option <= 1) {
                            option = 1;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        paused = true;
                    }
                    // Different inventorys selected.
                    if (option == 1) {
                        items = g.playerInv.getConsumables();
                    } else if (option == 2) {
                        items = g.playerInv.getWeapons();
                    } else if (option == 3) {
                        items = g.playerInv.getTrinkets();
                    } else if (option == 4) {
                        items = g.playerInv.getKeyItems();
                    }
                    // Redraws the items.
                    for (int i = 0; i < items.size(); i++) {
                        itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
                        (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                        (itemRect.get(i)).setPosition(10, 10 + ((screenHeight / 10 - 2) * i));

                        itemText.add(new Text(((Item) items.get(i)).getName(), text_font, screenHeight / 15));
                        (itemText.get(i)).setPosition(15, 5 + ((screenHeight / 10 - 2) * i));
                    }
                }
                showSelection(text, option);
            }
        }
        return 3;
    }
}
