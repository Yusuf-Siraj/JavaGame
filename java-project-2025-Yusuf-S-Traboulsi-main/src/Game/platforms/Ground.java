package game.platforms;

import city.cs.engine.StaticBody;
import city.cs.engine.BoxShape;
import city.cs.engine.BodyImage;
import city.cs.engine.World;
import org.jbox2d.common.Vec2;

import java.util.Objects;

/**
 * Represents a ground platform in the game.
 * The ground is created by tiling segments of a specified image.
 */
public class Ground {

    /**
     * The shape of the ground platform.
     */
    private static final String IMAGE_PATH = "/game/resources/platform/grassMid.png"; // Path to the image

    /**
     * Creates a ground platform by tiling segments of the specified image.
     *
     * @param world  The world in which the ground exists.
     * @param x      The X-coordinate of the ground's position.
     * @param y      The Y-coordinate of the ground's position.
     * @param width  The total width of the ground.
     * @param height The height of each segment of the ground.
     */
    public Ground(World world, float x, float y, float width, float height) {
        float segmentWidth = height * 2; // Use height to scale the image proportionally
        for (float i = -width; i < width; i += segmentWidth) {
            StaticBody segment = new StaticBody(world, new BoxShape(segmentWidth / 2, height));
            segment.setPosition(new Vec2(x + i + segmentWidth / 2, y));
            segment.addImage(new BodyImage(Objects.requireNonNull(Ground.class.getResource(IMAGE_PATH)), height * 2));
        }
    }
}