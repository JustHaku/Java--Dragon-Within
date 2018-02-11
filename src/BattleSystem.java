import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.*;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;
/**
*Class where the mechanics of turn-based battles are implemented
*@author Petros Soutzis
*/

public class BattleSystem extends Menu implements State
{

    private Character[] battle_participants;
    private int[] turn_state; //move turn_state array's elements in accordance to speed_array
    private int exp_gain, temp, characters_num, team_size;
    private Text[] attack_menu, items_menu;

  public BattleSystem(RenderWindow window, int scale, int options_num, ArrayList<Character> team) throws IOException
  {

    menuWindow(window, scale, 288, 160, options_num);
    attack_menu = newText(4+1);
    //items_menu = newText(number of items in inventory);

    text_font = new Font();
    text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

    text[0] = new Text("Attack", text_font, screenHeight/20);
    text[0].setPosition(35, 635);

    text[1] = new Text("Items", text_font, screenHeight/20);
    text[1].setPosition(35, 685);

    text[2] = new Text("Escape", text_font, screenHeight/20);
    text[2].setPosition(35, 735);

    team_size = team.size();
    characters_num = team_size * 2;   //the number of enemies you get is the same as the number of characters in your party
    battle_participants = new Character[characters_num];
    turn_state = new int[characters_num];
    for(int i=0; i<team_size; i++){     //copies the parsed battle_participants[] array into this class' battle_participants array
      battle_participants[i] = team.get(i);
    }
    for(int i=team_size; i<characters_num; i++){ //copies the parsed battle_participants[] array into this class' battle_participants array
      battle_participants[i] = new NPC(battle_participants[0].level);
    }

    exp_gain = 0;
  }

/**
*Initializes an array called turn_state in the order that each character can Attack (fastest goes first)
*/
  void getTurns()
  {
    int[] speed_array = new int[characters_num];

    for (int i = 0; i<speed_array.length; i++){
      turn_state[i] = i;
      speed_array[i] = battle_participants[i].speed;
    }

    turn_state = bubbleSort(turn_state, speed_array);
  }

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

  /**
  *@param enemy is the array of NPC's that the player fought. (It could be an array of a single element)
  *@return the total experience that the player would receive for defeating given enemy(ies)
  */
  public int exp_gain_calc(Character enemy)
  {
    Random r = new Random();
    int randInt = r.nextInt(6) + 9;
      
    int total_exp = 0;
    total_exp += enemy.level*randInt;
    return total_exp;
  }

  void playerTurn(Character attacker){
    attack_menu[0] = new Text(attacker.skill_names[0], text_font, screenHeight/20);
    attack_menu[0].setPosition(35, 535);
    attack_menu[0].setColor(Color.BLACK);

    attack_menu[1] = new Text(attacker.skill_names[1], text_font, screenHeight/20);
    attack_menu[1].setPosition(35, 585);

    attack_menu[2] = new Text(attacker.skill_names[2], text_font, screenHeight/20);
    attack_menu[2].setPosition(35, 635);

    attack_menu[3] = new Text(attacker.skill_names[3], text_font, screenHeight/20);
    attack_menu[3].setPosition(35, 685);

    attack_menu[4] = new Text("Cancel", text_font, screenHeight/20);
    attack_menu[4].setPosition(35, 750);
  }
  void enemyTurn(){

  }

