import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Properties;

public class Hammer extends GameObject {
    private boolean getByMario = false;

    public Hammer(Properties gameProperties, int level, int i) {
        super(0, 0, "res/hammer.png");
        String hammerPositionStr = gameProperties.getProperty("hammer.level" + level + "." + i);
        String[] coords = hammerPositionStr.split(",");
        int hammerX = Integer.parseInt(coords[0]), hammerY = Integer.parseInt(coords[1]);
        setPosition(hammerX, hammerY);
    }


    /** Draw the image only when it hasn't got by Mario
     * @param mario The mario that can get the Hammer */
    public void update(Mario mario) {
        // Move it out of the screen
        if (getByMario) {
            return;
        }

        if (this.getBoundingBox().intersects(mario.getBoundingBox())) {
            mario.obtainHammer();
            getByMario = true;
        }

        // Only draw the hammer when it hasn't obtained
        if (!getByMario) {
            getImage().draw(getPosition().x, getPosition().y);
        }
    }

    /** @return whether current hammer has got by Mario*/
    public boolean getGetByMario() {return getByMario;}
}
