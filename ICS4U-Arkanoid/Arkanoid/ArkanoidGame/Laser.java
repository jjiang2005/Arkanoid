/**
 * @(#)Laser.java
 * @Jack Jiang
 * @2023/05/03
 * This Laser class is used to create Laser object that is used for power up collections
 */

import java.awt.*;
import java.util.ArrayList;
public class Laser {
    int bullet_x1, bullet_x2, y, vy = 10;  //variables that contains the coordinates and speed for two laser bullets
    private int count = 50, total = 5;  //the count-down for laser bullets is 50 and there are 10 (5*2) shots total for one laser power up kit
    private ArrayList<Rectangle> laserRects = new ArrayList<>();
    private final Paddle p1;

    public Laser(Paddle player){
        p1 = player;
    }

    public void draw(Graphics g){
        for (Rectangle rect : laserRects){
            g.setColor(Color.BLACK);
            g.fillRect(rect.x,rect.y, rect.width, rect.height);
        }
    }
    public int shoot(ArrayList<Rectangle> block, ArrayList<Color> blockCols)  {
        if (count < 50 &&  total >0){
            //cool down counter for laser bullet
            count++;
        }
        else if ( count == 50 && total > 0) {
            count = 0;  //reset the counter if it is ready to shoot
            bullet_x1 = p1.getX();
            bullet_x2 = p1.getX() + p1.getWidth() - 5;
            y  = p1.getY() - 5;
            laserRects.add(new Rectangle(bullet_x1, y , 5, 5));  //the rectangle for the left laser bullet
            laserRects.add(new Rectangle(bullet_x2, y, 5, 5));  // the rectangle for the right laser bullet
            total --;
        }
        move(block, blockCols);
        return total;  //there is only 5 shots for each power up kit that has laser effect
    }
    private void move(ArrayList<Rectangle> block, ArrayList<Color> blockCols){
        ArrayList<Rectangle> laserToRemove = new ArrayList<>();  // the array list that to store the laser rectangles that needs to be removed
        for (Rectangle rect: laserRects){
            rect.y -= vy; //move up the laser bullets

            if (rect.y <= 0){
                laserToRemove.add(rect);  //remove the bullet if it hits the top edge of the window
            }
        }
        for (int i = block.size()-1; i >= 0; i--){
            //check for collisions between bullets and bricks
            for (Rectangle rect: laserRects){
                if (block.get(i).intersects(rect)){
                    laserToRemove.add(rect);
                    block.remove(block.get(i)); //remove the block that's already hit by the laser bullet
                    blockCols.remove(blockCols.get(i));
                    break;
                }
            }
        }
        laserRects.removeAll(laserToRemove); //remove all the lasers that are gone
    }
}
