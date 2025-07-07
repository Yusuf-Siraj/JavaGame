package game.controls;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.util.Objects;

/**
 * Manages the background music for the game.
 * Provides functionality to preload, start, and stop the background music.
 */
public class BackgroundMusicManager {
    /**
     * The clip used to play the background music.
     */
    private static Clip backgroundClip;
    static {
        preloadBackgroundMusic();
    }

    /**
     * Preloads the background music from the specified resource.
     * Initializes the audio clip and sets it to loop continuously.
     * Handles any exceptions that occur during the loading process.
     */
    private static void preloadBackgroundMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(BackgroundMusicManager.class.getResource("/game/resources/sound/ethbackground.wav")));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioInputStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
        } catch (Exception e) {
            System.err.println("Error loading background music: " + e.getMessage());
        }
    }

    /**
     * Starts the background music if it is not already playing.
     * Resets the clip to the beginning before starting it.
     */
    public static void startMusic() {
        if (backgroundClip != null && !backgroundClip.isRunning()) {
            backgroundClip.setFramePosition(0); // Reset to the beginning
            backgroundClip.start();
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    /**
     * Stops the background music if it is currently playing.
     */
    public static void stopMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }
}