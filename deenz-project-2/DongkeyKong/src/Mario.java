import bagel.*;
import bagel.util.Point;
import bagel.util.Rectangle;

import java.util.ArrayList;
import java.util.Properties;

public class Mario extends GameObject implements CanShoot, CanFall {
    private static final double TOLERANCE = 5;

    private final GameScreen gameScreen;

    private final int WINDOW_WIDTH;

    private final Image marioLeft = new Image("res/mario_left.png");
    private final Image marioRight = new Image("res/mario_right.png");
    private final Image marioHammerLeft = new Image("res/mario_hammer_left.png");
    private final Image marioHammerRight = new Image("res/mario_hammer_right.png");
    private final Image marioBlasterLeft = new Image("res/mario_blaster_left.png");
    private final Image marioBlasterRight = new Image("res/mario_blaster_right.png");

    private enum MarioState {
        LEFT_WITHOUT_ANY, RIGHT_WITHOUT_ANY,
        LEFT_WITH_HAMMER, RIGHT_WITH_HAMMER,
        LEFT_WITH_BLASTER, RIGHT_WITH_BLASTER
    }

    private boolean onAir = true;
    private boolean onLadder = false;
    private boolean alive = true;

    private double speedY; // Mario current speed on y

    private MarioState marioState = MarioState.RIGHT_WITHOUT_ANY;
    private boolean hasHammer = false;
    private boolean hasBlaster = false;
    private int bulletCount;


    public Mario(Properties gameProperties, GameScreen gameScreen, int level) {
        super(0, 0, "res/mario_left.png");
        WINDOW_WIDTH = Integer.parseInt(gameProperties.getProperty("window.width"));

        String marioPositionStr = gameProperties.getProperty("mario.level" + level);
        String[] coords = marioPositionStr.split(",");
        int marioX = Integer.parseInt(coords[0]), marioY = Integer.parseInt(coords[1]);
        setPosition(new Point(marioX, marioY));

        this.gameScreen = gameScreen;

        speedY = Utils.UNMOVE;
    }


    /* Return the correct image based on Mario current state */
    private Image getCurrentImage() {
        return switch (marioState) {
            case LEFT_WITH_HAMMER -> marioHammerLeft;
            case RIGHT_WITH_HAMMER -> marioHammerRight;
            case LEFT_WITH_BLASTER -> marioBlasterLeft;
            case RIGHT_WITH_BLASTER -> marioBlasterRight;
            case RIGHT_WITHOUT_ANY -> marioRight;
            default -> marioLeft;
        };
    }


    /** Draw the image at current position */
    @Override
    public void draw() {
        setImage(getCurrentImage());
        getImage().draw(getPosition().x, getPosition().y);
    }


    /** Update that handles moving and interact with enemy
     * @param donkeyKong  The final boss it needs to destroy
     * @param platforms  The list of platforms that Mario can stand on
     * @param barrels  The list of barrels that Mario can destroy with hammer, otherwise Mario will die when interacting with them
     * @param ladders The list of ladders that Mario can climb on
     * @param input  The input that controls Mario movement */
    public void update (DonkeyKong donkeyKong, ArrayList<Platform> platforms, ArrayList<Barrel> barrels,ArrayList<Ladder> ladders, Input input) {
        // Update Mario horizontal motion
        move(input);

        // Climbing ladder or not
        climbLadder(ladders, input);

        // Falling or not
        fallToGround(platforms);

        // If Mario has hammer, it can destroy barrels. If not, game over
        interactWithEnemyAndBarrels(barrels, donkeyKong);

        for (Barrel barrel : barrels) {
            jumpOverBarrel(barrel, platforms);
        }

    }


    /** Updates that handle the shooting of Mario.
     * @param input Mario can shoot at level 2 with key S*/
    public void update(Input input) {
        if (gameScreen.getLevel() == 2) {
            if (input.wasPressed(Keys.S)) {
                shoot((GameScreenLevelTwo) gameScreen);
            }

            if (bulletCount == 0) {
                switch (marioState) {
                    case LEFT_WITH_BLASTER -> marioState = MarioState.LEFT_WITHOUT_ANY;
                    case RIGHT_WITH_BLASTER -> marioState = MarioState.RIGHT_WITHOUT_ANY;
                }

                hasBlaster = false;
            }
        }

    }


    /** Switch statement of Mario. If Mario has blaster, reset the count to 0 after obtaining hammer */
    public void obtainHammer() {
        hasHammer = true;
        hasBlaster = false;
        bulletCount = 0;
        switch (marioState) {
            case LEFT_WITH_BLASTER, LEFT_WITHOUT_ANY, LEFT_WITH_HAMMER -> marioState = MarioState.LEFT_WITH_HAMMER;
            case RIGHT_WITH_BLASTER, RIGHT_WITHOUT_ANY, RIGHT_WITH_HAMMER -> marioState = MarioState.RIGHT_WITH_HAMMER;
        }
    }


