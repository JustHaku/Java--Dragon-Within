/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Petros Soutzis
 */
public class Tester {
    private int[] array;

    public Tester()
    {
        array = new int[3];
        array[0] = 12;
        array[1] = 8;

        for(int i=0; i<array.length; i++)
        {
            System.out.println(array[i]+" -> element "+i);
        }

        if(array[2] == 0)
        {
          System.out.println("un-initialised array variable is set as 0");
        }
    }

    boolean meow()
    {
      int y = 25;
      int f = 3;

      return y > f;
    }

    public static void main(String[] args)
    {
        Tester t = new Tester();
        System.out.println(t.meow());
    }
}
