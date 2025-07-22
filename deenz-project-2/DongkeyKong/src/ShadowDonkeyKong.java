import bagel.*;

import java.util.Properties;

/**
 * The main class for the Shadow Donkey Kong game.
 * This class extends {@code AbstractGame} and is responsible for managing game initialization,
 * updates, rendering, and handling user input.
 *
 * It sets up the game world, initializes characters, platforms, ladders, and other game objects,
 * and runs the game loop to ensure smooth gameplay.
 */
public class ShadowDonkeyKong extends AbstractGame {

    private final Properties GAME_PROPS;
    private final Properties MESSAGE_PROPS;
    private enum GameState {START, GAMING_ONE,GAMING_TWO, GAME_OVER}
    private final Image backgroundImage;

    private GameState gameState = GameState.START;
    private StartScreen startScreen;

    private GameScreenLevelOne gameScreenOne;
    private GameScreenLevelTwo gameScreenTwo;

    private EndScreen endScreen;

    public ShadowDonkeyKong(Properties gameProps, Properties messageProps) {
        super(Integer.parseInt(gameProps.getProperty("window.width")),
                Integer.parseInt(gameProps.getProperty("window.height")),
                messageProps.getProperty("home.title"));

        this.GAME_PROPS = gameProps;
        this.MESSAGE_PROPS = messageProps;

        backgroundImage = new Image(GAME_PROPS.getProperty("backgroundImage"));
        startScreen = new StartScreen(GAME_PROPS, MESSAGE_PROPS);
        gameScreenOne = new GameScreenLevelOne(GAME_PROPS);
        gameScreenTwo = new GameScreenLevelTwo(GAME_PROPS);

    }


    /**
     * Render the relevant screen based on the keyboard input given by the user and the status of the gameplay.
     * @param input The current mouse/keyboard input.
     */
    @Override
    protected void update(Input input) {
        if (input.wasPressed(Keys.ESCAPE)) {
            Window.close();
            return;
        }

        if (gameState == GameState.START) {
            startScreen.drawStartScreen();

            if (input.wasPressed(Keys.ENTER)) {
                gameScreenOne = new GameScreenLevelOne(GAME_PROPS);
                gameState = GameState.GAMING_ONE;
            }

            // If player press 2, go directly into level 2
            if (input.wasPressed(Keys.NUM_2)) {
                gameScreenTwo = new GameScreenLevelTwo(GAME_PROPS);
                gameState = GameState.GAMING_TWO;
            }
        }

        switch (gameState) {
            case GAMING_ONE:
                gameScreenOne.update(input);

                if (gameScreenOne.gameOver()) {
                    boolean win = gameScreenOne.isWin();
                    int score = gameScreenOne.getScore();
                    int timeLeft = gameScreenOne.remainTime();

                    // Win level 1, get into level 2
                    if (win) {
                        gameScreenTwo = new GameScreenLevelTwo(GAME_PROPS, score);
                        gameState = GameState.GAMING_TWO;
                    } else {
                        // Set score to 0 when lose
                        gameScreenOne.lostScore();
                        endScreen = new EndScreen(GAME_PROPS, MESSAGE_PROPS, score, timeLeft, win);
                        gameState = GameState.GAME_OVER;
                    }
                }
                break;
            case GAMING_TWO:
                gameScreenTwo.update(input);

                if (gameScreenTwo.gameOver()) {
                    boolean win = gameScreenTwo.isWin();
                    int score = gameScreenTwo.getScore();
                    int timeLeft = gameScreenTwo.remainTime();
                    // Set score to 0 if lose
                    if (!win) {
                        gameScreenTwo.lostScore();
                    }
                    endScreen = new EndScreen(GAME_PROPS, MESSAGE_PROPS, score, timeLeft, win);
                    gameState = GameState.GAME_OVER;

                }
                break;
        }


        if (gameState == GameState.GAME_OVER) {
            endScreen.drawEndScreen();

            if (input.wasPressed(Keys.SPACE)) {
                gameState = GameState.START;
            }
        }
    }



    /**
     * The main entry point of the Shadow Donkey Kong game.
     *
     * This method loads the game properties and message files, initializes the game,
     * and starts the game loop.
     *
     * @param args Command-line arguments (not used in this game).
     */
    public static void main(String[] args) {
        Properties gameProps = IOUtils.readPropertiesFile("res/app.properties");
        Properties messageProps = IOUtils.readPropertiesFile("res/message.properties");
        ShadowDonkeyKong game = new ShadowDonkeyKong(gameProps, messageProps);
        game.run();
    }


}
