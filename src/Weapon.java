/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author LBals
 */
public class Weapon extends Item{

    int dmg;

    public Weapon(int id, String name, int Damage) {
        super(id,name);
        this.dmg = Damage;
        isWeapon = true;
    }

}