    /** @return whether Mario has hammer*/
    public boolean isHasHammer() {return hasHammer;}


    /** Increment the bullet count and set Mario statement with blaster based on its direction */
    public void obtainBlaster() {
        hasBlaster = true;
        hasHammer = false;
        bulletCount += Utils.BULLET_FOR_EACH;
        switch (marioState) {
            case LEFT_WITH_HAMMER, LEFT_WITHOUT_ANY -> marioState = MarioState.LEFT_WITH_BLASTER;
            case RIGHT_WITH_HAMMER,RIGHT_WITHOUT_ANY -> marioState = MarioState.RIGHT_WITH_BLASTER;
        }
    }


    /** Mario can shoot when:
     * 1. Has blaster
     * 2. Has bullet */
    public boolean canShoot() {
        return hasBlaster && bulletCount > 0;
    }


    /** @return bullet count */
    public int getBulletCount() {
        return bulletCount;
    }

    /** Mario shoots with blaster */
    @Override
    public void shoot(GameScreenLevelTwo gameScreen) {
        if (canShoot()) {
            gameScreen.spawnBullet(getPosition().x, getPosition().y, marioState == MarioState.RIGHT_WITH_BLASTER);
            bulletCount--;
        }
    }


    /** Determine the motion of Mario when he is on the air
     * @param platforms The list of platforms that Mario to check against whether he stands on it or will land on it at the
     *  falling motion */
    @Override
    public void fallToGround(ArrayList<Platform> platforms) {
        onAir = !onLadder;
        setImage(getCurrentImage());

        double marioLeft = getPosition().x - getImage().getWidth() / 2;
        double marioRight = getPosition().x + getImage().getWidth() / 2;

        // It will only start falling when the speedY is towards bottom of the window
        if (speedY > 0) {
            double marioBottomY = getPosition().y + getImage().getHeight() / 2;

            for (Platform platform : platforms) {
                double platformTopY = platform.getPosition().y - platform.getImage().getHeight() / 2;
                // Tolerance for avoiding floating point comparison
                boolean onTop = marioBottomY <= platformTopY + TOLERANCE && marioBottomY >= platformTopY - TOLERANCE;

                double platformLeft = platform.getPosition().x - platform.getImage().getWidth() / 2;
                double platformRight = platform.getPosition().x + platform.getImage().getWidth() / 2;
                // Make sure it is actually above the platform
                boolean abovePlatform = marioRight > platformLeft && marioLeft < platformRight;

                // It do above a platForm and current is close to the top of it
                if (abovePlatform && onTop) {
                    onAir = false;
                    speedY = 0;
                    setPosition(new Point(getPosition().x, platformTopY - getImage().getHeight() / 2));
                    break;
                }
            }
        }

        if (onAir) {
            speedY = speedY + Utils.GRAVITY;
            setPosition(new Point(getPosition().x, getPosition().y + speedY));
        }
    }


    /* If input is LEFT or RIGHT, compute Mario new position */
    private void move(Input input) {
        if (!onLadder && !onAir && input.isDown(Keys.SPACE)) {
            speedY = Utils.MARIOSPEEDY;
            onAir = true;
        }

        if (input.isDown(Keys.LEFT)) {
            double halfMarioWidth = getCurrentImage().getWidth() / 2;
            double newX = getPosition().x - Utils.MARIOSPEEDX;
            if (newX - halfMarioWidth >= 0) {
                setPosition(new Point(newX, getPosition().y));
                if (hasHammer) {
                    marioState = MarioState.LEFT_WITH_HAMMER;
                } else if (hasBlaster) {
                    marioState = MarioState.LEFT_WITH_BLASTER;
                } else {
                    marioState = MarioState.LEFT_WITHOUT_ANY;
                }
            }
        }

        if (input.isDown(Keys.RIGHT)) {
            double marioHalfWidth = getCurrentImage().getWidth() / 2;
            double newX = getPosition().x + Utils.MARIOSPEEDX;
            if (newX + marioHalfWidth <= WINDOW_WIDTH) {
                setPosition(new Point(newX, getPosition().y));
                if (hasHammer) {
                    marioState = MarioState.RIGHT_WITH_HAMMER;
                } else if (hasBlaster) {
                    marioState = MarioState.RIGHT_WITH_BLASTER;
                } else {
                    marioState = MarioState.RIGHT_WITHOUT_ANY;
                }
            }
        }
    }


