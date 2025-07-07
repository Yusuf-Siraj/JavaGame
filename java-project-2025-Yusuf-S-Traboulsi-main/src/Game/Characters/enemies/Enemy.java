package game.characters.enemies;

import city.cs.engine.*;
import game.characters.Player;
import game.main.Game;
import org.jbox2d.common.Vec2;

/**
 * Represents a generic enemy in the game.
 * The enemy can patrol and follow the player within a certain range.
 */

public class Enemy extends Walker implements StepListener, CollisionListener {
    /**
     * The specific shape of the enemy.
     */
    private static final Shape enemyShape = new BoxShape(0.5f, 0.5f);
    /**
     * The speed at which the enemy moves.
     */
    private static final float SPEED = 2f;
    /**
     * The detection range within which the enemy can see the player.
     */
    private static final float DETECTION_RANGE = 5f;
    /**
     * The patrol range within which the enemy can move back and forth.
     */
    private static final float PATROL_RANGE = 2f;
    /**
     * The images used for walking right.
     */
    public BodyImage[] walkRightImages = new BodyImage[2];
    /**
     * The images used for walking left.
     */
    public BodyImage[] walkLeftImages = new BodyImage[2];
    /**
     * The images used for idle state.
     */
    public BodyImage idleImage;
    /**
     * The enemy starts moving to the right.
     */
    private boolean movingRight = true;

    // Animation
    /**
     * The player character in the game.
     */
    private Player player;
    /**
     * The initial x-coordinate of the enemy.
     */
    private float startX;
    /**
     * The main game instance.
     */
    private Game game;
    /**
     * The index used for walking animation.
     */
    private int walkIndex = 0;

    /**
     * Constructs an Enemy in the specified world.
     * Sets up the enemy's position, collision listener, and step listener.
     *
     * @param world  The physics world in which the enemy exists.
     * @param x      The starting x-coordinate of the enemy's position.
     * @param y      The starting y-coordinate of the enemy's position.
     * @param player The player character in the game.
     * @param game   The main game instance.
     */
    public Enemy(World world, float x, float y, Player player, Game game) {
        super(world, enemyShape);
        this.player = player;
        this.startX = x;
        this.game = game;
        setPosition(new Vec2(x, y));

        world.addStepListener(this);
        addCollisionListener(this);

        // Load Images
        setImages();
        addImage(idleImage);
    }

    /**
     * Sets the images for the enemy's animations.
     * This method should be overridden by subclasses to provide specific images.
     */
    public void setImages() {
        // override this code to add images for each type of enemy This only currently supports ground enemies
    }

    /**
     * Updates the enemy's behavior before each physics step.
     * Prevents the enemy from falling and determines whether to patrol or follow the player.
     *
     * @param e The step event triggered by the physics engine.
     */
    @Override
    public void preStep(StepEvent e) {
        setLinearVelocity(new Vec2(getLinearVelocity().x, -1)); // Prevent enemy from falling

        float distanceToPlayer = Math.abs(getPosition().x - player.getPosition().x);

        if (distanceToPlayer < DETECTION_RANGE) {
            followPlayer();
        } else {
            patrol();
        }
    }

    /**
     * Updates the enemy's animation after each physics step.
     * Changes the enemy's image based on its movement direction or idle state.
     *
     * @param e The step event triggered by the physics engine.
     */
    @Override
    public void postStep(StepEvent e) {
        // Handle animation updates
        walkIndex = (walkIndex + 1) % walkRightImages.length;
        removeAllImages();

        if (getLinearVelocity().x > 0) {
            addImage(walkRightImages[walkIndex]);
        } else if (getLinearVelocity().x < 0) {
            addImage(walkLeftImages[walkIndex]);
        } else {
            addImage(idleImage); // If stationary, show idle image
        }
    }

    /**
     * Makes the enemy follow the player if the player is within detection range.
     * Adjusts the enemy's walking direction based on the player's position.
     */
    private void followPlayer() {
        float playerX = player.getPosition().x;
        float enemyX = getPosition().x;

        if (playerX > enemyX) {
            startWalking(SPEED);
        } else {
            startWalking(-SPEED);
        }
    }

    /**
     * Makes the enemy patrol within a predefined range.
     * Changes the walking direction when the enemy reaches the patrol boundaries.
     */
    private void patrol() {
        float currentX = getPosition().x;

        if (movingRight) {
            startWalking(SPEED);
            if (currentX > startX + PATROL_RANGE) {
                movingRight = false;
            }
        } else {
            startWalking(-SPEED);
            if (currentX < startX - PATROL_RANGE) {
                movingRight = true;
            }
        }
    }

    /**
     * Handles collision events between the enemy and the player.
     * Damages the player, pushes the player back, and freezes the game if the player loses.
     *
     * @param e The collision event triggered by the physics engine.
     */
    @Override
    public void collide(CollisionEvent e) {
        if (e.getOtherBody() == player) {
            player.takeDamage(1);

            if (player.hasLost()) {
                game.freezeGame();
            } else {
                Vec2 pushDirection = player.getPosition().sub(getPosition());
                if (pushDirection.length() > 0) {
                    pushDirection = pushDirection.mul(1 / pushDirection.length());
                    player.applyImpulse(pushDirection.mul(5));
                }
            }
        }
    }

}

