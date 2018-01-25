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
    StateMachine rpgGame = new StateMachine();
    rpgGame.run();
  }
}