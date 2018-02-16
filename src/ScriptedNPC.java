
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author LBals
 */
public class ScriptedNPC extends Actor {

    private String name;
    private final IntRect state;
    private final Sprite img;
    private Thread t1;
    private Runnable r;
    private final float ps = (float) 1;
    private Music m = new Music();
    private ArrayList<MessageBox> preOption = null;
    private ArrayList<MessageBox> dialogue = null;
    private ArrayList<MessageBox> yesOption = null;
    private ArrayList<MessageBox> noOption = null;
    private MessageBox notEnough = null;
    private MessageBox itCosts = null;
    private ArrayList<MessageBox> itemMessages = null;
    private ArrayList<Item> items = null;
    private MessageBox receivedGold = null;
    private Integer gold = null;
    private Integer teleX = null;
    private Integer teleY = null;
    private Integer wNum = null;
    private Integer cost = null;
    public boolean hadItems = false;
    public static ArrayList<ScriptedNPC> scriptedNPCs = new ArrayList<>();

    public ScriptedNPC(Texture imgTexture, int c1, int c2, int x, int y) {
        
        scriptedNPCs.add(this);

        try {
            m.openFromFile(Paths.get("src/audio/ui/click1.ogg"));
        } catch (IOException ex) {
            Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
        }

        state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);

        img = new Sprite(imgTexture, state);
        img.setScale(Game.SCALE / ps, Game.SCALE / ps);

        this.x = x * Game.tileSize;
        this.y = y * Game.tileSize;

        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;

        r = () -> {
            nextLock();

            t1 = new Thread(r);
        };

