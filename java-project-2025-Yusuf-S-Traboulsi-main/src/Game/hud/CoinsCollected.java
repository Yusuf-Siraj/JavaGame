package game.hud;

import city.cs.engine.UserView;
import city.cs.engine.World;
import game.characters.Player;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a HUD element that displays the number of coins collected by the player.
 * Updates dynamically as the player collects coins.
 */
public class CoinsCollected extends UserView {
    /**
     * The player whose coins are being tracked.
     */
    private final Player player;
    /**
     * A map to store images of numbers 0-9 for displaying the coin count.
     */
    private final Map<Character, BufferedImage> numberImages = new HashMap<>();
    /**
     * The image representing the coin icon.
     */
    private BufferedImage coinImage;
    /**
     * The image representing the 'x' symbol.
     */
    private BufferedImage xSymbol;


    /**
     * Constructs a new CoinsCollected instance.
     * Loads images for the coin icon, 'x' symbol, and number images.
     *
     * @param w      The physics world in which the coins are collected.
     * @param player The player whose coins are being tracked.
     * @param width  The width of the view.
     * @param height The height of the view.
     */
    public CoinsCollected(World w, Player player, int width, int height) {
        super(w, width, height);
        this.player = player;
        loadImages();
    }

    /**
     * Loads images for the coin icon, 'x' symbol, and number images.
     * Handles potential IO exceptions during image loading.
     */
    private void loadImages() {
        try {
            coinImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/game/resources/hud/coin/hud_coins.png")));
            xSymbol = ImageIO.read(Objects.requireNonNull(getClass().getResource("/game/resources/hud/numbers/hud_x.png")));

            // Load numbers 0-9 into the map for easier access
            for (int i = 0; i <= 9; i++) {
                numberImages.put(Character.forDigit(i, 10),
                        ImageIO.read(Objects.requireNonNull(getClass().getResource("/game/resources/hud/numbers/hud_" + i + ".png"))));
            }
        } catch (IOException e) {
            System.err.println("Error loading HUD images: " + e.getMessage());
        }
    }

    /**
     * Paints the foreground elements of the HUD.
     * Displays the coin icon, 'x' symbol, and the number of coins collected.
     *
     * @param g The graphics context used for rendering.
     */
    @Override
    public void paintForeground(Graphics2D g) {
        int coinsCollected = player.getCoinsCollected();
        String coinText = String.valueOf(coinsCollected);

        // Draw coin icon
        if (coinImage != null) {
            g.drawImage(coinImage, 620, 20, 40, 40, null);  // Coin Icon Size and Position
        }

        // Draw 'x' symbol
        if (xSymbol != null) {
            g.drawImage(xSymbol, 665, 30, 20, 20, null);  // 'x' Symbol Size and Position
        }

        // Draw each digit of the coin count
        int xOffset = 690; // Position where numbers will be drawn
        for (char digit : coinText.toCharArray()) {
            BufferedImage digitImage = numberImages.get(digit);
            if (digitImage != null) {
                g.drawImage(digitImage, xOffset, 25, 20, 30, null);
                xOffset += 20; // Shift position for the next digit
            }
        }
    }
}
