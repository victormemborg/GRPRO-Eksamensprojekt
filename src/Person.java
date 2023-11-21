import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

import itumulator.world.Location;
import itumulator.world.World;

public class Person implements Actor, DynamicDisplayInformationProvider{
    boolean isNight = false;

    @Override
    public void act(World world){
        isNight = world.isNight();
        //move random
        if (!world.getEmptySurroundingTiles().isEmpty() && !isNight) {
            world.move(this, RandomEmptyAdjacentLocation(world));
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (isNight) {
            return new DisplayInformation(Color.red, "bear-small-sleeping");
        } else {
            return new DisplayInformation(Color.red, "bear-small");
        }
    }

    public Location RandomEmptyAdjacentLocation(World world){
        Set<Location> neighbors = world.getEmptySurroundingTiles();
        List<Location> list = new ArrayList<>(neighbors);
        Random ran = new Random();
        return list.get(ran.nextInt(list.size())); 
    }
}
