import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Player extends Rectangle {

    public boolean right, left, down, up;
    private int speed = 4;
    private int time = 0;
    private int targetTime = 10;
    public int imageIndex = 0;
    private int lastDir = 1;

    public Player(int x, int y) {
        setBounds(x, y, 32, 32);
    }

    public void tick() {
        if (right && canMove(x + speed, y)) {
            x += speed;
            lastDir = 1;
        }
        if (left && canMove(x - speed, y)) {
            x -= speed;
            lastDir = 2;
        }
        if (up && canMove(x, y - speed)) {
            y -= speed;
            lastDir = 3;
        }
        if (down && canMove(x, y + speed)) {
            y += speed;
            lastDir = 4;
        }

        Level level = Game.level;

        for (int i = 0; i < level.food.size(); i++) {
            if (this.intersects(level.food.get(i))) {
                level.food.remove(i);
                Game.score += 10;
                break;
            }
        }

        for (int i = 0; i < Game.level.enemies.size(); i++) {
            if (this.intersects(Game.level.enemies.get(i))) {
                Game.STATE = Game.PAUSE;
                return;
            }
        }

        if (level.food.size() == 0) {
            Game.STATE = Game.PAUSE;
            return;
        }

        time++;
        if (time == targetTime) {
            time = 0;
            imageIndex++;
        }
    }

    private boolean canMove(int nextX, int nextY) {
        Rectangle bounds = new Rectangle(nextX, nextY, width, height);
        Level level = Game.level;

        for (int tileX = 0; tileX < level.tiles.length; tileX++) {
            for (int tileY = 0; tileY < level.tiles[0].length; tileY++) {
                if (level.tiles[tileX][tileY] != null) {
                    if (bounds.intersects(level.tiles[tileX][tileY])) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public void render(Graphics g) {
        BufferedImage playerSprite;

        if (lastDir == 1) {
            playerSprite = Texture.player[imageIndex % 2];
            g.drawImage(playerSprite, x, y, width, height, null);
        } else if (lastDir == 2) {
            playerSprite = Texture.player1[imageIndex % 2];
            g.drawImage(playerSprite, x, y, width, height, null);
        } else if (lastDir == 3) {
            playerSprite = Texture.player2[imageIndex % 2];
            g.drawImage(playerSprite, x, y, width, height, null);
        } else if (lastDir == 4) {
            playerSprite = Texture.player3[imageIndex % 2];
            g.drawImage(playerSprite, x, y, width, height, null);
        }
    }
}
