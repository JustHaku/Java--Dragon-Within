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

    MainMenu mainMenu = new MainMenu(window);
    Game gameWorld = new Game(window, scale);
    //class[] states = new class[2];
    //states[0] = mainMenu;
    //states[1] = gameWorld;
    
    while (window.isOpen()){
      //state = states[state].run();
      if (state == 0)
          state = mainMenu.run();
      else if (state == 1)
        state = gameWorld.run();
    }
  }
}