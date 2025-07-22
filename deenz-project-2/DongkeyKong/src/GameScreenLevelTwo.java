import bagel.Font;
import bagel.Input;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Properties;

public class GameScreenLevelTwo extends GameScreen {
    private static final int POSITION = 0;
    private static final int POSITION_X = 0;
    private static final int POSITION_Y = 1;

    private static final int DIRECTION = 1;

    private static final int DESTROY_MONKEY = 100;

    private ArrayList<Platform> platforms = getPlatforms();

    private ArrayList<Monkey> monkeys = new ArrayList<>();
    private int intelliMonkeyCount;
    private int normalMonkeyCount;

    private ArrayList<Blaster> blasters = new ArrayList<>();
    private int blasterCount;

    private ArrayList<Bullet> bullets = new ArrayList<>();
    private final int bulletFontSize;
    private Point bulletFontPosition;
    private Font bulletFont;

    private Properties props;


    public GameScreenLevelTwo(Properties gameProps) {
        super(gameProps, 2);

        String keyForLevel = "level" + super.getLevel();
        props = gameProps;

        normalMonkeyCount = Integer.parseInt(gameProps.getProperty("normalMonkey." + keyForLevel + ".count"));
        for (int i = 1; i <= normalMonkeyCount; i++) {
            String key = "normalMonkey." + keyForLevel + "." + i;
            String[] value = gameProps.getProperty(key).split(";");
            String[] position = value[POSITION].split(",");

            double x = Double.parseDouble(position[POSITION_X]);
            double y = Double.parseDouble(position[POSITION_Y]);

            monkeys.add(new NormalMonkey(gameProps, i, x, y, value[DIRECTION], false, getPlatforms()));
        }

        intelliMonkeyCount = Integer.parseInt(gameProps.getProperty("intelligentMonkey." + keyForLevel + ".count"));
        for (int i = 1; i <= intelliMonkeyCount; i++) {
            String key = "intelligentMonkey." + keyForLevel + "." + i;
            String[] value = gameProps.getProperty(key).split(";");
            String[] position = value[POSITION].split(",");

            double x = Double.parseDouble(position[POSITION_X]);
            double y = Double.parseDouble(position[POSITION_Y]);

            monkeys.add(new IntelliMonkey(gameProps, i, x, y, value[DIRECTION], true, getPlatforms()));
        }

        blasterCount = Integer.parseInt(gameProps.getProperty("blaster." + keyForLevel + ".count"));
        for (int i = 1; i <= blasterCount; i++) {
            String key = "blaster." + keyForLevel + "." + i;
            String[] value = gameProps.getProperty(key).split(";");
            String[] position = value[POSITION].split(",");
            blasters.add(new Blaster(gameProps, 2, i));
        }

        bulletFontSize = Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"));
        String[] coords = gameProps.getProperty("gamePlay.donkeyhealth.coords").split(",");
        bulletFontPosition = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]) + Utils.DISTANCE_BETWEEN_FONT);
        bulletFont = new Font("res/FSO8BITR.TTF", bulletFontSize);
    }


    public GameScreenLevelTwo(Properties gameProps, int score) {
        super(gameProps, 2);

        incrementScore(score);

        String keyForLevel = "level" + super.getLevel();
        props = gameProps;

        normalMonkeyCount = Integer.parseInt(gameProps.getProperty("normalMonkey." + keyForLevel + ".count"));
        for (int i = 1; i <= normalMonkeyCount; i++) {
            String key = "normalMonkey." + keyForLevel + "." + i;
            String[] value = gameProps.getProperty(key).split(";");
            String[] position = value[POSITION].split(",");

            double x = Double.parseDouble(position[POSITION_X]);
            double y = Double.parseDouble(position[POSITION_Y]);

            monkeys.add(new NormalMonkey(gameProps, i, x, y, value[DIRECTION], false, getPlatforms()));
        }

        intelliMonkeyCount = Integer.parseInt(gameProps.getProperty("intelligentMonkey." + keyForLevel + ".count"));
        for (int i = 1; i <= intelliMonkeyCount; i++) {
            String key = "intelligentMonkey." + keyForLevel + "." + i;
            String[] value = gameProps.getProperty(key).split(";");
            String[] position = value[POSITION].split(",");

            double x = Double.parseDouble(position[POSITION_X]);
            double y = Double.parseDouble(position[POSITION_Y]);

            monkeys.add(new IntelliMonkey(gameProps, i, x, y, value[DIRECTION], true, getPlatforms()));
        }

        blasterCount = Integer.parseInt(gameProps.getProperty("blaster." + keyForLevel + ".count"));
        for (int i = 1; i <= blasterCount; i++) {
            String key = "blaster." + keyForLevel + "." + i;
            String[] value = gameProps.getProperty(key).split(";");
            String[] position = value[POSITION].split(",");
            blasters.add(new Blaster(gameProps, 2, i));
        }

        bulletFontSize = Integer.parseInt(gameProps.getProperty("gamePlay.score.fontSize"));
        String[] coords = gameProps.getProperty("gamePlay.donkeyhealth.coords").split(",");
        bulletFontPosition = new Point(Double.parseDouble(coords[0]), Double.parseDouble(coords[1]) + Utils.DISTANCE_BETWEEN_FONT);
        bulletFont = new Font("res/FSO8BITR.TTF", bulletFontSize);
    }


    /**
     * Render the relevant screen based on the keyboard input given by the user.
     * @param input The current mouse/keyboard input.
     */
    @Override
    public void update(Input input) {
        super.update(input);

        getMario().update(input);

        for (Monkey monkey : monkeys) {
            monkey.update(platforms);


            if (getMario().getBoundingBox().intersects(monkey.getBounding())) {
                if (getMario().isHasHammer()) {
                    monkey.destroy();
                    gainScoreDestroyMonkey();
                } else {
                    getMario().setAlive(false);
                }
            }

            if (monkey instanceof IntelliMonkey) {
                ArrayList<Banana> activateBanana = ((IntelliMonkey) monkey).getActivateBananas();
                for (Banana banana : activateBanana) {
                    if (getMario().getBoundingBox().intersects(banana.getBounding())) {
                        getMario().setAlive(false);
                    }
                }
            }
        }

        for (Blaster blaster : blasters) {
            blaster.update(getMario(), this);
        }

        if (!bullets.isEmpty()) {
            for (Bullet bullet : bullets) {
                bullet.update();

                if (bullet.isActive()) {
                    for (Platform platform : platforms) {
                        // It will disappear if it hits any platform
                        if (bullet.getBounding().intersects(platform.getBoundingBox())) {
                            bullet.deactivate();
                            break;
                        }

                    }

                    for (Monkey monkey : monkeys) {
                        // It will destroy the monkey and disappear
                        if (monkey.isAlive() && bullet.getBounding().intersects(monkey.getBounding())) {
                            monkey.destroy();
                            bullet.deactivate();
                            gainScoreDestroyMonkey();
                            break;
                        }
                    }

                    // Minus 1 health on donkey
                    if (getDonkeyKong().isAlive() && bullet.getBounding().intersects(getDonkeyKong().getBounding())) {
                        getDonkeyKong().reduceHealth(1);
                        bullet.deactivate();
                    }
                }
            }
        }

        bulletFont.drawString("BULLET " + getMario().getBulletCount(), bulletFontPosition.x, bulletFontPosition.y);

    }


    /** It will activate bullet at the given position
     * @param x The x position of the bullet needs to be activated
     * @param y The y position of the bullet needs to be activated
     * @param isRight Determine the image of the bullet */
    public void spawnBullet(double x, double y, boolean isRight) {
        Bullet bullet = getBullet();
        bullet.activate(x, y, isRight);
    }


    /* Get a bullet from the list */
    private Bullet getBullet() {
        for (Bullet bullet : bullets) {
            if (!bullet.isActive()) {
                return bullet;
            }
        }

        Bullet newBullet = new Bullet(props);
        bullets.add(newBullet);
        return newBullet;
    }


    /** Add bullet when Mario get a blaster */
    public void addBullet() {
        for (int i = 0; i < Utils.BULLET_FOR_EACH; i++) {
            bullets.add(new Bullet(props));
        }
    }


    /* Gain 100 score when destroyed a Monkey*/
    private void gainScoreDestroyMonkey() {
        incrementScore(DESTROY_MONKEY);
    }
}
