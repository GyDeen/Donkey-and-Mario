import java.util.ArrayList;
import java.util.Properties;

public class IntelliMonkey extends Monkey implements CanShoot{
    private int SAFE_BANANA_COUNT = 5;
    private int FRAME_TO_SHOOT = 300;

    private ArrayList<Banana> bananas = new ArrayList<>();
    private int shootCounter = 0;


    public IntelliMonkey(Properties gameProps, int index, double x, double y, String direction, boolean isIntelligent, ArrayList<Platform> platforms) {
        super(gameProps, index, x, y, direction, isIntelligent, platforms);
        for (int i = 0; i < SAFE_BANANA_COUNT; i++) {
            bananas.add(new Banana(gameProps));
        }
    }


    /** Intelligent Monkey can shoot banana based on the direction of it */
    @Override
    public void shoot() {
        for (Banana banana: bananas) {
            if (!banana.isActive()) {
                banana.activate(getPosition().x, getPosition().y, getDirection() == Direction.RIGHT);
                break;
            }
        }
    }


    /** Intelligent monkey can shoot banana every 5 seconds.
     * @parm platforms A list of platform that Intelligent Monkey to check against which one it lands on */
    @Override
    public void update(ArrayList<Platform> platforms) {
        super.update(platforms);

        shootCounter++;

        if (shootCounter == FRAME_TO_SHOOT) {
            shoot();
            shootCounter = 0;
        }

        for (Banana banana: bananas) {
            banana.update();
        }

    }

    /** Get all the activate banana
     * @return all the banana pool of this intelligent monkey */
    public ArrayList<Banana> getActivateBananas() {
        ArrayList<Banana> activateBananas = new ArrayList<>();
        for (Banana banana: bananas) {
            if (banana.isActive()) {
                activateBananas.add(banana);
            }
        }

        return activateBananas;
    }


}
