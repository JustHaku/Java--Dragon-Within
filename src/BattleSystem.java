
import java.io.IOException;
import java.util.*;
import java.nio.file.*;
import org.jsfml.system.*;
import org.jsfml.window.*;
import org.jsfml.window.event.*;
import org.jsfml.graphics.*;
import org.jsfml.audio.*;

/**
 * Class where the mechanics of turn-based battles are implemented
 *
 * @author Petros Soutzis
 */
public class BattleSystem extends Menu implements State {

    private Character[] battle_participants;
    private int[] turn_state; //move turn_state array's elements in accordance to speed_array
    private int exp_gain, temp, characters_num, team_size, textWidth, textHeight, textSpace;
    private Text[] attack_menu, items_menu;
    private ArrayList<Runnable> revertibles;
    private Animation villos;

    public BattleSystem(RenderWindow window, int scale, int options_num, ArrayList<Character> team) throws IOException
    {
        menuWindow(window, scale, options_num);
        attack_menu = newText(4 + 1);
        //items_menu = newText(number of items in inventory);
        villos = new Animation();
        villos.loadTextures(Game.playerSpriteSheet);

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        text[0] = new Text("Attack", text_font, screenHeight / 22);
        text[1] = new Text("Items", text_font, screenHeight / 22);
        text[2] = new Text("Escape", text_font, screenHeight / 22);

        textWidth = screenWidth/36 - screenWidth/42;
        textHeight = screenHeight/12*9+50;
        textSpace = screenHeight/18;
        for(int i=0; i<options_num; i++)
        {
          text[i].setPosition(textWidth, textHeight);
          textHeight += textSpace;
        }

        team_size = team.size();
        characters_num = team_size * 2;   //the number of enemies you get is the same as the number of characters in your party
        battle_participants = new Character[characters_num];
        turn_state = new int[characters_num];
        for (int i = 0; i < team_size; i++) {     //copies the parsed battle_participants[] array into this class' battle_participants array
            battle_participants[i] = team.get(i);
        }
        for (int i = team_size; i < characters_num; i++) { //copies the parsed battle_participants[] array into this class' battle_participants array
            battle_participants[i] = new NPC(battle_participants[0].level);
        }

        exp_gain = 0;
        revertibles = new ArrayList<>();
    }

    protected class Animation extends Actor
    {
      @Override
      void calcMove(int a, int b, int c, int d){}

      void loadTextures(Texture enemy_sheet) throws IOException
      {
        int scale = VideoMode.getDesktopMode().height/160;
        int x = 160*scale;
        int y = 288*scale;
        c1 = 1;
        c2 = 3;
        //enemy_sheet = new Texture();
        //enemy_sheet.loadFromFile(Paths.get("src/graphics/world/roguelikeChar_transparent.png"));
        state = new IntRect(((c1 * 16) + c1), ((c2 * 16) + c2), 16, 16);
        img = new Sprite(enemy_sheet, state);
        img.setScale(Game.SCALE / ps, Game.SCALE / ps); // Changes player scale to 2/3 of tile size.

        img.setPosition(x, y);

        obj = img; // Sets img as collision object.
        setPosition = img::setPosition;
      }
    }

    /**
     * Initializes an array called turn_state in the order that each character
     * can Attack (fastest goes first)
     */
    void getTurns() {
        int[] speed_array = new int[characters_num];

        for (int i = 0; i < speed_array.length; i++) {
            turn_state[i] = i;
            speed_array[i] = battle_participants[i].speed;
        }

        turn_state = bubbleSort(turn_state, speed_array);
    }

    /**
    *Method uses bubblesort technique for ordering an array from Largest to Smallest
    *@param arr is the array to be returned, which gets ordered according to the
    *second array parsed in arguments. It "mirrors" the other array's changes
    *@param turns is the array to be ordered from largest to smallest
    *@return the second array parsed as an argument, ordered in descending ordered
    *-
    *-
    *NOTE:Both arrays must be of same length, otherwise
    *the method will returned a distorted array, than a sorted one.
    *(Because the sorting of one array is affected directly to the other)
    */
    int[] bubbleSort(int[] arr, int[] turns) {
        int length = arr.length;
        int temp = 0;

        for (int i = 0; i < length - 1; i++) {
            for (int j = 1; j < length - i; j++) {
                if (turns[j - 1] < turns[j]) {
                    temp = turns[j - 1];
                    turns[j - 1] = turns[j];
                    turns[j] = temp;    // order the array of turn
                    if (arr[j - 1] < arr[j]) {
                        temp = arr[j - 1];
                        arr[j - 1] = arr[j];
                        arr[j] = temp;
                    }
                }
            }
        }
        return arr;
    }

