package game.characters.enemies;

import city.cs.engine.*;
import game.characters.Player;
import game.main.Game;
import org.jbox2d.common.Vec2;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a fly enemy in the game.
 * The fly has idle and walking animations and plays a sound when destroyed.
 */

public class Fly extends Enemy {
    /**
     * The speed at which the fly moves.
     */
    private static final float FLY_SPEED = 5;
    /**
     * The range within which the fly detects the player.
     */
    private static final float FLY_DETECTION_RANGE = 8;
    /**
     * The sound effect for the fly enemy.
     */
    private static final int CLIP_POOL_SIZE = 5; // Number of preloaded clips
    /**
     * The sound effect for the fly enemy.
     */
    private static final List<Clip> splatClipPool = new ArrayList<>();
    /**
     * The index of the current clip being played.
     */
    private static int currentClipIndex = 0;

    /**
     * The sound effect for the fly enemy.
     */
    static {
        preloadSplatSound(); // Preload the sound pool when the class is loaded
    }

    /**
     * The sound effect for the fly enemy.
     */
    private final Player player;
    /**
     * The sound effect for the fly enemy.
     */
    private float leftLimit, rightLimit, startY;

    /**
     * Constructs a fly enemy in the specified world.
     * Sets up the shape, image, and collision listener for the enemy.
     *
     * @param world      The physics world in which the enemy exists.
     * @param startX     The starting x-coordinate of the enemy's position.
     * @param startY     The starting y-coordinate of the enemy's position.
     * @param leftLimit  The left limit of the fly's patrol area.
     * @param rightLimit The right limit of the fly's patrol area.
     * @param player     The player character in the game.
     * @param game       The main game instance.
     */
    public Fly(World world, float startX, float startY, float leftLimit, float rightLimit, Player player, Game game) {
        super(world, startX, startY, player, game);
        this.leftLimit = leftLimit;
        this.rightLimit = rightLimit;
        this.startY = startY;
        this.player = player;
        setGravityScale(0); // Disable gravity for flying
    }

    /**
     * Preloads the splat sound effect.
     * This is done to avoid delays when playing the sound during gameplay.
     */
    private static void preloadSplatSound() {
        try {
            for (int i = 0; i < CLIP_POOL_SIZE; i++) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                        Objects.requireNonNull(Fly.class.getResource("/game/resources/sound/splat.wav")));
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
     * Sets the shape of the fly enemy.
     */
    @Override
    public void setImages() {
        idleImage = new BodyImage(Objects.requireNonNull(
                getClass().getResource("/game/resources/enemies/fly/Fly1.png"), "Idle image not found"), 1.5f);

        for (int i = 0; i < 2; i++) {
            walkRightImages[i] = new BodyImage(Objects.requireNonNull(
                    getClass().getResource("/game/resources/enemies/fly/FlyR" + (i + 1) + ".png"), "Right walk image not found"), 1.5f);
        }

        walkLeftImages[0] = new BodyImage(Objects.requireNonNull(
                getClass().getResource("/game/resources/enemies/fly/Fly1.png"), "Left walk image not found"), 1.5f);

        walkLeftImages[1] = new BodyImage(Objects.requireNonNull(
                getClass().getResource("/game/resources/enemies/fly/Fly2.png"), "Left walk image not found"), 1.5f);
    }

    /**
     * Sets the initial image of the fly enemy.
     */
    @Override
    public void preStep(StepEvent e) {
        Vec2 currentVel = getLinearVelocity();
        setLinearVelocity(new Vec2(currentVel.x, 0)); // Keep Y velocity at 0
        setPosition(new Vec2(getPosition().x, startY)); // Maintain Y position

        float distanceToPlayer = Math.abs(getPosition().x - player.getPosition().x);

        if (distanceToPlayer < FLY_DETECTION_RANGE) {
            followPlayer();
        } else {
            patrol();
        }
    }

    /**
     * Sets the enemey on a patrol path.
     */
    private void patrol() {
        if (getPosition().x > rightLimit) {
            startWalking(-FLY_SPEED);
        } else if (getPosition().x < leftLimit) {
            startWalking(FLY_SPEED);
        }
    }

    /**
     * Sets the fly enemy to follow the player.
     */
    private void followPlayer() {
        float playerX = player.getPosition().x;
        float flyX = getPosition().x;

        if (playerX > flyX) {
            startWalking(FLY_SPEED);
        } else {
            startWalking(-FLY_SPEED);
        }
    }

    /**
     * Handles the collision event with the player and plays the sound effect.
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