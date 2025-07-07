package game.levelLoader;

import city.cs.engine.*;
import game.characters.Player;
import game.main.Game;

import java.util.Objects;

/**
 * Represents a door in the game world.
 * The door transitions the player to the next level upon collision.
 */
public class Door extends StaticBody implements CollisionListener {
    /**
     * The shape of the door.
     */
    private static final String IMAGE_PATH = "/game/resources/Door/WoodDoor.png"; // Path to the image

    private final Game game;

    /**
     * Constructs a door in the specified world.
     * Adds an image to the door and sets up a collision listener.
     *
     * @param world The physics world in which the door exists.
     * @param game  The main game instance.
     */
    public Door(World world, Game game) {
        super(world, new BoxShape(1, 2)); // Example door shape
        this.game = game;

        // Add the image to the door
        BodyImage doorImage = new BodyImage(Objects.requireNonNull(Door.class.getResource(IMAGE_PATH)), 4); // Adjust the height (4) as needed
        addImage(doorImage);

        addCollisionListener(this);
    }

    /**
     * Handles the collision event with the player.
     * Transitions to the next level when the player collides with the door.
     *
     * @param e The collision event.
     */
    @Override
    public void collide(CollisionEvent e) {
        if (e.getOtherBody() instanceof Player) {
            game.loadNextLevel(); // Trigger level transition
        }
    }
}