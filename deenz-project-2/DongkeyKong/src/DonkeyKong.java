import java.util.Properties;

public class DonkeyKong extends Enemy {
    private int health = Utils.DONKEY_HEALTH;

    public DonkeyKong(double x, double y, Properties gameProperties) {
        super(x, y,"res/donkey_kong.png");
    }


    /** Reduce health on donkey when it gets hit by bullet */
    public void reduceHealth(int amount) {
        health -= amount;
        if (health <= 0) {
            destroy();
        }
    }


    /** When DonkeyKong get destroy, it health will be 0 */
    @Override
    public void destroy() {
        super.destroy();
        health = 0;
    }


    /** Update that the DonkeyKong need to get shoot DONKEY_HEALTH time to die
     * @return true if donkey is still alive else false */
    @Override
    public boolean isAlive() {return health > 0;}


    /** Return current health */
    public int getHealth() {return health;}
}
