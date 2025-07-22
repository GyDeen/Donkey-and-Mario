import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Properties;

public class Blaster extends GameObject {
    private boolean getByMario = false;

    public Blaster(Properties gameProperties, int level, int i) {
        super(0, 0, "res/blaster.png");
        String blasterPositionStr = gameProperties.getProperty("blaster.level" + level + "." + i);
        String[] coords = blasterPositionStr.split(",");
        int blasterX = Integer.parseInt(coords[0]), blasterY = Integer.parseInt(coords[1]);
        setPosition(blasterX, blasterY);
    }


    /** @return whether the blaster has got by Mario */
    public boolean getByMario() {return getByMario;}


    /** The blaster only belongs to Level 2. If it got by Mario, stop drawing it
     * @param mario The Player on Level 2
     * @param gameScreen The game screen it belongs to */
    public void update(Mario mario, GameScreenLevelTwo gameScreen) {
        if (getByMario) {
            return;
        }

        if (getBoundingBox().intersects(mario.getBoundingBox())) {
            mario.obtainBlaster();
            gameScreen.addBullet();
            getByMario = true;
        }

        // Only draw the hammer when it hasn't obtained
        if (!getByMario) {
            getImage().draw(getPosition().x, getPosition().y);
        }
    }
}
