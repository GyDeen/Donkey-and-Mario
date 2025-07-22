import bagel.Font;
import bagel.Image;
import bagel.Input;
import bagel.Window;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Properties;


/** remember adding score and timer words on the game screen */
public abstract class GameScreen {
    private final Image background;

    private Properties gameProperties;

    private final Mario player;

    private ArrayList<Hammer> hammers = new ArrayList<>();
    private final int hammerCount;

    private final DonkeyKong donkeyKong;

    private ArrayList<Ladder> ladders = new ArrayList<>();
    private int ladderCount;

    private ArrayList<Platform> platforms = new ArrayList<>();

    private ArrayList<Barrel> barrels = new ArrayList<>();
    private int barrelCount;

    private int score = 0;
    private final int scoreFontSize;
    private Point scorePosition;
    private final Font scoreFont;
    private static final int DESTROY_BARREL = 100;
    private static final int JUMP_OVER_BARREL = 30;

    private final int timeFontSize;
    private final int maxFrames;
    private int currentFrame = 0;
    private final Point timePosition;
    private final Font timeFont;

    private final int donkeyFontSize;
    private Point donkeyHealthPosition;
    private Font donkeyHealthFont;



    private int level;


    public GameScreen(Properties gameProps, int level) {
        this.gameProperties = gameProps;
        this.level = level;
        String keyForLevel = "level" + level;

        // Read in the number of platform and their position
        String platformData = gameProperties.getProperty("platforms." + keyForLevel);
        String[] platformPairs = platformData.split(";");
        for (String pair : platformPairs) {
            String pairX = pair.split(",")[0];
            String pairY = pair.split(",")[1];

            double x = Double.parseDouble(pairX);
            double y = Double.parseDouble(pairY);

            platforms.add(new Platform(x, y));
        }

        // Read in the number of ladder and store their position
        // Place each ladder to the top of the platform
        ladderCount = Integer.parseInt(gameProperties.getProperty("ladder." + keyForLevel + ".count"));
        for (int i = 1; i <= ladderCount; i++) {
            String key = "ladder." + keyForLevel + "." + i;
            String value = gameProperties.getProperty(key);
            String[] position = value.split(",");

            double x = Double.parseDouble(position[0]);
            double y = Double.parseDouble(position[1]);

            // Update the ladder to the correct position
            Ladder ladder = new Ladder(x, y, platforms);
            ladder.update();
            ladders.add(ladder);
        }

        // Read in the number of barrel and their position
        barrelCount = Integer.parseInt(gameProperties.getProperty("barrel." + keyForLevel + ".count"));
        for (int i = 1; i <= barrelCount; i++) {
            String key = "barrel." + keyForLevel + "." + i;
            String value = gameProperties.getProperty(key);
            String[] position = value.split(",");

            double x = Double.parseDouble(position[0]);
            double y = Double.parseDouble(position[1]);

            barrels.add(new Barrel(x, y, gameProperties));
        }

        // Read in hammer
        hammerCount = Integer.parseInt(gameProperties.getProperty("hammer.level" + level + ".count"));
        for (int i = 1; i <= hammerCount; i++) {
            String key = "hammer.level" + level + "." + i;
            String value = gameProperties.getProperty(key);
            hammers.add(new Hammer(gameProperties, level, i));
        }

        player = new Mario(gameProperties, this, level);

        double donkeyX = Double.parseDouble(gameProperties.getProperty("donkey." + keyForLevel).split(",")[0]);
        double donkeyY = Double.parseDouble(gameProperties.getProperty("donkey." + keyForLevel).split(",")[1]);
        donkeyKong = new DonkeyKong(donkeyX, donkeyY, gameProperties);

        scoreFontSize = Integer.parseInt(gameProperties.getProperty("gamePlay.score.fontSize"));
        scorePosition = new Point(Integer.parseInt(gameProperties.getProperty("gamePlay.score.x")), Integer.parseInt(gameProperties.getProperty("gamePlay.score.y")));
        scoreFont = new Font("res/FSO8BITR.TTF", scoreFontSize);

        timeFontSize = scoreFontSize;
        maxFrames = Integer.parseInt(gameProperties.getProperty("gamePlay.maxFrames"));
        timePosition = new Point(scorePosition.x, scorePosition.y + Utils.DISTANCE_BETWEEN_FONT);
        timeFont = new Font("res/FSO8BITR.TTF", timeFontSize);

        donkeyFontSize = scoreFontSize;
        String[] coords = gameProperties.getProperty("gamePlay.donkeyhealth.coords").split(",");
        donkeyHealthPosition = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]));
        donkeyHealthFont = new Font("res/FSO8BITR.TTF", timeFontSize);

        background = new Image(gameProps.getProperty("backgroundImage"));
    }


    /** Draw all the object needed in the window */
    public void draw() {
        background.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
        donkeyKong.draw();

        // Only draw the hammer before obtain by Mario
        for (Hammer hammer : hammers) {
            if (!hammer.getGetByMario()) {
                hammer.draw();
            }
        }

        for (Platform platform : platforms) {
            platform.draw();
        }

        for (Ladder ladder : ladders) {
            ladder.draw();
        }

        // Only draw the barrel when it hasn't being destroyed
        for (Barrel barrel : barrels) {
            if (barrel.isAlive()){
                barrel.draw();
            }
        }

        player.draw();
    }


    /**
     * Render the relevant screen based on the keyboard input given by the user.
     * @param input The current mouse/keyboard input.
     */
    public void update(Input input) {
        player.update(donkeyKong, platforms, barrels, ladders, input);
        donkeyKong.update(platforms);

        for (Barrel barrel : barrels) {
            barrel.update(platforms);
        }

        for (Hammer hammer : hammers) {
            hammer.update(player);
        }

        draw();

        timeFont.drawString("TIME LEFT " + String.valueOf((maxFrames - currentFrame) / 60), timePosition.x, timePosition.y);
        scoreFont.drawString("SCORE " + String.valueOf(score), scorePosition.x, scorePosition.y);
        donkeyHealthFont.drawString("DONKEY HEALTH " + donkeyKong.getHealth(), donkeyHealthPosition.x, donkeyHealthPosition.y);


        currentFrame++;
    }


    /** Increments score of destroying barrels */
    public void destroyBarrelScore() {
        score += DESTROY_BARREL;
    }


    /** Increment score of jumping over barrel */
    public void jumpOverBarrelScore() {
        score += JUMP_OVER_BARREL;
    }


    /** Get the score by jumping oer barrels and destroy them */
    public int getScore() {return score;}


    /** Increment score by given input number */
    public void incrementScore(int increment) {score += increment;}


    /** If lost, set score to 0 */
    public void lostScore() {score = 0;}


    /** Return the remain time of the game */
    public int remainTime() {return Math.max(0, (maxFrames - currentFrame) / 60);}


    /** Return whether Player wins the game */
    public boolean isWin() {return !donkeyKong.isAlive() && player.isAlive() && (maxFrames - currentFrame) > 0;}


    /** If the time runs out or the Donkey Kong or Mario isn't alive, game over.*/
    public boolean gameOver() {
        if (currentFrame >= maxFrames) {
            return true;
        }

        if (!player.isAlive()) {
            return true;
        }

        return !donkeyKong.isAlive();
    }

    /** Get the level of the game state
     * @return current level of the game */
    public int getLevel() {return level;}


    /** @return the ArrayList of Platform on this screen */
    public ArrayList<Platform> getPlatforms() {return platforms;}


    /** Get the Donkey
     * @return the final boss donkey kong*/
    public DonkeyKong getDonkeyKong() {return donkeyKong;}


    /** Get the Mario
     * @return the player Mario*/
    public Mario getMario() {return player;}

}
