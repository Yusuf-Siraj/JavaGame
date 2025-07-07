package game.characters;

import city.cs.engine.*;
import game.characters.enemies.Enemy;
import game.platforms.Trampoline;
import game.collectibles.Coin;
import org.jbox2d.common.Vec2;
import game.main.Game;

import java.util.Objects;

/**
 * Represents the player character in the game.
 * Handles movement, jumping, collision detection, and interactions with game objects.
 * Tracks health, coins collected, and other player states.
 */

public class Player extends Walker implements StepListener, CollisionListener {
    /**
     * The shape of the player.
     */
    private static final Shape playerShape = new BoxShape(1, 2);
    /**
     * The speed of the player.
     */
    private static final float SPEED = 6;
    /**
     * The jump speed of the player.
     */
    private static final float JUMP_SPEED = 14;
    /**
     * The gravity scale of the player.
     */
    private static final float GRAVITY_SCALE = 3f;
    /**
     * The maximum height of the trampoline jump.
     */
    private static final float MAX_TRAMPOLINE_HEIGHT = 20f; // Increased for quicker fall speed
    /**
     * The number of lives the player has.
     */
    private static final int MAX_HEALTH = 6; // 3 full hearts = 6 half-hearts
    /**
     * The interval between steps.
     */
    private static final int STEP_INTERVAL = 2;
    /**
     * The speed of the trampoline jump.
     */
    private static final float TRAMPOLINE_JUMP_SPEED = 30;
    /**
     * Tracks Ground detection.
     */
    // Ground Sensor
    private final Sensor groundSensor; // Tracks proper ground detection
    /**
     * The images for the player in different states.
     */
    BodyImage[] idleImages = new BodyImage[1];
    /**
     * The images for the player when jumping to the right.
     */
    BodyImage[] jumpRightImages = new BodyImage[1];


    // Movement State Tracking
    /**
     * The images for the player when jumping to the left.
     */
    BodyImage[] jumpLeftImages = new BodyImage[1];
    /**
     * The images for the player when walking to the right.
     */
    BodyImage[] walkRightImages = new BodyImage[11];

    // Images
    /**
     * The images for the player when walking to the left.
     */
    BodyImage[] walkLeftImages = new BodyImage[11];
    /**
     * The player sprite.
     */
    AttachedImage[] playerSprite;
    /**
     * The amount of coins collected by the player starting at 0.
     */
    private int coinsCollected = 0;
    /**
     * The game instance.
     */
    private Game game;
    /**
     * The invincibility state of the player.
     */
    private boolean invincible;

    // States
    /**
     * The health of the player.
     */
    private int health; // Tracks health in half-heart units
    /**
     * Tracks movement to the left.
     */
    private boolean isMovingLeft = false;
    /**
     * Tracks movement to the right.
     */
    private boolean isMovingRight = false;
    /**
     * Tracks if the player is idle.
     */
    private boolean isIdle = true;
    /**
     * Tracks if the player is walking.
     */
    private boolean isWalking = false;

    // Animation tracking
    /**
     * Tracks if the player is jumping.
     */
    private boolean isFacingRight = true;
    /**
     * Tracks if the player is jumping.
     */
    private boolean isJumping = false;
    /**
     * Tracks if the player is on the ground.
     */
    private boolean isOnGround = true;
    /**
     * Tracks the index of the idle image.
     */
    private int idleIndex = 0;
    /**
     * Tracks the index of the walking image.
     */
    private int walkRightIndex = 0;
    /**
     * Tracks the index of the walking image.
     */
    private int walkLeftIndex = 0;
    /**
     * Tracks the index of the jumping image.
     */
    private int jumpingIndex = 0;
    /**
     * Tracks the number of steps taken.
     */
    private int stepCounter = 0;
    /**
     * The number of touches the player has made.
     */
    private int touchCount = 0;
    /**
     * Tracks if the player is on a trampoline.
     */
    private boolean onGround;
    /**
     * Tracks if the player is on a trampoline.
     */
    private boolean onTrampoline;

