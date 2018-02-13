
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the inventory menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class InventoryMenu extends Menu implements State {

    //private Font text_font;
    private boolean paused = false;
    private RectangleShape menuRect;
    private RectangleShape playerRect;
    private ArrayList<Character> team;
    private ArrayList<RectangleShape> teamRect = new ArrayList<>();
    private ArrayList<Text> teamText = new ArrayList<>();

    public InventoryMenu(RenderWindow window, int scale, int options_num, ArrayList<Character> team) throws IOException {
        menuWindow(window, scale, options_num);
        this.team = team;

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

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

        // Sets the amount of rectangles and text to go in them.
        for (int i = 0; i < team.size(); i++) {
            teamRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 6 - 10)));
            (teamRect.get(i)).setFillColor(new Color(50, 45, 138));
            (teamRect.get(i)).setPosition(10, 10 + ((screenHeight / 6 - 2) * i));
            //int[] stats = (team.get(i)).getStats();
            //teamText.add(new Text((team.get(i)).name + "     LV: " + i + "\nHP:" + stats[2] + " / " + stats[3] + "     MP:" + stats[1] + " / " + stats[0], text_font, screenHeight/15));
            teamText.add(new Text((team.get(i)).name + "     LV: " + (team.get(i)).level + "\nHP:"
                    + (team.get(i)).max_health + " / " + (team.get(i)).health + "     MP:"
                    + (team.get(i)).max_mana + " / " + (team.get(i)).mana, text_font, screenHeight / 15));
            (teamText.get(i)).setPosition(15, 5 + ((screenHeight / 6 - 2) * i));
        }

        text[0] = new Text("Loadout", text_font, screenHeight / 15);
        bounds = text[0].getLocalBounds();
        text[0].setOrigin(bounds.width / 2, bounds.height / 2);
        text[0].setPosition((screenWidth / 8) * 7, screenHeight / 20);

        text[1] = new Text("Items", text_font, screenHeight / 15);
        bounds = text[1].getLocalBounds();
        text[1].setOrigin(bounds.width / 2, bounds.height / 2);
        text[1].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 3);

        text[2] = new Text("Skills", text_font, screenHeight / 15);
        bounds = text[2].getLocalBounds();
        text[2].setOrigin(bounds.width / 2, bounds.height / 2);
        text[2].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 5);

        text[3] = new Text("Magic", text_font, screenHeight / 15);
        bounds = text[3].getLocalBounds();
        text[3].setOrigin(bounds.width / 2, bounds.height / 2);
        text[3].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 7);

        text[4] = new Text("Config", text_font, screenHeight / 15);
        bounds = text[4].getLocalBounds();
        text[4].setOrigin(bounds.width / 2, bounds.height / 2);
        text[4].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 13);

        text[5] = new Text("Main Menu", text_font, screenHeight / 15);
        bounds = text[5].getLocalBounds();
        text[5].setOrigin(bounds.width / 2, bounds.height / 2);
        text[5].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 15);

        text[6] = new Text("Quit", text_font, screenHeight / 15);
        bounds = text[6].getLocalBounds();
        text[6].setOrigin(bounds.width / 2, bounds.height / 2);
        text[6].setPosition((screenWidth / 8) * 7, screenHeight / 20 * 17);
    }

    /*
  * Main loop draws and controlls moving through menu.
     */
    @Override
    public int run() {
        paused = false;
        // sets which item on the menu to be selected on return.
        if (SettingsMenu.returnTo == 3) {
            option = 5;
            SettingsMenu.returnTo = 0;
        } else if (ItemsMenu.returnTo == true) {
            option = 2;
            ItemsMenu.returnTo = false;
        } else if (SkillsMenu.returnTo == true) {
            option = 3;
            SkillsMenu.returnTo = false;
        } else if (MagicMenu.returnTo == true) {
            option = 4;
            MagicMenu.returnTo = false;
        } else {
            option = 1;
        }
        showSelection(text, option);
        boolean closeReq = false; // used to close window from second while loop.
        while (window.isOpen() && paused == false) {
            window.clear(Color.BLACK);
            window.draw(menuRect);
            window.draw(playerRect);
            drawText(text);

            // draws players.
            for (int i = 0; i < teamRect.size(); i++) {
                window.draw(teamRect.get(i));
                window.draw(teamText.get(i));
            }

            window.display();

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED || closeReq == true) {
                    window.close();
                } else if (event.type == Event.Type.KEY_PRESSED) {
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
                        if (option == 7) {
                            window.close();
                        } // Loadout menu.
                        else if (option == 1) {
                            int hover = 0;
                            int select = -1;
                            boolean selected = false;
                            boolean breakOut = false; // used to escape second loop.

                            while (breakOut == false) {
                                for (Event events : window.pollEvents()) {
                                    KeyEvent keyEvents = events.asKeyEvent();

                                    if (events.type == Event.Type.CLOSED) {
                                        closeReq = true;
                                        breakOut = true;
                                    } else if (events.type == Event.Type.KEY_PRESSED) {
                                        if (keyEvents.key == Keyboard.Key.valueOf("S")) {
                                            menuSound.play();
                                            hover++;
                                            if (hover >= teamRect.size()) {
                                                hover = teamRect.size() - 1;
                                            }
                                        } else if (keyEvents.key == Keyboard.Key.valueOf("W")) {
                                            menuSound.play();
                                            hover--;
                                            if (hover <= 0) {
                                                hover = 0;
                                            }
                                        } else if (keyEvents.key == Keyboard.Key.valueOf("E")) {
                                            if (selected == false) {
                                                select = hover;
                                                selected = true;
                                                (teamRect.get(hover)).setFillColor(new Color(10, 45, 138));
                                            } else if (selected == true) {
                                                if (hover == select) {
                                                    select = -1;
                                                    selected = false;
                                                    (teamRect.get(hover)).setFillColor(new Color(50, 45, 138));
                                                } else {
                                                    //swaps the charecters.
                                                    Character tempChar = team.get(select);
                                                    team.set(select, team.get(hover));
                                                    team.set(hover, tempChar);
                                                    Text tempText = teamText.get(select);
                                                    teamText.set(select, teamText.get(hover));
                                                    teamText.set(hover, tempText);
                                                    (teamRect.get(select)).setFillColor(new Color(50, 45, 138));
                                                    select = -1;
                                                    selected = false;
                                                }
                                            }
                                        } else if (keyEvents.key == Keyboard.Key.valueOf("ESCAPE")) {
                                            breakOut = true;
                                            hover = -1;
                                            for (int i = 0; i < teamRect.size(); i++) {
                                                (teamRect.get(i)).setFillColor(new Color(50, 45, 138));
                                            }
                                        }
                                    }

                                }
                                window.clear(Color.BLACK);
                                window.draw(menuRect);
                                window.draw(playerRect);
                                drawText(text);

                                // resets loadout menu.
                                for (int i = 0; i < teamRect.size(); i++) {
                                    if (select != i) {
                                        if (hover == i) {
                                            (teamRect.get(i)).setFillColor(new Color(104, 89, 183));
                                        } else {
                                            (teamRect.get(i)).setFillColor(new Color(50, 45, 138));
                                        }
                                    }
                                    window.draw(teamRect.get(i));
                                    window.draw(teamText.get(i));
                                }

                                window.display();

                            }
                        } else {
                            paused = true;
                            if (option == 2) {
                                option = 5;
                            } else if (option == 3) {
                                option = 6;
                            } else if (option == 4) {
                                option = 7;
                            } else if (option == 5) {
                                SettingsMenu.returnTo = 3;
                                option = 4;
                            } else if (option == 6) {
                                option = 0;
                            }
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        paused = true;
                        option = 1;
                    }

                    showSelection(text, option);
                }
            }
        }
        return option;
    }
}
