package game.collectibles;

import city.cs.engine.*;

import java.util.Objects;

import game.characters.Player;

/**
 * Represents a coin collectible in the game.
 * When collected by the player, it increases the player's coin count.
 */
public class Coin extends StaticBody {
    /**
     * The image path for the collectible.
     */
    private static final Shape collectibleShape = new CircleShape(1.5f);
    /**
     * The sound effect for collecting the coin.
     */
    BodyImage coinImage;

    /**
     * Constructs a Coin in the specified world.
     * Sets up the shape, image, and collision listener for the collectible.
     *
     * @param world The physics world in which the collectible exists.
     */
    public Coin(World world) {
        super(world, collectibleShape);
        SolidFixture fixture = new SolidFixture(this, collectibleShape);
        fixture.setRestitution(0.5f); // Optional: make the collectible bouncy

        loadImage();
    }

    /**
     * Handles the collision event with the player.
     * Increases the player's coin count and destroys the collectible.
     */
    private void loadImage() {
        coinImage = new BodyImage(Objects.requireNonNull(Coin.class.getResource("/game/resources/hud/coin/hud_coins.png")), 1.5f);
        addImage(coinImage);
    }
}