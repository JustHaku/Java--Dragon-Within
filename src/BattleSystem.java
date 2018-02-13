
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

    public BattleSystem(RenderWindow window, int scale, int options_num, ArrayList<Character> team) throws IOException {

        menuWindow(window, scale, options_num);
        attack_menu = newText(4 + 1);
        //items_menu = newText(number of items in inventory);

        text_font = new Font();
        text_font.loadFromFile(Paths.get("src/graphics/Menu/CaviarDreams.ttf"));

        text[0] = new Text("Attack", text_font, screenHeight / 22);
        text[1] = new Text("Items", text_font, screenHeight / 22);
        text[2] = new Text("Escape", text_font, screenHeight / 22);

        textWidth = screenWidth/36 - screenWidth/42;
        textHeight = screenHeight/12*10;
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

                            while (fight_end == false) {
                                for (int x = 0; x < characters_num; x++) {
                                    if (battle_participants[(turn_state[x])].isFriendly && battle_participants[(turn_state[x])].isAlive) {
                                        if (fight_end == false) {
                                            turn_end = false;
                                        }

                                        playerTurn(battle_participants[(turn_state[x])]);
                                        int fight_option = 1;
                                        int char_select;
                                        boolean pressed = false;
                                        System.out.println("It's " + battle_participants[(turn_state[x])].name + "'s turn!\n");

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
                                                    }
                                                    else if (battleEvent.key == Keyboard.Key.valueOf("W") && !pressed) {
                                                      pressed = true;
                                                      menuSound.play();
                                                      if (fight_option != 1) {
                                                          fight_option--;
                                                      }
                                                    }
                                                    else if (battleEvent.key == Keyboard.Key.valueOf("E") && !pressed)
                                                    {
                                                      pressed = true;
                                                      int range_low = 0;
                                                      int range_high = characters_num;

                                                      if (fight_option != 5 && !attack_menu[fight_option - 1].getString().equals("-"))
                                                      {
                                                        if(battle_participants[(turn_state[x])].isFriendly && battle_participants[(turn_state[x])].isAlive)
                                                        {
                                                          Skills skill = battle_participants[(turn_state[x])].skills[fight_option-1];
                                                          if(skill != null)
                                                          {
                                                            if(!skill.doesitAffect(Skills.ENEMY))
                                                            {
                                                              range_high = team_size;
                                                            }
                                                            else if(!skill.doesitAffect(Skills.FRIENDLY))
                                                            {
                                                              range_low = team_size;
                                                            }
                                                          }
                                                          char_select = range_low;
                                                          //ContinueAttack:

                                                          /***HERE IS APPLYEE SELECTION, EXIT LOOP ONCE SELECTED***/
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
                                                                if(char_select != range_low)
                                                                  char_select--;
                                                              }
                                                              else if(select.key == Keyboard.Key.valueOf("D"))
                                                              {
                                                                if(char_select != range_high)
                                                                  char_select++;
                                                              }
                                                              else if(select.key == Keyboard.Key.valueOf("E"))
                                                              {
                                                                //break ContinueAttack;
                                                                break;    //BREAK SELECT
                                                              }
                                                            }
                                                          }

                                                          //Program resumes from here after break 'select'
                                                          skill.applyTo(battle_participants[char_select]);
                                                          try
                                                          {
                                                            skill.executeSkill();
                                                          }
                                                          catch(Exception e)
                                                          {
                                                            e.printStackTrace();
                                                          }
                                                          skill.unBindAll();
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
                                                      /*for (Event battle : window.pollEvents())
                                                          KeyEvent battleEvent = battle.asKeyEvent();*/
                                                      /*while (skill.needsMoreCharacters()){



                                                      }*/









                                                        /*if (fight_option == 1) {
                                                            if (attack_menu[fight_option - 1].getString().equals("-")) {
                                                                System.out.println("No skill assigned to this slot!");
                                                            }
                                                            else {
                                                                battle_participants[2].health -= 27;
                                                                if (battle_participants[2].health <= 0) {
                                                                    battle_participants[2].isAlive = false;
                                                                    battle_participants[2].health = 0;
                                                                }
                                                                turn_end = true;
                                                                System.out.println(battle_participants[(turn_state[x])].name
                                                                        + " inflicted 20 damage to " + battle_participants[2].name + "1");
                                                                System.out.println(battle_participants[2].name + "1 has "
                                                                        + battle_participants[2].health + "/" + battle_participants[2].max_health);
                                                            }
                                                        }
                                                        else if (fight_option == 2)
                                                        {
                                                            if (attack_menu[fight_option - 1].getString().equals("-"))
                                                            {
                                                                System.out.println("No skill assigned to this slot!");
                                                            }
                                                            else
                                                            {
                                                                battle_participants[3].health -= 50;
                                                                if (battle_participants[3].health <= 0)
                                                                {
                                                                    battle_participants[3].isAlive = false;
                                                                    battle_participants[3].health = 0;
                                                                }
                                                                turn_end = true;
                                                                System.out.println(battle_participants[(turn_state[x])].name
                                                                        + " inflicted 50 damage to " + battle_participants[3].name + "2");
                                                                System.out.println(battle_participants[3].name + "2 has "
                                                                        + battle_participants[3].health + "/" + battle_participants[3].max_health);
                                                            }
                                                        }
                                                        else if (fight_option == 3)
                                                        {
                                                            if (attack_menu[fight_option - 1].getString().equals("-"))
                                                            {
                                                                System.out.println("No skill assigned to this slot!");
                                                            }
                                                        }
                                                        else if (fight_option == 4)
                                                        {
                                                            if (attack_menu[fight_option - 1].getString().equals("-"))
                                                            {
                                                                System.out.println("No skill assigned to this slot!");
                                                            }
                                                        }
                                                        else if (fight_option == 5)
                                                        {
                                                            turn_end = true;
                                                            fight_end = true;
                                                        }*/
                                                    }
                                                    showSelection(attack_menu, fight_option);
                                                    pressed = false;
                                                }
                                            }
                                        }

                                    } else if (battle_participants[(turn_state[x])].isAlive == true && fight_end == false && battle_participants[(turn_state[x])].isFriendly == false) {
                                        System.out.println("\nEnemy turn!   (Wait 1.5 seconds..)");
                                        Text enemyAttack = new Text("Enemy turn!", text_font, screenHeight/25);
                                        enemyAttack.setPosition(30, (screenHeight/2 + screenHeight/4 + screenHeight/8 + screenHeight/16));
                                        enemyAttack.setColor(Color.RED);

                                        window.clear(new Color(192, 192, 192, 200)); //colour is gray, so options can be visible while white
                                        window.draw(enemyAttack);
                                        window.display();
                                        enemyTurn(battle_participants[(turn_state[x])], battle_participants, team_size);

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
                                    int dead_counter_team = 0;
                                    int dead_counter_enemy = 0;
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

                        } else if (option == 2) {
                            //open inventoryMenu
                            //end = true;
                        } else if (option == 3) {
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
