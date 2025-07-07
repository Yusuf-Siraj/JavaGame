package game.collectibles;

import city.cs.engine.DynamicBody;
import city.cs.engine.Shape;
import city.cs.engine.SolidFixture;
import city.cs.engine.World;
import city.cs.engine.CircleShape;
import city.cs.engine.BodyImage;
import game.characters.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

import java.util.Objects;

/**
 * Represents a health collectible in the game.
 * When collected by the player, it restores health and grants temporary invincibility.
 */
public class HealthCollectible extends DynamicBody {
    /**
     * The image path for the collectible.
     */
    private static final String IMAGE_PATH = "/game/resources/gem/gemRed.png";
    /**
     * The sound effect for collecting the health item.
     */
    private static Clip popcartClip;

    static {
        preloadPopCartSound();
    }

    /**
     * Constructs a HealthCollectible in the specified world.
     * Sets up the shape, image, and collision listener for the collectible.
     *
     * @param world The physics world in which the collectible exists.
     */
    public HealthCollectible(World world) {
        super(world);

        // Define the shape of the collectible
        Shape shape = new CircleShape(0.5f); // Adjust size as needed

        // Attach the shape using a SolidFixture
        new SolidFixture(this, shape);

        // Add the gemRed image
        BodyImage gemImage = new BodyImage(Objects.requireNonNull(getClass().getResource(IMAGE_PATH)), 3.0f);
        addImage(gemImage);

        // Add collision listener
        this.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Player player) {
                // Recover health only if below the maximum
                if (player.getHealth() < 6) {
                    player.setHealth(player.getHealth() + 1);
                }

                // Grant temporary invincibility
                player.setInvincible(true);

                // Remove invincibility after 5 seconds
                new javax.swing.Timer(10000, evt -> player.setInvincible(false)).start();

                // Play the popcart sound
                playPopCartSound();

                // Destroy the collectible
                destroy();
            }
        });

    }


    /**
     * Preloads the popcart sound effect.
     */
    private static void preloadPopCartSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(HealthCollectible.class.getResource("/game/resources/sound/popcart.wav")));
            popcartClip = AudioSystem.getClip();
            popcartClip.open(audioInputStream);
        } catch (Exception e) {
            System.err.println("Error preloading popcart sound: " + e.getMessage());
        }
    }

    /**
     * Plays the popcart sound effect.
     */
    private void playPopCartSound() {
        if (popcartClip != null) {
            popcartClip.setFramePosition(0); // Reset the clip to the beginning
            FloatControl volumeControl = (FloatControl) popcartClip.getControl(FloatControl.Type.MASTER_GAIN);
            volumeControl.setValue(6.0f); // Increase volume (value in decibels, adjust as needed)
            popcartClip.start();
        }
    }


}