import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;

public class Ladder extends GameObject implements CanFall {
    private boolean onGround = false;
    private double ladderSpeedY = Utils.GRAVITY_LADDER;
    private ArrayList<Platform> platforms;

    public Ladder(double x, double y, ArrayList<Platform> platforms) {
        super(x, y, "res/ladder.png");
        this.platforms = platforms;
    }


    /** It will call at the time ladder got read in. This method will place the ladder at the top of the closest
     *  platform. */
    public void update() {
        fallToGround(platforms);
    }


    /** Generating ladder's falling motion.
     * @parm platforms The list of platform that it needs to check against */
    @Override
    public void fallToGround(ArrayList<Platform> platforms) {
        if (onGround) return;

        // Simulate gravity
        setPosition(new Point(getPosition().x, getPosition().y + ladderSpeedY));

        // Check if landed on any platform
        for (Platform platform : platforms) {
            if (getBoundingBox().intersects(platform.getBoundingBox())) {
                // Snap ladder to the top of the platform
                double platformTopY = platform.getPosition().y - platform.getImage().getHeight() / 2;
                setPosition(getPosition().x, platformTopY - getImage().getHeight() / 2);
                onGround = true;
                ladderSpeedY = 0;
                break;
            }
        }
    }


    /** @return the width of the image */
    public double getWidth() {
        return getImage().getWidth();
    }


    /** @return the height of the image */
    public double getHeight() {
        return getImage().getHeight();
    }
}
