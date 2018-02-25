
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
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Text;
import org.jsfml.system.Vector2f;
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
    private Text pi;
    private Text ti;
    private Text value;
    private Text sell;
    private Text valueTrader;
    private Text buy;
    private Text goldTrader;
    private RectangleShape traderBackground;
    private RectangleShape playerBackground;

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
            if(t.getGlobalBounds().top + t.getLocalBounds().height < Game.screenHeight + StateMachine.yOffset && t.getGlobalBounds().top > StateMachine.yOffset  ){
                System.out.println(t.getGlobalBounds().top + t.getGlobalBounds().height);
                window.draw(t);                
            }
        }
    }

    @Override
    void drawText(Text[] textArray) {
        
        for (Text t : textArray) {
            if(t.getGlobalBounds().top + t.getLocalBounds().height < Game.screenHeight*scale){
                window.draw(t);                
            }
            
        }
    }

    @Override
    void menuWindow(RenderWindow window, int scale, int options_num) {
        this.options_num = options_num;
        this.window = window;
        this.scale = scale;
        this.screenWidth = 288 * scale;
        this.screenHeight = 160 * scale;
        //text = newText(options_num);
    }

    void showSelection(ArrayList<Text> textArray, int optionValue) {
        for (int i = 0; i < textArray.size(); i++) {
            if ((i + 1) == optionValue) {
                textArray.get(i).setColor(new Color(255, 128, 128));
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
        for (Consumable c : StateMachine.gameWorld.playerInv.getConsumables()) {
            playerText.add(new Text(c.getName()/* + " [" + c.value + "]"*/, text_font, screenHeight / 20));
            playerText.get(count).setPosition((screenWidth / 4) + (2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * count) + StateMachine.yOffset);
            count++;
        }

        count = 0;
        for (Consumable c : traderInventory.getConsumables()) {
            traderText.add(new Text(c.getName() /*+ " [" + c.value + "]"*/, text_font, screenHeight / 20));
            traderText.get(count).setPosition((float) (screenWidth / 1.333) + StateMachine.xOffset, (scale * 3) + (13 * scale * count)+ StateMachine.yOffset);
            count++;
        }

        gold = new Text("Gold: " + Integer.toString(StateMachine.gameWorld.playerInv.getGold()), text_font, screenHeight / 20);
        gold.setColor(Color.YELLOW);
        gold.setPosition(2 * scale + StateMachine.xOffset, (scale * 3) + (13 * scale * 1) + StateMachine.yOffset);

        goldTrader = new Text("Gold: ∞", text_font, screenHeight / 20);
        goldTrader.setColor(Color.YELLOW);
        goldTrader.setPosition((screenWidth / 2) + (2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 1) + StateMachine.yOffset);

        pi = new Text("Player Inventory: ", text_font, screenHeight / 20);
        pi.setPosition((2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 0) + StateMachine.yOffset);
        pi.setColor(Color.BLACK);

        ti = new Text("Trader Inventory: ", text_font, screenHeight / 20);
        ti.setPosition((screenWidth / 2) + (2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 0)+ StateMachine.yOffset);
        ti.setColor(Color.BLACK);

        traderBackground = new RectangleShape(new Vector2f((screenWidth / 2), screenHeight));
        traderBackground.setFillColor(new Color(204, 204, 255));
        traderBackground.setPosition((screenWidth / 2)+ StateMachine.xOffset, + StateMachine.yOffset);

        playerBackground = new RectangleShape(new Vector2f((screenWidth / 2), screenHeight));
        playerBackground.setFillColor(new Color(204, 204, 255));
        playerBackground.setPosition(+ StateMachine.xOffset, 0 + StateMachine.yOffset);

    }

    void updateValue() {
        try {
            value = new Text("Value: " + StateMachine.gameWorld.playerInv.getConsumables().get(option - 1).value, text_font, screenHeight / 20);
            if (subState == 1) {
                value.setString("Value: ");
            }
            value.setPosition((2 * scale) +  StateMachine.xOffset, (scale * 3) + (13 * scale * 2) + StateMachine.yOffset);
            value.setColor(Color.BLACK);
        } catch (IndexOutOfBoundsException e) {
            value = new Text("Value: ", text_font, screenHeight / 20);
            value.setPosition((2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 2) + StateMachine.yOffset);
            value.setColor(Color.BLACK);

        } catch (NullPointerException e) {
        }

        try {
            valueTrader = new Text("Value: " + traderInventory.getConsumables().get(option - 1).value, text_font, screenHeight / 20);
            if (subState == 0) {
                valueTrader.setString("Value: ");
            }
            valueTrader.setPosition((screenWidth / 2) + (2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 2) + StateMachine.yOffset);
            valueTrader.setColor(Color.BLACK);

        } catch (IndexOutOfBoundsException e) {
            valueTrader = new Text("Value: ", text_font, screenHeight / 20);
            valueTrader.setPosition((screenWidth / 2) + (2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 2) + StateMachine.yOffset);
            valueTrader.setColor(Color.BLACK);

        }

        buy = new Text("Buy", text_font, screenHeight / 20);
        buy.setPosition((screenWidth / 2) + (2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 3) + StateMachine.yOffset);
        buy.setColor(Color.BLACK);

        sell = new Text("Sell", text_font, screenHeight / 20);
        sell.setPosition((2 * scale) + StateMachine.xOffset, (scale * 3) + (13 * scale * 3) + StateMachine.yOffset);
        sell.setColor(Color.BLACK);

    }

    @Override
    public int run() {
        menuWindow(window, scale, 0);
        updateInventory();
        int count = 0;

        option = 1;
        showSelection(playerText, option);

        int state = 4;
        updateValue();

        Dude:
        while (window.isOpen() && state == 4) {
            if (subState == 0) {
                playerBackground.setFillColor(new Color(153, 153, 255));
                traderBackground.setFillColor(new Color(204, 204, 255));
            }
            if (subState == 1) {
                traderBackground.setFillColor(new Color(153, 153, 255));
                playerBackground.setFillColor(new Color(204, 204, 255));
            }

            window.clear(Color.BLACK);
            window.draw(traderBackground);
            window.draw(playerBackground);
            //showSelection(text, 0);
            //System.out.println("Text array size: " + text.length);
            if (playerText.size() == 0) {
                subState = 1;
            }
            updateValue();
            window.draw(value);
            drawText(playerText);
            drawText(traderText);
            window.draw(gold);
            window.draw(pi);
            window.draw(sell);
            window.draw(ti);
            window.draw(valueTrader);
            window.draw(buy);
            window.draw(goldTrader);

            if (subState == 0) {
                showSelection(playerText, option);
                if (!playerText.isEmpty()) {
                    //System.out.println(screenHeight);
                    //System.out.println(playerText.get(option - 1).getGlobalBounds().top);
                    if (playerText.get(option - 1).getGlobalBounds().top >= screenHeight - screenHeight / 20) {
                        //System.out.println("This is greater");
                        for (Text t : playerText) {
                            t.move(0, -((scale * 3) + (13 * scale * count)));
                        }
                    }
                }

                if (!playerText.isEmpty()) {
                    //System.out.println(screenHeight);
                    //System.out.println(playerText.get(option - 1).getGlobalBounds().top);
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
                            updateValue();
                        } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                            menuSound.play();
                            option--;
                            if (option <= 1) {
                                option = 1;
                            }
                            updateValue();
                        } else if (keyEvent.key == Keyboard.Key.valueOf("E")) {
                            StateMachine.gameWorld.playerInv.setGold(StateMachine.gameWorld.playerInv.getConsumables().get(option - 1).value);
                            StateMachine.gameWorld.playerInv.getConsumables().remove(option - 1);
                            if (option == StateMachine.gameWorld.playerInv.getConsumables().size() + 1) {
                                option--;
                            }
                            if (StateMachine.gameWorld.playerInv.getConsumables().isEmpty()) {
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
                            updateValue();
                        } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                            menuSound.play();
                            option--;
                            if (option <= 1) {
                                option = 1;
                            }
                            updateValue();
                        } else if (keyEvent.key == Keyboard.Key.valueOf("E")) {

                            if (traderInventory.getConsumables().get(option - 1).value <= StateMachine.gameWorld.playerInv.getGold()) {
                                menuSound.play();
                                StateMachine.gameWorld.playerInv.setGold(-traderInventory.getConsumables().get(option - 1).value);
                                StateMachine.gameWorld.playerInv.addItem(new Consumable(
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
