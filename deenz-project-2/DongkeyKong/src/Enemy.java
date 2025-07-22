import bagel.util.Point;
import bagel.*;
import bagel.util.Rectangle;

import java.util.ArrayList;

public abstract class Enemy extends GameObject implements CanFall{
    private static final double OUT_OF_WINDOW = -200;

    private double speedY = 0;

    private boolean hasLand = false;
    private boolean isAlive = true;


    public Enemy(double x, double y, String imagePath) {
        super(x, y, imagePath);
    }


    /** Draw the object */
    public void draw() {
        if (isAlive) {
            getImage().draw(getPosition().x, getPosition().y - getImage().getHeight() / 2);
        }
    }


    /** It will let the object falling from the position it spawns landing on the
     *  desire position
     *  @param platforms An arraylist that contains all the platforms to check whether the Enemy has land on it */
    public void update(ArrayList<Platform> platforms) {
        if (!isAlive) {
            destroy();
            return;
        }

        // Object will fall only when its on air
        if (!hasLand) {
            fallToGround(platforms);
        }

        draw();
    }


    /** Generate the bound for the enemy which can interact with other objects
     * @return hitbox of current enemy */
    public Rectangle getBounding() {
        return getImage().getBoundingBoxAt(getPosition());
    }


    /** @return whether the Enemy has landed or not */
    public boolean isHasLand() {
        return hasLand;
    }


    /** Destroy the enemy, and remove the hitbox from the window */
    public void destroy() {
        isAlive = false;
        setPosition(OUT_OF_WINDOW, OUT_OF_WINDOW);
    }


    /** @return whether this enemy is alive or not */
    public boolean isAlive() {return isAlive;}


    /** Calculate the falling motion of the object
     * @param platforms The platform list that the Enemy need to check against with */
    @Override
    public void fallToGround(ArrayList<Platform> platforms) {
        double gravity = Utils.ENEMY_GRAVITY;

        if (this.getClass() == Barrel.class) {
            gravity = Utils.GRAVITY;
        }

        speedY += gravity;
        double nextY = getPosition().y + speedY;

        // Checking whether next position will have collision with platform
        Point nextPosition = new Point(getPosition().x, nextY);
        Rectangle nextBounds = getImage().getBoundingBoxAt(nextPosition);

        // Check whether it will interact with any platform
        for (Platform platform : platforms) {
            // There is an intersection with the platform, it should land on the top of it
            if (nextBounds.intersects(platform.getBoundingBox())) {
                setPosition(nextPosition.x, platform.getPosition().y - platform.getImage().getHeight() / 2);
                speedY = 0;
                hasLand = true;
                draw();
                return;
            }
        }

        setPosition(nextPosition);
    }
}

