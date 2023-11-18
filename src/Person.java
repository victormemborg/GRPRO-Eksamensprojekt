import java.awt.Color;
import java.util.*;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

import itumulator.world.Location;
import itumulator.world.World;

public class Person implements Actor, DynamicDisplayInformationProvider{
    World world;

    public Person(World world) {
        this.world = world;
    }

    @Override
    public void act(World world){
        //die at night
        /* 
        if (world.isNight()) {
            world.delete(this);
            return;
        }
        */
        //move random
        if (!world.getEmptySurroundingTiles().isEmpty() && world.isDay()) {
            world.move(this, RandomEmptyAdjacentLocation(world));
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (world.isNight()) {
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
