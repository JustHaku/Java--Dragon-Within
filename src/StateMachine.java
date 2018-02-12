import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.jsfml.window.*;
import org.jsfml.graphics.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsfml.system.Vector2i;

/**
 * The class that changes the scenes of the game controlling what the player sees.
 *
 * @author Kirk Sparnenn
 */
public class StateMachine
{
  public static ArrayList<Character> team = new ArrayList<>();
  public static State[] states = new State[16];
  private static boolean locked = false;
  
  public static void toggleLock(){
      locked = locked != true;
  }

  /**
  * Changes states/ screens when given the relevent number.
  */
  public void run() throws InterruptedException, IOException
  {
    int screenWidth = 288;
    int screenHeight = 160;
    int scale = 6;
    int state = 0;

    RenderWindow window = new RenderWindow();
    window.create(new VideoMode(screenWidth*scale, screenHeight*scale), "The Dragon Within Vol.1",WindowStyle.CLOSE);
    window.setFramerateLimit(60); // Limit the framerate to 60.

    Game gameWorld = new Game(window, scale);
    try {
      Save s = Save.load("src/saves/save000");
      gameWorld.load(s);
    }
    catch (FileNotFoundException ex) {
      Logger.getLogger(StateMachine.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (ClassNotFoundException ex) {
      Logger.getLogger(StateMachine.class.getName()).log(Level.SEVERE, null, ex);
    }
    catch (EOFException ex) {
      ex.printStackTrace();
    }

    State mainMenu = new MainMenu(window, scale, 4);

    team.add(Game.player1);
    team.add(Game.Petros);


    State battleSystem = new BattleSystem(window, scale, 3, team);
    State inventoryMenu = new InventoryMenu(window, scale, 7, team);
    State settingsMenu = new SettingsMenu(window, scale);
    State itemsMenu = new ItemsMenu(window, scale, 4, gameWorld);
    State skillsMenu = new SkillsMenu(window, scale, 1);
    State magicMenu = new MagicMenu(window, scale, 1);
    states[0] = mainMenu;
    states[1] = gameWorld;
    states[2] = battleSystem;
    states[3] = inventoryMenu;
    states[4] = settingsMenu;
    states[5] = itemsMenu;
    states[6] = skillsMenu;
    states[7] = magicMenu;

    Vector2i v = new Vector2i(100,100);
    window.setKeyRepeatEnabled(true);


    while (window.isOpen())
    {
      if(state == 2){
        states[state] = new BattleSystem(window, scale, 3, team);
      }

      else if(state == 3){
        states[state] = new InventoryMenu(window, scale, 7, team);
      }
      
      // Change state if no new class is required.
      if(!locked){
          state = states[state].run();              
      }
      
      //FileManager.save("src/saves/save000", (Game)gameWorld);
      if (state == 99)
      {
          
          
          gameWorld = new Game(window, scale);
          //window.setSize(new Vector2i(100,100));
          //System.out.println(window.getSize());
          states[1] = gameWorld;
          state = 1;
      }
    }
  }
}
