import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Enemy extends Rectangle {
    private int random = 0;
    private int smart = 1;
    private int findPath = 2;
    private int state = smart;
    private int right = 0, left = 1, up = 2, down = 3;
    private int dir = -1;
    private int lastDir = -1;
    private int time = 0;
    private int targetTime = 60 * 4;
    private int speed = 2;

    public int imageIndex = 0;
    public Random randomDir;

    public Enemy(int x, int y){
        randomDir = new Random();
        setBounds(x, y, 32, 32);
        dir = randomDir.nextInt(4);
    }
    public void render(Graphics g) {
        BufferedImage ghostSprite;

        if (lastDir == right) {
            ghostSprite = Texture.ghost[imageIndex % 2];
            g.drawImage(ghostSprite, x, y, width, height, null);
        } else if (lastDir == left) {
            ghostSprite = Texture.ghost1[imageIndex % 2];
            g.drawImage(ghostSprite, x, y, width, height, null);
        } else if (lastDir == up) {
            ghostSprite = Texture.ghost2[imageIndex % 2];
            g.drawImage(ghostSprite, x, y, width, height, null);
        } else if (lastDir == down) {
            ghostSprite = Texture.ghost3[imageIndex % 2];
            g.drawImage(ghostSprite, x, y, width, height, null);
        }
    }
    public void tick(){
        if(state == random){
            if(dir == right){
                if(canMove(x + speed, y)){
                    x += speed;
                    lastDir = right;
                }else{
                    dir = randomDir.nextInt(4);
                }
            }else if(dir == left){
                if(canMove(x - speed, y)){
                    x -= speed;
                    lastDir = left;
                }else{
                    dir = randomDir.nextInt(4);
                }
            }else if(dir == up){
                if(canMove(x, y - speed)){
                    y -= speed;
                    lastDir = up;
                }else{
                    dir = randomDir.nextInt(4);
                }
            }else if(dir == down){
                if(canMove(x, y + speed)){
                    y += speed;
                    lastDir = down;
                }else{
                    dir = randomDir.nextInt(4);
                }
            }
            time++;
            if (time == targetTime) {
                state = smart;
                time = 0;
            }
        }else if(state == smart){
            boolean move = false;

            if (x < Game.player.x) {
                if (canMove(x + speed, y)) {
                    x += speed;
                    move = true;
                    lastDir = right;
                }
            }
            if (x > Game.player.x) {
                if (canMove(x - speed, y)) {
                    x -= speed;
                    move = true;
                    lastDir = left;
                }
            }
            if (y < Game.player.y) {
                if (canMove(x, y + speed)) {
                    y += speed;
                    move = true;
                    lastDir = down;
                }
            }
            if (y > Game.player.y) {
                if (canMove(x, y - speed)) {
                    y -= speed;
                    move = true;
                    lastDir = up;
                }
            }

            if(x == Game.player.x && y == Game.player.y){
                move = true;
            }

            if(!move){
                state = findPath;
            }

        } else if (state == findPath) {
            if (lastDir == right) {
                if (canMove(x + speed, y)) {
                    x += speed;
                } else {
                    state = smart;
                }
            } else if (lastDir == left) {
                if (canMove(x - speed, y)) {
                    x -= speed;
                } else {
                    state = smart;
                }
            } else if (lastDir == up) {
                if (canMove(x, y - speed)) {
                    y -= speed;
                } else {
                    state = smart;
                }
            } else if (lastDir == down) {
                if (canMove(x, y + speed)) {
                    y += speed;
                } else {
                    state = smart;
                }
            }
        }
    }

    private boolean canMove(int nextX, int nextY){
        Rectangle bounds = new Rectangle(nextX,nextY,width,height);
        Level level = Game.level;

        for (int tileX = 0; tileX < level.tiles.length; tileX++) {
            for (int tileY = 0; tileY < level.tiles[0].length; tileY++) {
                if(level.tiles[tileX][tileY] != null){
                    if(bounds.intersects(level.tiles[tileX][tileY])){
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
