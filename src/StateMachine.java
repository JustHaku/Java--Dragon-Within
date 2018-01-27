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
    int scale = 4;
    int state = 0;
    
    RenderWindow window = new RenderWindow();    
    window.create(new VideoMode(screenWith*scale, screenHight*scale), "The Dragon Within Vol.1");
    // Limit the framerate to 60.
    window.setFramerateLimit(60);

    State mainMenu = new MainMenu(window, scale);
    State gameWorld = new Game(window, scale);
    State battleSystem = new BattleSystem(window, scale);
    State[] states = new State[3];
    states[0] = mainMenu;
    states[1] = gameWorld;
    states[2] = battleSystem;
    
    while (window.isOpen())
    {
      state = states[state].run();
    }
  }
}