/**
 * @(#)Game.java
 * @Jack Jiang
 * @2023/05/03
 * This class is used to create the panel of the game using JPanel and is able to handling basic IOs for the game
 * including but not limited to keyboard inputs and mouser input
 */

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.swing.*;
import javax.sound.sampled.*;
import static javax.sound.sampled.Clip.LOOP_CONTINUOUSLY;

class GamePanel extends JPanel implements KeyListener, ActionListener, MouseListener{
	//PowerUp variables:
	private PowerUps kit;
	private Laser laser;
	private boolean[] powerUpStatus = new boolean[4];
	int shrinkCounter = 500; //the default cool-down for the paddle shrinking power up
	int growCounter = 500;

	//Game Variables
	public static final int LEVEL_X = 45;
	public static final int INTRO=0, GAME=1, END=2, WIN=3;
	private int current_level = 1;
	private int screen = INTRO;
	private final boolean []keys; //a boolean array that stores the state of all the keys on the keyboard (true = pressed; false = not press)
	Timer timer;
	Image introBack, gameBackground, b1, b2;
	private Ball ball;
	private Paddle player;
	private Bricks brick;
	private int score = 0;
	public static final int WIN_WIDTH = 800, WIN_HEIGHT = 600;
	public static int live = 5;
	private Clip clip;
	private final MyButton startButton;
	public GamePanel() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
		player = new Paddle(WIN_WIDTH/2 - 100/2,510,KeyEvent.VK_A,KeyEvent.VK_D);

		kit = new PowerUps();
		laser = new Laser(player);


		//Background images
		introBack =  new ImageIcon("images\\background6.gif").getImage().getScaledInstance(WIN_WIDTH,WIN_HEIGHT,introBack.SCALE_DEFAULT);
		gameBackground =  new ImageIcon("images\\background8.gif").getImage().getScaledInstance(WIN_WIDTH,WIN_HEIGHT,gameBackground.SCALE_DEFAULT);

		//Sound effects & audio file
		File bgMusicFile = new File("sound\\bg.wav");
		AudioInputStream bgStream = AudioSystem.getAudioInputStream(bgMusicFile);
		clip = AudioSystem.getClip();
		clip.open(bgStream);
		clip.start();
		clip.loop(LOOP_CONTINUOUSLY);

		brick = new Bricks(LEVEL_X, 30, 60, 20, current_level);

		//Buttons
		b1 = new ImageIcon("images\\startButton.png").getImage();
		startButton = new MyButton(WIN_WIDTH/2 - b1.getWidth(null)/2, 400, b1.getWidth(null), b1.getHeight(null), b1);

		//Keyboard Array
		keys = new boolean[KeyEvent.KEY_LAST+1];

		//Objects for the game
		ball = new Ball();



