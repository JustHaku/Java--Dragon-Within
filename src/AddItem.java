
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
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
public class AddItem extends Activator {

    public static ArrayList<MessageBox> messages = new ArrayList<>();
    private MessageBox p;
    private Inventory inventory;
    private Item item = null;
    private Integer gold = null;
    private Music m = null;
    private WorldPieceActor wp = null;
    private boolean remove = false;
    private int wn = 0;

    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory) {
        super(imgTexture, text, x, y);

        p = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You received: " + item.getName(), Color.BLACK);

        this.inventory = inventory;
        this.item = item;
    }

    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory, Music m) {
        super(imgTexture, text, x, y);

        p = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You received: " + item.getName(), Color.BLACK);

        this.inventory = inventory;
        this.item = item;

        this.m = m;
    }

    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory, Music m, WorldPieceActor wp) {
        super(imgTexture, text, x, y);

        p = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You received: " + item.getName(), Color.BLACK);

        this.inventory = inventory;
        this.item = item;

        this.m = m;
        this.wp = wp;

    }

    public AddItem(Texture imgTexture, String text, int x, int y, int gold, Inventory inventory, Music m, WorldPieceActor wp, boolean remove, int wn) {
        super(imgTexture, text, x, y);
        this.remove = remove;
        this.wn = wn;
        p = new MessageBox(0, Game.screenHeight - (49 * (Game.SCALE / 2)), "You received: " + gold + " gold", Color.BLACK);

        this.inventory = inventory;
        this.gold = gold;

        this.m = m;
        this.wp = wp;

    }

    public void addAlt(int c1, int c2) {
        if (wp != null) {
            wp.addAlt(c1, c2);
        }
    }

    @Override
    public void setActivated() {
        activated = true;

        if (wp != null) {
            wp.setAlt();
            if(remove == true){
                try{
                    StateMachine.gameWorld.maps.get(wn).getActor().remove(wp);
                    
                }catch(NullPointerException e){
                    
                }
                
            }
        }
        
        

    }

    @Override
    void activate() {

        if (activated == false) {
            activated = true;
            //activatedList.add(id);

            if (item != null) {
                System.out.print("You received: ");
                System.out.println(item.getName());
                StateMachine.gameWorld.playerInv.addItem(item);

            }
            if (gold != null) {
                StateMachine.gameWorld.playerInv.setGold(gold);
            }
            
            if(remove == true){
                try{
                    StateMachine.gameWorld.maps.get(wn).getActor().remove(wp);                    
                }catch (NullPointerException e){
                    
                }
                
            }

            messages.add(p);
            boolean done = true;
            while (true) {
                done = true;
                for (MessageBox m : messages) {
                    if (p != m && m.hidden == false) {
                        done = false;
                    }
                }
                if (done == true) {
                    break;
                }
            }
            if (wp != null) {
                wp.setAlt();
            }
//            if (remove == true) {
//                StateMachine.gameWorld.maps.get(wn).getActor().remove(wp);
//
//            }

            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    long startTime = System.currentTimeMillis(); //fetch starting time
                    Game.player1.movementLock = true;
                    p.showHide();
                    if (m != null) {
                        m.play();
                    }

                    try {
                        Thread.sleep(350);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    while (true) {
                        if (Keyboard.isKeyPressed(Keyboard.Key.E)) {
                            p.hide();
                            try {
                                Thread.sleep(350);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(Trader.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            break;
                        }
                    }

                    Game.player1.movementLock = false;

                }
            });

            t1.start();
        } else {
            //p.hidden = true;
        }

    }

}
