import java.util.Properties;

public class Barrel extends Enemy {
    private boolean scoredCurrentJump = false;


    public Barrel(double x, double targetY, Properties gameProperties) {
        super(x, targetY, "res/barrel.png");
    }

    /** Check whether Mario has scored on  current Jump
     * @return whether this barrel has being scored */
    public boolean hasScored() {return scoredCurrentJump;}

    /** It has being scored on current jump for Mario s*/
    public void scored() {scoredCurrentJump = true;}


    /** Work with scored and hasScored to make sure Mario won't gain score
     *  multiple times on one jump over */
    public void resetJumpScore() {scoredCurrentJump = false;}
}
