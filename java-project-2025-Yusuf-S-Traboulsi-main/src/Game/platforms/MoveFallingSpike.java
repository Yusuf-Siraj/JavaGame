package game.platforms;

import city.cs.engine.*;
import game.characters.Player;

/**
 * A spike that falls when the player is detected and inflicts damage on contact.
 * The spike moves vertically between a start and end position at a specified speed.
 */

public class MoveFallingSpike extends Elevator {

    /**
     * The shape of the spike.
     */
    private static final Shape spikeShape = new PolygonShape(0f, 0.5f, -0.5f, -0.5f, 0.5f, -0.5f // Triangle shape
    );

    /**
     * Constructs a moving falling spike that moves between two points and damages the player on contact.
     *
     * @param world  The physics world in which the spike exists.
     * @param x      The X-coordinate of the spike.
     * @param startY The starting Y-coordinate of the spike's movement.
     * @param endY   The ending Y-coordinate of the spike's movement.
     * @param speed  The speed of the spike's vertical movement.
     */
    public MoveFallingSpike(World world, float x, float startY, float endY, float speed) {
        super(world, x, startY, endY, speed);

        // Replace the default shape with the triangular shape
        new SolidFixture(this, spikeShape);

        // Add a sensor to detect collisions
        Sensor sensor = new Sensor(this, spikeShape);
        sensor.addSensorListener(new SensorListener() {

            /**
             * Handles the collision with the player and applies damage.
             *
             * @param e The sensor event triggered by the player.
             */
            @Override
            public void beginContact(SensorEvent e) {
                if (e.getContactBody() instanceof Player player) {
                    if (!player.isInvincible()) { // Check if the player is not invincible
                        player.decreaseHealth(1); // Apply damage
                    }
                }
            }

            /**
             * Handles the end of contact with the player.
             *
             * @param e The sensor event triggered by the player.
             */
            @Override
            public void endContact(SensorEvent e) {
                // No action needed on end contact
            }
        });
    }
}