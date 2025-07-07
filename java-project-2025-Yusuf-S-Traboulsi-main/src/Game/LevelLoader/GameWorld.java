package game.levelLoader;

import game.characters.Player;
import game.main.Game;
import city.cs.engine.World;

/**
 * Represents the game world where all game objects and interactions take place.
 * Manages the player, door, and background, and sets up the physics environment.
 */
public class GameWorld extends World {
    /**
     * The player character in the game world.
     */
    private Player player;
    /**
     * The main game instance associated with this world.
     */
    private Game game;
    /**
     * The door that signifies the level's exit.
     */
    private Door door;

    /**
     * Constructs a new game world with the specified game instance.
     * Initializes the player and sets the gravity for the world.
     *
     * @param game The main game instance.
     */
    public GameWorld(Game game) {
        super();
        this.game = game;

        // Set gravity
        setGravity(10);

        // Create player
        player = new Player(this, game);
    }

    /**
     * Retrieves the player character in the game world.
     *
     * @return The player character.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Retrieves the door in the game world.
     *
     * @return The door object.
     */
    public Door getDoor() {
        return door;
    }

    /**
     * Sets the door for the game world.
     *
     * @param door The door to be set.
     */
    public void setDoor(Door door) {
        this.door = door;
    }
}