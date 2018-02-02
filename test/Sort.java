public class Sort
{
  int[] array = {6, 2, 8, 12, 25, 1};
  int[] turns = {0, 1, 2, 3, 4, 5};
  int[] dummy;

  public Sort()
  {
    display(array);
    System.out.println("turns is -> [0, 1, 2, 3, 4, 5]");
    dummy = bubbleSort(array, turns);
    display(dummy);
    display(turns);
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

  public static void main(String[] args)
  {
    Sort s = new Sort();
  }
}
