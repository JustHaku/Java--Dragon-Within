import java.io.IOException;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * The class that provides the main menu functionality.
 *
 * @author Kirk Sparnenn
 */
public class MainMenu extends State
{ 
  private RenderWindow window;
  private int scale;
  private int screenWith;
  private int screenHight;
  private int option = 1;
  private Texture mainBG;
  private Sprite mainBGsp;
  private Font stayWildy;
  private SoundBuffer soundBuffer;
  private Text text1;
  private Text text2;
  private Text text3;
  private Sound menuSound;
  private Music menuMusic;
  
  public MainMenu(RenderWindow window, int scale)
  {
    this.window = window;
    this.scale = scale;
    this.screenWith = 288*scale;
    this.screenHight = 160*scale;
    
    mainBG = new Texture();
    stayWildy = new Font();
    soundBuffer = new SoundBuffer();
    menuMusic = new Music();
    menuMusic.setLoop(true);
    try {
        mainBG.loadFromFile(Paths.get("src/graphics/Menu/main_m.jpg"));
        stayWildy.loadFromFile(Paths.get("src/graphics/Menu/Stay_Wildy.ttf"));
        menuMusic.openFromFile(Paths.get("src/audio/Menu/Soliloquy.wav"));
        soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));
    } catch (IOException ex) {
        ex.printStackTrace();
    }
    mainBG.setSmooth(true);
    mainBGsp = new Sprite(mainBG);
    
    mainBGsp.setOrigin(Vector2f.div(new Vector2f(mainBG.getSize()), 2));
    mainBGsp.setPosition(screenWith/2, screenHight/2);
    
    text1 = new Text("New Game", stayWildy, screenHight/10);
    FloatRect text1bounds = text1.getLocalBounds();
    text1.setColor(Color.BLACK);
    text1.setOrigin(text1bounds.width / 2, text1bounds.height / 2);
    text1.setPosition(screenWith/2, screenHight/4);
    
    text2 = new Text("continue", stayWildy, screenHight/10);
    FloatRect text2bounds = text2.getLocalBounds();
    text2.setOrigin(text2bounds.width / 2, text2bounds.height / 2);
    text2.setPosition(screenWith/2, screenHight/4 + screenHight/10);
    
    text3 = new Text("Settings", stayWildy, screenHight/10);
    FloatRect text3bounds = text3.getLocalBounds();
    text3.setOrigin(text3bounds.width / 2, text3bounds.height / 2);
    text3.setPosition(screenWith/2, screenHight/4 + screenHight/5);
    

    
    menuSound = new Sound();
    menuSound.setBuffer(soundBuffer);
  }
  @Override
  public int run()
  {
      boolean paused = false;
      menuMusic.play();
      option = 1;
    while(window.isOpen() && paused == false) 
    {
      window.clear(Color.WHITE);
      window.draw(mainBGsp);
      window.draw(text1);
      window.draw(text2);
      window.draw(text3);
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
          if (keyEvent.key == Keyboard.Key.valueOf("S"))
          {
            menuSound.play();
            option++;
            if (option >=3)
            {
              option=3;              
            }            
          }
          else if (keyEvent.key == Keyboard.Key.valueOf("W"))
          {
            menuSound.play();
            option--;
            if (option <=1)
            {
              option=1;
            }
          }
          else if (keyEvent.key == Keyboard.Key.valueOf("E"))
          {
            if (option == 1)
            {
              menuMusic.stop();
              paused = true;
              option = 99;
            } else if (option == 2)
            {
              menuMusic.stop();
              paused = true;
              option = 1;
            }           
          }
          
          else if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE"))
          {
              window.close();         
          }
          if (option == 1)
          {
            text1.setColor(Color.BLACK);
            text2.setColor(Color.WHITE);
            text3.setColor(Color.WHITE);
          } else if (option == 2)
          {
            text1.setColor(Color.WHITE);
            text2.setColor(Color.BLACK);
            text3.setColor(Color.WHITE);
          } else if (option == 3)
          {
            text1.setColor(Color.WHITE);
            text2.setColor(Color.WHITE);
            text3.setColor(Color.BLACK);
          }
        }
      }
    }
    return option;
  }
}