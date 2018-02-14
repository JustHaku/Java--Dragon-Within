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

    public Consumable(int id, String name, int heal, int regen, int value) {
        super(id, name);

        this.value = value;

        this.heal = heal;
        this.regen = regen;

    }

    Consumable(int id, String name, Consumable c) {
        super(id, name);
        heal = c.heal;
        regen = c.regen;
        value = c.value;

    }

    public void use(Character p) {
        p.heal(heal);
        p.regen(regen);
    }

}
