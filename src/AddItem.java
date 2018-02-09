
import java.util.ArrayList;
import org.jsfml.audio.Music;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.Texture;

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
    private Item item;
    private Music m = null;
    private WorldPieceActor wp = null;

    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory) {
        super(imgTexture, text, x, y);

        p = new MessageBox(2 * Game.SCALE, Game.screenHeight - (12 * Game.SCALE), "You received: " + item.getName(), Color.BLACK);

        this.inventory = inventory;
        this.item = item;
    }
    
    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory, Music m) {
        super(imgTexture, text, x, y);

        p = new MessageBox(2 * Game.SCALE, Game.screenHeight - (12 * Game.SCALE), "You received: " + item.getName(), Color.BLACK);

        this.inventory = inventory;
        this.item = item;
        
        this.m = m;
    }
    
    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory, Music m, WorldPieceActor wp) {
        super(imgTexture, text, x, y);

        p = new MessageBox(2 * Game.SCALE, Game.screenHeight - (12 * Game.SCALE), "You received: " + item.getName(), Color.BLACK);

        this.inventory = inventory;
        this.item = item;
        
        this.m = m;
        this.wp = wp;
        
    }
    
    public void addAlt(int c1, int c2){
        if(wp != null){
            wp.addAlt(c1, c2);
        }
    }
    
    @Override
    public void setActivated(){
        activated = true;

        if(wp != null){
            wp.setAlt();
         }
        
        
    }
    
    
    

    @Override
    void activate() {

        if (activated == false) {
            activated = true;
            //activatedList.add(id);
            System.out.print("You received: ");
            System.out.println(item.getName());
            inventory.addItem(item);
            messages.add(p);
            boolean done = true;
            while (true) {
                done = true;                
                for (MessageBox m : messages) {
                    if (p != m && m.hidden == false) {
                        done = false;
                    }
                }
                if(done == true){
                    break;
                }
            }
            if(wp != null){
                wp.setAlt();
            }
            if(m != null){
                m.play();
            }

            p.showHide();
            
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    long startTime = System.currentTimeMillis(); //fetch starting time
                    while (false || (System.currentTimeMillis() - startTime) < 2000) {
                        // do something
                    }
                    p.hide();

                }
            });

            t1.start();

        } else {
            p.hidden = true;
        }

    }

}
