package game.levelLoader;

import game.characters.Player;
import city.cs.engine.UserView;
import city.cs.engine.World;
import game.hud.CoinsCollected;
import game.hud.HealthBar;
import game.hud.Timer;
import org.jbox2d.common.Vec2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import game.controls.BackgroundMusicManager;

/**
 * Represents the view for the game world.
 * Handles rendering of the background, foreground, HUD elements, and pause state.
 * Also manages the camera and temporary messages.
 */
public class GameView extends UserView {

    /**
     * The health bar displayed on the screen.
     */
    private final HealthBar healthBar;
    /**
     * The coins collected by the player displayed on the screen.
     */
    private final CoinsCollected coinsCollected;
    /**
     * The timer displayed on the end screen.
     */
    private final Timer timer;// Timer instance
    /**
     * The player character whose position is tracked by the camera.
     */
    private Player player;
    /**
     * The background image displayed in the game view.
     */
    private BufferedImage backgroundImage; // Background image
    /**
     * Indicates whether the game is paused.
     */
    private boolean paused = false;
    /**
     * The message to be displayed temporarily on the screen.
     */
    private String temporaryMessage;// Field to hold the message
    /**
     * The time when the temporary message should disappear.
     */
    private long messageEndTime;

    /**
     * Constructs a new game view for the specified world and player.
     * Initializes HUD elements and loads the background image.
     *
     * @param world  The game world to be rendered.
     * @param width  The width of the view.
     * @param height The height of the view.
     * @param player The player character to track.
     */
    public GameView(World world, int width, int height, Player player) {
        super(world, width, height);
        this.player = player;
        this.healthBar = new HealthBar(world, player, width, height);
        this.coinsCollected = new CoinsCollected(world, player, width, height);
        this.timer = new Timer(); // Initialize the timer


        // Load the background image
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource("/game/resources/background/level2_background.png")));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
        }
    }

    /**
     * Loads a new background image for the game view.
     *
     * @param imagePath The path to the background image file.
     */
    public void loadBackgroundImage(String imagePath) {
        try {
            backgroundImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(imagePath)));
        } catch (IOException e) {
            System.err.println("Error loading background image: " + e.getMessage());
            backgroundImage = null; // Fallback to no image
        }
    }

    /**
     * Renders the background of the game view.
     *
     * @param g The graphics context used for rendering.
     */
    @Override
    protected void paintBackground(Graphics2D g) {
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.CYAN); // Fallback sky color if image fails to load
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * Renders the foreground elements, including HUD and pause state.
     *
     * @param g The graphics context used for rendering.
     */
    @Override
    protected void paintForeground(Graphics2D g) {
        super.paintForeground(g);
        healthBar.paintForeground(g);
        coinsCollected.paintForeground(g);
        timer.paintForeground(g, getWidth()); // Render the timer


        if (paused) {
            g.setColor(new Color(0, 0, 0, 150)); // Semi-transparent overlay
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("Paused", getWidth() / 2 - 60, getHeight() / 2);
        }
        if (temporaryMessage != null && System.currentTimeMillis() < messageEndTime) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 18));
            g.drawString(temporaryMessage, 10, getHeight() / 2); // Display on the left in the center
            temporaryMessage = null; // Clear the message after the duration
        }
    }

    /**
     * Updates the camera position to follow the player.
     * The camera is centered on the player's X position.
     */
    public void updateCamera() {
        this.setCentre(new Vec2(player.getPosition().x, 0));
    }

    /**
     * Retrieves the timer HUD element.
     *
     * @return The timer instance.
     */
    public Timer getTimer() {
        return timer; // Provide access to the timer
    }

    /**
     * Sets the world to be rendered by the view.
     *
     * @param world The new game world.
     */
    public void setWorld(World world) {
        this.setWorld(world);
    }

    /**
     * Sets the player character to be tracked by the camera.
     *
     * @param player The new player character.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }


    /**
     * Toggles the pause state of the game.
     * Stops or resumes the physics world and background music.
     */
    public void togglePause() {
        paused = !paused; // Toggle pause state
        if (paused) {
            getWorld().stop(); // Stop the physics world
            BackgroundMusicManager.stopMusic(); // Pause the music
        } else {
            getWorld().start(); // Resume the physics world
            BackgroundMusicManager.startMusic(); // Resume the music
        }
    }
}
