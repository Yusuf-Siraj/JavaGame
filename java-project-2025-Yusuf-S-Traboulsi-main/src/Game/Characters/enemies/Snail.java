package game.characters.enemies;

import city.cs.engine.BodyImage;
import city.cs.engine.World;
import game.characters.Player;
import game.main.Game;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a Snail enemy in the game.
 * The Snail has idle and walking animations and plays a sound when destroyed.
 */

public class Snail extends Enemy {
    /**
     * The image path for the Snail enemy.
     */
    private static final int CLIP_POOL_SIZE = 5; // Number of preloaded clips
    /**
     * The sound effect for the Snail enemy.
     */
    private static final List<Clip> splatClipPool = new ArrayList<>();
    /**
     * The index of the current clip being played.
     */
    private static int currentClipIndex = 0;

    static {
        preloadSplatSound(); // Preload the sound pool when the class is loaded
    }

    /**
     * Constructs a Snail enemy in the specified world.
     * Sets up the shape, image, and collision listener for the enemy.
     *
     * @param world  The physics world in which the enemy exists.
     * @param x      The x-coordinate of the enemy's position.
     * @param y      The y-coordinate of the enemy's position.
     * @param player The player character in the game.
     * @param game   The main game instance.
     */
    public Snail(World world, float x, float y, Player player, Game game) {
        super(world, x, y, player, game);
    }

    /**
     * Preloads the splat sound effect into a pool of clips.
     * This is done to avoid delays when playing the sound during gameplay.
     */
    private static void preloadSplatSound() {
        try {
            for (int i = 0; i < CLIP_POOL_SIZE; i++) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                        Objects.requireNonNull(Snail.class.getResource("/game/resources/sound/splat.wav")));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);

                // Reduce the volume
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f); // Lower the volume by 10 decibels

                splatClipPool.add(clip);
            }
        } catch (Exception e) {
            System.err.println("Error preloading splat sound: " + e.getMessage());
        }
    }

    /**
     * Sets the shape of the Snail enemy.
     */
    @Override
    public void setImages() {
        idleImage = new BodyImage(Objects.requireNonNull(getClass().getResource("/game/resources/enemies/snail/idle/snailShell.png")), 1.5f);

        for (int i = 0; i < 2; i++) {
            walkRightImages[i] = new BodyImage(Objects.requireNonNull(getClass().getResource(
                    "/game/resources/enemies/snail/right/snailWalk" + (i + 1) + ".png")), 1.5f);

            walkLeftImages[i] = new BodyImage(Objects.requireNonNull(getClass().getResource(
                    "/game/resources/enemies/snail/left/snailWalk" + (i + 1) + ".png")), 1.5f);
        }
    }

    /**
     * Sets the initial image of the Snail enemy.
     */
    @Override
    public void destroy() {
        playSplatSound(); // Play the sound
        super.destroy();  // Call the parent class's destroy method
    }

    /**
     * Plays the splat sound effect from the preloaded pool.
     * This method cycles through the clips in the pool to avoid overlapping sounds.
     */
    private void playSplatSound() {
        if (!splatClipPool.isEmpty()) {
            Clip clip = splatClipPool.get(currentClipIndex);
            clip.setFramePosition(0); // Reset the clip to the beginning
            clip.start();
            currentClipIndex = (currentClipIndex + 1) % CLIP_POOL_SIZE; // Move to the next clip in the pool
        }
    }
}