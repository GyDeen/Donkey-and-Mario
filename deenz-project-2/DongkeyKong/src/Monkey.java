import bagel.Image;
import bagel.util.Point;

import java.util.ArrayList;
import java.util.Properties;

public abstract class Monkey extends Enemy {
    private int INIT_ALIGNMENT = 0;
    private int RIGHT_WINDOW_EDGE = 0;
    private int LEFT_WINDOW_EDGE = 1;
    private int PLATFORM_EDGE = 2;
    private int NOT_EDGE = 3;

    private int ROUTE = 2;

    public enum Direction {LEFT, RIGHT}
    private Direction direction;

    private boolean isIntelligent;
    String type;

    private Image leftImage;
    private Image rightImage;
    private Image currentImage;

    private ArrayList<Platform> platforms;
    private Platform supportPlatform;

    private ArrayList<Double> routes = new ArrayList<>();
    private double currentRouteLeft;
    private int routeIndex;
    private int routesSize;

    private Properties gameProps;

    private int label;


    public Monkey (Properties gameProps,int index, double x, double y, String direction, boolean isIntelligent, ArrayList<Platform> platforms) {
        super(x, y, monkeyImagePath(isIntelligent, direction));
        this.direction = Direction.valueOf(direction.toUpperCase());

        this.gameProps = gameProps;
        label = index;

        this.isIntelligent = isIntelligent;
        type = isIntelligent ? "intelligent" : "normal";

        this.platforms = platforms;

        setRotate();
        currentRouteLeft = routes.get(INIT_ALIGNMENT);
        routeIndex = INIT_ALIGNMENT;
        routesSize = routes.size();

        leftImage = new Image(monkeyImagePath(isIntelligent, "left"));
        rightImage = new Image(monkeyImagePath(isIntelligent, "right"));
        currentImage = new Image(monkeyImagePath(isIntelligent, direction));
        setImage(currentImage);

    }


    /* Get the initial image of monkey for constructor */
    private static String monkeyImagePath(boolean intelligent, String direction) {
        String typeOfMonkey = intelligent ? "intelli" : "normal";
        return "res/" + typeOfMonkey + "_monkey_" + direction + ".png";
    }


    /* Read in route for the monkey */
    private void setRotate() {
        String key = type + "Monkey.level2." + label;
        String[] parts = gameProps.getProperty(key).split(";")[ROUTE].split(",");
        routes = new ArrayList<>();
        for (String part : parts) {
            routes.add(Double.parseDouble(part));
        }
    }


    /* Make monkey moves follow its route */
    private void followRoutes() {
        double speed = Utils.MONKEY_SPEED;
        Point currentPosition = getPosition();
        double newX = (direction == Direction.RIGHT) ? currentPosition.x + speed : currentPosition.x - speed;

        double windowWidth = Double.parseDouble(gameProps.getProperty("window.width"));
        double halfWidth = currentImage.getWidth() / 2.0;

        setPosition(newX, currentPosition.y);
        currentRouteLeft -= speed;

        int edgeType = reachEdge(newX);

        // The monkey reach an edge
        if (edgeType != NOT_EDGE) {
            if (edgeType == RIGHT_WINDOW_EDGE) {
                setPosition(windowWidth - halfWidth, currentPosition.y);
            } else if (edgeType == LEFT_WINDOW_EDGE) {
                setPosition(halfWidth, currentPosition.y);
            }

            if (edgeType == PLATFORM_EDGE) {
                double platLeft = supportPlatform.getPosition().x - supportPlatform.getImage().getWidth() / 2.0;
                double platRight = supportPlatform.getPosition().x + supportPlatform.getImage().getWidth() / 2.0;

                if (direction == Direction.RIGHT) {
                    setPosition(platRight - halfWidth, currentPosition.y);
                } else {
                    setPosition(platLeft + halfWidth, currentPosition.y);
                }
            }

            advanceRoute();
            return;
        }

        // Move to next route if the route has finished
        if (currentRouteLeft <= 0) {
            advanceRoute();
        }

        // Update image based on direction
        currentImage = (direction == Direction.RIGHT) ? rightImage : leftImage;
        setImage(currentImage);
    }



    /* Reach edge, stop walking, turn around and move to next route */
    private void advanceRoute() {
        routeIndex = (routeIndex + 1) % routesSize;
        currentRouteLeft = routes.get(routeIndex);
        reverseDirection();
    }


    /* Reverse the direction of Monkey */
    private void reverseDirection() {
        if (direction == Direction.LEFT) {
            direction = Direction.RIGHT;
        } else if (direction == Direction.RIGHT) {
            direction = Direction.LEFT;
        }
    }


    /* Check whether Monkey will reach edges such as windows' edges or platforms edges at the input x_axis */
    private int reachEdge(double x) {
        double windowWidth = Double.parseDouble(gameProps.getProperty("window.width"));
        double monkeyLeft = x - leftImage.getWidth() / 2;
        double monkeyRight = x + rightImage.getWidth() / 2;


        if (monkeyRight > windowWidth) {
            return RIGHT_WINDOW_EDGE;
        } else if (monkeyLeft < 0) {
            return LEFT_WINDOW_EDGE;
        }

        // Make sure it only check with the platform that it stand on
        double platformLeft = supportPlatform.getPosition().x - supportPlatform.getImage().getWidth() / 2;
        double platformRight = supportPlatform.getPosition().x + supportPlatform.getImage().getWidth() / 2;
        // Will go off the platform
        if ((monkeyLeft < platformLeft) || (monkeyRight > platformRight)) {
            return PLATFORM_EDGE;
        }

        return NOT_EDGE;
    }


    /** Monkey will follow its route when it is alive.
     * @parm platforms The list of platforms that Monkey needs to check against whether it stand on it or will fall on it */
    @Override
    public void update(ArrayList<Platform> platforms) {
        super.update(platforms);

        if (!isAlive()) {
            return;
        }

        if (!isHasLand()) {
            fallToGround(platforms);
        }

        if (isHasLand()) {
            for (Platform platform : platforms) {
                if (getBounding().intersects(platform.getBoundingBox())) {
                    supportPlatform = platform;
                    break;
                }
            }
        }

        if (isHasLand()) {
            followRoutes();
        }

        draw();
    }


    /** Draw the image based on current direction at current position */
    @Override
    public void draw() {
        if (isAlive()) {
            currentImage.draw(getPosition().x, getPosition().y - currentImage.getHeight() / 2);
        }
    }


    /** @return the direction of the monkey */
    public Direction getDirection() {
        return direction;
    }


    /** @return the platforms */
    public ArrayList<Platform> getPlatforms() {return platforms;}

}
