
import java.util.ArrayList;
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
    
    public AddItem(Texture imgTexture, String text, int x, int y, Item item, Inventory inventory) {
        super(imgTexture, text, x, y);
        
        p = new MessageBox(2*Game.SCALE,Game.screenHeight-100, "You received: " + item.getName(), Color.BLACK);
        
        this.inventory = inventory;
        this.item = item;
    }
    
    @Override 
    void activate(){
 
        if(activated == false){
            System.out.print("You received: ");
            System.out.println(item.getName());
            inventory.addItem(item);
            messages.add(p);
            p.showHide();
            activated = true;
        }else{
            p.hidden = true;
        }
                
        
    }
    
    
    
}