    /**
     * The constructor for the Player class.
     *
     * @param world The physics world in which the player exists.
     * @param game  The game instance.
     */
    public Player(World world, Game game) {
        super(world, playerShape);
        this.game = game;

        setGravityScale(GRAVITY_SCALE); // Increases fall speed
        this.health = MAX_HEALTH; // Start with full health

        this.invincible = false;

        world.addStepListener(this);
        addCollisionListener(this);

        // Add Ground Sensor
        groundSensor = new Sensor(this, new BoxShape(0.9f, 0.1f, new Vec2(0, -2f)));
        groundSensor.addSensorListener(new SensorListener() {
            @Override
            public void beginContact(SensorEvent e) {
                if (e.getContactBody() instanceof Trampoline) {
                    onTrampoline = true;
                    isJumping = false; // Ensure the player can bounce immediately
                }
                if (e.getContactBody() instanceof StaticBody) {
                    isOnGround = true;
                    isJumping = false;
                }
            }

            @Override
            public void endContact(SensorEvent e) {
                if (e.getContactBody() instanceof Trampoline) {
                    onTrampoline = false;
                }
                if (e.getContactBody() instanceof StaticBody) {
                    isOnGround = false;
                }
            }
        });

        addCollisionListener(new CollisionListener() {
            @Override
            public void collide(CollisionEvent e) {
                if (e.getOtherBody() instanceof Coin) {
                    collect((Coin) e.getOtherBody());
                }
            }
        });

        setImages();
        playerSprite = new AttachedImage[]{addImage(idleImages[0])};

        // Add collision listener for collectibles

    }

    /**
     * Sets the images for the player in different states.
     */
    public void setImages() {
        idleImages[0] = new BodyImage(Objects.requireNonNull(Player.class.getResource("/game/resources/playerOne/idle/p1_front.png")), 4);
        jumpRightImages[0] = new BodyImage(Objects.requireNonNull(Player.class.getResource("/game/resources/playerOne/jump/right/p1_jump.png")), 4);
        jumpLeftImages[0] = new BodyImage(Objects.requireNonNull(Player.class.getResource("/game/resources/playerOne/jump/left/p1_jump.png")), 4);
        for (int i = 0; i < walkRightImages.length; i++) {
            walkRightImages[i] = new BodyImage(Objects.requireNonNull(Player.class.getResource("/game/resources/playerOne/walking/right/p1_walk" + String.format("%02d", i + 1) + ".png")), 4);
        }

        for (int i = 0; i < walkLeftImages.length; i++) {
            walkLeftImages[i] = new BodyImage(Objects.requireNonNull(Player.class.getResource("/game/resources/playerOne/walking/left/p1_walk_left" + String.format("%02d", i + 1) + ".png")), 4);
        }
    }


    // Updated Movement Methods with Immediate Switching

    /**
     * Moves the player to the left.
     */
    public void moveLeft() {
        isMovingLeft = true;
        isMovingRight = false; // Cancel right movement instantly
        setLinearVelocity(new Vec2(-SPEED, getLinearVelocity().y));
        this.isFacingRight = false;
    }

    /**
     * Moves the player to the right.
     */
    public void moveRight() {
        isMovingRight = true;
        isMovingLeft = false; // Cancel left movement instantly
        setLinearVelocity(new Vec2(SPEED, getLinearVelocity().y));
        this.isFacingRight = true;
    }

    // Stop Movement When No Keys Are Pressed

    /**
     * Stops the player's movement.
     */
    public void stopMoving() {
        isMovingLeft = false;
        isMovingRight = false;
        setLinearVelocity(new Vec2(0, getLinearVelocity().y));
    }

    /**
     * Makes the player jump.
     */
    public void jump() {
        if (isOnGround || onTrampoline) { // Jump logic handles both normal jump and trampoline
            if (onTrampoline) {
                setLinearVelocity(new Vec2(getLinearVelocity().x, TRAMPOLINE_JUMP_SPEED));
                isJumping = true;     // Ensure jump animation remains active
            } else {
                setLinearVelocity(new Vec2(getLinearVelocity().x, JUMP_SPEED));
                isJumping = true;
                isOnGround = false;  // Reset ground state
            }
        }
    }

    /**
     * Returns the current position of the player.
     *
     * @return The current position of the player.
     */
    @Override
    public void preStep(StepEvent stepEvent) {
        // Ensure trampoline jump is recognized even if the player hits the side
        if (onTrampoline && Math.abs(getLinearVelocity().y) < 0.01f) {
            setLinearVelocity(new Vec2(getLinearVelocity().x, TRAMPOLINE_JUMP_SPEED));
            isJumping = true;
        }
    }

