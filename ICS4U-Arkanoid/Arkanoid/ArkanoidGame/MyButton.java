/**
 * @(#)Button.java
 * @Jack Jiang
 * @2023/05/03
 * This Button class is used to create Button objects
 */
import java.awt.*;
import java.awt.event.MouseEvent;

public class MyButton {
    int x, y, w, h;
    private final boolean clicked = true;
    Font fnt;
    Image img;
    public MyButton(int x, int y, int w, int h, Image img){
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.img = img;
    }
    //getter methods
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public int getWidth(){
        return w;
    }
    public int getHeight(){
        return h;
    }


    public Rectangle getButtonRect(){
        return new Rectangle(x, y, w, h);
    }
    public boolean click(MouseEvent e){
        if (getButtonRect().contains(e.getPoint()) && e.getButton() == MouseEvent.BUTTON1){
            return clicked;
        }
        return !clicked;
    }
    public void drawButton(Graphics g, String label){
        g.drawImage(img, x, y, null);
        if (!label.equals("")) {
            g.drawString(label, x, y);
            g.setColor(Color.WHITE);
            fnt = new Font("Arial", Font.PLAIN, h - 10);
            g.setFont(fnt);
            g.drawString(label, x + 10, y + 5);
        }
    }
}
