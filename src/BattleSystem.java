
import java.io.IOException;
import java.util.*;
import java.nio.file.*;
//import org.jsfml.system.*;
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

    private Drawable[] obj;
    private Character[] battle_participants;
    private Text[] attack_menu, items_menu;
    private int[] turn_state; //move turn_state array's elements in accordance to speed_array
    private int exp_gain, temp, characters_num, team_size, textWidth, textHeight, textSpace;
    private ArrayList<Runnable> revertibles;
    private Animation anim;
    private Texture mainBG;
    private Sprite mainBGsp;


    /**
    *Initiates a battle between the player's party and random generated
    *enemies, who will always be the same number as the player's party.
    *@param window the window where the battle will be instantiated
    *@param scale the scaling of the window
    *@param options_num the number of options the player will have the ability
    *to choose from, when BattleSystem is instantiated
    *@param team is a list holding all members of the player's team
    */
    public BattleSystem(RenderWindow window, int scale, int options_num, ArrayList<Character> team) throws IOException
    {
        menuWindow(window, scale, options_num);
        System.out.println("SCREENHEIGHT IS: "+ screenHeight+"\nSCREENWIDTH IS: "+ screenWidth);
        attack_menu = newText(4 + 1);
        //items_menu = newText(number of items in inventory);

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        text[0] = new Text("Attack", text_font, screenHeight / 25);
        text[1] = new Text("Items", text_font, screenHeight / 25);
        text[2] = new Text("Escape", text_font, screenHeight / 25);

        //set the position of options on screen
        textWidth = screenWidth/36 - screenWidth/42;
        textHeight = (screenHeight/11*9) + screenHeight/20;
        textSpace = screenHeight/18;
        for(int i=0; i<options_num; i++)
        {
          text[i].setPosition(textWidth, textHeight);
          textHeight += textSpace;
        }

        revertibles = new ArrayList<>();

        //set up the number of characters that will battle and generate
        //the appropriate number of enemies (same as party's size)
        //turn_state array will hold the order of turns (who goes first, second, etc..)
        team_size = team.size();
        characters_num = team_size * 2;
        battle_participants = new Character[characters_num];
        turn_state = new int[characters_num];
        //copies the Character objects in team into battle_participants array
        //and generates as much NPC(lvl) objects to the other half of the array
        for (int i = 0; i < characters_num; i++)
        {
          if (i < team_size)
            battle_participants[i] = team.get(i);
          else
            battle_participants[i] = new NPC(battle_participants[0].level); //SETS THE NPC TO BE THE SAME LEVEL AS PLAYER AT POSITION 1
        }
        //exp_gain is initially set to 0
        exp_gain = 0;

        this.mainBG = getBackground("src/graphics/world/Spritesheet/battle_bg.png");
        this.mainBGsp = getBGSprite(mainBG);
        this.mainBGsp.setScale(Game.SCALE, Game.SCALE);
        //this.mainBGsp.setPosition(screenWidth / 2 + StateMachine.xOffset, screenHeight/2 - screenHeight/2);

        anim = new Animation(3);
        obj = anim.loadTextures();
    }

    /**
    *Inner class of Battle System, to be used for animation drawing during battle
    */
    protected class Animation extends Actor
    {
      Drawable[] objects;
      int team_size;

      /**
      *Constructor of Animation class
      *@param team_size the number of characters that will be drawn for each team
      */
      public Animation(int team_size)
      {
        objects = new Drawable[team_size*2];
        this.team_size = team_size;
      }

      @Override
      void calcMove(int a, int b, int c, int d){
      }

      /**
      *Method for drawing all drawable objects on screen, in a drawable array
      *@param obj is the array containing all drawable objects
      */
      void draw(Drawable[] obj)
      {
        for(Drawable o : obj)
          window.draw(o);
      }


      /**
      *This method will generate the a sprite with the correct size and set its
      *position information for drawing it on the window
      *@param posX the sprite's X position on window
      *@param posY the sprite's Y position on window
      *@param spriteX the sprite's X location on the spritesheet
      *@param spriteY the sprite's Y location on the spritesheet
      *@param img_selection the number indicating the spritesheet to use, from path
      *@return the generated sprite
      */
      Sprite generateSprite(int posX, int posY, int spriteX, int spriteY, int img_selection) throws IOException
      {
        Texture sprites = new Texture();
        sprites.loadFromFile(Paths.get("src/graphics/world/Spritesheet/sheet"+img_selection+".png"));
        this.state = new IntRect(((spriteX * 64) + spriteX), ((spriteY * 64) + spriteY), 64, 64);
        Sprite img = new Sprite(sprites, state);
        img.setPosition(posX, posY);

        return img;
      }


      /**
      *This method will initialize the sprites, their dimensions, as well
      *as their location on the window.
      *@return an array of Drawables, so they can be drawn on the window
      */
      Drawable[] loadTextures() throws IOException
      {
        Random random = new Random();
        int scale = VideoMode.getDesktopMode().height/160;

        x = (288*scale);
        y = (160*scale);

        int spriteX = 0;  //STARTS FROM 0
        int spriteY = 0;  // STARTS FROM 1


        int enemyPosX = x - (x/4 +x/7);
        int enemyPosY = y/2+y/8;
        int playerPosX = x/4+x/7;
        int playerPosY = y/2+y/8;

        for(int i = 0; i < objects.length; i++)
        {
          System.out.println("i is ->"+i);
          if (i<team_size)
          {
            r = 1;
            if (r == 1)
            {
              spriteX = 6;
              spriteY = 11;
            }
            this.img = generateSprite(playerPosX, playerPosY, spriteX, spriteY, r);
            playerPosX -= x/12;
            playerPosY -= y/14;
          }

          else if( i >= team_size)
          {
            r = random.nextInt(3) + 4;

            if (r == 4 || r == 5)
            {
              spriteX = 1;
              spriteY = 13;
            }
            else if (r == 6)
            {
              spriteX = 0;
              spriteY = 5;
            }
            this.img = generateSprite(enemyPosX, enemyPosY, spriteX, spriteY, r);
            enemyPosX += x/12;
            enemyPosY -= y/14;
          }

          img.setScale(Game.SCALE / 3, Game.SCALE / 3); // Changes player scale to 2/3 of tile size.
          objects[i] = img;
          setPosition = img::setPosition;
        }
        return objects;
      }

    }

  /**
  * Initialises an array called turn_state, (holding integers from 0 - characters_num)
  * and the re-orders it, in the order that each character is allowed to attack, based
  * on their speed
  */
    void getTurns() {
        int[] speed_array = new int[characters_num];

        for (int i = 0; i < speed_array.length; i++) {
            turn_state[i] = i;
            speed_array[i] = battle_participants[i].speed;
        }

        this.turn_state = bubbleSort(turn_state, speed_array);
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
                    turns[j] = temp;
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
     * @param enemy is the NPC that the player beat and will gain some experience
     * from.
     * @return the total experience that the player will receive for defeating
     * given enemy
     */
    public int exp_gain_calc(Character enemy) {
        Random r = new Random();
        int randInt = r.nextInt(6) + 9;

        int total_exp = 0;
        total_exp += enemy.level * randInt;
        return total_exp;
    }

    /**
    *Method will be called only when a it is a friendly player's turn.
    *It will read that player's skills and display them on the screen
    *@param attacker the (friendly) Character that the method will display the
    *skills of
    */
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

    /**
    *This method is the equivalent of playerTurn(), but for an enemy Character.
    *It will only be called when it is an enemy's turn to fight and will automate
    *the procedure of attacking. It is not AI, but more of a method using the
    *Random() class to produce the desired result.
    *@param enemy is the automated attacker
    *@param player is the array holding the battle_participants
    *@param range is the maximume element in player array that the enemy can
    *choose someone to attack from. (This is done to block enemies attacking themselves)
    *NOTE: If this method is called without making a check if all the members of a party
    *(friendly or hostile) are dead, it will it to ender an infinite loop.
    *--->see first while loop in method.
    */
    void enemyTurn(Character enemy, Character[] player, int range)
    {
        Random rand = new Random();
        int randInt = rand.nextInt(range);

        //Check if you're attacking a dead man
        while (player[randInt].isAlive == false) {
            randInt = rand.nextInt(range);
        }
        //reduce player's health
        player[randInt].health -= enemy.attack;

        if (player[randInt].health <= 0)
        {
            player[randInt].health = 0;
            player[randInt].isAlive = false;
            System.out.println(player[randInt].name + " has died.");
        }
        else
        {
            System.out.println(player[randInt].name + " took " + enemy.attack
                    + " amount of damage.\n" + player[randInt].name
                    + " has " + player[randInt].health + " / "
                    + player[randInt].max_health + " HP");
        }
    }


    /**
    *@return An integer inticating if a team has been defeated or still has
    *alive members
    */
    int checkSurvivors()
    {
      final int BOTH_HAVE_ALIVE = 0;
      final int FRIENDLIES_DEAD = 1;
      final int ENEMIES_DEAD = 2;

      int dead_counter_team = 0;
      int dead_counter_enemy = 0;

      for (int i = 0; i < characters_num; i++)
      {
        if (battle_participants[i].isAlive == false)
        {
          if (i < team_size)
            dead_counter_team++;
          else if (i >= team_size && i < characters_num)
            dead_counter_enemy++;
        }
      }

      if (dead_counter_team == team_size)
      {
        return FRIENDLIES_DEAD;
      }
      else if (dead_counter_enemy == team_size)
      {
        return ENEMIES_DEAD;
      }
      else
        return BOTH_HAVE_ALIVE;
    }

    /**
    *This is the main fighting loop of the game. Here the player can fight an
    *enemy(ies), or try to escape and continue the story mode
    *@return the variable 'option'. That is the state that the game will go to,
    *when the battle is exited.
    *NOTE: Battle is exited only when all the members of a team are dead, or if
    *the player manages to successfully 'escape' the battle
    FIXME after sellecting attack, player can choose cancel and the order of attacking
    would reset. need a temporary buffer to hold who attacked and who didnt.
    FIXME maybe call garbage collector when fight is over ?
    */
    @Override
    public int run() {
        boolean end = false;
        option = 1;

        text[0].setColor(Color.BLACK);
        getTurns();

        //TEMP ARRAY TO PRINT WHO ATTACKS, IN WHAT ORDER
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

        /*This is the most OuterLoop of the battle phase holding the options
          'Attack','Items','Escape'.
          TODO: MAKE ESCAPE NOT GUARANTEED
        */
        OuterLoop:
        while (window.isOpen() && end == false) {
          //TEMP //colour is gray, so options can be visible while white
            window.clear(new Color(192, 192, 192, 200));
            window.draw(mainBGsp);
            drawText(text);
            anim.draw(obj);
            window.display();

/*This snippet is how to make enemy blink when selected
            try
            {
              Thread.sleep(1500);
              window.clear(new Color(192, 192, 192, 200));
              drawText(text);

              window.display();
              Thread.sleep(1500);
            }
            catch (InterruptedException e){
              e.printStackTrace();
            }
*/
            //event loop that has an aim to capture user input, in this case the
            //else if statement focuses on the keys: ESC, S, W, E
            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close(); //User closes window.
                }
                else if (event.type == Event.Type.KEY_PRESSED)
                {
                    if (keyEvent.key == Keyboard.Key.valueOf("ESCAPE")) {
                        option = 0;
                        end = true;
                    }
                    else if (keyEvent.key == Keyboard.Key.valueOf("S")) {
                        menuSound.play();
                        if (option != 3) {
                            option++;
                        }
                    }
                    else if (keyEvent.key == Keyboard.Key.valueOf("W")) {
                        menuSound.play();
                        if (option != 1) {
                            option--;
                        }
                    }
                    else if (keyEvent.key == Keyboard.Key.valueOf("E")) {
                        if (option == 1) {
                            boolean turn_end = false;
                            boolean fight_end = false;
                            boolean victory = false;
                            int there_is_victor;

                            while (fight_end == false)
                            {
                              for (int x = 0; x < characters_num; x++)
                              {
                                Character attacker = battle_participants[(turn_state[x])];
                                  if (attacker.isFriendly && attacker.isAlive)
                                  {
                                    if (fight_end == false)
                                      turn_end = false;

                                    playerTurn(attacker);
                                    int fight_option = 1;
                                    int char_select;
                                    boolean pressed = false;
                                    boolean has_chosen = false;
                                    System.out.println("It's " + attacker.name + "'s turn!\n");
                                    System.out.println("fight option is "+fight_option);

                                    /*This is the loop that cycles for each
                                    character's turn. Unless there is a victor
                                    or the user presses cancel, this loop will run
                                    Here is where the ATTACK selection happens.
                                    */
                                    while (window.isOpen() && turn_end == false)
                                    {
                                      window.clear(new Color(192, 192, 192, 200));
                                      drawText(attack_menu);
                                      window.display();

                                      for (Event battle : window.pollEvents()) {
                                        KeyEvent battleEvent = battle.asKeyEvent();

                                        if (battle.type == Event.Type.CLOSED)
                                        {
                                          window.close();
                                          break OuterLoop;
                                        }
                                        else if (battle.type == Event.Type.KEY_PRESSED)
                                        {


                                          if (battleEvent.key == Keyboard.Key.valueOf("S") && !pressed)
                                          {
                                            pressed = true;
                                            menuSound.play();
                                            if (fight_option != 5)
                                            {
                                              fight_option++;
                                            }
                                                      /*********/
                                            System.out.println("fight option is "+fight_option);
                                        }
                                        else if (battleEvent.key == Keyboard.Key.valueOf("W") && !pressed)
                                        {
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
                                              /*
                                              TODO FIND A WAY TO SKIP DEAD ENEMIES
                                              */
                                              /*
                                              for(int i = char_select; i < range_high; i++)
                                              {
                                                if(!battle_participants[i].isAlive)
                                                {

                                                }
                                              }
                                              */

                                              System.out.println("Range high: "+range_high+"\nRange low: "+range_low);
                                              /*
                                              This is the loop where the player makes the selection of
                                              his/her target. If the skill chosen indicates that the player
                                              should choose more than 1 target, then loop will re-enter
                                              */
                                              while (skill.needsMoreTargets())
                                              {
                                                has_chosen = false;
                                                while(!has_chosen)
                                                {
                                                  for (Event selectSomeone : window.pollEvents())
                                                  {
                                                    KeyEvent select = selectSomeone.asKeyEvent();

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
                                                        if(char_select != range_low)
                                                          char_select--;
                                                        /*
                                                        if(char_select < range_low)
                                                          char_select = range_high;
                                                        */
                                                       }
                                                       else if(select.key == Keyboard.Key.valueOf("D"))
                                                       {
                                                         menuSound.play();
                                                         if(char_select != range_high)
                                                            char_select++;

                                                          /*
                                                          if(char_select > range_high)
                                                            char_select = range_low;
                                                          */

                                                        }
                                                        else if(select.key == Keyboard.Key.valueOf("E"))
                                                        {
                                                          has_chosen = true;
                                                        }


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

                                  try
                                  {
                                    Thread.sleep(1500);
                                  }
                                  catch (InterruptedException e){
                                    e.printStackTrace();
                                  }

                                }

                                /*
                                At this part we check if all the members
                                of a team are dead. if not, reenter loop,
                                if yes, end the battle.
                                */
                                there_is_victor = checkSurvivors();

                                if (there_is_victor == 1)
                                {
                                  fight_end = true;
                                  end = true;
                                  option = 1;
                                }
                                else if (there_is_victor == 2)
                                {
                                  fight_end = true;
                                  victory = true;
                                  option = 1;
                                }


                            }
                          }

                        if (victory)
                        {
                          for (int i = team_size; i < characters_num; i++)
                          {
                            exp_gain += exp_gain_calc(battle_participants[i]);
                            System.out.println("exp_gain = " + exp_gain);
                          }
                          System.out.println("Total experience gained is: " + exp_gain);
                          exp_gain = exp_gain / team_size;
                          System.out.println("Experience divided to characters is: " + exp_gain + "\n");
                          for (int i = 0; i < team_size; i++)
                          {
                            System.out.println(battle_participants[i].name + " gained " + exp_gain + " experience points");
                            int temp_level = battle_participants[i].level_calc(exp_gain);
                            if (battle_participants[i].level < temp_level)
                            {
                              int levels_to_grow = temp_level - battle_participants[i].level;
                              System.out.println(battle_participants[i].name + " has reached level " + temp_level);
                              for (int j = 0; j < levels_to_grow; j++)
                              {
                                battle_participants[i].levelUP();
                              }
                            }
                            battle_participants[i].exp += exp_gain;
                            System.out.println(battle_participants[i].name + " has " + battle_participants[i].exp
                                              + " experience points and is LVL: " + battle_participants[i].level);
                          }
                          end = true;
                        }
                      //TODO go through the list of of reversables and so that buffes are removed
                        for(Runnable r : revertibles)
                          r.run();

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
