import java.io.IOException;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;


public class BattleSystem extends Menu implements State
{

    private Character[] party = new Character[6];
    private int[] turn_state = new int[6]; //move turn_state array's elements in accordance to speed_array
    private int[] speed_array = new int[6];

    private final int FRIENDLY_START = 0;
    private final int FRIENDLY_END = 3;
    private final int ENEMY_END = 6;
    private int exp_gain, temp;


    /*void initTeams()
    {
      for(int i=0; i<party.length; i++)
      {
        turn_state[i] = i;

        if(party[i] != null)
        {
          if(i < FRIENDLY_END)
          {
            party[i] = new MainCharacter(int characterID); //tell apart unique characters
            party[i].getStats();
          }
          else if(i >= FRIENDLY_END)
          {
            party[i] = new NPC();
            party[i].generateStats();
          }
          speed_array[i] = party[i].getSpeed();
         }
       }
       turn_state = bubbleSort(speed_array, turn_state);
    }*/

    int[] bubbleSort(int[] arr, int[] turns)
    {
      int length = arr.length;
      int temp = 0;
      for (int i = 0; i<length-1; i++)
      {
        for (int j = 1; j<length-i; j++)
        {
          if(turns[j-1] < turns[j])
          {
            temp = turns[j-1];
            turns[j-1] = turns[j];
            turns[j] = temp;    // order the array of turn
            if(arr[j-1] < arr[j])
            {
              temp = arr[j-1];
              arr[j-1] = arr[j];
              arr[j] = temp;
            }
          }
        }
      }
      return arr;
    }
  public BattleSystem(RenderWindow window, int scale, int options_num) throws IOException
  {

    menuWindow(window, scale, 288, 160, options_num);

    text_font = new Font();
    text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

    text[0] = new Text("Attack", text_font, screenHeight/14);
    /*bounds = text[0].getLocalBounds();

    text[0].setOrigin(bounds.width/2, bounds.height/2);*/
    text[0].setPosition(35, 700);

    text[1] = new Text("Items", text_font, screenHeight/14);
    /*bounds = text[1].getLocalBounds();
    text[1].setOrigin(bounds.width/2, bounds.height/2);*/
    text[1].setPosition(35, 770);

    text[2] = new Text("Escape", text_font, screenHeight/14);
    /*bounds = text[2].getLocalBounds();
    text[2].setOrigin(bounds.width/2, bounds.height/2);*/
    text[2].setPosition(35, 840);

  }

  /**
  *@param enemy is the array of NPC's that the player fought. (It could be an array of a single element)
  *@return the total experience that the player would receive for defeating given enemy(ies)
  */
  public int exp_gain_calc(NPC[] enemy)
  {
    int total_exp = 0;
    for(int i = 0; i<enemy.length; i++)
    {
      total_exp += enemy[i].getLevel()*20;
    }
    return total_exp;
  }


  @Override
  public int run()
  {
    boolean end = false;
    option = 1;
    text[0].setColor(Color.BLACK);

    try
    {
      soundBuffer = new SoundBuffer();
      menuSound = new Sound();
      soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));
      menuSound.setBuffer(soundBuffer);
    }
    catch(IOException e){
      e.printStackTrace();
    }

    while(window.isOpen() && end == false)
    {
      window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
      drawText(text);  //method that draws all elements in text[] array on the screen
      window.display();

      for(Event event : window.pollEvents())
      {
        KeyEvent keyEvent = event.asKeyEvent();

        if(event.type == Event.Type.CLOSED)
        {
          window.close(); //User closes window.
        }


        else if (event.type == Event.Type.KEY_PRESSED)
        {
          if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE"))
          {
            option = 0;
            end = true;
          }
          else if (keyEvent.key == Keyboard.Key.valueOf("S"))
          {
            menuSound.play();
            if(option != 3)
            {
              option++;
            }
          }
          else if (keyEvent.key == Keyboard.Key.valueOf("W"))
          {
            menuSound.play();
            if(option != 1)
            {
              option--;
            }
          }
          else if (keyEvent.key == Keyboard.Key.valueOf("E"))
          {
            if(option == 1)
            {
              //Attack
              //end = true;
            }
            else if(option == 2)
            {
              //open inventoryMenu
              //end = true;
            }
            else if(option == 3)
            {
              //go back to gameWorld
              option = 99;
              end = true;
            }
          }
          showSelection(text, option);
        }
      }
    }
      return option;
  }
}
