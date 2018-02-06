import java.io.IOException;
import org.jsfml.window.*;
import org.jsfml.graphics.*;
import java.util.*;
import org.jsfml.system.Vector2i;

/**
 * The class that changes the scenes of the game controlling what the player sees.
 *
 * @author KSparnenn
 */
public class StateMachine
{
  public void run() throws InterruptedException, IOException
  {
    ArrayList<Character> team = new ArrayList<>();
    State[] states = new State[100];
    int screenWidth = 288;
    int screenHeight = 160;
    int scale = 3;
    int state = 0;

    RenderWindow window = new RenderWindow();
    window.create(new VideoMode(screenWidth*scale, screenHeight*scale), "The Dragon Within Vol.1",WindowStyle.CLOSE);
    window.setFramerateLimit(60); // Limit the framerate to 60.

    State mainMenu = new MainMenu(window, scale, 3);
    State gameWorld = new Game(window, scale);

    team.add(Game.player1);
    /*team[1] = Game.player2;
    team[2] = Game.player3;
    team[3] = Game.player4;
    team[4] = Game.player5;
    team[5] = Game.player6;*/

    State battleSystem = new BattleSystem(window, scale, 3, team);
    State inventoryMenu = new InventoryMenu(window, scale);
    states[0] = mainMenu;
    states[1] = gameWorld;
    states[2] = battleSystem;
    states[3] = inventoryMenu;
    
    Vector2i v = new Vector2i(100,100);
    window.setKeyRepeatEnabled(false);
    while (window.isOpen())
    {
      state = states[state].run();
      if (state == 99)
      {
          gameWorld = new Game(window, scale);
          window.setSize(new Vector2i(100,100));
          System.out.println(window.getSize());
          states[1] = gameWorld;
          state = 1;
      }
    }
  }
}
