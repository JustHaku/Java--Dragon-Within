import java.lang.Math;
import java.util.*;

public class Sort
{
  int[] array = {6, 2, 8, 12, 25, 1};
  int[] turns = {0, 1, 2, 3, 4, 5};
  int[] dummy;
  int level, exp, exp_gain;
  int health = 100;
  int speed = 10;
  int def = 10;
  int mana = 100;
  int atk = 10;

  int level_calc(int constant, int current_exp, int exp_gain)
  {
    int current_level = 0;
    int experience = current_exp + exp_gain;
    //multiplying exp gain by 8, so that with each level increment, the character will need 50 more xp to gain a level
    double value = 1 + (8*experience/constant);
    return current_level = (1 + (int)Math.sqrt(value))/2;
  }

  void levelUP()
  {
    Random rand = new Random();
    int rand_number = rand.nextInt((8 - 3) + 1) + 3;
    health += rand_number;
    System.out.println("\nHealth + "+rand_number);
    rand_number = rand.nextInt((8 - 3) + 1) + 3;
    atk += rand_number;
    System.out.println("Attack + "+rand_number);
    rand_number = rand.nextInt((8 - 3) + 1) + 3;
    def += rand_number;
    System.out.println("Defence + "+rand_number);
    rand_number = rand.nextInt((8 - 3) + 1) + 3;
    mana += rand_number;
    System.out.println("Mana + "+rand_number);
    rand_number = rand.nextInt((8 - 3) + 1) + 3;
    speed += rand_number;
    System.out.println("Speed + "+rand_number);
  }

  public int exp_calc(int constant, int lvl)
  {
    double level = lvl;
    double current_exp = ((Math.pow(level, 2) - level) * constant)/2;
    return (int)current_exp;
  }

  public Sort()
  {
    exp = 48;
    exp_gain = 3;
    level = level_calc(50, exp, exp_gain);
    System.out.println("Player level is: "+level);
    System.out.println("Health "+health+"\nMana "+mana+"\nAttack "+atk+
    "\nDefence "+def+"\nSpeed "+speed);
    levelUP();
    System.out.println("\nHealth "+health+"\nMana "+mana+"\nAttack "+atk+
    "\nDefence "+def+"\nSpeed "+speed);
    exp = exp_calc(50, 5);
    System.out.println("A player of level 5 has "+exp+" experience !");


    /*display(array);
    System.out.println("turns is -> [0, 1, 2, 3, 4, 5]");
    dummy = bubbleSort(array, turns);
    display(dummy);
    display(turns);*/
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

  void display(int[] arr)
  {
    System.out.print("array is -> [");
    for(int i=0; i<arr.length; i++)
    {
      if(i == (arr.length - 1))
        System.out.print(arr[i]+"]\n");
      else
        System.out.print(arr[i]+", ");
    }
  }


  public void newPlayer(int lvl)
  {
    Random rand = new Random();
    int[] stats = new int[5];
    int randomInt;

    int max_health = 80;
    int max_mana = 80;
    int attack = 7;
    int defence = 7;
    int speed = 6;
    for(int i = 0; i<stats.length; i++){
      stats[i] = 0;
      for(int j = 0; j<lvl; j++){
        randomInt = rand.nextInt((8 - 3) + 1) + 3;
        stats[i] += randomInt;
      }
    }
    max_health += stats[0];
    max_mana += stats[1];
    attack += stats[2];
    defence += stats[3];
    speed += stats[4];
    health = max_health;
    mana = max_mana;
    System.out.println(max_health);
    System.out.println(max_mana);
    System.out.println(attack);
    System.out.println(defence);
    System.out.println(speed);
  }

  public static void main(String[] args)
  {
    Sort s = new Sort();
    System.out.println();
    s.newPlayer(5);
  }
}
