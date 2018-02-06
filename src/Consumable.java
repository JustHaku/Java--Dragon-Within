/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LBals
 */
public class Consumable extends Item {
    
    private int heal;
    private int regen;
    
    public Consumable(int id,String name, int heal, int regen) {
        super(id,name);
        
        this.heal = heal;
        this.regen = regen;
        
        
        
    }
    
    public void use(Player p){
        p.heal(heal);
        p.regen(regen);
        
    }
    
}
