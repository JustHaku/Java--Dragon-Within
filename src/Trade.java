
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class Trade extends Menu implements State {

    private Inventory playInv;
    private Inventory traderInventory;
    private int subState = 0;
    private Text gold;
    private ArrayList<Text> playerText = new ArrayList<>();
    private ArrayList<Text> traderText = new ArrayList<>();

    public Trade(RenderWindow window, int scale, Inventory playInv, Inventory traderInventory) {
        this.playInv = playInv;
        this.window = window;
        this.scale = scale;
        this.traderInventory = traderInventory;

        soundBuffer = new SoundBuffer();
        try {
            soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));
        } catch (IOException ex) {
            Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, null, ex);
        }

        menuSound = new Sound();
        menuSound.setBuffer(soundBuffer);

        text_font = new Font();
        try {
            text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
        } catch (IOException ex) {
            Logger.getLogger(Trade.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("sfd");

    }

    void drawText(ArrayList<Text> textArray) {
        for (Text t : textArray) {
            window.draw(t);
        }
    }

    @Override
    void drawText(Text[] textArray) {
        for (Text t : textArray) {
            window.draw(t);
        }
    }

    @Override
    void menuWindow(RenderWindow window, int scale, int width, int height, int options_num) {
        this.options_num = options_num;
        this.window = window;
        this.scale = scale;
        this.screenWidth = width * scale;
        this.screenHeight = height * scale;
        //text = newText(options_num);
    }

    void showSelection(ArrayList<Text> textArray, int optionValue) {
        for (int i = 0; i < textArray.size(); i++) {
            if ((i + 1) == optionValue) {
                textArray.get(i).setColor(Color.BLACK);
            } else {
                textArray.get(i).setColor(Color.WHITE);
            }
        }
    }

    void deselect(ArrayList<Text> textArray) {
        for (Text t : textArray) {
            t.setColor(Color.WHITE);
        }
    }

    void updateInventory() {
        int count; //= playInv.getWeapons().size();
        playerText.clear();
        traderText.clear();

        count = 0;
        for (Consumable c : this.playInv.getConsumables()) {
            playerText.add(new Text(c.getName() + " [" + c.value + "]", text_font, screenHeight / 20));
            playerText.get(count).setPosition(2 * scale, (scale * 3) + (13 * scale * count));
            count++;
        }

        count = 0;
        for (Consumable c : traderInventory.getConsumables()) {
            traderText.add(new Text(c.getName() + " [" + c.value + "]", text_font, screenHeight / 20));
            traderText.get(count).setPosition((screenWidth / 2), (scale * 3) + (13 * scale * count));
            count++;
        }

        gold = new Text("Gold: " + Integer.toString(playInv.getGold()), text_font, screenHeight / 20);
        gold.setColor(Color.YELLOW);
        gold.setPosition((screenWidth / 2) - (gold.getLocalBounds().width / 2), (screenHeight - screenHeight / 20) - screenHeight / 20);

    }

    @Override
    public int run() {
        menuWindow(window, scale, 288, 160, 0);
        updateInventory();
        int count = 0;

        option = 1;
        showSelection(playerText, option);

        int state = 4;

        Dude:
        while (window.isOpen() && state == 4) {
            window.clear(Color.GREEN);
            //showSelection(text, 0);
            //System.out.println("Text array size: " + text.length);

            if (playerText.size() == 0) {
                subState = 1;
            }

            drawText(playerText);
            drawText(traderText);
            window.draw(gold);
            if (subState == 0) {
                showSelection(playerText, option);
                if (!playerText.isEmpty()) {
                    //System.out.println(screenHeight);
                    System.out.println(playerText.get(option - 1).getGlobalBounds().top);
                    if (playerText.get(option - 1).getGlobalBounds().top >= screenHeight - screenHeight / 20) {
                        System.out.println("This is greater");
                        for (Text t : playerText) {
                            t.move(0, -((scale * 3) + (13 * scale * count)));
                        }
                    }
                }

                if (!playerText.isEmpty()) {
                    //System.out.println(screenHeight);
                    System.out.println(playerText.get(option - 1).getGlobalBounds().top);
                    if (playerText.get(option - 1).getGlobalBounds().top <= 0) {
                        System.out.println("This is greater");
                        for (Text t : playerText) {
                            t.move(0, +((scale * 3) + (13 * scale * count)));
                        }
                    }
                }

            } else {
                showSelection(traderText, option);
            }

            window.display();
            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close();
                } else if (event.type == Event.Type.KEY_PRESSED) {
                    if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        break Dude;
                    }
                    if (subState == 0) {
                        if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                            menuSound.play();
                            option++;
                            if (option >= playerText.size()) {
                                option = playerText.size();
                            }
                        } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                            menuSound.play();
                            option--;
                            if (option <= 1) {
                                option = 1;
                            }
                        } else if (keyEvent.key == Keyboard.Key.valueOf("E")) {
                            playInv.setGold(playInv.getConsumables().get(option - 1).value);
                            playInv.getConsumables().remove(option - 1);
                            if (option == playInv.getConsumables().size() + 1) {
                                option--;
                            }
                            if (playInv.getConsumables().isEmpty()) {
                                option++;
                                subState = 1;

                            }

                            updateInventory();
                            menuSound.play();
                        } else if (keyEvent.key == Keyboard.Key.valueOf("D")) {
                            subState = 1;
                            menuSound.play();
                            deselect(playerText);
                            option = 1;
                        }

                    } else {

                        if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                            menuSound.play();
                            option++;
                            if (option >= traderText.size()) {
                                option = traderText.size();
                            }
                        } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                            menuSound.play();
                            option--;
                            if (option <= 1) {
                                option = 1;
                            }
                        } else if (keyEvent.key == Keyboard.Key.valueOf("E")) {

                            if (traderInventory.getConsumables().get(option - 1).value <= playInv.getGold()) {
                                menuSound.play();
                                playInv.setGold(-traderInventory.getConsumables().get(option - 1).value);
                                playInv.addItem(new Consumable(
                                        traderInventory.getConsumables().get(option - 1).getId(),
                                        traderInventory.getConsumables().get(option - 1).getName(),
                                        traderInventory.getConsumables().get(option - 1)));
                                updateInventory();

                            }

                            //traderInventory.removeConsumable(option);
                        } else if (keyEvent.key == Keyboard.Key.valueOf("A")) {
                            menuSound.play();
                            subState = 0;
                            deselect(traderText);
                            option = 1;
                        }

                    }

                }
            }
        }

        return 1;
    }

}
