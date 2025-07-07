package game.hud;

import city.cs.engine.*;
import game.characters.Player;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * Represents a health bar HUD element that displays the player's health.
 * The health bar uses heart icons to visually represent full, half, and empty health states.
 */
public class HealthBar extends UserView {
    /**
     * The player whose health is displayed.
     */
    private final Player player;
    /**
     * The image representing a full heart.
     */
    private BufferedImage fullHeart;
    /**
     * The image representing a half heart.
     */
    private BufferedImage halfHeart;
    /**
     * The image representing an empty heart.
     */
    private BufferedImage emptyHeart;

    /**
     * Constructs a new HealthBar instance.
     * Loads heart images and initializes the health bar for the specified player.
     *
     * @param world  The physics world in which the health bar exists.
     * @param player The player whose health is displayed.
     * @param width  The width of the health bar view.
     * @param height The height of the health bar view.
     */
    public HealthBar(World world, Player player, int width, int height) {
        super(world, width, height);
        this.player = player;

        // Load heart images
        try {
            fullHeart = ImageIO.read(Objects.requireNonNull(HealthBar.class.getResource("/game/resources/hud/health/hud_heartFull.png")));
            halfHeart = ImageIO.read(Objects.requireNonNull(getClass().getResource("/game/resources/hud/health/hud_heartHalf.png")));
            emptyHeart = ImageIO.read(Objects.requireNonNull(getClass().getResource("/game/resources/hud/health/hud_heartEmpty.png")));
        } catch (IOException e) {
            System.err.println("Error loading heart images: " + e.getMessage());
        }
    }

    /**
     * Renders the health bar on the screen.
     * Displays full, half, or empty hearts based on the player's current health.
     *
     * @param g The graphics context used for rendering.
     */
    @Override
    public void paintForeground(Graphics2D g) {
        int health = player.getHealth(); // Retrieve the player's current health

        // Draw 3 hearts, tracking full/half/empty
        for (int i = 0; i < 3; i++) {
            int heartX = 20 + (i * 40);

            if (health >= (i + 1) * 2) {
                // Full heart
                g.drawImage(fullHeart, heartX, 20, 32, 32, null);
            } else if (health == (i * 2) + 1) {
                // Half heart
                g.drawImage(halfHeart, heartX, 20, 32, 32, null);
            } else {
                // Empty heart
                g.drawImage(emptyHeart, heartX, 20, 32, 32, null);
            }
        }
    }
}