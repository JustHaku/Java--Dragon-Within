/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author valka
 */
public class BasicSkills {

    public static Skills heal = new Skills("Heal",
            30, 40,//costs: 30; the effected stat is changed by 40  
            (character, value) -> {/*character.reduceManaBy(value)*/
            },// a function describing how the character performming the skill is going to pay for the skill
            (character, value) -> {/*character.increaseHealthby(value) */
            },// a function describing how the skill is going affect the character that the skill is applied on 
            false, false); // skill is not unary; the effects of it cannot be reverted after the battle ends

    public static Skills fireball = new Skills("Fireball",
            50, 60,//
            (character, value) -> {/*character.reduceManaBy(value)*/
            },
            (character, value) -> {/*character.reduceHealthBy(value)*/
            }, false, false);

    public static Skills speedbuff = new Skills("Speed Up",
            20, 40,//
            (character, value) -> {/*character.reduceActionPointsBy(value)*/
            },//
            (character, value) -> {/*character.increaseSpeedBy(value)*/
            },//
            true, true);// skill is performed on the character casting it; the buff is removed after battle
}
