/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LBals
 */
public class Item {
    
    int id;
    String name;
    
    public Item(int id, String name){
        this.id = id;
        this.name = name;
        
    }
    
    int getId(){
        return id;
    }
    
    String getName(){
        return name;
    }
    
}
