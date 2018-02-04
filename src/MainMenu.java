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
public class MainMenu extends Menu implements State
{
  private Texture mainBG;
  private Sprite mainBGsp;
  private Music menuMusic;

  public MainMenu(RenderWindow window, int scale, int options_num) throws IOException
  {
    menuWindow(window, scale, 288, 160, options_num);

    text_font = new Font();
    text_font.loadFromFile(Paths.get("src/graphics/Menu/Stay_Wildy.ttf"));

    soundBuffer = new SoundBuffer();
    soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));

    this.menuMusic = getMenuMusic();
    menuMusic.setLoop(true);
    menuMusic.openFromFile(Paths.get("src/audio/Menu/Soliloquy.wav"));

    this.mainBG = getBackground();
    mainBG.loadFromFile(Paths.get("src/graphics/Menu/main_m.jpg"));
    mainBG.setSmooth(true);

    this.mainBGsp = getBGSprite(mainBG);
    mainBGsp.setOrigin(Vector2f.div(new Vector2f(mainBG.getSize()), 2));
    mainBGsp.setPosition(screenWidth/2, screenHeight/2);

    text[0] = new Text("New Game", text_font, screenHeight/10);
    bounds = text[0].getLocalBounds();
    text[0].setOrigin(bounds.width / 2, bounds.height / 2);
    text[0].setPosition(screenWidth/2, screenHeight/4);

    text[1] = new Text("Continue", text_font, screenHeight/10);
    bounds = text[1].getLocalBounds();
    text[1].setOrigin(bounds.width/2, bounds.height/2);
    text[1].setPosition(screenWidth/2, screenHeight/4 + screenHeight/10);

    text[2] = new Text("Settings", text_font, screenHeight/10);
    bounds = text[2].getLocalBounds();
    text[2].setOrigin(bounds.width/2, bounds.height/2);
    text[2].setPosition(screenWidth/2, screenHeight/4 + screenHeight/5);

    menuSound = new Sound();
    menuSound.setBuffer(soundBuffer);
  }
  @Override
  public int run()
  {
    boolean paused = false;
    menuMusic.play();
    option = 1;
    text[0].setColor(Color.BLACK);

    while(window.isOpen() && paused == false)
    {
      window.clear(Color.WHITE);
      window.draw(mainBGsp);
      drawText(text);
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
          /*if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE"))
          {
              window.close();
          }*/

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
            }
            else if (option == 2)
            {
              menuMusic.stop();
              paused = true;
              option = 1;
            }
          }
          showSelection(text, option);
        }
      }
    }
    return option;
  }
}
