/**
 * @(#)PowerUps.java
 * @Jack Jiang
 * @2023/05/03
 * This PowerUps class is used to mainly generate and store random powerUps based on collision and probability
 */
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class PowerUps {
    private String randPowerUp;
    private int x, y;
    public static ArrayList<Rectangle> powerUpRects = new ArrayList<>(); //an array list to store kit rectangles
    public static final int KIT_WIDTH = 45, KIT_HEIGHT = 15; //the size for the power up kit
    private final String[] types = {"laser","shrink", "grow", "speed"};  //power ups that is currently available for my game
    private final static Image [] power_img = new Image[4];
    private static ArrayList<String> currentImages = new ArrayList<>();

    public PowerUps(){
        for (int i = 1; i < 5; i++){
            //load the power pill images
            power_img[i - 1] = new ImageIcon("images\\power"+i+".png").getImage();
        }
    }
    public void setX(int x){
        //this method is used to set the kit's current x position to the same as the brick that it just break
        this.x = x;
    }
    public void setY(int y){
        //this method is used to set the kit's current y position similar to the setX method
        this.y = y;
    }
    public void powerUpRect(){
        powerUpRects.add(new Rectangle(x, y, KIT_WIDTH, KIT_HEIGHT));
    }

    public String randomPowerUP(){
        //this method will return a selected random power up
        return types[Util.randint(0, types.length - 1)];
    }
    public void movePowerUP(Rectangle rect){
        rect.y += 10;
    }
    public static void draw(Graphics g, Rectangle rect){
        for (String power: currentImages){
            if (power.equals("laser")){
                g.drawImage(power_img[2], rect.x, rect.y, null);
            }
            else if (power.equals("shrink")){
                g.drawImage(power_img[3], rect.x, rect.y, null);
            }
            else if (power.equals("grow")){
                g.drawImage(power_img[0], rect.x, rect.y, null);
            }
            else if (power.equals("speed")){
                g.drawImage(power_img[1], rect.x, rect.y, null);
            }
        }
    }
    public String collide(Paddle p1){
        //powerUp collision checks
        for (int i = powerUpRects.size() - 1; i >= 0; i--){
            if (powerUpRects.get(i).intersects(p1.getRect())) {
                    powerUpRects.remove(i);
                    randPowerUp = randomPowerUP();
                    currentImages.add(randPowerUp);
                    return randPowerUp;
            }
            if(powerUpRects.get(i).y + KIT_HEIGHT >= GamePanel.WIN_HEIGHT) {
                //delete the power up rectangle if it has go beyond the screen height
                powerUpRects.remove(i);
            }
        }
        return "";
    }
    public void reset(){
        powerUpRects.clear();
    }
}
