import java.io.Serializable;
import java.util.*;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard;
/**
*Base-Class for creating characters in the game.
*@author Petros Soutzis
*/
public class Character extends Actor implements Serializable
{
    
  static final long serialVersionUID = 42L;
  protected String name;
  protected Skills[] skills = new Skills[4]; //The skills each player has
  protected Item[] held_items = new Item[4];
  protected int level, exp, health, mana, speed, attack, defence, max_health, max_mana;

  protected boolean isFriendly;
  protected boolean isAlive = true;

  protected final int exp_const = 50;

  public ArrayList<Object> collectStats()
  {
    ArrayList<Object> stats = new ArrayList<>();

    stats.add(name);
    
    stats.add(held_items);
//    stats.add(c1);
//    stats.add(c2);
    stats.add(level);
    stats.add(exp);
    stats.add(health);
    stats.add(mana);
    stats.add(speed);
    stats.add(attack);
    stats.add(defence);
    stats.add(max_health);
    stats.add(max_mana);
    stats.add(isAlive);
    stats.add(isFriendly);
    //stats.add(skills);

    return stats;
  }
  
  public void distributeStats(ArrayList<Object> stats){
      name = (String)stats.get(0);
      held_items = (Item[])stats.get(1);
      level = (int)stats.get(2);
      exp = (int)stats.get(3);
      health = (int)stats.get(4);
      mana = (int)stats.get(5);
      speed = (int)stats.get(6);
      attack = (int)stats.get(7);
      defence = (int)stats.get(8);
      max_health = (int)stats.get(9);
      max_mana = (int)stats.get(10);
      isAlive = (boolean)stats.get(11);
      isFriendly = (boolean)stats.get(12);
      //skills = (Skills[])stats.get(13);
  }

  @Override
  void calcMove(int minX, int minY, int maxX, int maxY){
  }

  /**
  *exp_const will be the value needed (quadratically) for each level-up.
  *e.g: if constant is 50, then lvl2 -> 50xp, lvl3 -> 150xp, lvl4 -> 300xp
  *lvl(4+1) = lvl4_exp + (lvl)4*constant
  *lvl5 = 300 + 200 -> lvl5 = 500xp
  *exp is the current experience points that the character has
  *@param exp_gain -> the experience points that the character gained (probably after battle)
  *@return the character's current level
  */
  public int level_calc(int exp_gain)
  {
    int current_level = 0;
    int experience = exp + exp_gain;
    //multiplying exp gain by 8, so that with each level increment, the character will need 50 more xp to gain a level
    double value = 1 + (8*experience/exp_const);
    return current_level = (1 + (int)Math.sqrt(value))/2;
  }

  /**
  *@return the current experience points that the character has, according to its level
  */
  public int exp_calc()
  {
    double level = this.level;
    double current_exp = ((Math.pow(level, 2) - level) * exp_const)/2;
    return (int)current_exp;
  }


/**
*Method will be called when player get +1 level
*and it will generate random numbers from 3 to 8 and increase
*the player's stats
*/
  public void levelUP()
  {
    Random rand = new Random();
    int rand_number;

    level += 1;

    rand_number = rand.nextInt(6) + 3;
    max_health += rand_number;

    rand_number = rand.nextInt(6) + 3;
    max_mana += rand_number;

    rand_number = rand.nextInt(6) + 3;
    attack += rand_number;

    rand_number = rand.nextInt(6) + 3;
    defence += rand_number;

    rand_number = rand.nextInt(6) + 3;
    speed += rand_number;
  }

  void heal(int heal) {
      isAlive = true;
      health = Math.min(health + heal, max_health);
      if(health < 0)
      {
        health = 0;
        isAlive = false;
      }
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

  void takeDamage(String type, int value)
  {
    int armor = totalResistance(value);
    int deductby = value - armor;
    health = Math.max(health-deductby, 0);
    isAlive = health > 0;
  }

  int totalDmg()
  {
    int damage = attack;

    for(int i = 0; i<held_items.length; i++)
    {
      if(held_items[i] != null)
      {
        if(held_items[i].isWeapon)
        {
          damage = damage + (attack * ((Weapon)held_items[i]).dmg)/100;
          return damage;
        }
      }
    }
    return damage;
  }

  int totalResistance(int dmg_value)
  {
    int resistance = (dmg_value * this.defence)/100;
    return resistance;
  }

  void addSkill(Skills s)
  {
    boolean duplicate = false;

    for(int i=0; i<skills.length; i++)
    {
      for(int j=0; j<skills.length; j++)
      {
        if(skills[j] != null)
        {
          if(skills[j].getName().equals(s.getName()))
            duplicate = true;
        }
      }
      if(skills[i] == null && !duplicate){
        skills[i] = s;
        break;
      }
      else if(duplicate){
        System.out.println("Skill already learned!");
        break;
      }
      else if(skills[i] != null){
        System.out.println("All skills full");
      }
    }
  }

  void replaceSkill(Skills s, int index)
  {
    boolean duplicate = false;
    for(int j=0; j<skills.length; j++)
    {
      if(skills[j] != null)
      {
        if(skills[j].getName().equals(s.getName()))
          duplicate = true;
      }
    }
    if(!duplicate)
      skills[index] = s;
  }

}
