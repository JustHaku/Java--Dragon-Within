import java.util.*;
import org.jsfml.graphics.IntRect;
import org.jsfml.graphics.Sprite;
import org.jsfml.graphics.Texture;
import org.jsfml.window.Keyboard;

public class NPC extends Character
{

  /**
  *Constructs a unique character, with all the required information manually entered.
  *Most of the character created this way will be playable and the player will have the ability to
  *add them to his party
  *@param name The identifier of each unique character. Also its name :)
  *@param health The health that the character will have
  *@param mana The mana that the character will have
  *@param atk The attack points that the character will have
  *@param def The defence points that the character will have
  *@param spd The speed that the character will have
  *@param lvl The level of the character at the time of creation
  */
  //*@param item The held item(s) by given character
  //*@param imgTexture spritesheet for character
  public NPC(String name, int health, int mana, int atk, int def, int spd, int lvl, boolean isFriendly/*, Item[] item, Texture imgTexture*/)
  {
    this.name = name;
    max_health = health;
    this.health = health;
    max_mana = mana;
    this.mana = mana;
    attack = atk;
    defence = def;
    speed = spd;
    level = lvl;
    exp = exp_calc();
    this.isFriendly = isFriendly;


    /***THIS IS TEMPORARY, JUST FOR TESTING***/
    //skill_names[0] = "Dance";
    //skill_names[1] = "Splash";


    /*held_items[0] = item;
    c1 = ?;
    c2 = ?;
    state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16); // Creates the rectangle for the spritesheet.

    img = new Sprite(imgTexture, state);
    img.setScale(Game.SCALE / ps, Game.SCALE / ps); // Changes player scale to 2/3 of tile size.

    x = 0; // Default position.
    y = 0;

    obj = img; // Sets img as collision object.
    setPosition = img::setPosition;
    */
  }

  /**
  *Constructs a character with random stats, based on the level inputted
  *Characters created this way, will most likely be enemies spawned at battle
  *@param lvl The level that the character will have at the time of creation
  */
  public NPC(int lvl)
  {
    Random rand = new Random();
    int[] stats = new int[5];
    int randomInt;

    name = "Enemy";
    max_health = 80;
    max_mana = 80;
    attack = 8;
    defence = 8;
    speed = 7;
    for(int i = 0; i<stats.length; i++){
      stats[i] = 0;
      for(int j = 0; j<lvl; j++){
        randomInt = rand.nextInt(5) + 1;
        stats[i] += randomInt;
      }
    }
    max_health += stats[0];
    max_mana += stats[1];
    attack += stats[2];
    defence += stats[3];
    speed += stats[4];
    health = max_health;
    mana = max_mana;
    level = lvl;
    isFriendly = false;
  }

}
