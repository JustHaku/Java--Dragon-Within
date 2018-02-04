import java.io.IOException;
import org.jsfml.window.*;
import org.jsfml.graphics.*;

/**
 * The class that changes the scenes of the game controlling what the player sees.
 *
 * @author KSparnenn
 */
public class StateMachine
{
  public void run() throws InterruptedException, IOException
  {
    Character[] team = new Character[6];
    State[] states = new State[4];
    int screenWidth = 288;
    int screenHeight = 160;
    int scale = 6;
    int state = 0;

    RenderWindow window = new RenderWindow();
    window.create(new VideoMode(screenWidth*scale, screenHeight*scale), "The Dragon Within Vol.1",WindowStyle.CLOSE);
    window.setFramerateLimit(60); // Limit the framerate to 60.

    State mainMenu = new MainMenu(window, scale, 3);
    State gameWorld = new Game(window, scale);

    team[0] = Game.player1;
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

    while (window.isOpen())
    {
      state = states[state].run();
      if (state == 99)
      {
          state = 1;
      }
    }
  }
}