package game.main;

import city.cs.engine.StepEvent;
import city.cs.engine.StepListener;
import game.characters.enemies.Snail;
import game.characters.enemies.Fly;
import game.collectibles.Coin;
import game.collectibles.HealthCollectible;
import game.controls.PlayerController;
import game.levelLoader.GameView;
import game.levelLoader.GameWorld;
import game.characters.Player;
import game.platforms.Elevator;
import game.platforms.Ground;
import game.platforms.Trampoline;
import org.jbox2d.common.Vec2;
import game.levelLoader.Door;
import game.platforms.FallingSpike;
import game.platforms.MoveFallingSpike;
import game.controls.PauseControl;
import game.controls.BackgroundMusicManager;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The main class for the game "Tamaros's Adventure: The Invincible Jump".
 * This class initializes the game, manages levels, handles UI components, and controls game flow.
 */


public class Game {

    /**
     * The main game window.
     */
    private JFrame frame;

    /**
     * The main panel for the game UI.
     */
    private JPanel mainPanel;

    /**
     * The current game world.
     */
    private GameWorld world;

    /**
     * The view for rendering the game world.
     */
    private GameView view;

    /**
     * The player character.
     */
    private Player player;

    /**
     * The list of levels in the game.
     */
    private List<Level> levels;

    /**
     * The index of the current level.
     */
    private int currentLevelIndex = 0;

    /**
     * The start time of the game in milliseconds.
     */
    private long startTime;

    /**
     * The saved health of the player for level transitions.
     */
    private int savedHealth;

    /**
     * The saved coin count of the player for level transitions.
     */
    private int savedCoins;

    // UI for game over/reset
    /**
     * The layered pane for managing UI components.
     */
    private JLayeredPane layeredPane;

    /**
     * The reset button for restarting the game.
     */
    private JButton resetButton;

    /**
     * The label displayed when the player loses.
     */
    private JLabel lostLabel;

    /**
     * Constructs a new Game instance and initializes the game.
     */
    public Game() {
        initialiseGame();
        BackgroundMusicManager.startMusic();
    }

    /**
     * The main method to start the game.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        new Game();
    }

    /**
     * Initializes the game by setting up the main window and showing the main menu.
     */
    private void initialiseGame() {
        frame = new JFrame("Tamaros's Adventure: The Invincible Jump");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setSize(800, 600);

        mainPanel = new JPanel(new BorderLayout());
        frame.add(mainPanel);
        frame.setVisible(true);

        showMainMenu();
    }

    /**
     * Displays the main menu of the game.
     */
    private void showMainMenu() {
        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/game/resources/background/level1_background.png"));
        Image backgroundImage = backgroundIcon.getImage();

        // Custom panel to draw the background
        JPanel menu = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10); // Add padding

        JLabel title = new JLabel("Tamaros's Adventure: The Invincible Jump", SwingConstants.CENTER);
        title.setFont(new Font("LuckiestGuy", Font.BOLD, 32));
        title.setForeground(Color.BLACK); // Ensure text is visible on the background
        menu.add(title, gbc);

        gbc.gridy = 1; // Move to the next row
        JButton startButton = new JButton("Start Game");
        startButton.setPreferredSize(new Dimension(150, 40)); // Adjust button size
        startButton.setBackground(new Color(0, 100, 0)); // Set deep green background color
        startButton.setForeground(Color.WHITE); // Optional: Set text color to white for better contrast
        startButton.addActionListener(e -> startGame());
        menu.add(startButton, gbc);

