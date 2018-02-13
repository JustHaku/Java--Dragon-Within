import java.io.IOException;
/**
 * The class used to start the game, Variables can be called here if implemented.
 *
 * @author Kirk Sparnenn
 */
public class Driver
{  
  public static void main(String args[]) throws InterruptedException, IOException
  {
    int scale = 4;
    int setScale = 0;
    while (scale != setScale)
    {
      setScale = scale;
      StateMachine rpgGame = new StateMachine(scale);
      scale = rpgGame.run();  
    }
    
  }
}