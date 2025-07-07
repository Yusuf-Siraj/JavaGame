package game.controls;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import game.levelLoader.GameView;

/**
 * Handles the pause functionality in the game.
 * Listens for key events to toggle the pause state of the game.
 */
public class PauseControl implements KeyListener {
    /**
     * The game view associated with this control.
     */
    private final GameView gameView;

    /**
     * Constructs a new PauseControl for the specified game view.
     *
     * @param gameView The game view to control.
     */
    public PauseControl(GameView gameView) {
        this.gameView = gameView;
    }

    /**
     * Handles key pressed events to toggle the pause state.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_P) { // Press 'P' to toggle pause
            gameView.togglePause();
        }
    }

    /**
     * Handles key released events. No action needed for pause control.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        // No action needed
    }

    /**
     * Handles key typed events. No action needed for pause control.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // No action needed
    }
}