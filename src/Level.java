import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Level {

    public int width;
    public int height;
    public Tile[][] tiles;
    public List<Food> food;
    public List<Enemy> enemies;
    private int foodTextureX = 0;
    private int foodTextureY = 48;
    private int tileTextureX = 0;
    private int tileTextureY = 32;

    public Level(String path) {
        food = new ArrayList<>();
        enemies = new ArrayList<>();
        loadMap(path);
    }

    public void loadMap(String path) {
        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            this.width = map.getWidth();
            this.height = map.getHeight();
            int[] pixels = new int[width * height];
            tiles = new Tile[width][height];
            map.getRGB(0, 0, width, height, pixels, 0, width);

            BufferedImage foodTexture = Texture.getSprite(foodTextureX, foodTextureY);
            BufferedImage tileTexture = Texture.getSprite(tileTextureX, tileTextureY);

            for (int tileX = 0; tileX < width; tileX++) {
                for (int tileY = 0; tileY < height; tileY++) {
                    int val = pixels[tileX + (tileY * width)];

                    if (val == 0xFF000000) {
                        // Wall
                        tiles[tileX][tileY] = new Tile(tileX * 32, tileY * 32, tileTexture);
                    } else if (val == 0xFF0000FF) {
                        // Player
                        Game.player.x = tileX * 32;
                        Game.player.y = tileY * 32;
                    } else if (val == 0xFFFF0000) {
                        // Enemy
                        enemies.add(new Enemy(tileX * 32, tileY * 32));
                    } else {
                        // Food
                        food.add(new Food(tileX * 32, tileY * 32, foodTexture));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void tick() {
        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).tick();
        }
    }

    public void render(Graphics g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (tiles[i][j] != null) {
                    tiles[i][j].render(g);
                }
            }
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).render(g);
        }

        for (int i = 0; i < enemies.size(); i++) {
            enemies.get(i).render(g);
        }
    }
}
