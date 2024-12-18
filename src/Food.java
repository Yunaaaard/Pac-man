import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Food extends Rectangle {
    private BufferedImage foodTexture;

    public Food(int x, int y, BufferedImage foodTexture) {
        setBounds(x + 10, y + 8, 8, 8);
        this.foodTexture = foodTexture;
    }

    public void render(Graphics g) {
        g.drawImage(foodTexture, x, y, width, height, null);
    }
}