    /**
     * Updates the player's state and handles animations.
     *
     * @param stepEvent The step event triggered by the physics engine.
     */
    @Override
    public void postStep(StepEvent stepEvent) {
        stepCounter++;
        if (stepCounter < STEP_INTERVAL) {
            return;
        }
        stepCounter = 0;

        // Movement logic
        if (isMovingLeft) {
            setLinearVelocity(new Vec2(-SPEED, getLinearVelocity().y));
        } else if (isMovingRight) {
            setLinearVelocity(new Vec2(SPEED, getLinearVelocity().y));
        }

        // Cap trampoline jump height
        if (onTrampoline && getLinearVelocity().y > MAX_TRAMPOLINE_HEIGHT) {
            setLinearVelocity(new Vec2(getLinearVelocity().x, MAX_TRAMPOLINE_HEIGHT));
        }

        // State Control
        if (Math.abs(getLinearVelocity().y) > 0.01f) {
            isJumping = true;
            isWalking = false;
            isIdle = false;
        } else if (Math.abs(getLinearVelocity().x) > 1 && isOnGround) {
            isWalking = true;
            isIdle = false;
            isJumping = false;
        } else if (isOnGround) {
            isIdle = true;
            isWalking = false;
            isJumping = false;
        }

        // Animation Handling
        removeAllImages();

        if (isJumping) {
            playerSprite[0] = addImage(isFacingRight ? jumpRightImages[0] : jumpLeftImages[0]);
        } else if (isWalking) {
            walkRightIndex = (walkRightIndex + 1) % walkRightImages.length;
            playerSprite[0] = addImage(isFacingRight ? walkRightImages[walkRightIndex] : walkLeftImages[walkRightIndex]);
        } else {
            playerSprite[0] = addImage(idleImages[idleIndex]);
        }
    }

    /**
     * Handles collision events with other game objects.
     *
     * @param e The collision event triggered by the physics engine.
     */
    @Override
    public void collide(CollisionEvent e) {
        if (e.getOtherBody() instanceof StaticBody) {
            isOnGround = true;
            isJumping = false;
        }

        if (e.getOtherBody() instanceof Enemy && isJumping) {
            e.getOtherBody().destroy(); // Destroy the enemy if the player is jumping
        }
    }

    /**
     * Returns the player's current position.
     *
     * @return The player's current position.
     */
    public boolean getIsMovingLeft() {
        return isMovingLeft;
    }

    /**
     * Returns the player's current position.
     *
     * @return The player's current position.
     */
    public boolean getIsMovingRight() {
        return isMovingRight;
    }


    /**
     * Returns the player's current position and apply the impulse.
     *
     * @return The player's current position and apply the impulse.
     */
    public void applyImpulse(Vec2 impulse) {
        super.applyImpulse(impulse);
    }

    /**
     * Returns the player's current position and apply the force.
     *
     * @return The player's current position and apply the force.
     */
    public void applyForce(Vec2 force) {
        super.applyForce(force);
    }

    /**
     * Increments the touch count of the player.
     */
    public void incrementTouchCount() {
        touchCount++;
    }

    /**
     * Get the touch count of the player.
     */
    public int getTouchCount() {
        return touchCount;
    }

    /**
     * Checks if the player has lost all health.
     */
    public boolean hasLost() {
        return health == 0;
    }

    /**
     * Checks if the player has taken damage and if not they are invincible to it.
     */
    public void takeDamage(int damage) {
        if (!isInvincible()) { // Check if the player is not invincible
            health = Math.max(0, health - damage); // Reduce health safely
        }
    }

    /**
     * Prevents the player from overhealing from using the healhcollectible.
     */

    public void heal(int amount) {
        health = Math.min(MAX_HEALTH, health + amount); // Prevent overhealing
    }

    /**
     * Gets the current health of the player.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Sets the health of the player.
     *
     * @param health The new health value.
     */

    public void setHealth(int health) {
        this.health = Math.min(health, 6); // Cap health at 6
    }

    /**
     * Gets the current coins collected of the player.
     */
    public int getCoinsCollected() {
        return coinsCollected;
    }

    /**
     * Collects a coin and increments the coins collected count.
     *
     * @param collectible The collectible to be collected.
     */
    private void collect(Coin collectible) {
        coinsCollected++;
        collectible.destroy();
    }

    /**
     * Resets the player to its initial state.
     */
    public void resetPlayer() {
        coinsCollected = 0;
        health = 6; // Reset health to max
        setPosition(new Vec2(-8, -8)); // Reset position
    }
    // setter for restoring health between levels

    /**
     * Gets the score of the player and multiplies it by 10.
     */
    public int getScore() {
        // Example: Score is based on coins collected
        return coinsCollected * 10; // Each coin is worth 10 points
    }

    /**
     * Sets the coins collected by the player.
     *
     * @param score The new coins collected value.
     */
    // setter for restoring score between levels
    public void setScore(int score) {
        this.coinsCollected = score / 10;  // or adjust if your getScore() logic differs
    }

    /**
     * Decreases the player's health by a specified amount.
     *
     * @param amount The amount to decrease health by.
     */
    public void decreaseHealth(int amount) {
        health -= amount; // Assuming 'health' is a field in the Player class
        if (health <= 0) {
            // Handle player death
        }
    }

    /**
     * Sets the invincibility state of the player.
     */
    public boolean isInvincible() {
        return invincible;
    }

    /**
     * Sets the invincibility state of the player.
     *
     * @param invincible The new invincibility state.
     */
    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }

    /**
     * Destroys the player if not invincible.
     */
    @Override
    public void destroy() {
        if (!invincible) {
            super.destroy();
        }
    }
}