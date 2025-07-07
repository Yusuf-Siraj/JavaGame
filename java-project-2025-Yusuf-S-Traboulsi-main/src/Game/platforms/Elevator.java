package game.platforms;

import city.cs.engine.*;
import org.jbox2d.common.Vec2;

/**
 * Represents an elevator platform that moves vertically between two points.
 * The elevator alternates its direction when reaching the start or end position.
 */
public class Elevator extends StaticBody implements StepListener {
    /**
     * The shape of the elevator platform.
     */
    private static final Shape platformShape = new BoxShape(1.5f, 0.25f);

    /**
     * The starting Y-coordinate of the elevator.
     */
    private float startY;

    /**
     * The ending Y-coordinate of the elevator.
     */
    private float endY;

    /**
     * The speed of the elevator's movement.
     */
    private float speed;

    /**
     * Indicates whether the elevator is currently moving up or down.
     */
    private boolean movingUp;

    /**
     * Creates an elevator that moves between two vertical positions.
     *
     * @param world  The world in which the elevator exists.
     * @param x      The X-coordinate of the elevator's position.
     * @param startY The starting Y-coordinate of the elevator.
     * @param endY   The ending Y-coordinate of the elevator.
     * @param speed  The speed of the elevator's movement.
     */
    public Elevator(World world, float x, float startY, float endY, float speed) {
        super(world, platformShape);
        setPosition(new Vec2(x, startY));
        this.startY = startY;
        this.endY = endY;
        this.speed = speed;
        this.movingUp = true;
        world.addStepListener(this);
    }

    /**
     * Sets the images for the elevator.
     * This method can be overridden to customize the appearance of the elevator.
     */
    @Override
    public void preStep(StepEvent e) {
        Vec2 position = getPosition();
        if (movingUp) {
            setPosition(new Vec2(position.x, position.y + speed * e.getStep()));
            if (position.y >= endY) {
                movingUp = false;
            }
        } else {
            setPosition(new Vec2(position.x, position.y - speed * e.getStep()));
            if (position.y <= startY) {
                movingUp = true;
            }
        }
    }

    /**
     * This method is called after each physics step.
     * It can be used to update the elevator's state or perform other actions.
     *
     * @param e The step event.
     */
    @Override
    public void postStep(StepEvent e) {
        // No need to do anything after the step
    }
}