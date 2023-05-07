/**
 * @(#)Ball.java
 * @Jack Jiang
 * @2023/05/03
 * This Ball class is used to create ball object for the Arkanoid game and is able to
 * check collisions if the ball object is involved
 */
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Ball {
	private PowerUps kit;
	private int x,y,vx,vy;
	private final int ballSize = 13; //diameter

	//the boolean variable bouncedOffBlock prevents when a single shot removes and pass through all four blocks when it hits four corners in a directly striaght shot.
	// (ie. vy's (-1)^4 = 1, therefore, the ball passes through 4 blocks and only changes direction when it reaches the top edge of the game window).
	private boolean shoot, bouncedOffBlock = false;
	private final Clip clip;
	private int [] collideTop = new int[2];
	public Ball() throws LineUnavailableException, IOException, UnsupportedAudioFileException {
		final File hitSoundFile;
		kit = new PowerUps();

		//load the sound effect
		hitSoundFile = new File("sound\\hit.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(hitSoundFile);
		clip = AudioSystem.getClip();
		clip.open(audioStream);

		//initial velocity for the ball in its x direction (Horizontal Component)
		if(Util.randint(0,1) == 0){
			vx = -1;
		}
		else{
			vx = 1;
		}

		//initial velocity for the ball in its y direction (Vertical Component)
		vy = -15;
	}
	public void move(Paddle p1, boolean []keys, ArrayList<Rectangle> bricks, ArrayList<Color> colors) {
		kit = new PowerUps();

		if (keys[KeyEvent.VK_SPACE] && GamePanel.live > 0 && y == 498) {
			shoot = true;
		}
		if (shoot) {
			y += vy;
			x += vx;
			if (y <= 0) {
				vy *= -1;
			}
			if (x <= 1 || x + ballSize >= GamePanel.WIN_WIDTH) {
				//when the ball reaching the two ends of the game window
				vx = -vx;
			}

			if(ballRect().intersects(p1.getRect())){
				/*If there is a collision happened between the ball and the paddle, there is the possibility for the ball to be stuck inside the paddle
				  And the ball's direction will be changed based on which part of the paddle it hits.*/
				int tempx = x, tempy = y; //temporary variables for storing the initial collision position

				/*Horizontal adjustment (x-axis) when the ball hits the side of the paddle
				back off the x until it is no longer intersects with the paddle*/
				x -= vx;
				if(!ballRect().intersects(p1.getRect())) {
					vx *= -1;	//if the ball is not stuck inside the paddle (ie. touching the edge), reverse the horizontal velocity to form a "bounce effect" that has a "V" shape projectile
				}
				x = tempx;

				/*Vertical adjustment (y-axis) when the ball hits top of the paddle
				similar idea to horizontal checks except this time I reflect the vertical velocity if the ball is not inside the paddle and bounced off from the paddle*/
				y -= vy;
				if(!ballRect().intersects(p1.getRect())){
					bouncedOffBlock = false;
					vy *= -1;
					if (p1.getX() - ballSize <= x && x <= p1.getX() +p1.getWidth()/3){
						//if the ball hit the left top of the paddle (ie. paddle's width /3) the ball will bounce to the left
						//the angle of resultant trajectory will be randomized
						vx = Util.randint(-7,-3);

					}
					else if(p1.getX() + ((2*p1.getWidth())/3) <= x && x <= p1.getX() + p1.getWidth()){
						//if the ball hit the right top of the paddle, the ball will bounce to the right
						//the angle of resultant trajectory will be randomized
						vx = Util.randint(3,7);
					}

				}
				y = tempy;

			}
			//Use a copy of Collections that needs to be removed from the brickRect Array and the Color Array while dealing with Concurrent Modification Exceptions
			ArrayList<Rectangle> toBeRemoved = new ArrayList<>();
			ArrayList<Color> colToBeRemoved = new ArrayList<>();

			for (Rectangle brickRect: bricks){
				if(brickRect.intersects(ballRect())){
					if (Util.randint(2,5) == 3 && bricks.size() > 2){
						//the power up will drop based on odds
						kit.setX(brickRect.x);  //initialize kit's x and y coordinates with the coordinate of the block that the ball just hit.
						kit.setY(brickRect.y);
						kit.powerUpRect();
					}


					int tempx = x, tempy = y;
					//horizontal adjustments
					x -= vx;
					if (!brickRect.intersects(ballRect())){
						vx *= -1;
					}
					x = tempx;

					//vertical adjustments
					y -= y;
					if(!brickRect.intersects(ballRect()) & !bouncedOffBlock){
						vy *= -1;
						bouncedOffBlock = true;
					}

					y = tempy;
					toBeRemoved.add(brickRect);
					colToBeRemoved.add(colors.get(bricks.indexOf(brickRect)));

					if (!clip.isActive()) {
						//avoid duplicate sound effect
						clip.start(); //activate the sound effect
						clip.setMicrosecondPosition(0); //reset the sound effect
					}
				}
			}
			bricks.removeAll(toBeRemoved);
			colors.removeAll(colToBeRemoved);

		}
		if(y > GamePanel.WIN_HEIGHT){
			//reset when the ball falls off the game
			shoot = false; //modify the current launch state
			vy = -15;//reset ball's vertical velocity
			if(Util.randint(0,1) == 0){
				//reset ball's horizontal velocity
				vx = -1;
			}
			else{
				vx = 1;
			}
		}
		if (!shoot) {
			x = p1.getRect().x + (p1.getRect().width / 2) - 5; //the x-coordinate for the ball in idle state (when not shoot)
			y = 498; //the y-coordinate for the ball in idle state (when not shoot)
		}
	}

	//Getter methods
	public int getSpeed(){
		return vy;
	}
	public void speedUp(){
		vy -= 2;
	}
	public void resetSpeed(){
		vy = -15;
	}
	public Rectangle ballRect(){
		return new Rectangle(x - 2, y - 2,  ballSize + 4, ballSize + 4);
	}
	public int getY(){
		return y;
	}
	public int getBallSize(){
		return ballSize;
	}
	public void draw(Graphics g){
		g.setColor(Color.GRAY);
		g.fillOval(x - 1, y - 1, ballSize + 2, ballSize + 2);
		g.setColor(Color.ORANGE);
		g.fillOval(x,y,ballSize,ballSize);

	}
}
