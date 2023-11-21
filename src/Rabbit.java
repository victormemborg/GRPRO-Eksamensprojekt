import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.*;
import itumulator.world.*;


public class Rabbit implements Actor, DynamicDisplayInformationProvider{
    int age = 0;
    int foodLevel = 19; //it gets deleted one tick before the 2nd night, if it has not found food.
    Burrow burrow = null;
    Location loc;

    @Override
    public void act(World world) {
        System.out.println(foodLevel);
        if(!world.getEmptySurroundingTiles().isEmpty() && !world.isNight()) {
            world.move(this, getEmptyRandomLocations(world));
            foodLevel--;
            if(foodLevel <= 0) {
                die(world);
            }
            if(burrow == null) {
                digHole(world);
            }    
        } 
        if(world.isNight()) {
            age += 1;
            if(burrow != null) {
                world.move(this, loc);
            }
        }
    }
    
    public void die(World world) {
        world.delete(this);
    }

    public void digHole(World world) {
        loc = world.getLocation(this);
        burrow = new Burrow();
        world.setTile(loc, burrow);
    }

    @Override
    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.DARK_GRAY, "rabbit-small");
    }

    public Location getEmptyRandomLocations(World world) {
        Random r = new Random();
        Set<Location> neighbours = world.getEmptySurroundingTiles();
        System.out.println(neighbours.size());
        if (neighbours.isEmpty()) {
            return null; 
        }
        List<Location> list = new ArrayList<>(neighbours);
        return list.get(r.nextInt(list.size())); 
    }    
    
}
