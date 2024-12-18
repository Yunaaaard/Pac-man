import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Tile extends Rectangle {
    private BufferedImage tileTexture;

    public Tile(int x, int y, BufferedImage tileTexture) {
        setBounds(x, y, 32, 32);
        this.tileTexture = tileTexture;
    }

    public void render(Graphics g) {
        g.drawImage(tileTexture, x, y, width, height, null);
    }
}
