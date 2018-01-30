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
    int screenWith = 288;
    int screenHight = 160;
    int scale = 6;
    int state = 0;
    
    RenderWindow window = new RenderWindow();    
    window.create(new VideoMode(screenWith*scale, screenHight*scale), "The Dragon Within Vol.1",WindowStyle.CLOSE);
    // Limit the framerate to 60.
    window.setFramerateLimit(60);

    State mainMenu = new MainMenu(window, scale);
    State gameWorld = new Game(window, scale);
    State battleSystem = new BattleSystem(window, scale);
    State inventoryMenu = new InventoryMenu(window, scale);
    State[] states = new State[4];
    states[0] = mainMenu;
    states[1] = gameWorld;
    states[2] = battleSystem;
    states[3] = inventoryMenu;
    
    while (window.isOpen())
    {
      state = states[state].run();
    }
  }
}