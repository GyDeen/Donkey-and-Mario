import bagel.Image;

import java.util.Properties;

public class Banana extends Shootable {
    private static final Image bananaImage = new Image("res/banana.png");

    public Banana(Properties props) {
        super(props, Utils.BANANA_SPEED, Utils.BANANA_DISTANCE);
    }


    /** Set the image to bananaImage */
    @Override
    public void chooseImage() {
        setCurrentImage(bananaImage);
    }
}