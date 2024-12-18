import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import javax.sound.sampled.Clip;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.*;
import java.io.IOException;


public class Game extends Canvas implements Runnable, KeyListener {

    private boolean isRunning = false;
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final String TITLE = "Pac-Man";
    private Thread thread;
    public static Player player;
    public static Level level;
    public static final int PAUSE = 0, GAME = 1;
    public static int STATE = -1;
    public boolean RESTART = false;
    private int time = 0;
    private final int targetFrame = 25;
    private boolean showText = true;
    public static int score = 0;
    private int highscore = 0;
    private int totalScore = 0;
    private final String[] maps = {"/images/map.png", "/images/map2.png", "/images/map3.png", "/images/map4.png", "/images/map5.png",
            "/images/map6.png", "/images/map7.png"};

    private boolean gameWon = false;
    private boolean gameOver = false;

    private Clip backgroundMusic;
    public Game() {
        Dimension dimension = new Dimension(Game.WIDTH, Game.HEIGHT);
        setPreferredSize(dimension);
        setMinimumSize(dimension);
        setMaximumSize(dimension);
        addKeyListener(this);
        new Texture();
        STATE = PAUSE;
        playBackgroundMusic();
    }

    public synchronized void start() {
        if (isRunning) {
            return;
        }
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop() {
        if (!isRunning) {
            return;
        }
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void renderGameWon(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        if (time == targetFrame) {
            time = 0;
            gameWon = false;
            level = new Level(getRandomMap());
            STATE = GAME;
            return;
        }

        if (STATE != PAUSE) {
            playBackgroundMusic();
            return;
        }

        int boxW = 330;
        int boxH = 70;
        int boxX = WIDTH / 2 - boxW / 2;
        int boxY = HEIGHT / 2 - boxH / 2;

        g.setColor(new Color(0, 0, 150));
        g.fillRect(boxX, boxY, boxW, boxH);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 19));

        if (showText) {
            g.drawString("Press 'ENTER' to start the game", boxX + 21, boxY + 40);
        }
    }


    private void playBackgroundMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Game.class.getResource("/SoundSystem/BG.wav"));
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioInputStream);
            if (STATE != GAME) {
                backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }


    private void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }
    }

    private void renderGameOver(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.DIALOG, Font.BOLD, 24));

        String gameOverMessage = "Game Over!";
        String totalScoreMessage = "Highscore: " + totalScore;
        String restartMessage = "Press 'ENTER' to play again.";

        int columnX = WIDTH / 2;
        int startY = HEIGHT / 4;

        g.drawString(totalScoreMessage, columnX - g.getFontMetrics().stringWidth(totalScoreMessage) / 2, startY + 80);
        g.drawString(gameOverMessage, columnX - g.getFontMetrics().stringWidth(gameOverMessage) / 2, startY);
        if (gameWon) {
            updateHighscore(score);
            score = 0;
        }
        g.drawString(restartMessage, columnX - g.getFontMetrics().stringWidth(restartMessage) / 2, startY + 120);
        stopBackgroundMusic();

        if (gameWon) {
            String winMessage = "You won! Press 'ENTER' to play again.";
            g.drawString(winMessage, columnX - g.getFontMetrics().stringWidth(winMessage) / 2, HEIGHT / 2);
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        if (gameWon) {
            renderGameWon(g);
        } else if (gameOver) {
            renderGameOver(g);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, WIDTH, HEIGHT);

            if (STATE == GAME) {
                player.render(g);
                level.render(g);
                g.setColor(Color.WHITE);
                g.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
                g.drawString("Score: " + score, 70, 20);
                g.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
                g.drawString("Highscore: " + totalScore, 470, 20);
            } else if (STATE == PAUSE) {
                int boxW = 330;
                int boxH = 70;
                int boxX = WIDTH / 2 - boxW / 2;
                int boxY = HEIGHT / 2 - boxH / 2;
                g.setColor(new Color(0, 0, 150));
                g.fillRect(boxX, boxY, boxW, boxH);
                g.setColor(Color.WHITE);
                g.setFont(new Font(Font.DIALOG, Font.BOLD, 19));
                if (showText) {
                    g.drawString("Press 'ENTER' to start the game", boxX + 21, boxY + 40);
                }
            }
        }

        g.dispose();
        bs.show();
    }

    private void tick() {
        if (!gameOver) {
            if (STATE == GAME) {
                player.tick();
                level.tick();

                if (level.food.size() == 0) {
                    score += 100;
                    updateHighscore(score);
                    time = 0;
                    gameWon = false;
                    level = new Level(getRandomMap());
                    STATE = GAME;
                    return;
                }

                for (int i = 0; i < level.enemies.size(); i++) {
                    if (player.intersects(level.enemies.get(i))){
                        gameOver = true;
                        updateHighscore(score);
                        return;
                    }
                }
            } else if (STATE == PAUSE) {
                time++;
                if (time == targetFrame) {
                    time = 0;
                    if (showText) {
                        showText = false;
                    } else {
                        showText = true;
                    }
                }
                if (RESTART) {
                    restartGame();
                }
            }
        }
    }
    private void updateHighscore(int currentScore) {
        if (gameWon) {

            totalScore += currentScore;

        } else if (gameOver) {

            if (currentScore > highscore) {
                highscore = currentScore;
            }
            if (currentScore > totalScore) {
                totalScore = currentScore;
            }
        }
    }

    private void init() {
        score = 0;
    }

    private void restartGame() {
        RESTART = false;
        player = new Player(WIDTH / 2, HEIGHT / 2);
        level = new Level(getRandomMap());
        STATE = GAME;
        gameWon = false;
        gameOver = false;
        init();
        playBackgroundMusic();
    }


    private String getRandomMap() {
        int randomMapIndex = (int) (Math.random() * maps.length);
        return maps[randomMapIndex];
    }

    @Override
    public void run() {
        requestFocus();
        int FPS = 0;
        long lastTime = System.nanoTime();
        double timer = System.currentTimeMillis();
        double targetTicks = 60.0;
        double delta = 0;
        double ns = 1000000000 / targetTicks;

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            while (delta >= 1) {
                tick();
                render();
                FPS++;
                delta--;
            }
            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + FPS);
                FPS = 0;
                timer += 1000;
            }
        }
        
        stopBackgroundMusic();
        stop();
    }

    public static void main(String[] args) {
        Game game = new Game();
        JFrame frame = new JFrame();
        frame.setTitle(Game.TITLE);

        ImageIcon icon = new ImageIcon(Game.class.getResource("PACMAN ICON.png"));
        frame.setIconImage(icon.getImage());

        frame.add(game);
        frame.setResizable(false);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
        game.start();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameOver || gameWon) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                restartGame();
            }
        } else if (STATE == GAME) {
            if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
                player.right = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
                player.left = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
                player.up = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
                player.down = true;
            }
        } else if (STATE == PAUSE) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                RESTART = true;
            }
        }
    }


    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            player.up = false;
        }
        if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = false;
        }
    }
}