    /**
     * @param enemy is the array of NPC's that the player fought. (It could be
     * an array of a single element)
     * @return the total experience that the player would receive for defeating
     * given enemy(ies)
     */
    public int exp_gain_calc(Character enemy) {
        Random r = new Random();
        int randInt = r.nextInt(6) + 9;

        int total_exp = 0;
        total_exp += enemy.level * randInt;
        return total_exp;
    }

    void playerTurn(Character attacker) {
        int length = 5;
        int width = textWidth;
        int height = textHeight - (textHeight/10 * 3);

        for (int i = 0; i < length; i++) {
            if (i == 4) {
                height += textSpace/3;
                attack_menu[i] = new Text("Cancel", text_font, screenHeight / 29);
            } else if (attacker.skills[i] != null) {
                attack_menu[i] = new Text(attacker.skills[i].getName(), text_font, screenHeight / 29);
            } else {
                attack_menu[i] = new Text("-", text_font, screenHeight / 29);
            }
            attack_menu[i].setPosition(width, height);
            height += textSpace;
        }
        attack_menu[0].setColor(Color.BLACK);
    }

    void enemyTurn(Character enemy, Character[] player, int range) {
        Random rand = new Random();
        int randInt = rand.nextInt(range);

        //Check if you're attacking a dead man
        while (player[randInt].isAlive == false) {
            randInt = rand.nextInt(range);
        }
        //reduce player's health
        player[randInt].health -= enemy.attack;

        if (player[randInt].health <= 0) {
            player[randInt].health = 0;
            player[randInt].isAlive = false;
            System.out.println(player[randInt].name + " has died.");
        } else {
            System.out.println(player[randInt].name + " took " + enemy.attack
                    + " amount of damage.\n" + player[randInt].name
                    + " has " + player[randInt].health + " / "
                    + player[randInt].max_health + " HP");
        }
    }