        t1 = new Thread(r);
    }

    @Override
    void calcMove(int minX, int minY, int maxX, int maxY) {

    }

    void nextLock() {
        Game.player1.movementLock = true;

        cycleOption(dialogue);

        if (yesOption != null && noOption != null && preOption != null) {
            System.out.println("Not null");
            for (MessageBox d : preOption) {
                d.showHide();
                m.play();
                sleepy();

                if (preOption.indexOf(d) == preOption.size() - 1) {
                    while (true) {
                        if (Keyboard.isKeyPressed(Keyboard.Key.Y)) {
                            d.showHide();
                            sleepy();
                            if (cost != null) {
                                if (StateMachine.gameWorld.playerInv.getGold() - cost < 0) {

                                } else {
                                    cycleOption(yesOption);
                                }
                            } else {
                                cycleOption(yesOption);

                            }

                            tele();
                            items();
                            gold();
                            break;
                        } else if (Keyboard.isKeyPressed(Keyboard.Key.N)) {
                            d.showHide();
                            sleepy();
                            cycleOption(noOption);
                            break;
                        }
                    }
                } else {
                    waitE(d);
                }
            }

        } else {
            tele();
            items();
            gold();
        }

        Game.player1.movementLock = false;
    }

    private void sleepy() {
        try {
            Thread.sleep(350);
        } catch (InterruptedException ex) {
            Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void cycleOption(ArrayList<MessageBox> dialogue) {
        if (dialogue != null) {
            for (MessageBox d : dialogue) {
                d.showHide();
                m.play();
                try {
                    Thread.sleep(350);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
                }
                waitE(d);
            }
        }
    }

    private void waitE(MessageBox d) {

        while (true) {
            if (Keyboard.isKeyPressed(Keyboard.Key.E)) {
                d.showHide();
                try {
                    Thread.sleep(350);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
            }
        }
    }

    private void tele() {

        if (teleX != null && teleY != null && wNum != null) {
            if (cost != null) {
                if (StateMachine.gameWorld.playerInv.getGold() - cost > 0) {
                    StateMachine.gameWorld.playerInv.setGold(-cost);
                    Game.player1.setTilePosition(teleX, teleY);
                    StateMachine.gameWorld.worldNum = wNum;
                } else {
                    notEnough.showHide();
                    m.play();
                    waitE(notEnough);

                }
            } else {
                Game.player1.setTilePosition(teleX, teleY);
                StateMachine.gameWorld.worldNum = wNum;
            }
        }
    }

    private void items() {

        if (itemMessages != null && items != null) {
            
            if (!hadItems) {
                hadItems = true;
                cycleOption(itemMessages);
                for (Item a : items) {
                    StateMachine.gameWorld.playerInv.addItem(a);
                }
                dialogue.clear();
                if (preOption != null) {
                    preOption.clear();
                }

                dialogue.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "It appears you have taken everything.", Color.BLACK));
            }
            hadItems = true;

        }
    }

    private void gold() {
        if (gold != null && receivedGold != null) {

            if (!hadItems) {
                hadItems = true;
                waitE(receivedGold);
                dialogue.clear();
                if (preOption != null) {
                    preOption.clear();
                }

                dialogue.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "It appears you have taken everything.", Color.BLACK));

            }
                        hadItems = true;

        }
    }

    public void addCompanion(Character c) {

    }

    public void addGold(int g) {
        teleX = null;
        teleY = null;
        wNum = null;
        itemMessages = null;
        items = null;

        gold = g;
        receivedGold = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You received: " + gold + " gold.", Color.BLACK);
    }

    public void addItems(ArrayList<Item> i) {
        teleX = null;
        teleY = null;
        wNum = null;
        gold = null;
        receivedGold = null;
        itemMessages = new ArrayList<>();
        items = new ArrayList<>();
        for (Item a : i) {
            //System.out.println("aoskmd");
            items.add(a);
            //i.remove(a);
            itemMessages.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You received: " + a.getName(), Color.BLACK));

        }
    }

    public void setCost(int c) {
        cost = c;
        preOption.get(preOption.size() - 1).editText(preOption.get(preOption.size() - 1).getText() + " Cost: " + cost);
        //itCosts = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "The price is: " + cost + ". (Y/N)", Color.BLACK);
        notEnough = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You don't have enough gold.", Color.BLACK);
    }

    public void addTeleport(int x, int y, int w) {
        teleX = x;
        teleY = y;
        wNum = w;
        itemMessages = null;
        items = null;
        gold = null;
        receivedGold = null;
    }

    public void addDialogue(String[] dialogue) {
        this.dialogue = new ArrayList<>();

        for (String p : dialogue) {
            this.dialogue.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), p, Color.BLACK));
            //System.out.println(p);
        }

    }

    public void addOptional(String[] pre, String[] yes, String[] no) {
        preOption = new ArrayList<>();
        yesOption = new ArrayList<>();
        noOption = new ArrayList<>();

        for (String p : pre) {
            preOption.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), p, Color.BLACK));
        }
        for (String p : yes) {
            yesOption.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), p, Color.BLACK));
        }
        for (String p : no) {
            noOption.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), p, Color.BLACK));
        }
    }

    public void setHad() {
        hadItems = true;
        dialogue.clear();
        if (preOption != null) {
            preOption.clear();
        }

        dialogue.add(new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "It appears you have taken everything.", Color.BLACK));

    }

    @Override
    synchronized void activate() {

        StateMachine.toggleLock();
        StateMachine.toggleLock();
        if (!t1.isAlive()) {
            t1.start();
        }
    }

    @Override
    boolean isInteractive() {
        return true;

    }

    public void drawMessage(RenderWindow w) {
        if (dialogue != null) {
            for (MessageBox p : dialogue) {
                if (!p.hidden) {
                    p.draw(w);
                }
            }
        }

        if (preOption != null) {
            for (MessageBox p : preOption) {
                if (!p.hidden) {
                    p.draw(w);
                }
            }

        }

        if (yesOption != null) {
            for (MessageBox p : yesOption) {
                if (!p.hidden) {
                    p.draw(w);
                }
            }

        }

        if (noOption != null) {
            for (MessageBox p : noOption) {
                if (!p.hidden) {
                    p.draw(w);
                }
            }
        }

        if (notEnough != null) {
            if (!notEnough.hidden) {
                notEnough.draw(w);
            }
        }

        if (itCosts != null) {
            if (!itCosts.hidden) {
                itCosts.draw(w);
            }
        }

        if (itemMessages != null) {
            for (MessageBox p : itemMessages) {
                if (!p.hidden) {
                    p.draw(w);
                }
            }
        }

        if (receivedGold != null) {
            if (!receivedGold.hidden) {
                receivedGold.draw(w);
            }
        }

    }

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean within(int px, int py) {
        return px > x - ((state.height * 1.2) * (Game.SCALE)) && px < x + ((state.height * 1.2) * (Game.SCALE))
                && py > y - ((state.height * 1.2) * (Game.SCALE)) && py < y + ((state.height * 1.2) * (Game.SCALE));
    }

    @Override // Uses a rectangle around the player to detect if this actor is within other actors.
    boolean withinInteractive(int px, int py) {
        return px > x - ((state.height * 1) * (Game.SCALE)) && px < x + ((state.height * 1) * (Game.SCALE))
                && py > y - ((state.height * 1) * (Game.SCALE)) && py < y + ((state.height * 1) * (Game.SCALE));
    }

    public void setTilePosition(int x, int y) {
        this.setPosition(x * Game.tileSize, y * Game.tileSize);
    }

    void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
