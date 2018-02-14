
import java.io.IOException;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * Base-Class of which other menu-classes will be children-classes
 *
 * @author Petros Soutzis
 */
public class Menu {

    protected int option;
    protected int scale, screenHeight, screenWidth, options_num;
    protected SoundBuffer soundBuffer;
    protected Sound menuSound;
    protected FloatRect bounds;
    protected RenderWindow window;
    protected Font text_font;
    protected Text[] text;

    private Texture mainBG;
    private Sprite mainBGsp;
    private Music menuMusic;

    void menuWindow(RenderWindow window, int scale, int options_num) {
        this.options_num = options_num;
        this.window = window;
        this.scale = scale;
        this.screenWidth = 288 * scale;
        this.screenHeight = 160 * scale;
        text = newText(options_num);
    }

    /**
     * @param options_num the number of options available
     * @return a new Text array with a size equal to the number of options
     */
    Text[] newText(int options_num) {
        return new Text[options_num];
    }

    /**
     * Creates a new texture for usage as background for the menu
     *
     * @return a new Texture object to be used as background
     */
    Texture getBackground() {
        return mainBG = new Texture();
    }

    /**
     * Creates a new Sprite for usage as background for the menu
     *
     * @param texture the specified texture with which the sprite will be
     * constructed
     * @return a new Sprite object to be used as a background
     */
    Sprite getBGSprite(Texture texture) {
        return mainBGsp = new Sprite(texture);
    }

    /**
     * @return a new Music object, to play while menu is open
     */
    Music getMenuMusic() {
        return menuMusic = new Music();
    }

    /**
     * Draw the options that the player will have to choose from.
     *
     * @param textArray The array with the initialized Text objects
     */
    void drawText(Text[] textArray) {
        for (int i = 0; i < textArray.length; i++) {
            window.draw(textArray[i]);
        }
    }

    /**
     * Highlight the selected option in the menu box
     *
     * @param textArray The array with the initialized Text objects
     * @param optionValue The value of the option selected
     */
    void showSelection(Text[] textArray, int optionValue) {
        for (int i = 0; i < textArray.length; i++) {
            if ((i + 1) == optionValue) {
                textArray[i].setColor(Color.BLACK);
            } else {
                textArray[i].setColor(Color.WHITE);
            }
        }
    }
}
