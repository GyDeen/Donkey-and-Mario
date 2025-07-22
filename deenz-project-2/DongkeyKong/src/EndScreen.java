import bagel.Font;
import bagel.Image;
import bagel.Window;

import java.util.Properties;

public class EndScreen {
    private final Image background;

    private int statusFontSize;
    private final double statusY;
    private final double statusX;
    private final String gameStatus;
    private final Font statusFont;

    private final int scoreFontSize;
    private int finalScore;
    private final double scoreY;
    private final double scoreX;
    private final String scoreText;
    private final Font scoreFont;

    private final double returnX;
    private final double returnY;
    private final String returnHome;
    private final Font returnFont;

    public EndScreen(Properties gameProperties, Properties messageProperties, int score, int time, boolean win) {
        background = new Image(gameProperties.getProperty("backgroundImage"));

        if (win) {
            gameStatus = messageProperties.getProperty("gameEnd.won");
        } else {
            gameStatus = messageProperties.getProperty("gameEnd.lost");
        }
        statusFontSize = Integer.parseInt(gameProperties.getProperty("gameEnd.status.fontSize"));
        statusFont = new Font(gameProperties.getProperty("font"), statusFontSize);
        statusY = Double.parseDouble(gameProperties.getProperty("gameEnd.status.y"));
        statusX = (Window.getWidth() - statusFont.getWidth(gameStatus)) / 2;

        // Only win will gain time bonus score
        if (win) {
            finalScore = score + 3 * time;
        } else {
            finalScore = 0;
        }
        scoreText = messageProperties.getProperty("gameEnd.score") + " " + finalScore;
        scoreFontSize = Integer.parseInt(gameProperties.getProperty("gameEnd.scores.fontSize"));
        scoreFont = new Font(gameProperties.getProperty("font"), scoreFontSize);
        scoreY = Double.parseDouble(gameProperties.getProperty("gameEnd.scores.y"));
        scoreX = (Window.getWidth() - scoreFont.getWidth(scoreText)) / 2;

        returnHome = messageProperties.getProperty("gameEnd.continue");
        returnFont = new Font(gameProperties.getProperty("font"), statusFontSize);
        returnX = (Window.getWidth() - returnFont.getWidth(returnHome)) / 2;
        returnY = Window.getHeight() - 100;
    }


    /** Draw the end screen */
    public void drawEndScreen() {
        background.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);

        statusFont.drawString(gameStatus, statusX, statusY);
        scoreFont.drawString(scoreText, scoreX, scoreY);
        returnFont.drawString(returnHome, returnX, returnY);
    }


}
