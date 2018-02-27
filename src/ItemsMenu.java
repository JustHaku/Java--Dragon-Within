
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

    //private RenderWindow window;
    //private int scale;
    //private Font text_font;
    private boolean paused = false;
    //private int screenHeight;
    //private int screenWidth;
    private RectangleShape menuRect;
    private RectangleShape playerRect;
    private ArrayList items;
    private ArrayList<Text> itemText = new ArrayList<>();
    private ArrayList<RectangleShape> itemRect = new ArrayList<>();
    public static boolean returnTo = false;
    public Game g;
    private Inventory playInv;
    private ArrayList<Character> team;

    public ItemsMenu(RenderWindow window, int scale, int options_num, Inventory playInv, ArrayList<Character> team) throws IOException {
        menuWindow(window, scale, options_num);
        this.team = team;
        /*this.window = window;
        this.scale = scale;
        screenHeight = 160 * scale;
        screenWidth = 288 * scale;*/
        this.playInv = playInv;
        //this.g = g;

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        soundBuffer = new SoundBuffer();
        soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));

        menuSound = new Sound();
        menuSound.setBuffer(soundBuffer);

        menuRect = new RectangleShape(new Vector2f((screenWidth / 4) * 3, screenHeight - 10));
        menuRect.setFillColor(new Color(11, 2, 138));
        menuRect.setPosition(5 + StateMachine.xOffset, 5 + StateMachine.yOffset);

        playerRect = new RectangleShape(new Vector2f((screenWidth / 4) - 15, screenHeight - 10));
        playerRect.setFillColor(new Color(11, 2, 138));
        playerRect.setPosition(((screenWidth / 4) * 3) + 10 + StateMachine.xOffset, 5 + StateMachine.yOffset);

        text[0] = new Text("consumables", text_font, screenHeight / 15);
        bounds = text[0].getLocalBounds();
        text[0].setOrigin(bounds.width / 2, bounds.height / 2);
        text[0].setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 + StateMachine.yOffset);

        text[1] = new Text("weapons", text_font, screenHeight / 15);
        bounds = text[1].getLocalBounds();
        text[1].setOrigin(bounds.width / 2, bounds.height / 2);
        text[1].setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 * 3 + StateMachine.yOffset);

        text[2] = new Text("trinkets", text_font, screenHeight / 15);
        bounds = text[2].getLocalBounds();
        text[2].setOrigin(bounds.width / 2, bounds.height / 2);
        text[2].setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 * 5 + StateMachine.yOffset);

        text[3] = new Text("keyItems", text_font, screenHeight / 15);
        bounds = text[3].getLocalBounds();
        text[3].setOrigin(bounds.width / 2, bounds.height / 2);
        text[3].setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 * 7 + StateMachine.yOffset);
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
        items = StateMachine.gameWorld.playerInv.getConsumables(); // gets first inventory.
        boolean closeReq = false;
        itemRect = new ArrayList<>();
        itemText = new ArrayList<>();

        // sets up rectangles and text for items.
        for (int i = 0; i < items.size(); i++) {
            itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
            (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);

            itemText.add(new Text(((Item) items.get(i)).name, text_font, screenHeight / 15));
            (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);
        }

        showSelection(text, option);

        while (window.isOpen() && paused == false) {
            window.clear(Color.BLACK);
            window.draw(menuRect);
            window.draw(playerRect);
            drawText(text);

            for (int i = 0; i < items.size(); i++) {
                if (itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getLocalBounds().height < Game.screenHeight + StateMachine.yOffset && itemRect.get(i).getGlobalBounds().top > StateMachine.yOffset) {
                    System.out.println(itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getGlobalBounds().height);
                    window.draw(itemRect.get(i));
                    window.draw(itemText.get(i));
                }

            }

            window.display();

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED || closeReq == true) {
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
                    } else if (keyEvent.key == Keyboard.Key.valueOf("E") && option == 1 && items.size() != 0) {
                        int hover = 0;
                        int top = 0;
                        int bottom = 9;
                        int offset = 0;
                        boolean breakOut = false; // used to escape second loop.

                        itemRect = new ArrayList<>();
                        itemText = new ArrayList<>();

                        for (int i = 0; i < items.size(); i++) {
                            itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
                            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                            (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);

                            itemText.add(new Text(((Item) items.get(i)).getName(), text_font, screenHeight / 15));
                            (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);
                        }

                        window.clear(Color.BLACK);
                        window.draw(menuRect);
                        window.draw(playerRect);
                        drawText(text);

                        // resets items menu.
                        for (int i = 0; i < itemRect.size(); i++) {
                            if (hover == i) {
                                (itemRect.get(i)).setFillColor(new Color(104, 89, 183));
                            } else {
                                (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                            }
                            if (itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getLocalBounds().height < Game.screenHeight + StateMachine.yOffset && itemRect.get(i).getGlobalBounds().top > StateMachine.yOffset) {
                                System.out.println(itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getGlobalBounds().height);
                                window.draw(itemRect.get(i));
                                window.draw(itemText.get(i));
                            }
                        }

                        window.display();

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
                                        if (hover > bottom && !(hover > items.size() - 1)) {
                                            bottom++;
                                            top++;
                                            offset--;
                                            for (int i = 0; i < items.size(); i++) {
                                                (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset + ((screenHeight / 10 - 2) * offset));
                                                (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset + ((screenHeight / 10 - 2) * offset));
                                            }
                                        }
                                    } else if (keyEvents.key == Keyboard.Key.valueOf("W")) {
                                        menuSound.play();
                                        hover--;
                                        if (hover < top && !(hover < 0)) {
                                            top--;
                                            bottom--;
                                            offset++;
                                            for (int i = 0; i < items.size(); i++) {
                                                (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset + ((screenHeight / 10 - 2) * offset));
                                                (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset + ((screenHeight / 10 - 2) * offset));
                                            }
                                        }
                                    } else if (keyEvents.key == Keyboard.Key.valueOf("E")) {
                                        int selected = 0;
                                        boolean breakOut2 = false; // used to escape second loop.
                                        ArrayList<Text> teamText = new ArrayList<>();

                                        for (int i = 0; i < StateMachine.team.size(); i++) {
                                            teamText.add(new Text(StateMachine.team.get(i).name, text_font, screenHeight / 15));
                                            bounds = teamText.get(i).getLocalBounds();
                                            teamText.get(i).setOrigin(bounds.width / 2, bounds.height / 2);
                                            teamText.get(i).setPosition((screenWidth / 8) * 7 + StateMachine.xOffset, screenHeight / 20 * (i * 2 + 1) + StateMachine.yOffset);
                                        }

                                        itemRect = new ArrayList<>();
                                        itemText = new ArrayList<>();
                                        for (int i = 0; i < items.size(); i++) {
                                            itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
                                            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                                            (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);

                                            itemText.add(new Text(((Item) items.get(i)).getName(), text_font, screenHeight / 15));
                                            (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);
                                        }

                                        while (breakOut2 == false) {
                                            for (Event events2 : window.pollEvents()) {
                                                KeyEvent keyEvents2 = events2.asKeyEvent();

                                                if (events2.type == Event.Type.CLOSED) {
                                                    closeReq = true;
                                                    breakOut = true;
                                                    breakOut2 = true;
                                                } else if (events2.type == Event.Type.KEY_PRESSED) {
                                                    if (keyEvents2.key == Keyboard.Key.valueOf("S")) {
                                                        menuSound.play();
                                                        selected++;
                                                    } else if (keyEvents2.key == Keyboard.Key.valueOf("W")) {
                                                        menuSound.play();
                                                        selected--;
                                                    } else if (keyEvents2.key == Keyboard.Key.valueOf("E")) {
                                                        ((Consumable) items.get(hover)).use(StateMachine.team.get(selected));
                                                        items.remove(hover);
                                                        breakOut2 = true;
                                                        breakOut = true;
                                                    } else if (keyEvents2.key == Keyboard.Key.valueOf("ESCAPE")) {
                                                        breakOut2 = true;
                                                        hover = 0;
                                                    }
                                                }
                                            }
                                            if (selected >= StateMachine.team.size()) {
                                                selected = StateMachine.team.size() - 1;
                                            } else if (selected <= 0) {
                                                selected = 0;
                                            }
                                            window.clear(Color.BLACK);
                                            window.draw(menuRect);
                                            window.draw(playerRect);
                                            for (int i = 0; i < StateMachine.team.size(); i++) {
                                                teamText.get(i).setColor(Color.WHITE);
                                                teamText.get(selected).setColor(Color.BLACK);
                                                window.draw(teamText.get(i));
                                            }

                                            // resets items menu.
                                            for (int i = 0; i < itemRect.size(); i++) {
                                                if (itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getLocalBounds().height < Game.screenHeight + StateMachine.yOffset && itemRect.get(i).getGlobalBounds().top > StateMachine.yOffset) {
                                                    System.out.println(itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getGlobalBounds().height);
                                                    window.draw(itemRect.get(i));
                                                    window.draw(itemText.get(i));
                                                }
                                            }

                                            window.display();
                                        }
                                        itemRect = new ArrayList<>();
                                        itemText = new ArrayList<>();
                                        for (int i = 0; i < items.size(); i++) {
                                            itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
                                            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                                            (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);

                                            itemText.add(new Text(((Item) items.get(i)).getName(), text_font, screenHeight / 15));
                                            (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);
                                        }
                                    } else if (keyEvents.key == Keyboard.Key.valueOf("ESCAPE")) {
                                        breakOut = true;
                                        for (int i = 0; i < itemRect.size(); i++) {
                                            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                                        }
                                    }
                                }
                                if (hover >= items.size()) {
                                    hover = items.size() - 1;
                                } else if (hover <= 0) {
                                    hover = 0;
                                }
                                window.clear(Color.BLACK);
                                window.draw(menuRect);
                                window.draw(playerRect);
                                drawText(text);

                                // resets items menu.
                                for (int i = 0; i < itemRect.size(); i++) {
                                    if (hover == i) {
                                        (itemRect.get(i)).setFillColor(new Color(104, 89, 183));
                                    } else {
                                        (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                                    }
                                    if (itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getLocalBounds().height < Game.screenHeight + StateMachine.yOffset && itemRect.get(i).getGlobalBounds().top > StateMachine.yOffset) {
                                        System.out.println(itemRect.get(i).getGlobalBounds().top + itemRect.get(i).getGlobalBounds().height);
                                        window.draw(itemRect.get(i));
                                        window.draw(itemText.get(i));
                                    }
                                }

                                window.display();
                            }
                        }
                        itemRect = new ArrayList<>();
                        itemText = new ArrayList<>();
                        for (int i = 0; i < items.size(); i++) {
                            itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
                            (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                            (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);

                            itemText.add(new Text(((Item) items.get(i)).getName(), text_font, screenHeight / 15));
                            (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        paused = true;
                    }
                    // Different inventorys selected.
                    if (option == 1) {
                        items = StateMachine.gameWorld.playerInv.getConsumables();
                    } else if (option == 2) {
                        items = StateMachine.gameWorld.playerInv.getWeapons();
                    } else if (option == 3) {
                        items = StateMachine.gameWorld.playerInv.getTrinkets();
                    } else if (option == 4) {
                        items = StateMachine.gameWorld.playerInv.getKeyItems();
                    }
                    // Redraws the items.
                    for (int i = 0; i < items.size(); i++) {
                        itemRect.add(new RectangleShape(new Vector2f((screenWidth / 4) * 3 - 10, screenHeight / 10 - 10)));
                        (itemRect.get(i)).setFillColor(new Color(50, 45, 138));
                        (itemRect.get(i)).setPosition(10 + StateMachine.xOffset, 10 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);

                        itemText.add(new Text(((Item) items.get(i)).getName(), text_font, screenHeight / 15));
                        (itemText.get(i)).setPosition(15 + StateMachine.xOffset, 5 + ((screenHeight / 10 - 2) * i) + StateMachine.yOffset);
                    }
                }
                showSelection(text, option);
            }
        }
        return 3;
    }
}
