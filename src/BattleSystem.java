
import java.io.IOException;
import java.lang.*;
import java.util.*;
import java.nio.file.*;
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
    private Text[] attack_menu, items_menu, stat_texts;
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

        stat_texts = newText(characters_num);

        anim = new Animation(team_size);
        obj = anim.loadTextures();

        //copies the Character objects in team into battle_participants array
        //and generates as much NPC(lvl) objects to the other half of the array
        for (int i = 0; i < characters_num; i++)
        {
          if (i < team_size)
            battle_participants[i] = team.get(i);
          else
            battle_participants[i] = new NPC(battle_participants[0].level); //SETS THE NPC TO BE THE SAME LEVEL AS PLAYER AT POSITION 1
          Character c = battle_participants[i];
          stat_texts[i] = new Text(c.name+" LvL "+c.level+"\n"+c.health+"/"+c.max_health+"HP", text_font, screenHeight/40);
          stat_texts[i].setStyle(TextStyle.BOLD);
          stat_texts[i].setPosition(((Sprite)obj[i]).getPosition());
          stat_texts[i].setColor(Color.BLACK);
          stat_texts[i].move(-55,-110);
        }
        //exp_gain is initially set to 0
        exp_gain = 0;

        this.mainBG = getBackground("src/graphics/world/Spritesheet/battle_bg.png");
        this.mainBGsp = getBGSprite(mainBG);
        this.mainBGsp.setScale(Game.SCALE, Game.SCALE);
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
      void draw(Drawable[] obj, Character[] chars, Text[] stat_texts)
      {
          for(int i = 0; i<obj.length; i++)
          {
            if (chars[i].isAlive)
            {
              window.draw(obj[i]);
              window.draw(stat_texts[i]);
            }
            else if(!chars[i].isAlive)
            {
              sleepThread(1);
            }
          }
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
          if (i<team_size)
          {
            if(i==0)
            {
              r = 1;
              spriteX = 6;
              spriteY = 11;
            }
            else if(i==1)
            {
              r = 2;
              spriteX = 1;
              spriteY = 7;
            }
            else if(i==2)
            {
              r = 3;
              spriteX = 0;
              spriteY = 11;
            }

            this.img = generateSprite(playerPosX, playerPosY, spriteX, spriteY, r);
            playerPosX -= x/10;
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
            enemyPosX += x/10;
            enemyPosY -= y/14;
          }

          img.setScale(Game.SCALE/2, Game.SCALE/2); // Changes player scale to 2/3 of tile size.
          objects[i] = img;
          setPosition = img::setPosition;
        }
        return objects;
      }

    }

    /**
    *Method to make selected character blink, until the player chooses a
    *character to use a skill at
    *@param mainBGsp the background
    *@param attack_menu the menu options of attacks that the character has
    *@param obj the character drawables
    *@param char_selected the currently selected character
    */
    void blink(Sprite mainBGsp, Text[] attack_menu,Text[] stat_texts, Drawable[] obj, Character char_selected)
    {
      draw(mainBGsp, attack_menu, stat_texts, obj);
      sleepThread(200);
      window.clear();
      window.draw(mainBGsp);
      for(int i = 0; i<obj.length; i++)
      {
        if (battle_participants[i] != char_selected && battle_participants[i].isAlive)
        {
          window.draw(obj[i]);
          window.draw(stat_texts[i]);
        }
      }
      drawText(attack_menu);
      window.display();
      sleepThread(60);
    }

    /**
    *Method sleeps the thread for n milliseconds
    *@param millisec is the time that the thread will sleep for in milliseconds
    */
    void sleepThread(int millisec)
    {
      try
      {
        Thread.sleep(millisec);
      }
      catch (InterruptedException e){
        e.printStackTrace();
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
            if (i == 4)
            {
                height += textSpace/3;
                attack_menu[i] = new Text("Cancel", text_font, screenHeight / 29);
            }
            else if (attacker.skills[i] != null)
                attack_menu[i] = new Text(attacker.skills[i].getName(), text_font, screenHeight / 29);
            else
                attack_menu[i] = new Text("-", text_font, screenHeight / 29);

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
    void enemyTurn(Character enemy, Character[] player)
    {
        Random rand = new Random();
        Text after_attack;
        int randInt;
        Character victim;
        Skills skill;
        ArrayList<Character> char_selected = new ArrayList<>(Arrays.asList(player));

        randInt = rand.nextInt(4);
        skill = enemy.skills[randInt];

        char_selected.removeIf((Character c) -> !c.isAlive);
        if(!skill.doesitAffect(Skills.ENEMY))
        {
          char_selected.removeIf((Character c) -> c.isFriendly);
        }
        else if(!skill.doesitAffect(Skills.FRIENDLY))
        {
          char_selected.removeIf((Character c) -> !c.isFriendly);
        }

        do
        {
          randInt = rand.nextInt(char_selected.size());
          victim = char_selected.remove(randInt);
          skill.addTarget(victim);
        } while (skill.needsMoreTargets());

        try
        {
          skill.executeSkill();
          if(skill.revertable)
            revertibles.add(skill.getReverted());
          after_attack = createText(skill.getPostEffectText());
          draw(mainBGsp,after_attack,stat_texts,obj);
          sleepThread(1000);
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
        finally
        {
          skill.unBindAll();
        }
        if (!victim.isAlive)
        {
          after_attack = createText(victim.name+" has died..");
          draw(mainBGsp, after_attack, stat_texts, obj);
          sleepThread(1100);
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



    Runnable drawBS(Sprite background, Text text, Text[] stat_texts, Drawable[] images)
    {
      return () ->
      {
        draw(background,text,stat_texts, images);
      };
    }

    void draw(Sprite background, Text[] menu_options, Text[] stat_texts, Drawable[] images)
    {
      window.clear();
      window.draw(background);
      drawText(menu_options);
      anim.draw(images, battle_participants, stat_texts);
      window.display();
    }

    void draw(Sprite background, Text text, Text[] stat_texts, Drawable[] images)
    {
      window.clear();
      window.draw(background);
      window.draw(text);
      anim.draw(images, battle_participants, stat_texts);
      window.display();
    }

    /**
    *This is the main fighting loop of the game. Here the player can fight an
    *enemy(ies), or try to escape and continue the story mode
    *@return the variable 'option'. That is the state that the game will go to,
    *when the battle is exited.
    *NOTE: Battle is exited only when all the members of a team are dead, or if
    *the player manages to successfully 'escape' the battle
    FIXME maybe call garbage collector when fight is over ?
    */
    @Override
    public int run() {
        boolean end = false;
        boolean escaped = false;
        int paused_turn = 0;
        int escape_chance = 0;
        option = 1;

        text[0].setColor(Color.BLACK);
        getTurns();

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
        while (window.isOpen() && end == false)
        {
          draw(mainBGsp, text, stat_texts, obj);
            //event loop that has an aim to capture user input, in this case the
            //else if statement focuses on the keys: ESC, S, W, E
            for (Event event : window.pollEvents()) {
                KeyEvent keyEvent = event.asKeyEvent();

                if (event.type == Event.Type.CLOSED) {
                    window.close(); //User closes window.
                }
                else if (event.type == Event.Type.KEY_PRESSED)
                {
                    if (keyEvent.key == Keyboard.Key.valueOf("S")) {
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
                                if(escaped == true && battle_participants[(turn_state[x])].isFriendly)
                                {
                                  x = paused_turn;
                                  escaped = false;
                                }
                                Character attacker = battle_participants[(turn_state[x])];
                                for(int i = 0; i<stat_texts.length; i++)
                                {
                                  Character c = battle_participants[i];
                                  stat_texts[i].setString(c.name+" LvL "+c.level+"\n"+c.health+"/"+c.max_health+"HP");
                                }

                                  if (attacker.isFriendly && attacker.isAlive)
                                  {
                                    if (fight_end==false)
                                      turn_end = false;

                                    playerTurn(attacker);
                                    int fight_option = 1;
                                    boolean pressed = false;
                                    boolean has_chosen = false;
                                    Text player_text = createText("It's "+attacker.name+"'s turn");

                                    if(fight_end == false)
                                    {
                                      draw(mainBGsp,player_text, stat_texts, obj);
                                      sleepThread(1500);
                                    }

                                    /*This is the loop that cycles for each
                                    character's turn. Unless there is a victor
                                    or the user presses cancel, this loop will run
                                    Here is where the ATTACK selection happens.
                                    */
                                    while (window.isOpen() && turn_end == false)
                                    {
                                      draw(mainBGsp,attack_menu,stat_texts, obj);

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

                                        }
                                        else if (battleEvent.key == Keyboard.Key.valueOf("W") && !pressed)
                                        {
                                          pressed = true;
                                          menuSound.play();
                                          if (fight_option != 1) {
                                            fight_option--;
                                          }

                                        }
                                        else if (battleEvent.key == Keyboard.Key.valueOf("E") && !pressed)
                                        {
                                          pressed = true;
                                          ArrayList<Character> char_selected = new ArrayList<>(Arrays.asList(battle_participants));
                                          char_selected.removeIf((Character c) -> !c.isAlive);


                                          if (fight_option != 5 && !attack_menu[fight_option - 1].getString().equals("-"))
                                          {
                                            if(attacker.isFriendly && attacker.isAlive)
                                            {
                                              Skills skill = attacker.skills[fight_option-1];
                                              if(skill != null)
                                              {
                                                if(!skill.doesitAffect(Skills.ENEMY))
                                                {
                                                  char_selected.removeIf((Character c) -> !c.isFriendly);
                                                }
                                                else if(!skill.doesitAffect(Skills.FRIENDLY))
                                                {
                                                  char_selected.removeIf((Character c) -> c.isFriendly);
                                                }
                                              }
                                              while (skill.needsMoreTargets())
                                              {
                                                has_chosen = false;
                                                while(!has_chosen)
                                                {
                                                  blink(mainBGsp, attack_menu,stat_texts, obj, char_selected.get(0));
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
                                                        char_selected.add(0,char_selected.remove(char_selected.size()-1));
                                                       }
                                                       else if(select.key == Keyboard.Key.valueOf("D"))
                                                       {
                                                         menuSound.play();
                                                         char_selected.add(char_selected.remove(0));
                                                        }
                                                        else if(select.key == Keyboard.Key.valueOf("E"))
                                                        {
                                                          has_chosen = true;
                                                        }
                                                      }

                                                    }
                                                  }

                                                  skill.addTarget(char_selected.remove(0));
                                                }

                                                try
                                                {
                                                  skill.executeSkill();
                                                  if(skill.revertable)
                                                    revertibles.add(skill.getReverted());
                                                }
                                                catch(Exception e)
                                                {
                                                  e.printStackTrace();
                                                }
                                                Text post_effect = createText(skill.getPostEffectText());
                                                draw(mainBGsp,post_effect,stat_texts,obj);
                                                sleepThread(1000);
                                                skill.unBindAll();
                                                turn_end = true;
                                              }
                                            }
                                            else if(attack_menu[fight_option - 1].getString().equals("-"))
                                            {
                                              //System.out.println("No skill assigned!");
                                            }
                                            else
                                            {
                                              escaped = true;
                                              turn_end = true;
                                              fight_end = true;
                                              paused_turn = x;
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
                                  Text enemyAttack = createText("Enemy "+(turn_state[x] - (team_size - 1))+" is attacking!");
                                  draw(mainBGsp, enemyAttack, stat_texts, obj);
                                  enemyTurn(attacker, battle_participants);
                                  sleepThread(1500);
                                }

                                /*
                                At this part we check if all the members
                                of a team are dead. if not, reenter loop,
                                if yes, end the battle.
                                */
                                there_is_victor = checkSurvivors();

                                //player's team is DEAD. THEY DIED!!! D-E-A-D, DEAD. time for new game
                                if (there_is_victor == 1)
                                {
                                  fight_end = true;
                                  end = true;
                                  option = 1;
                                }
                                //player won
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
                      //if(escape_chance >= 5)
                    //  {
                        option = 1;
                        end = true;
                    //  }
                    }
                  }
                  showSelection(text, option);
                }
            }
        }
        return option;
    }
}