    @Override
    public int run() {
        boolean end = false;
        option = 1;

        text[0].setColor(Color.BLACK);
        getTurns();

        for (int i = 0; i < turn_state.length; i++) {
            System.out.println(battle_participants[(turn_state[i])].name + " at position " + turn_state[i] + " is number "
                    + (i + 1) + " to attack with a " + battle_participants[(turn_state[i])].speed
                    + " speed stat and is LVL: " + battle_participants[(turn_state[i])].level);
        }
        //(StateMachine.team).add(Game.Petros);

        try {
            soundBuffer = new SoundBuffer();
            menuSound = new Sound();
            soundBuffer.loadFromFile(Paths.get("src/audio/Menu/Cursor_Move.wav"));
            menuSound.setBuffer(soundBuffer);
        } catch (IOException e) {
            e.printStackTrace();
        }

        OuterLoop:
        while (window.isOpen() && end == false) {
            window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
            drawText(text);  //method that draws all elements in text[] array on the screen
            window.draw(villos.obj);
            window.display();

            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close(); //User closes window.
                } else if (event.type == Event.Type.KEY_PRESSED) {
                    if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        option = 0;
                        end = true;
                    } else if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                        menuSound.play();
                        if (option != 3) {
                            option++;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                        menuSound.play();
                        if (option != 1) {
                            option--;
                        }
                    } else if (keyEvent.key == Keyboard.Key.valueOf("E")) {
                        if (option == 1) {
                            boolean turn_end = false;
                            boolean fight_end = false;
                            boolean victory = false;
                            int dead_counter_team = 0;
                            int dead_counter_enemy = 0;

                            while (fight_end == false) {
                                for (int x = 0; x < characters_num; x++) {
                                  Character attacker = battle_participants[(turn_state[x])];
                                    if (attacker.isFriendly && attacker.isAlive) {
                                        if (fight_end == false) {
                                            turn_end = false;
                                        }

                                        playerTurn(attacker);
                                        int fight_option = 1;
                                        int char_select;
                                        boolean pressed = false;
                                        boolean has_chosen = false;
                                        System.out.println("It's " + attacker.name + "'s turn!\n");
                                        System.out.println("fight option is "+fight_option);
                                        while (window.isOpen() && turn_end == false) {
                                            window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
                                            drawText(attack_menu);
                                            window.display();

                                            for (Event battle : window.pollEvents()) {
                                                KeyEvent battleEvent = battle.asKeyEvent();

                                                if (battle.type == Event.Type.CLOSED) {
                                                    window.close(); //User closes window.
                                                    break OuterLoop;
                                                }
                                                else if (battle.type == Event.Type.KEY_PRESSED) {
                                                    if (battleEvent.key == Keyboard.Key.valueOf("S") && !pressed) {
                                                      pressed = true;
                                                      menuSound.play();
                                                      if (fight_option != 5) {
                                                          fight_option++;
                                                      }
                                                      /*********/
                                                      System.out.println("fight option is "+fight_option);
                                                    }
                                                    else if (battleEvent.key == Keyboard.Key.valueOf("W") && !pressed) {
                                                      pressed = true;
                                                      menuSound.play();
                                                      if (fight_option != 1) {
                                                          fight_option--;
                                                      }
                                                      /**********/
                                                      System.out.println("fight option is "+fight_option);
                                                    }
                                                    else if (battleEvent.key == Keyboard.Key.valueOf("E") && !pressed)
                                                    {
                                                      System.out.println("Chosen fight option is "+fight_option);
                                                      pressed = true;
                                                      int range_low = 0;
                                                      int range_high = characters_num-1;

                                                      if (fight_option != 5 && !attack_menu[fight_option - 1].getString().equals("-"))
                                                      {
                                                        if(attacker.isFriendly && attacker.isAlive)
                                                        {
                                                          Skills skill = attacker.skills[fight_option-1];
                                                          if(skill != null)
                                                          {
                                                            if(!skill.doesitAffect(Skills.ENEMY))
                                                            {
                                                              range_high = team_size - 1;
                                                            }
                                                            else if(!skill.doesitAffect(Skills.FRIENDLY))
                                                            {
                                                              range_low = team_size;
                                                            }
                                                          }
                                                          char_select = range_low;
                                                          //ContinueAttack:
                                                          /*********/
                                                          System.out.println("Range high: "+range_high+"\nRange low: "+range_low);
                                                          /***HERE IS APPLYEE SELECTION, EXIT LOOP ONCE SELECTED***/
                                                          while (skill.needsMoreTargets())
                                                          {
                                                            has_chosen = false;
                                                            while(!has_chosen)
                                                            {
                                                              for (Event selectSomeone : window.pollEvents())
                                                              {
                                                                KeyEvent select = selectSomeone.asKeyEvent();
                                                                //int sign = 1;

                                                                if (selectSomeone.type == Event.Type.CLOSED)
                                                                {
                                                                    window.close(); //User closes window.
                                                                    break OuterLoop;
                                                                }
                                                                else if (selectSomeone.type == Event.Type.KEY_PRESSED)
                                                                {
                                                                  if (select.key == Keyboard.Key.valueOf("A"))
                                                                  {
                                                                    menuSound.play();
                                                                    char_select--;
                                                                    //sign = -1;
                                                                    //IF ENEMY IS DEAD, GO TO NEXT ENEMY, IF ALL DEAD, EXIT
                                                                    if(char_select < range_low)
                                                                      char_select = range_high;
                                                                      /*
                                                                    while(!battle_participants[char_select].isAlive)
                                                                    {
                                                                      if(dead_counter_enemy == team_size)
                                                                        break;
                                                                      char_select--;
                                                                      if(char_select < range_low)
                                                                        char_select = range_high;
                                                                      dead_counter_enemy++;
                                                                    }*/

                                                                  }
                                                                  else if(select.key == Keyboard.Key.valueOf("D"))
                                                                  {
                                                                    menuSound.play();
                                                                    char_select++;
                                                                    //sign = 1;
                                                                    if(char_select > range_high)
                                                                      char_select = range_low;
                                                                      /*
                                                                    while(!battle_participants[char_select].isAlive)
                                                                    {
                                                                      if(dead_counter_enemy == team_size)
                                                                        break;
                                                                      char_select++;
                                                                      if(char_select > range_high)
                                                                        char_select = range_low;
                                                                      dead_counter_enemy++;
                                                                    }
                                                                    */

                                                                  }
                                                                  else if(select.key == Keyboard.Key.valueOf("E"))
                                                                  {
                                                                    has_chosen = true;
                                                                  }


                                                                  /****TRYING TO FIND DEAD CHARS AND REMOVE FROM SELECTION****/
                                                                  /*System.out.println("Before while loop: "+ char_select);
                                                                  while(true)
                                                                  {
                                                                    if(char_select < range_low)
                                                                      char_select = range_high;
                                                                    else if(char_select > range_high)
                                                                      char_select = range_low;
                                                                    if(!battle_participants[char_select].isAlive)
                                                                      char_select += sign*1;
                                                                    else
                                                                      break;
                                                                    System.out.println("after one iteration " + char_select );
                                                                  }
                                                                  System.out.println("after while loop: "+ char_select);*/





                                                                }
                                                              }
                                                            }
                                                            skill.addTarget(battle_participants[char_select]);
                                                            System.out.println("Selected target is number: "+char_select);
                                                          }



                                                          try
                                                          {
                                                            System.out.println("Before exec, health is: "+battle_participants[char_select].health);
                                                            skill.executeSkill();
                                                            System.out.println("After exec, health is: "+battle_participants[char_select].health);
                                                            if(skill.revertable)
                                                              revertibles.add(skill.getReverted());
                                                            //TODO gget the reversable from the skill and add it to a list
                                                          }
                                                          catch(Exception e)
                                                          {
                                                            e.printStackTrace();
                                                          }
                                                          skill.unBindAll();
                                                          System.out.println("Removing targets");
                                                          turn_end = true;

                                                        }
                                                      }
                                                      else if(attack_menu[fight_option - 1].getString().equals("-"))
                                                      {
                                                        System.out.println("No skill assigned!");
                                                      }
                                                      else
                                                      {
                                                        turn_end = true;
                                                        fight_end = true;
                                                      }
                                                    }
                                                    showSelection(attack_menu, fight_option);
                                                    pressed = false;
                                                }
                                            }
                                        }

                                    }
                                    else if (attacker.isAlive == true &&
                                             fight_end == false && attacker.isFriendly == false)
                                    {
                                        System.out.println("\nEnemy turn!   (Wait 1.5 seconds..)");
                                        Text enemyAttack = new Text("Enemy turn!", text_font, screenHeight/25);
                                        enemyAttack.setPosition(30, (screenHeight/2 + screenHeight/4 + screenHeight/8 + screenHeight/25));
                                        enemyAttack.setColor(Color.RED);

                                        window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
                                        window.draw(enemyAttack);
                                        window.display();
                                        enemyTurn(attacker, battle_participants, team_size);

                                        try {
                                            Thread.sleep(1500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    /**
                                     * *TESTING THAT A TEAM IS DEAD - END
                                     * BATTLE**
                                     */

                                    for (int i = 0; i < characters_num; i++) {
                                        if (battle_participants[i].isAlive == false) {
                                            if (i < team_size) {
                                                dead_counter_team++;
                                            } else if (i >= team_size && i < characters_num) {
                                                dead_counter_enemy++;
                                            }
                                        }
                                    }
                                    if (dead_counter_team == 2) {
                                        fight_end = true;
                                        end = true;
                                        option = 1;
                                    } else {
                                        dead_counter_team = 0;
                                    }
                                    if (dead_counter_enemy == 2) {
                                        fight_end = true;
                                        victory = true;
                                        option = 1;
                                    } else {
                                        dead_counter_enemy = 0;
                                    }
                                }
                            }

                            if (victory) {
                                for (int i = team_size; i < characters_num; i++) {
                                    exp_gain += exp_gain_calc(battle_participants[i]);
                                    System.out.println("line 363 exp_gain = " + exp_gain);
                                }
                                System.out.println("Total experience gained is: " + exp_gain);
                                exp_gain = exp_gain / team_size;
                                System.out.println("Experience divided to characters is: " + exp_gain + "\n");
                                for (int i = 0; i < team_size; i++) {
                                    System.out.println(battle_participants[i].name + " gained " + exp_gain + " experience points");
                                    int temp_level = battle_participants[i].level_calc(exp_gain);
                                    if (battle_participants[i].level < temp_level) {
                                        int levels_to_grow = temp_level - battle_participants[i].level;
                                        System.out.println(battle_participants[i].name + " has reached level " + temp_level);
                                        for (int j = 0; j < levels_to_grow; j++) {
                                            battle_participants[i].levelUP();
                                        }
                                    }
                                    battle_participants[i].exp += exp_gain;
                                    System.out.println(battle_participants[i].name + " has " + battle_participants[i].exp
                                            + " experience points and is LVL: " + battle_participants[i].level);
                                }
                                end = true;
                            }

                        // TODO go through the list of of reversables and so that buffes are removed
                           for(Runnable r : revertibles)
                           {
                             r.run();
                           }
                        }
                        else if (option == 2)
                        {
                            //open inventoryMenu
                            //end = true;
                        }
                        else if (option == 3)
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
