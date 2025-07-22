import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;

public abstract class GameObject {
    private Point position;
    private Image image;

    public GameObject(double x, double y, String imagePath) {
        this.position = new Point(x, y);
        if (imagePath != null) {
            this.image = new Image(imagePath);
        }
    }

    public GameObject(Point position, Image image) {
        this.position = position;
        this.image = image;
    }

    /** Draws the image at the object's position. */
    public void draw() {
        if (image != null) {
            image.draw(position.x, position.y);
        }
    }

    /** @return Bounding box at current position */
    public Rectangle getBoundingBox() {
        return image.getBoundingBoxAt(position);
    }


    /** @return current position */
    public Point getPosition() {
        return position;
    }


    /** Set current position
     * @param x position in x coord
     * @param y position in y coord*/
    public void setPosition(double x, double y) {
        this.position = new Point(x, y);
    }


    /** Set current position
     * @param position position */
    public void setPosition(Point position) {
        this.position = position;
    }


    /** @return current image*/
    public Image getImage() {
        return image;
    }


    /** Set current image
     * @param image image to set */
    public void setImage(Image image) {
        this.image = image;
    }
}
