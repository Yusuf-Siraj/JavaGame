package game.controls;

import game.characters.Player;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Handles player input for controlling the character's movement and actions.
 * Listens for key events and updates the player's state accordingly.
 */

public class PlayerController extends KeyAdapter {
    /**
     * The player character being controlled.
     */
    private final Player player;

    /**
     * Constructs a new PlayerController for the specified player.
     *
     * @param player The player character to control.
     */
    public PlayerController(Player player) {
        this.player = player;
    }

    /**
     * Handles key pressed events to control the player's movement and actions.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT -> player.moveLeft();
            case KeyEvent.VK_RIGHT -> player.moveRight();
            case KeyEvent.VK_SPACE -> player.jump();
        }
    }

    /**
     * Handles key released events to stop the player's movement.
     *
     * @param e The key event triggered by the user.
     */
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT && player.getIsMovingLeft()) {
            player.stopMoving();
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && player.getIsMovingRight()) {
            player.stopMoving();
        }
    }
}

