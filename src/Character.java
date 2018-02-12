import java.util.*;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard;
/**
*Base-Class for creating characters in the game.
*@author Petros Soutzis
*/
public class Character extends Actor
{
  protected Sprite img;
  protected String name;
  protected String[] skill_names = new String[4]; //dummy of the skills that each player has
  //protected Skills[] skills = new Skills[4]; //The skills each player has
  protected IntRect state; // The Players current character model from the spritesheet.
  protected int[] held_items = new int[4];
  protected int c1, c2, level, exp, health, mana, speed, attack, defence, max_health, max_mana;
  protected boolean isFriendly;

  protected boolean isAlive = true;
  protected final int exp_const = 50;
  protected final float ps = (float) 1;

  @Override
  void calcMove(int minX, int minY, int maxX, int maxY){
  }

  /**
  *@param constant will be the value needed (quadratically) for each level-up.
  *e.g: if constant is 50, then lvl2 -> 50xp, lvl3 -> 150xp, lvl4 -> 300xp
  *lvl(4+1) = lvl4_exp + (lvl)4*constant
  *lvl5 = 300 + 200 -> lvl5 = 500xp
  *@param current_exp -> the current experience points that the character has
  *@param exp_gain -> the experience points that the character gained (probably after battle)
  *@return the character's current level
  */
  public int level_calc(int constant, int current_exp, int exp_gain)
  {
    int current_level = 0;
    int experience = current_exp + exp_gain;
    //multiplying exp gain by 8, so that with each level increment, the character will need 50 more xp to gain a level
    double value = 1 + (8*experience/constant);
    return current_level = (1 + (int)Math.sqrt(value))/2;
  }

  /**
  *@param constant will be the value needed (quadratically) for each level-up.
  *e.g: if constant is 50, then lvl2 -> 50xp, lvl3 -> 150xp, lvl4 -> 300xp
  *lvl(4+1) = lvl4_exp + (lvl)4*constant
  *lvl5 = 300 + 200 -> lvl5 = 500xp
  *@param lvl -> the current level of the character
  *@return the current experience points that the character has, according to its level
  */
  public int exp_calc(int constant, int lvl)
  {
    double level = lvl;
    double current_exp = ((Math.pow(level, 2) - level) * constant)/2;
    return (int)current_exp;
  }


/**
*@param a Is the character which will level up
*Method will be called when player get +1 level
*and it will generate random numbers from 3 to 8 and increase
*the player's stats
*/
  public void levelUP(Character a)
  {
    Random rand = new Random();
    int rand_number;

    a.level += 1;

    rand_number = rand.nextInt(6) + 3;
    a.max_health += rand_number;

    rand_number = rand.nextInt(6) + 3;
    a.max_mana += rand_number;

    rand_number = rand.nextInt(6) + 3;
    a.attack += rand_number;

    rand_number = rand.nextInt(6) + 3;
    a.defence += rand_number;

    rand_number = rand.nextInt(6) + 3;
    a.speed += rand_number;
  }

  void heal(int heal) {
      health = Math.min(health + heal, max_health);
  }

  void regen(int regen) {
      mana = Math.min(mana + regen,max_mana);
  }

  void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
  }

  /**
  *@return the character's level
  */
    public int getLevel(){  //might not be needed if stats[x][8] is used instead
      return level;
    }

}
