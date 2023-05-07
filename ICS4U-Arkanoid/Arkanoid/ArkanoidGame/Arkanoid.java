/**
 * @Arkanoid.java
 * @Jack Jiang
 * @2023/05/03
 * main program
 */
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.io.IOException;

public class Arkanoid extends JFrame{
    private GamePanel game= new GamePanel();
    public Arkanoid() throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        super("Arkanoid Game");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(game);
        pack();  // set the size of my Frame exactly big enough to hold the contents
        setLocationRelativeTo(null);  //game window starts right in the center on our screen
        setVisible(true);
        setResizable(false);
    }
    public void exit(){
        //shortcut for exiting my game by using the escape key
        System.exit(getDefaultCloseOperation());
    }
    public static void main(String[] args) throws UnsupportedAudioFileException, LineUnavailableException, IOException {
        Arkanoid frame = new Arkanoid();
    }
}