import java.util.logging.Level;
import java.util.logging.Logger;
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
  protected IntRect state; // The Players current character model from the spritesheet.
  protected int[] held_items = new int[4];
  protected int c1, c2, level, exp, health, mana, speed, attack, defence, max_health, max_mana;

  protected final float ps = (float) 1;

  @Override
  void calcMove(int minX, int minY, int maxX, int maxY){
  }

}
