package game.platforms;

import city.cs.engine.BoxShape;
import city.cs.engine.CollisionEvent;
import city.cs.engine.CollisionListener;
import city.cs.engine.DynamicBody;
import city.cs.engine.PolygonShape;
import city.cs.engine.Sensor;
import city.cs.engine.SensorEvent;
import city.cs.engine.SensorListener;
import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;
import game.characters.Player;

/**
 * A spike that remains stationary until the player moves underneath,
 * then drops straight down and deals 2 points of damage on collision.
 */
public class FallingSpike extends DynamicBody implements SensorListener, StepListener, CollisionListener {
    private static final PolygonShape spikeShape = new PolygonShape(
            0f, -1f,
            -0.5f, 0.5f,
            0.5f, 0.5f
    );

    /**
     * The sensor that detects the player.
     */
    private final Sensor trigger;

    /**
     * The player to inflict damage on.
     */
    private final Player player;

    /**
     * Indicates whether the spike has been activated.
     */
    private boolean activated = false;

    /**
     * @param world  the physics world
     * @param x      horizontal position of spike and trigger zone
     * @param y      vertical base position before falling
     * @param player reference to the player to inflict damage
     */
    public FallingSpike(World world, float x, float y, Player player) {
        super(world, spikeShape);
        this.player = player;

        // Place the spike at its starting position
        setPosition(new Vec2(x, y + 3));
        setGravityScale(0);  // initially inert

        // Sensor zone beneath spike to detect the player
        trigger = new Sensor(this, new BoxShape(1, 100, new Vec2(0, 0)));
        trigger.addSensorListener(this);

        // Listen for collisions with player
        addCollisionListener(this);

        // Constrain horizontal movement when falling
        world.addStepListener(this);
    }

    /**
     * Called when the player enters the sensor zone beneath the spike.
     * Activates the spike to start falling.
     *
     * @param e The sensor event triggered by the player.
     */
    @Override
    public void beginContact(SensorEvent e) {
        // Activate the spike when player enters trigger
        if (!activated && e.getContactBody() == player) {
            activated = true;
            setGravityScale(5);    // enable falling
            setLinearVelocity(new Vec2(0, 0)); // drop vertically
        }
    }

    /**
     * Called when the player leaves the sensor zone beneath the spike.
     * No action is performed in this implementation.
     *
     * @param e The sensor event triggered by the player.
     */
    @Override
    public void endContact(SensorEvent e) {
        // no action
    }

    /**
     * Locks the horizontal velocity of the spike during its fall.
     * Ensures the spike only moves vertically.
     *
     * @param e The step event containing the time step information.
     */
    @Override
    public void preStep(StepEvent e) {
        if (activated) {
            // lock horizontal velocity
            Vec2 vel = getLinearVelocity();
            setLinearVelocity(new Vec2(0, vel.y));
        }
    }

    /**
     * Performs any necessary actions after each physics step.
     * This implementation does not require any post-step actions.
     *
     * @param e The step event containing the time step information.
     */
    @Override
    public void postStep(StepEvent e) {
        // no additional logic
    }

    /**
     * Handles the collision between the falling spike and the player.
     * Inflicts damage to the player and destroys the spike after contact.
     *
     * @param e The collision event triggered by the spike and the player.
     */
    @Override
    public void collide(CollisionEvent e) {
        if (activated && e.getOtherBody() == player) {
            if (!player.isInvincible()) { // Check if the player is not invincible
                player.takeDamage(2); // Apply damage
            }
            destroy(); // Destroy the spike after contact
        }
    }
}
