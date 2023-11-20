import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;

import java.util.Set;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Color;

public class Person implements Actor, DynamicDisplayInformationProvider {
    World world;

    public Person(World world) {
        this.world = world;
    }
    
    @Override
    public void act(World world) {
        if(world.isDay() && getEmptyRandomLocations(world) != null) {
            world.move(this, getEmptyRandomLocations(world));
        }
    }

    public Location getEmptyRandomLocations(World world) {
        Random r = new Random();
        Set<Location> neighbours = world.getEmptySurroundingTiles();
        if (neighbours.isEmpty()) {
            return null; 
        }
        List<Location> list = new ArrayList<>(neighbours);
        return list.get(r.nextInt(list.size())); 
    }
    
    @Override
    public DisplayInformation getInformation() {
        if(world.isNight()) {
            return new DisplayInformation(Color.blue, "bear-small-sleeping");
        } else {
            return new DisplayInformation(Color.gray, "bear-small");
        }
    }
}