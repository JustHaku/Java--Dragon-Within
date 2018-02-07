
import java.io.Serializable;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LBals
 */
public class Inventory implements Serializable {
    
    static final long serialVersionUID = 42L;
    
    private ArrayList<Consumable> consumables = new ArrayList<>();
    private ArrayList<Weapon> weapons = new ArrayList<>();   
    private ArrayList<Trinket> trinkets = new ArrayList<>();
    private ArrayList<KeyItem> keyItems = new ArrayList<>();
    
    
    public Inventory(){
        
    }
    
    public ArrayList<Consumable> getConsumables(){
        return consumables;
    }
    
    public ArrayList<Weapon> getWeapons(){
        return weapons;
    }
    
    public ArrayList<Trinket> getTrinkets(){
        return trinkets;
    }
    
    public ArrayList<KeyItem> getKeyItems(){
        return keyItems;
    }
    
    void addItem(Item item){
        try{
            keyItems.add((KeyItem)item);            
        }catch(Exception e){
                        
        }
         try{
            weapons.add((Weapon)item);            
        }catch(Exception e){
                        
        }
          try{
            trinkets.add((Trinket)item);            
        }catch(Exception e){
                        
        }
           try{
            consumables.add((Consumable)item);            
        }catch(Exception e){
                        
        }
    }
    
    void addKeyItem(KeyItem keyItem){
        keyItems.add(keyItem);
    }
    
    void addWeapon(Weapon weapon){
        weapons.add(weapon);
    }
    
    void addTrinket(Trinket trinket){
        trinkets.add(trinket);
    }
    
    void addConsumable(Consumable consumable){
        consumables.add(consumable);
    }
    
    void removeKeyItem(int id){
        for(KeyItem ki: keyItems){
            if(ki.getId() == id){
                keyItems.remove(ki);
            }            
        }
    }
    
    void removeWeapon(int id){
        for(Weapon w: weapons){
            if(w.getId() == id){
                weapons.remove(w);
            }            
        }
    }
    
    void removeTrinket(int id){
        for(Trinket t: trinkets){
            if(t.getId() == id){
                trinkets.remove(t);
            }            
        }
    }
    
    void removeConsumable(int id){
        for(Consumable c: consumables){
            if(c.getId() == id){
                consumables.remove(c);
            }            
        }
    }
    
    void dumpContents(){
        System.out.println("Consumables:");
        for(Consumable c: consumables){
            System.out.println(c.name);
        }
    }
    
    
}
