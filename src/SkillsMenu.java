import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the skills menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class SkillsMenu extends Menu implements State
{
  private RenderWindow window;
  private int scale;
  private Font text_font;
  private Text text;
  private boolean paused = false;
  private int screenHeight;
  private int screenWidth;
  private RectangleShape menuRect;
  private RectangleShape playerRect;
  public static boolean returnTo = false;
  
  public SkillsMenu(RenderWindow window, int scale, int options_num) throws IOException
  {
    menuWindow(window, scale, 288, 160, options_num);
    this.window = window;
    this.scale = scale;
    screenHeight = 160*scale;
    screenWidth = 288*scale;
    
    text_font = new Font();
    text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));
    
    menuRect = new RectangleShape(new Vector2f((screenWidth/4)*3, screenHeight - 10));
    menuRect.setFillColor(new Color(11,2,138));
    menuRect.setPosition(5,5);

    playerRect = new RectangleShape(new Vector2f((screenWidth/4) - 15, screenHeight - 10));
    playerRect.setFillColor(new Color(11,2,138));
    playerRect.setPosition(((screenWidth/4)*3)+10,5);
  }
  
  /*
  * Main loop draws and controlls moving through menu.
  */
  @Override
  public int run()
  {
    returnTo = true;
    paused = false;
    while(window.isOpen() && paused == false)
    {
      window.clear(Color.BLACK);
      window.draw(menuRect);
      window.draw(playerRect);
      window.display();
      
      for(Event event : window.pollEvents())
      {
        KeyEvent keyEvent = event.asKeyEvent();

        if(event.type == Event.Type.CLOSED)
        {
          window.close();
        }

        else if (event.type == Event.Type.KEY_PRESSED)
        {
          if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE"))
          {
            paused = true;            
          }
        }
      }
    }
      return 3;
  }
}