package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Main extends JPanel implements KeyListener {

    Cat cat = new Cat(100, 300);

    ArrayList<Laser> lasers = new ArrayList<>();

    ArrayList<Zombie> zombies = new ArrayList<>();

    boolean upPressed = false;
    boolean downPressed = false;
    boolean gameOver = false;
    int spawnCounter = 0;
    int score = 0;
    int houseHealth = 100;
    int level = 1;

    //cat
    BufferedImage[] catFrames = new BufferedImage[4];

    int currentCatFrame = 0;
    int animationCounter = 0;

    //zombie
    BufferedImage[] zombieFrames = new BufferedImage[4];

    int currentZombieFrame = 0;

    public Main() {

        JFrame frame = new JFrame("Cat Zombie Defense");

        frame.add(this);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.addKeyListener(this);

        // cat initilization
        try {

            BufferedImage catSheet =
                    ImageIO.read(
                            getClass().getResource("/images/cat.png")
                    );

            for (int i = 0; i < 4; i++) {

                catFrames[i] =
                        catSheet.getSubimage(
                                i * 32,
                                0,
                                32,
                                32
                        );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // zombie initilization
        try {

            BufferedImage zombieSheet =
                    ImageIO.read(
                            getClass().getResource("/images/zombie.png")
                    );

            for (int i = 0; i < 4; i++) {

                zombieFrames[i] =
                        zombieSheet.getSubimage(
                                i * 32,
                                0,
                                32,
                                32
                        );
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Timer timer = new Timer(20, e -> {

            updateGame();
            repaint();

        });

        timer.start();

        frame.setVisible(true);
    }

    public void updateGame() {


        if (gameOver) {
            return;
        }


        if (upPressed) {
            cat.y -= 5;
        }

        if (downPressed) {
            cat.y += 5;
        }

        // keep cat on screen
        if (cat.y < 0) {
            cat.y = 0;
        }

        if (cat.y > getHeight() - cat.height) {
            cat.y = getHeight() - cat.height;
        }

        // move lasers
        for (Laser laser : lasers) {
            laser.x += laser.speed;
        }
        spawnCounter++;
        // move zombies
        if (spawnCounter >= 100) {

            int randomY = (int)(Math.random() * (getHeight() - 50));

            zombies.add(new Zombie(800, randomY));

            spawnCounter = 0;
        }

        for (Zombie zombie : zombies) {

            if (level == 2) {
                zombie.speed = 3;
            }

            if (level == 3) {
                zombie.speed = 4;
            }

            zombie.x -= zombie.speed;
        }

        for (int i = lasers.size() - 1; i >= 0; i--) {

            Laser laser = lasers.get(i);

            Rectangle laserRect = new Rectangle(
                    laser.x,
                    laser.y,
                    laser.width,
                    laser.height
            );

            for (int j = zombies.size() - 1; j >= 0; j--) {

                Zombie zombie = zombies.get(j);

                Rectangle zombieRect = new Rectangle(
                        zombie.x,
                        zombie.y,
                        zombie.width,
                        zombie.height
                );

                if (laserRect.intersects(zombieRect)) {

                    lasers.remove(i);
                    zombies.remove(j);

                    score += 10;

                    break;
                }
            }
        }
        // house hit
        for (int i = zombies.size() - 1; i >= 0; i--) {

            Zombie zombie = zombies.get(i);

            if (zombie.x <= 0) {

                zombies.remove(i);

                houseHealth -= 10;
            }
        }

        if (houseHealth <= 0) {
            gameOver = true;
        }

        //levels
        if (score >= 100) {
            level = 2;
        }

        if (score >= 250) {
            level = 3;
        }

        animationCounter++;

        if (animationCounter >= 10) {

            currentCatFrame++;

            if (currentCatFrame >= 4) {
                currentCatFrame = 0;
            }

            animationCounter = 0;
        }

        if (animationCounter == 0) {

            currentZombieFrame++;

            if (currentZombieFrame >= 4) {
                currentZombieFrame = 0;
            }
        }


    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        draw(g);
    }

    public void draw(Graphics g) {

        // background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // cat
        g.drawImage(
                catFrames[currentCatFrame], cat.x, cat.y, 64, 64, null
        );

        //zombies
        for (Zombie zombie : zombies) {

            g.drawImage(
                    zombieFrames[currentZombieFrame], zombie.x, zombie.y, 80, 80, null
            );
        }
        //laser
        g.setColor(Color.RED);

        for (Laser laser : lasers) {


            g.fillRect(laser.x, laser.y, 20, 5
            );
        }

        //scoreboard
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 20, 20);

        g.setColor(Color.WHITE);
        g.drawString("House Health: " + houseHealth, 20, 40);

        g.setColor(Color.GRAY);
        g.fillRect(20, 250, 60, 100);

        //game over screen
        if (gameOver) {

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 40));

            g.drawString("GAME OVER", 250, 300);
        }
        //health bar
        g.setColor(Color.RED);
        g.fillRect(20, 50, houseHealth * 2, 20);

        g.setColor(Color.WHITE);
        g.drawRect(20, 50, 200, 20);

        // level
        g.drawString("Level: " + level, 20, 90);
    }

    @Override
    public void keyPressed(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {

            lasers.add(
                    new Laser(
                            cat.x + cat.width,
                            cat.y + cat.height / 2
                    )
            );

        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        if (e.getKeyCode() == KeyEvent.VK_UP) {
            upPressed = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            downPressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    public static void main(String[] args) {
        new Main();
    }
}