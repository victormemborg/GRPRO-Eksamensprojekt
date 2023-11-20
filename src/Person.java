import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.awt.Color;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public class Person implements Actor, DynamicDisplayInformationProvider {

    boolean isNight = false;

    @Override
    public void act(World world) {

        isNight = world.isNight();

        Set<Location> neighbours = world.getEmptySurroundingTiles();
        List<Location> list = new ArrayList<>(neighbours);

        Random ran = new Random();

        Location move = list.get(ran.nextInt(list.size()));

        if (!list.isEmpty() && !isNight) {
            world.move(this, move);
        }

    }

    @Override
    public DisplayInformation getInformation() {

        if (isNight) {
            return new DisplayInformation(Color.DARK_GRAY, "bear-sleeping");
        } else {
            return new DisplayInformation(Color.pink, "bear");
        }

    }

}