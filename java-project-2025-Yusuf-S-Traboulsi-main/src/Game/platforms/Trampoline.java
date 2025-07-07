package game.platforms;

import city.cs.engine.*;
import game.characters.Player;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A trampoline platform that launches the player upward when they collide with it.
 * Plays a jump sound effect upon contact.
 */
public class Trampoline extends StaticBody {

    /**
     * The shape of the trampoline platform.
     */
    private static final Shape trampolineShape = new BoxShape(2, 0.5f);
    /**
     * The sound effect for the jump action.
     */
    private static final int CLIP_POOL_SIZE = 5; // Number of preloaded clips
    /**
     * The sound effect for the jump action.
     */
    private static final List<Clip> jumpClipPool = new ArrayList<>();
    /**
     * The index of the current clip in the pool.
     */
    private static int currentClipIndex = 0;

    static {
        preloadJumpSound(); // Preload the sound pool when the class is loaded
    }

    /**
     * Constructs a trampoline platform in the specified world.
     * The trampoline has a high restitution value for bounciness and plays a sound on contact.
     *
     * @param world The physics world in which the trampoline exists.
     */
    public Trampoline(World world) {
        super(world, trampolineShape);
        SolidFixture fixture = new SolidFixture(this, trampolineShape);
        fixture.setRestitution(1.2f); // Set a high restitution value for bounciness

        // Add a collision listener to play the sound on contact
        this.addCollisionListener(e -> {
            if (e.getOtherBody() instanceof Player) {
                playJumpSound();
            }
        });
    }

    /**
     * Preloads the jump sound clips into a pool for efficient playback.
     * This method is called when the class is loaded.
     */
    private static void preloadJumpSound() {
        try {
            for (int i = 0; i < CLIP_POOL_SIZE; i++) {
                AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(Trampoline.class.getResource("/game/resources/sound/jump.wav")));
                Clip clip = AudioSystem.getClip();
                clip.open(audioInputStream);
                jumpClipPool.add(clip);
            }
        } catch (Exception e) {
            System.err.println("Error preloading jump sound: " + e.getMessage());
        }
    }

    /**
     * Plays a jump sound effect from the preloaded pool.
     * Cycles through the pool to avoid overlapping sounds.
     */
    private void playJumpSound() {
        if (!jumpClipPool.isEmpty()) {
            Clip clip = jumpClipPool.get(currentClipIndex);
            clip.setFramePosition(0); // Reset the clip to the beginning
            clip.start();
            currentClipIndex = (currentClipIndex + 1) % CLIP_POOL_SIZE; // Move to the next clip in the pool
        }
    }
}