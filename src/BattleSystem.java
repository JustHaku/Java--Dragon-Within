import java.io.IOException;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

public class BattleSystem extends State
{ 
  private RenderWindow window;
  private int scale;
  private int screenWith;
  private int screenHight;
  private Font stayWildy;
  private Text text1;
  
  public BattleSystem(RenderWindow window, int scale)
  {
    this.window = window;
    this.scale = scale;
    this.screenWith = 288*scale;
    this.screenHight = 160*scale;
    
    stayWildy = new Font();
    try {
      stayWildy.loadFromFile(Paths.get("src/graphics/Menu/Stay_Wildy.ttf"));
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    
    text1 = new Text("Battle System\nPlace Holder", stayWildy, screenHight/10);
    FloatRect text1bounds = text1.getLocalBounds();
    text1.setColor(Color.BLACK);
    text1.setOrigin(text1bounds.width / 2, text1bounds.height / 2);
    text1.setPosition(screenWith/2, screenHight/4);
    
  }
  
  @Override
  public int run()
  {
    boolean end = false;
    //System.out.print("sad\n");
    while(window.isOpen() && end == false) 
    {
      window.clear(Color.WHITE);
      window.draw(text1);
      window.display();
      
      for(Event event : window.pollEvents()) 
      {
        if(event.type == Event.Type.CLOSED) 
        {
          // User closes window.
          window.close();
        }
        else if (event.type == Event.Type.KEY_PRESSED)
        {
          KeyEvent keyEvent = event.asKeyEvent();
          if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE"))
          {
            end = true;
          }
        }
      }
    }
      return 1;
  }
}