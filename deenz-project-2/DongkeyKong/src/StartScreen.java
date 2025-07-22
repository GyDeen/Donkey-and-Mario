import bagel.*;
import java.util.Properties;

public class StartScreen {
    private final int titleFontSize;
    private final int titleY;
    private final String title;
    private final Font titleFont;

    private final int promptFontSize;
    private final int promptY;
    private final String prompt;
    private final Font promptFont;

    private final Image backgroundImage;


    public StartScreen(Properties gameProps, Properties messageProps) {
        titleFontSize = Integer.parseInt(gameProps.getProperty("home.title.fontSize"));
        titleY = Integer.parseInt(gameProps.getProperty("home.title.y"));
        title = messageProps.getProperty("home.title");
        titleFont = new Font(gameProps.getProperty("font"), titleFontSize);

        promptFontSize = Integer.parseInt(gameProps.getProperty("home.prompt.fontSize"));
        promptY = Integer.parseInt(gameProps.getProperty("home.prompt.y"));
        prompt = messageProps.getProperty("home.prompt");
        promptFont = new Font(gameProps.getProperty("font"), promptFontSize);

        backgroundImage = new Image(gameProps.getProperty("backgroundImage"));
    }


    /** Draw the start screen */
    public void drawStartScreen() {
        backgroundImage.draw(Window.getWidth() / 2.0, Window.getHeight() / 2.0);
        double titleX = (Window.getWidth() - titleFont.getWidth(title)) / 2;
        double promptX = (Window.getWidth() - promptFont.getWidth(prompt)) / 2;

        titleFont.drawString(title, titleX, titleY);
        promptFont.drawString(prompt, promptX, promptY);
    }
}
