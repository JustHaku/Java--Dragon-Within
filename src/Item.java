
import java.io.Serializable;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LBals
 */
public class Item implements Serializable{
    static final long serialVersionUID = 42L;
    int id, value;
    String name;
    boolean isWeapon;

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
