package game.hud;

import java.awt.*;

/**
 * Represents a timer used to track elapsed time in the game.
 * Provides functionality to start, stop, and display the timer.
 */
public class Timer {
    private long startTime;
    private boolean running;

    /**
     * Constructs a new Timer instance.
     * Initializes the timer to a stopped state.
     */
    public Timer() {
        this.running = false;
    }

    /**
     * Starts the timer and records the start time.
     */
    // Start the timer
    public void start() {
        this.startTime = System.currentTimeMillis();
        this.running = true;
    }

    /**
     * Stops the timer.
     */
    // Stop the timer
    public void stop() {
        this.running = false;
    }

    /**
     * Retrieves the elapsed time in milliseconds since the timer started.
     *
     * @return The elapsed time in milliseconds, or 0 if the timer is not running.
     */
    // Get the elapsed time in milliseconds
    public long getElapsedTime() {
        if (running) {
            return System.currentTimeMillis() - startTime;
        }
        return 0;
    }

    /**
     * Formats the elapsed time as MM:SS.
     *
     * @return A string representing the formatted elapsed time.
     */
    // Format the elapsed time as MM:SS
    public String getFormattedTime() {
        long elapsedMillis = getElapsedTime();
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Renders the timer on the screen.
     *
     * @param g           The graphics context used for rendering.
     * @param screenWidth The width of the screen, used to center the timer.
     */
    // Render the timer on the screen
    public void paintForeground(Graphics2D g, int screenWidth) {
        if (running) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            String time = getFormattedTime();
            int stringWidth = g.getFontMetrics().stringWidth(time);
            g.drawString(time, (screenWidth - stringWidth) / 2, 30); // Top center
        }
    }
}