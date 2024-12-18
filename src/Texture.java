import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class Texture {
    public static BufferedImage[] player, player1, player2, player3;
    public static BufferedImage[] ghost, ghost1, ghost2, ghost3;
    public static BufferedImage spritesheet;

    public Texture() {
        try {
            spritesheet = ImageIO.read(getClass().getResource("/sprites/spritesheet.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // PLAYER SPRITES

        player = new BufferedImage[2];
        player[0] = getSprite(0, 0);
        player[1] = getSprite(16, 0);

        player1 = new BufferedImage[2];
        player1[0] = getSprite(32, 0);
        player1[1] = getSprite(48, 0);

        player2 = new BufferedImage[2];
        player2[0] = getSprite(64, 0);
        player2[1] = getSprite(80, 0);

        player3 = new BufferedImage[2];
        player3[0] = getSprite(96, 0);
        player3[1] = getSprite(112, 0);

        // GHOST SPRITES

        ghost = new BufferedImage[1];
        ghost[0] = getSprite(16, 16);   // Turning left

        ghost1 = new BufferedImage[1];
        ghost1[0] = getSprite(0, 16);  // Turning right

        ghost2 = new BufferedImage[1];
        ghost2[0] = getSprite(32, 16);  // Turning up

        ghost3 = new BufferedImage[1];
        ghost3[0] = getSprite(48, 16);  // Turning down

    }

    public static BufferedImage getSprite(int imageX, int imageY) {
        return spritesheet.getSubimage(imageX, imageY, 16, 16);
    }
}
