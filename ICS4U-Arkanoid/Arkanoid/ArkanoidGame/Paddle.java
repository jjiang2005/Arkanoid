/**
 * @(#)Paddle.java
 * @Jack Jiang
 * @2023/05/03
 * Paddle class is used for creating Paddle objects that player controls and this class is able to move the paddle
 * while also contains two power up methods that involves modifying the paddle (shrinking & growing).
 */
import javax.swing.*;
import java.awt.*;

public class Paddle {
	private int left, right;
	private int x,y;
	private int paddleWidth = 80, paddleSpeed = 12;
	Image paddleImage;

    public Paddle(int xx, int yy,int l, int r) {
		paddleImage = new ImageIcon("images\\paddle.png").getImage();
    	x = xx;
    	y = yy;
    	left = l;
    	right = r;
    }
    public void move(boolean []keys){
    	if(keys[right] && x < GamePanel.WIN_WIDTH - paddleWidth){
    		x += paddleSpeed;
    	}
    	if(keys[left] && x > 10){
    		x -= paddleSpeed;
    	}

    }
	//Accessor methods for paddle object
    public int getX(){
		return x;
	}
	public int getY(){
		return y;
	}
	public int getWidth(){
		return paddleWidth;
	}
    public Rectangle getRect(){
    	return new Rectangle(x - 1,y - 1,paddleWidth + 2,13 + 2);
    }
    public void draw(Graphics g){
		g.drawImage(paddleImage, x - 1, y - 1, null);
    }
	public void shrink(){
		paddleWidth /= 1.5;
		paddleImage = paddleImage.getScaledInstance(paddleWidth, -1, Image.SCALE_SMOOTH); //parameter -1 to maintain the same aspect of ratio of the original image
	}
	public void grow(){
		paddleWidth *= 1.5;
		paddleImage = paddleImage.getScaledInstance(paddleWidth, -1, Image.SCALE_SMOOTH); //parameter -1 to maintain the same aspect of ratio of the original image
	}
}