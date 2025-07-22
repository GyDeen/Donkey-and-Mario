import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import java.util.Properties;

/**
 * Abstract superclass representing a projectile such as a Banana or Bullet.
 * It encapsulates the common state and behaviour needed by both.
 */
public abstract class Shootable {
    private Image currentImage;

    private final double speed;
    private final double maxDistance;
    private double currentDistance;

    private Point currentPoint;
    private boolean isRight;

    private boolean active;

    private final double windowWidth;


    public Shootable(Properties gameProps, double speed, double maxDistance) {
        this.windowWidth = Double.parseDouble(gameProps.getProperty("window.width"));
        this.speed = speed;
        this.maxDistance = maxDistance;
        this.active = false;
    }


    /** Activate the shootable based on input position and determine the image based on whether it is facing right */
    public void activate(double x, double y, boolean isRight) {
        this.currentPoint = new Point(x, y);
        this.isRight = isRight;
        this.currentDistance = 0;
        this.active = true;
        chooseImage();
    }


    /** Choose the correct image */
    public abstract void chooseImage();


    /** Deactivate this object */
    public void deactivate() {active = false;}


    /** @return active */
    public boolean isActive() {return active;}


    /** Update the object based on whether it is active or not */
    public void update() {
        if (!active) { return; }

        currentDistance += speed;
        currentPoint = new Point(currentPoint.x + (isRight ? speed : -speed), currentPoint.y);

        // If out‑of‑bounds or exceeded range, deactivate
        if (currentDistance >= maxDistance || currentPoint.x > windowWidth || currentPoint.x < 0) {
            deactivate();
        }

        if (active) {
            draw();
        }
    }

    /** Renders the current image centred vertically on the logical y coordinate */
    public void draw() {
        currentImage.draw(currentPoint.x, currentPoint.y - currentImage.getHeight() / 2);
    }

    /** @return a rectangle representing this projectile's hitbox */
    public Rectangle getBounding() {
        return currentImage.getBoundingBoxAt(currentPoint);
    }


    /** Set current image */
    public void setCurrentImage(Image image) {this.currentImage = image;}


    /** @return isRight */
    public boolean isRight() {return isRight;}
}