		//Game Window variables and settings
		setPreferredSize(new Dimension(WIN_WIDTH, WIN_HEIGHT));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
		addMouseListener(this);
		timer = new Timer(20, this);
		timer.start();

	}


	public void move() throws UnsupportedAudioFileException, LineUnavailableException, IOException{
		int LASER = 0, SHRINK = 1, GROW = 2, SPEED = 3;   //the corressponding element in my arrays of power up rectangles from the PowerUps class
		int ballSpeedLimit = -18;  //the fastest speed that the ball could get

		if(screen == GAME){
			for (Rectangle powerRect: kit.powerUpRects){
				//move the rectangles from the Rectangle ArrayList from PowerUps class
				kit.movePowerUP(powerRect);
			}
			String power_up = kit.collide(player);
			switch(power_up){
				case "laser":
					if (!powerUpStatus[LASER]) {
						//if laser has not been activated
						powerUpStatus[LASER] = true;
					}

				case "shrink":
					if (!powerUpStatus[SHRINK]){
						powerUpStatus[SHRINK] = true;
						player.shrink();
					}
					break;

				case "grow":
					if (!powerUpStatus[GROW]){
						powerUpStatus[GROW] = true;
						player.grow();
					}
					break;
				case "speed":
					if ((!powerUpStatus[SPEED]) && ball.getSpeed() > ballSpeedLimit){
						powerUpStatus[SPEED] = true;
						ball.speedUp();  //this speed power up wil only impact once for each kit
						powerUpStatus[SPEED] = false;
					}
			}
			//Apply Power Up Effects:
			if (ball.getSpeed() <= ballSpeedLimit){
				//Note: negative values comparisons
				//if the ball's current speed has exceeded the max speed limit reset it back the ball speed to normal.
				ball.resetSpeed();
			}
			//Applying power up when player has collected the kit
			if(powerUpStatus[SHRINK]){
				if (shrinkCounter == 0){
					shrinkCounter = 500;  //reset back the cool-down
					powerUpStatus[SHRINK] = false;
					player.grow(); //once shrinking effect is over, I will change the paddle size back to normal
				}
				else if (shrinkCounter > 0) {
					shrinkCounter--; //decrease the cool-down
				}
			}

			if(powerUpStatus[GROW]){
				//used the exact same logic as shrinking power up
				if(growCounter == 0){
					growCounter = 500;
					powerUpStatus[GROW] = false;
					player.shrink();
				} else if (growCounter > 0) {
					growCounter--;
				}
			}

			if (powerUpStatus[LASER]){
				//when laser is activated
				int laserRemain = laser.shoot(brick.getRectArray(), brick.getColorsArray());

				/*
				Reset back laser effect to normal if one of following conditions has been met:
				shooting laser bullets when there has laser remain(each kit can only shoot 5 times),
				when the ball drop off from the screen, and if the ball is currently on the paddle
				*/
				if (laserRemain == 0 || ball.getY() + ball.getBallSize() >= 560 || ball.getY() == 498){
					powerUpStatus[LASER]= false;
					resetLaser(player);
				}
			}




			player.move(keys);
			ball.move(player, keys, brick.getRectArray(), brick.getColorsArray());
			if(live <= 0){
				screen = END;
			}

			if (current_level == 1 && brick.getRectArray().size() == 0){
				//if there is no block left and current level is level one that means the player has passed level 1
				current_level = 2;
				resetGame();
			}

			if (current_level == 2 && brick.getRectArray().size() == 0){
				//if the player has won the game
				screen = WIN;
				resetBall();
			}
			if (ball.getY() + ball.getBallSize() >= WIN_HEIGHT){
				//if the ball fall off the screen, then create a new ball object
				resetBall();
				live -- ;
			}
		}
		int totalBlock = (current_level == 1)? 30: 50;
		score = Math.abs(totalBlock - brick.getRectArray().size());
	}
	public void resetLaser(Paddle player) {
		laser = new Laser(player);
	}
	private void resetGame(){
		powerUpStatus = new boolean[4];
		resetBall();
		player = new Paddle(WIN_WIDTH/2 - 100/2,510,KeyEvent.VK_A,KeyEvent.VK_D);
		brick = new Bricks(LEVEL_X, 30, 60, 20, current_level);
		resetLaser(player);
		kit.reset();
		screen = GAME;
		score = 0;
		live = 5;
	}
	private void resetBall(){
		try {
			ball = new Ball();
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e) {
			throw new RuntimeException(e);
		}
	}
	@Override
	public void actionPerformed(ActionEvent e){
		try {
			move(); // never draw in move
		} catch (UnsupportedAudioFileException | LineUnavailableException | IOException ex) {
			throw new RuntimeException(ex);
		}
		repaint(); // only draw
	}

	@Override
	public void keyReleased(KeyEvent ke){
		int key = ke.getKeyCode();
		keys[key] = false;
	}

	@Override
	public void keyPressed(KeyEvent ke){
		int key = ke.getKeyCode();  //get the key code ASCII
		keys[key] = true;	//set the state of the pressed key to true

		if (ke.getKeyCode() == KeyEvent.VK_ESCAPE){
			try {
				Arkanoid window = new Arkanoid();
				window.exit();  //when shortcut is hit, quit the game
			} catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
				throw new RuntimeException(e);
			}
		}
		if (ke.getKeyCode() == KeyEvent.VK_ENTER && screen == INTRO) {
			//enter the game
			screen = GAME;
		}
		if(screen == END && ke.getKeyCode() == KeyEvent.VK_ENTER){
			//restart after the player lose the game and pressed enter
			resetGame();
		}
		if(screen == WIN && ke.getKeyCode() == KeyEvent.VK_ENTER){
			//restart after the player won the game and pressed enter
			screen = INTRO;
		}
	}

	@Override
	public void keyTyped(KeyEvent ke){}
	@Override
	public void	mouseClicked(MouseEvent e){
		if (screen == INTRO && startButton.click(e)){
			screen = GAME;
		}
	}

	@Override
	public void	mouseEntered(MouseEvent e){}

	@Override
	public void	mouseExited(MouseEvent e){}

	@Override
	public void	mousePressed(MouseEvent e){
		if(screen == WIN){
			screen = INTRO;
		}
	}

	@Override
	public void	mouseReleased(MouseEvent e){}

	@Override
	public void paint(Graphics g){
		if(screen == INTRO){
			//introduction screen
			g.drawImage(introBack,0,0,null);
			g.setColor(Color.WHITE);
			Font fntOutline = new Font("Georgia", Font.BOLD, 80);
			g.setFont(fntOutline);
			g.drawString("Arkanoid",WIN_WIDTH/2 - 200, WIN_HEIGHT/2);

			Color col = new Color(0, 104, 222);
			g.setColor(col);
			Font fnt = new Font("Georgia", Font.PLAIN, 95);
			g.setFont(fnt);
			g.drawString("Arkanoid", WIN_WIDTH/2 - 200, WIN_HEIGHT/2 );
			startButton.drawButton(g,"");
		}
		else{
			g.setColor(Color.WHITE);
			g.drawImage(gameBackground, 0, 0, null);
			Font fnt = new Font("Arial",Font.BOLD,20);
			g.fillRect(0, WIN_HEIGHT - 70, 110, 70);
			Color darkGreen = new Color(1, 70, 32);
			g.setColor(darkGreen);
			g.drawRect(0, WIN_HEIGHT - 70, 110, 70);
			g.setFont(fnt);
			g.drawString("Score: "+score, 10,WIN_HEIGHT - 50);
			g.setFont(fnt);
			g.setColor(Color.GREEN);
			g.drawString("Health: " + live, 10, WIN_HEIGHT - 20);
			ball.draw(g);
			player.draw(g);
			brick.draw(g);
			laser.draw(g);
			for (Rectangle i : kit.powerUpRects){
				kit.draw(g, i);
			}

			if (screen == WIN){
				//if the player has won the game
				Color col = new Color(0,0,0,100);
				g.setColor(col);
				g.fillRect(0,0, WIN_WIDTH, WIN_HEIGHT);

				g.setColor(darkGreen);
				g.setFont(new Font("serif", Font.BOLD, 40));
				g.drawString("Score: "+score, WIN_WIDTH/2 - 70, WIN_HEIGHT/2);

				g.setFont(new Font("serif", Font.BOLD, 30));
				g.drawString("You won the game!! ", WIN_WIDTH/2 - 110, WIN_HEIGHT/2 + 45);
				g.drawString("Press Enter to restart ", WIN_WIDTH/2 - 145, WIN_HEIGHT/2 + 80);
			}
			else if (screen == END) {
				//game over if the player has no live left
				//a color with alpha value setup for the death screen
				Color col = new Color(0,0,0,100);
				g.setColor(col);
				g.fillRect(0,0, WIN_WIDTH, WIN_HEIGHT);
				g.setColor(Color.RED);
				g.setFont(new Font("serif", Font.BOLD, 40));
				g.drawString("Score: "+score, WIN_WIDTH/2 - 75, WIN_HEIGHT/2);

				g.setFont(new Font("serif", Font.BOLD, 30));
				g.drawString("Gameover!!!! ", WIN_WIDTH/2 - 100, WIN_HEIGHT/2 + 45);
				g.drawString("Press Enter to restart ", WIN_WIDTH/2 - 145, WIN_HEIGHT/2 + 80);
			}
		}

	}
}