        mainPanel.removeAll();
        mainPanel.add(menu, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Starts the game by resetting stats, setting up levels, and loading the first level.
     */
    private void startGame() {
        // reset saved stats
        savedHealth = 6;
        savedCoins = 0;
        startTime = System.currentTimeMillis();
        currentLevelIndex = 0;

        setupLevels();
        loadCurrentLevel();

        // Reset via R in view
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    resetGame();
                }
            }
        });
    }

    /**
     * Freezes the game when the player loses.
     */
    public void freezeGame() {
        world.stop(); // Stop the physics simulation
        resetButton.setVisible(true); // Show the reset button
        BackgroundMusicManager.stopMusic();
    }

    /**
     * Sets up the levels for the game.
     */
    private void setupLevels() {
        levels = new ArrayList<>();
        levels.add(new Level1(this));
        levels.add(new Level2(this));
        levels.add(new Level3(this));
    }

    /**
     * Loads the current level based on the `currentLevelIndex`.
     */
    private void loadCurrentLevel() {
        if (world != null) {
            world.stop();
        }

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800, 600));

        Level lvl = levels.get(currentLevelIndex);
        world = lvl.createWorld();
        player = world.getPlayer();

        // Restore stats
        player.setHealth(savedHealth);
        player.setScore(savedCoins);

        view = new GameView(world, 800, 600, player);
        view.setBounds(0, 0, 800, 600);
        view.setFocusable(true);

        // Add KeyListener for resetting the game with the R key
        view.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    resetGame();
                }
            }
        });

        // Set the background for the current level
        if (currentLevelIndex == 0) {
            view.loadBackgroundImage("/game/resources/background/level1_background.png");
        } else if (currentLevelIndex == 1) {
            view.loadBackgroundImage("/game/resources/background/level2_background.png");
        } else if (currentLevelIndex == 2) {
            view.loadBackgroundImage("/game/resources/background/level3_background.png");
        }

        // Add temporary message for Level 1
        if (currentLevelIndex == 0) {
            JLabel messageLabel = new JLabel("Press P to pause, R to reset", SwingConstants.LEFT);
            messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
            messageLabel.setForeground(Color.BLACK);
            messageLabel.setBounds(250, 250, 400, 30); // Positioned on the left in the center
            layeredPane.add(messageLabel, JLayeredPane.PALETTE_LAYER);

            // Hide the message after 10 seconds
            new javax.swing.Timer(10000, e -> layeredPane.remove(messageLabel)).start();
        }

        // Attach controls
        view.addKeyListener(new PlayerController(player));
        view.addKeyListener(new PauseControl(view)); // Attach the PauseController

        layeredPane.add(view, JLayeredPane.DEFAULT_LAYER);

        // Reset and Lost UI
        resetButton = new JButton("Reset");
        resetButton.setBounds(350, 10, 100, 30);
        resetButton.addActionListener(e -> resetGame());
        resetButton.setVisible(false);
        layeredPane.add(resetButton, JLayeredPane.PALETTE_LAYER);

        lostLabel = new JLabel("Game Over", SwingConstants.CENTER);
        lostLabel.setFont(new Font("Serif", Font.BOLD, 32));
        lostLabel.setBounds(250, 250, 300, 50);
        lostLabel.setVisible(false);
        layeredPane.add(lostLabel, JLayeredPane.PALETTE_LAYER);

        mainPanel.removeAll();
        mainPanel.add(layeredPane, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();

        view.requestFocusInWindow();

        world.start();
        world.addStepListener(new StepListener() {
            @Override
            public void preStep(StepEvent e) {
            }

            @Override
            public void postStep(StepEvent e) {
                view.updateCamera();
                view.repaint();
                checkPlayerLost();
            }
        });
    }

    /**
     * Loads the next level in the game. If all levels are completed, shows the end credits.
     */
    public void loadNextLevel() {
        // snapshot stats
        savedHealth = player.getHealth();
        savedCoins = player.getScore();
        currentLevelIndex++;
        if (currentLevelIndex < levels.size()) {
            loadCurrentLevel();
        } else {
            showEndCredits();
        }
    }

    /**
     * Checks if the player has lost the game and handles the game-over state.
     */
    private void checkPlayerLost() {
        if (player.hasLost()) {
            world.stop();
            lostLabel.setVisible(true);

            // Center the reset button
            resetButton.setBounds(325, 300, 150, 40); // Centered horizontally and vertically
            resetButton.setBackground(new Color(0, 128, 0)); // Set green background
            resetButton.setForeground(Color.WHITE); // Optional: Set text color to white for contrast
            resetButton.setVisible(true);

            BackgroundMusicManager.stopMusic();
            playTromboneSound();
        }
    }

    /**
     * Resets the game to the first level and restores initial stats.
     */
    private void resetGame() {
        // Reset stats for the game
        savedHealth = 6;
        savedCoins = 0;
        startTime = System.currentTimeMillis();

        // Reset to Level 1
        currentLevelIndex = 0;

        // Restart the background music
        BackgroundMusicManager.startMusic();

        // Load Level 1
        loadCurrentLevel();
    }

    /**
     * Displays the end credits when the game is completed.
     */
    private void showEndCredits() {
        BackgroundMusicManager.stopMusic();
        playEndScreenMusic();
        long totalTime = System.currentTimeMillis() - startTime;
        int score = player.getScore();

        // Load the background image
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/game/resources/background/EndScreen.png"));
        Image backgroundImage = backgroundIcon.getImage();

        // Custom panel to draw the background
        JPanel credits = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        // Add labels for score and time
        JLabel congratsLabel = new JLabel("Congratulations!", SwingConstants.CENTER);
        congratsLabel.setFont(new Font("Serif", Font.BOLD, 32));
        congratsLabel.setBounds(250, 100, 300, 50);
        credits.add(congratsLabel);

        JLabel scoreLabel = new JLabel("Your Score: " + score, SwingConstants.CENTER);
        scoreLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        scoreLabel.setBounds(250, 160, 300, 30);
        credits.add(scoreLabel);

        JLabel timeLabel = new JLabel("Time: " + formatTime(totalTime), SwingConstants.CENTER);
        timeLabel.setFont(new Font("Serif", Font.PLAIN, 24));
        timeLabel.setBounds(250, 200, 300, 30);
        credits.add(timeLabel);

        // Add a smaller reset button at the bottom center
        JButton resetButton = new JButton("Reset Game");
        resetButton.setPreferredSize(new Dimension(150, 40));
        resetButton.setBounds(325, 500, 150, 40); // Centered at the bottom
        resetButton.setBackground(new Color(0, 100, 0)); // Deep green background
        resetButton.setForeground(Color.WHITE); // White text for contrast
        resetButton.addActionListener(e -> {
            savedHealth = 6;
            savedCoins = 0;
            currentLevelIndex = 0;
            startTime = System.currentTimeMillis();
            BackgroundMusicManager.startMusic();
            startGame();
        });
        credits.add(resetButton);

        mainPanel.removeAll();
        mainPanel.add(credits, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
     * Plays the end screen music.
     */
    private void playEndScreenMusic() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource("/game/resources/sound/orchestralwin.wav")));
            Clip endScreenClip = AudioSystem.getClip();
            endScreenClip.open(audioInputStream);
            endScreenClip.start(); // Play the sound once
        } catch (Exception e) {
            System.err.println("Error playing end screen music: " + e.getMessage());
        }
    }

    /**
     * Formats the elapsed time into a readable string (MM:SS).
     *
     * @param millis The elapsed time in milliseconds.
     * @return The formatted time string.
     */
    private String formatTime(long millis) {
        long seconds = (millis / 1000) % 60;
        long minutes = (millis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    /**
     * Plays a trombone sound when the player loses.
     */
    private void playTromboneSound() {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                    Objects.requireNonNull(getClass().getResource("/game/resources/sound/trombone.wav")));
            Clip tromboneClip = AudioSystem.getClip();
            tromboneClip.open(audioInputStream);
            tromboneClip.start(); // Play the sound once
        } catch (Exception e) {
            System.err.println("Error playing trombone sound: " + e.getMessage());
        }
    }

    /**
     * Interface for defining levels in the game.
     */
    private interface Level {
        /**
         * Creates the game world for the level.
         *
         * @return The created game world.
         */
        GameWorld createWorld();
    }

    /**
     * Represents Level 1 of the game.
     */
    private static class Level1 implements Level {
        private final Game game;

        /**
         * Constructs Level 1 with a reference to the main game.
         *
         * @param game The main game instance.
         */
        Level1(Game game) {
            this.game = game;
        }

        @Override
        public GameWorld createWorld() {
            GameWorld world = new GameWorld(game);

            // Start player in a safe position
            world.getPlayer().setPosition(new Vec2(-14, -8));

            // Basic ground platform
            new Ground(world, 0, -10, 20, 1f);

            // Introduce trampoline
            new Trampoline(world).setPosition(new Vec2(-10, -9f));

            // Introduce falling spike
            new FallingSpike(world, 4, 10, world.getPlayer());

            // Introduce moving falling spike
            new MoveFallingSpike(world, -10, 0f, 10f, 2f);

            // Simple platform arrangement
            new Ground(world, -4, -7, 2, 0.5f);
            new Ground(world, 4, -5, 3, 0.5f);

            // Basic coin placement for teaching collection
            new Coin(world).setPosition(new Vec2(-4, -6));
            new Coin(world).setPosition(new Vec2(4, -4));

            // Add a health collectible on top of the trampoline
            HealthCollectible healthCollectible = new HealthCollectible(world);
            healthCollectible.setPosition(new Vec2(-10, -7.5f)); // Set position for the collectible

            // Single enemy for teaching combat
            new Snail(world, 0, -8f, world.getPlayer(), game);

            // Introduce flying enemy
            new Fly(world, -2, -1, -4, 0, world.getPlayer(), game);

            // Door at an easy-to-reach location
            Door door = new Door(world, game);
            door.setPosition(new Vec2(14, -8));
            world.setDoor(door);

            return world;
        }
    }


    /**
     * Represents Level 2 of the game.
     */
    private static class Level2 implements Level {
        private final Game game;

        /**
         * Constructs Level 2 with a reference to the main game.
         *
         * @param game The main game instance.
         */
        Level2(Game game) {
            this.game = game;
        }

        @Override
        public GameWorld createWorld() {
            GameWorld world = new GameWorld(game);
            world.getPlayer().setPosition(new Vec2(-25, -8));

            // Single long ground section
            new Ground(world, 2, -10, 32, 1);

            // Platforms
            new Ground(world, -8, 0, 2, 0.5f);
            new Ground(world, 8, 2, 2, 0.5f);
            new Ground(world, 20, 4, 2, 0.5f);

            // Falling spikes
            new FallingSpike(world, -13, 5, world.getPlayer());
            new FallingSpike(world, 0, 6, world.getPlayer());
            new FallingSpike(world, 11, 7, world.getPlayer());

            // Moving falling spikes
            new MoveFallingSpike(world, -8, 2, 8, 2f);
            new MoveFallingSpike(world, 8, 4, 12, 2.5f);

            // Elevators
            Elevator elev1 = new Elevator(world, -4, -8, 4, 3f);
            elev1.setPosition(new Vec2(-4, -8));

            Elevator elev2 = new Elevator(world, 4, -6, 4, 3f);
            elev2.setPosition(new Vec2(4, -6));

            Elevator elev3 = new Elevator(world, 16, -4, 4, 3f);
            elev3.setPosition(new Vec2(16, -4));

            // Trampolines
            new Trampoline(world).setPosition(new Vec2(-8, -9));
            new Trampoline(world).setPosition(new Vec2(8, -9));
            new Trampoline(world).setPosition(new Vec2(22, -9));

            // Snails on the ground
            new Snail(world, -15, -9, world.getPlayer(), game); // On the first ground section
            new Snail(world, 0, -9, world.getPlayer(), game);   // On the second ground section
            new Snail(world, 15, -9, world.getPlayer(), game);  // On the third ground section
            new Snail(world, 30, -9, world.getPlayer(), game);  // On the fourth ground section


            // Adding fly enemies
            new Fly(world, -18, 2, -8, 0, world.getPlayer(), game);
            new Fly(world, 10, 6, 8, 4, world.getPlayer(), game);

            // Coins
            new Coin(world).setPosition(new Vec2(-8, 1));
            new Coin(world).setPosition(new Vec2(8, 3));

            // Door
            Door door = new Door(world, game);
            door.setPosition(new Vec2(24, 6));
            world.setDoor(door);

            return world;
        }
    }


    /**
     * Represents Level 3 of the game.
     */
    private static class Level3 implements Level {
        private final Game game;

        /**
         * Constructs Level 3 with a reference to the main game.
         *
         * @param game The main game instance.
         */
        Level3(Game game) {
            this.game = game;
        }

        @Override
        public GameWorld createWorld() {
            GameWorld world = new GameWorld(game);
            world.getPlayer().setPosition(new Vec2(-17, -8));

            // Ground sections
            new Ground(world, -14, -10, 8, 1);
            new Ground(world, 2, -10, 8, 1);
            new Ground(world, 16, -10, 8, 1);

            // Platforms
            new Ground(world, -6, -8, 2, 0.5f);
            new Ground(world, 4, -3, 2, 0.5f);
            new Ground(world, 12, -6, 2, 0.5f);

            // Falling spikes
            new FallingSpike(world, -15, 5, world.getPlayer());
            new FallingSpike(world, -4, 6, world.getPlayer());
            new FallingSpike(world, 6, 7, world.getPlayer());
            new FallingSpike(world, 14, 8, world.getPlayer());

            // Moving falling spikes
            new MoveFallingSpike(world, -12, -4, 5, 2f);
            new MoveFallingSpike(world, 0, 4, 12, 2.5f);
            new MoveFallingSpike(world, 8, 6, 16, 3f);

            // Elevator
            Elevator elevator = new Elevator(world, -2, -8, 4, 2f);
            elevator.setPosition(new Vec2(-2, -8));

            // Trampolines
            new Trampoline(world).setPosition(new Vec2(8, -9));
            new Trampoline(world).setPosition(new Vec2(16, -9));

            // Enemies
            new Snail(world, 2, -8f, world.getPlayer(), game);
            new Snail(world, 12, -8f, world.getPlayer(), game);

            // Adding fly enemies to Level 3
            new Fly(world, -8, 3, -10, 1, world.getPlayer(), game);
            new Fly(world, 14, 7, 12, 5, world.getPlayer(), game);

            // Coins
            new Coin(world).setPosition(new Vec2(-6, -4));
            new Coin(world).setPosition(new Vec2(12, -5));

            // Health collectible
            HealthCollectible healthCollectible = new HealthCollectible(world);
            healthCollectible.setPosition(new Vec2(4, -2));

            // Door
            Door door = new Door(world, game);
            door.setPosition(new Vec2(20, -8));
            world.setDoor(door);

            return world;
        }
    }

}