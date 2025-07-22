import java.util.ArrayList;
import java.util.Properties;

public class NormalMonkey extends Monkey {
    public NormalMonkey(Properties gameProps, int index, double x, double y, String direction, boolean isIntelligent, ArrayList<Platform> platforms) {
        super(gameProps, index, x, y, direction, isIntelligent, platforms);
    }
}
