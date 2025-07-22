import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.Properties;

public class Bullet extends Shootable {
    private Image leftImage = new Image("res/bullet_left.png");
    private Image rightImage = new Image("res/bullet_right.png");

    private double bulletDistance = Utils.BULLET_DISTANCE;
    private double currentDistance;
    private double bulletSpeed = Utils.BULLET_SPEED;

    private Point currentPoint;
    private boolean active;
    private boolean isRight;
    private double windowWidth;


    public Bullet(Properties gamePros) {
        super(gamePros, Utils.BULLET_SPEED, Utils.BULLET_DISTANCE);
    }


    /** Choose the correct image based on the direction of the bullet */
    @Override
    public void chooseImage() {setCurrentImage(isRight() ? rightImage : leftImage);}
}