    /* It takes in a list of ladders and check based on the input whether Mario can climb on any ladders */
    private void climbLadder(ArrayList<Ladder> ladders, Input input) {
        onLadder = false;
        // Check whether it has intersection with any ladder with UP or DOWN
        for (Ladder ladder : ladders) {
            if (this.getBoundingBox().intersects(ladder.getBoundingBox()) && isBetweenLadder(ladder)) {
                onLadder = true;
                speedY = 0;

                if (input.isDown(Keys.UP)) {
                    setPosition(new Point(getPosition().x, getPosition().y - Utils.SPEED_CLIMB));
                    return;
                }

                if (input.isDown(Keys.DOWN)) {
                    double marioBottom = getPosition().y + getCurrentImage().getHeight() / 2;
                    double ladderBottom = ladder.getPosition().y + ladder.getHeight() / 2;

                    // Make sure Mario won't go under the ladder's bottom
                    if (marioBottom + Utils.SPEED_CLIMB <= ladderBottom) {
                        setPosition(new Point(getPosition().x, getPosition().y + Utils.SPEED_CLIMB));
                        return;
                    }
                }
            }
        }
    }


    /* It will return true if Mario is between the left and right edges of the ladder */
    private boolean isBetweenLadder(Ladder ladder) {
        double ladderLeftEdge = ladder.getPosition().x - ladder.getWidth() / 2 ,
                ladderRightEdge = ladder.getPosition().x + ladder.getWidth() / 2;
        return (getPosition().x <= ladderRightEdge && getPosition().x >= ladderLeftEdge);
    }


    /* Interact with enemies. When Mario has interaction with enemies, if Mario has hammer, it will destroy it. Else
     *  you lose. If Mario has hammer and destroy DonkeyKong, you win. */
    private void interactWithEnemyAndBarrels(ArrayList<Barrel> barrels, DonkeyKong donkeyKong) {
        for (Barrel barrel: barrels) {
            if (getBoundingBox().intersects(barrel.getBounding()) && !onLadder) {

                // Mario doesn't obtain the hammer, game over
                if (!hasHammer) {
                    alive = false;
                    return;
                }

                // If Mario has hammer, he can destroy the barrel
                if (marioState == MarioState.LEFT_WITH_HAMMER || marioState == MarioState.RIGHT_WITH_HAMMER) {
                    barrel.destroy();
                    gameScreen.destroyBarrelScore();
                }
            }
        }

        // Interaction with DonkeyKong
        if (getBoundingBox().intersects(donkeyKong.getBounding()) && !onLadder) {
            // Mario doesn't obtain the hammer, game over
            if (!hasHammer) {
                alive = false;
                return;
            }

            // Mario has hammer, destroy DonkeyKong
            // GAME OVER
            if (marioState == MarioState.LEFT_WITH_HAMMER || marioState == MarioState.RIGHT_WITH_HAMMER) {
                donkeyKong.destroy();
            }
        }
    }


    /* When Mario jumps over a barrel, gain score. It won't gain score if there is a platform
     *  between Mario and barrel. Mario can gain score everytime he jumps over a barrel */
    private void jumpOverBarrel(Barrel barrel, ArrayList<Platform> platforms) {
        Rectangle marioBox = getBoundingBox();
        Rectangle barrelBox = barrel.getBounding();

        boolean horizontallyAligned = marioBox.right() > barrelBox.left() && marioBox.left() < barrelBox.right();
        boolean verticallyAbove = marioBox.bottom() <= barrelBox.top();

        // It is finishing the jump motion (already across the barrel), reset it for future jump over
        if (!(onAir && speedY > 0)) {
            barrel.resetJumpScore();
            return;
        }

        // Check if any platform blocks between Mario and barrel
        for (Platform platform : platforms) {
            Rectangle platformBox = platform.getBoundingBox();

            // There is a platform between Mario and barrel
            boolean verticallyBlocks = platformBox.top() >= marioBox.bottom() - TOLERANCE &&
                                        platformBox.top() < barrelBox.top();
            boolean horizontallyOverlap = marioBox.right() > platformBox.left() && marioBox.left() < platformBox.right();

            // If there is a platform between Mario and barrel, it cannot score. Reset it for future directly jump over
            if (verticallyBlocks && horizontallyOverlap) {
                barrel.resetJumpScore();
                return;
            }
        }

        // If it is horizontal and vertically align with the barrel, and it hasn't scored in this jump, score
        if (horizontallyAligned && verticallyAbove && !barrel.hasScored()) {
            gameScreen.jumpOverBarrelScore();
            barrel.scored();
        }
    }


    /** @return whether Mario is alive */
    public boolean isAlive () {return alive;}


    /** Set alive based on the input */
    public void setAlive(boolean alive) {
        this.alive = alive;
    }

}
