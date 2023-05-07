/**
 * @(#)Util.java
 * @Jack Jiang
 * @2023/05/03
 * This class is a utility clas that can generate a random integer in the given range of the parameters
 */
public class Util {
   public static int randint(int x, int y){
     return (int)(Math.random()*(y - x + 1) + x);
   }
}