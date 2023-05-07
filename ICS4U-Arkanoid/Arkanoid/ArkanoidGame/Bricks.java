/**
 * @(#)Bricks.java
 * @Jack Jiang
 * @2023/05/03
 * This Bricks class is intended to be used to create the game map for the current levels
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Bricks {
    private int x, y, width, height;
    private int level;
    Random rand = new Random();
    Color col;
    private ArrayList<Color> colorsArray = new ArrayList<>();
    private ArrayList<Rectangle> rectArray;
    public Bricks(int x, int y, int w, int h , int cur_level){
        this.x = x;   //the initial, the top left brick's top left's x-coordinate
        this.y = y;   //the initial, the top left brick's top right's y-coordinate
        width = w;
        height = h;
        level = cur_level;
        if (level == 1) {
            //level 1's color arrayList (containing 20 colors)
            for (int i = 0; i <= 30; i++) {
                colorsArray.add(randCol());
            }
        }
        else if(level == 2){
            //level 2's color arrayList (containing 40 colors)
            for (int i = 0; i <= 50; i++) {
                colorsArray.add(randCol());
            }
        }
        rectArray = new ArrayList<>(brickRectArray());
    }
    public void draw(Graphics g){
        for (Rectangle rect: rectArray) {
            this.col = randCol();
            g.setColor(Color.WHITE);
            g.fillRect(rect.x, rect.y, width, height);
            g.setColor(colorsArray.get(rectArray.indexOf(rect))); //draw the brick based with the corresponding index of color in the color arraylist
            g.fillRect(rect.x, rect.y, width - 1, height - 1);
        }
    }
    private Color randCol(){
        //this method is used to generate random color for the bricks
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        Color randomColor = new Color(r, g, b);
        return randomColor;
    }
    private ArrayList<Rectangle> brickRectArray(){
        ArrayList<Rectangle> r = new ArrayList<>();
        //iterate through the rectangle object array and set up the top-left coordinate for each brick and return the arrayList at the end
        if (level == 1) {
            //level 1 has 10 blocks each row and a total of 2 rows
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 10; j++) {
                    r.add(new Rectangle(GamePanel.LEVEL_X + j * (width + 10), y, width, height));
                }
                y += (height + 15);
            }
        }
        if (level == 2) {
            //level 2 has 10 blocks each row and a total of 4 rows
            for (int i = 0; i < 5; i++){
                for (int j = 0; j < 10; j++){
                    r.add(new Rectangle(GamePanel.LEVEL_X + j * (width + 10),y, width, height));
                }
                y += (height + 15);
            }
        }
        return r;
    }
    public ArrayList<Rectangle> getRectArray(){
        return rectArray;
    }
    public ArrayList<Color> getColorsArray(){
        return colorsArray;
    }

}