  @Override
  public int run()
  {
    boolean end = false;
    option = 1;

    text[0].setColor(Color.BLACK);
    getTurns();

    for(int i = 0; i<turn_state.length; i++)
    {
      System.out.println(battle_participants[(turn_state[i])].name+" at position "+turn_state[i]+" is number "+
      (i+1)+" to attack with a "+ battle_participants[(turn_state[i])].speed+
      " speed stat");
    }
    //(StateMachine.team).add(Game.Petros);

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
              boolean turn_end = false;
              boolean fight_end = false;
              int fight_option = 1;

              while(fight_end == false)
              {
                for(int x = 0; x<characters_num; x++)
                {
                  if(battle_participants[(turn_state[x])].isFriendly == true)
                  {
                    if(fight_end == false){
                      turn_end = false;
                    }

                    playerTurn(battle_participants[(turn_state[x])]);
                    fight_option = 1;

                    while(window.isOpen() && turn_end == false)
                    {
                      window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
                      drawText(attack_menu);
                      window.display();

                      for(Event battle : window.pollEvents())
                      {
                        KeyEvent battleEvent = battle.asKeyEvent();

                        if(battle.type == Event.Type.CLOSED)
                        {
                          window.close(); //User closes window.
                        }

                        else if (battle.type == Event.Type.KEY_PRESSED)
                        {
                          if (battleEvent.key == Keyboard.Key.valueOf("S"))
                          {
                            menuSound.play();
                            if(fight_option != 5)
                            {
                              fight_option++;
                            }
                          }
                          else if (battleEvent.key == Keyboard.Key.valueOf("W"))
                          {
                            menuSound.play();
                            if(fight_option != 1)
                            {
                              fight_option--;
                            }
                          }
                          else if (keyEvent.key == Keyboard.Key.valueOf("E"))
                          {
                              switch (fight_option) {
                                  case 1:
                                      if(attack_menu[fight_option-1].equals("-")){
                                          System.out.println("No skill assigned to this slot!");
                                      }
                                      else
                                      {
                                          battle_participants[2].health-=20;
                                          if(battle_participants[2].health <= 0)
                                              battle_participants[2].isAlive = false;
                                          turn_end = true;
                                          System.out.println(battle_participants[(turn_state[x])].name+" inflicted 20 damage to "+battle_participants[2].name+"1");
                                          System.out.println(battle_participants[2].name+"1 has "+battle_participants[2].health+"/"+battle_participants[2].max_health);
                                      }       break;
                                  case 2:
                                      if(attack_menu[fight_option-1].equals("-")){
                                          System.out.println("No skill assigned to this slot!");
                                      }
                                      else
                                      {
                                          battle_participants[3].health-=50;
                                          if(battle_participants[3].health <= 0)
                                              battle_participants[3].isAlive = false;
                                          turn_end = true;
                                          System.out.println(battle_participants[(turn_state[x])].name+" inflicted 50 damage to "+battle_participants[3].name+"2");
                                          System.out.println(battle_participants[3].name+"2 has "+battle_participants[3].health+"/"+battle_participants[3].max_health);
                                      }       break;
                                  case 3:
                                      System.out.println("No skill assigned to this slot!");
                                      break;
                                  case 4:
                                      System.out.println("No skill assigned to this slot!");
                                      break;

                                  case 5:
                                      turn_end = true;
                                      fight_end = true;
                                      break;
                                  default:
                                      break;
                              }
                          }
                          showSelection(attack_menu, fight_option);
                        }
                      }
                    }
                  }
                  else if(battle_participants[(turn_state[x])].isAlive == true && fight_end == false)
                  {
                      System.out.println("Enemy turn!   (Wait 1.5 seconds..)");
                      Text enemyAttack = new Text("Enemy turn!", text_font, screenHeight/20);
                      enemyAttack.setPosition(35, 750);
                      enemyAttack.setColor(Color.RED);

                      window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
                      window.draw(enemyAttack);
                      window.display();

                      try{
                          Thread.sleep(1500);}
                      catch(InterruptedException e){
                        e.printStackTrace();}
                  }


                  /***TESTING THAT A TEAM IS DEAD - END BATTLE***/
                  int dead_counter = 0;
                  for(int i=0; i<team_size; i++){
                    if(battle_participants[i].isAlive == false){
                      dead_counter++;
                    }
                  }
                  if(dead_counter == team_size){
                    fight_end = true;
                    //turn_end = true;
                    end = true;
                    option = 1;
                  }
                  else{
                    dead_counter = 0;
                  }
                  for(int i=team_size; i<characters_num; i++){
                    if(battle_participants[i].isAlive == false){
                      dead_counter++;
                    }
                  }
                  if(dead_counter == team_size){
                    fight_end = true;
                    end = true;
                    option = 1;
                    for(int i=team_size; i<characters_num; i++)
                    {
                      exp_gain += exp_gain_calc(battle_participants[i]);
                    }
                    exp_gain = exp_gain/team_size;
                    for(int i=0; i<team_size; i++){
                      System.out.println(battle_participants[i].name+" gained "+exp_gain+" experience points");
                      int temp_level = battle_participants[i].level_calc(battle_participants[i].exp_const, battle_participants[i].exp, exp_gain);
                      if (battle_participants[i].level < temp_level){
                          int levels = temp_level - battle_participants[i].level;
                          System.out.println(battle_participants[i].name+" is now level "+temp_level);
                          for(int j=0; j<levels; j++){
                              battle_participants[i].levelUP(battle_participants[i]);
                          }
                      }
                      battle_participants[i].exp += exp_gain;
                    }

                  }
                  else{
                    dead_counter = 0;
                  }
                }
              }



            }
            else if(option == 2)
            {
              //open inventoryMenu
              //end = true;
            }
            else if(option == 3)
            {
              //go back to gameWorld
              option = 1;
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
