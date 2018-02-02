import java.io.IOException;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;
/**
*Base-Class of which other menu-classes will be children-classes
*@author Petros Soutzis
*/
public class Menu
{
  protected int option = 1;
  protected int scale, screenHeight, screenWidth, options_num;
  protected FloatRect bounds;
  protected RenderWindow window;
  protected Font text_font;
  protected Text[] text;

  void menuWindow(RenderWindow window, int scale, int width, int height, int options_num)
  {
    this.options_num = options_num;
    this.window = window;
    this.scale = scale;
    this.screenWidth = width*scale;
    this.screenHeight = height*scale;
    text = new Text[options_num];
  }
}
