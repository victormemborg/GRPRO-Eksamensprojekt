import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import java.util.Random;
import java.awt.Color;

public class Burrow implements Actor, NonBlocking, DynamicDisplayInformationProvider {
    World world;
    Random r = new Random();
    boolean big_hole = r.nextBoolean();

    @Override
    public void act(World world) {

    }

    public DisplayInformation getInformation() {

        if (big_hole) {
            return new DisplayInformation(Color.black, "hole");
        } else {
            return new DisplayInformation(Color.black, "hole-small");
        }
    }
}