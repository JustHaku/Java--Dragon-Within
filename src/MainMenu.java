
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the main menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class MainMenu extends Menu implements State {

    private Texture mainBG;
    private Sprite mainBGsp;
    private Music menuMusic;

    public MainMenu(RenderWindow window, int scale, int options_num) throws IOException {
        menuWindow(window, scale, options_num);

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        soundBuffer = new SoundBuffer();
        soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));

        this.menuMusic = getMenuMusic();
        menuMusic.setLoop(true);
        menuMusic.openFromFile(Paths.get("src/audio/Menu/Soliloquy.wav"));

        this.mainBG = getBackground();
        mainBG.loadFromFile(Paths.get("src/graphics/Menu/main_m.jpg"));
        mainBG.setSmooth(true);

        this.mainBGsp = getBGSprite(mainBG);
        mainBGsp.setOrigin(Vector2f.div(new Vector2f(mainBG.getSize()), 2));
        mainBGsp.setPosition(screenWidth / 2 + StateMachine.xOffset, screenHeight / 2 + StateMachine.yOffset);
        mainBGsp.setScale(Game.SCALE/5, Game.SCALE/5);

        text[0] = new Text("New Game", text_font, screenHeight / 10);
        bounds = text[0].getLocalBounds();
        text[0].setOrigin(bounds.width / 2, bounds.height / 2);
        text[0].setPosition(screenWidth / 2 + StateMachine.xOffset, screenHeight / 4 + StateMachine.yOffset);

        text[1] = new Text("Continue", text_font, screenHeight / 10);
        bounds = text[1].getLocalBounds();
        text[1].setOrigin(bounds.width / 2, bounds.height / 2);
        text[1].setPosition(screenWidth / 2 + StateMachine.xOffset, screenHeight / 4 + screenHeight / 10 + StateMachine.yOffset);

        text[2] = new Text("Credits", text_font, screenHeight / 10);
        bounds = text[2].getLocalBounds();
        text[2].setOrigin(bounds.width / 2, bounds.height / 2);
        text[2].setPosition(screenWidth / 2 + StateMachine.xOffset, screenHeight / 4 + screenHeight / 5 + StateMachine.yOffset);

        text[3] = new Text("Quit", text_font, screenHeight / 10);
        bounds = text[3].getLocalBounds();
        text[3].setOrigin(bounds.width / 2, bounds.height / 2);
        text[3].setPosition(screenWidth / 2 + StateMachine.xOffset, screenHeight / 4 + screenHeight / 5 + screenHeight / 5 + StateMachine.yOffset);

        menuSound = new Sound();
        menuSound.setBuffer(soundBuffer);
    }

    /*
  * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        boolean paused = false; // Used to escape the main while loop
        menuMusic.play();
        option = 1;
        showSelection(text, option);
        Game.mainTheme.pause();

        while (window.isOpen() && paused == false) {
            window.clear(Color.BLACK);
            window.draw(mainBGsp);
            drawText(text);
            window.display();

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close();
                } else if (event.type == Event.Type.KEY_PRESSED) {
                    /*if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE"))
          {
              window.close();
          }*/

                    if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                        menuSound.play();
                        option++;
                        if (option >= options_num) {
                            option = options_num;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                        menuSound.play();
                        option--;
                        if (option <= 1) {
                            option = 1;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("E")) {
                        menuMusic.stop();
                        paused = true;
                        if (option == 1) {
                            Game.mainTheme.stop();
                            option = 99; // new game.
                        } else if (option == 2) {
                            StateMachine.team.clear();
                            try {
                                Save s = Save.load("src/saves/save000");
                                StateMachine.gameWorld.load(s);
                            } catch (FileNotFoundException ex) {
                                Logger.getLogger(StateMachine.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(StateMachine.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (EOFException ex) {
                                ex.printStackTrace();
                            } catch (IOException ex) {
                                Logger.getLogger(MainMenu.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            option = 1; // game world.
                        } else if (option == 3) {
                            Credits.returnTo = 0; // tells the inv menu where you returned from.
                            option = 4; // settings menu.
                        } else if (option == 4) {
                            window.close();
                        }
                    }
                    showSelection(text, option);
                }
            }
        }
        return option;
    }
}
