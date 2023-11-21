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
    int foodLevel = 5; //it gets deleted one tick before the 2nd night, if it has not found food.
    Burrow burrow = null;
    Location loc;
    boolean isNight = false;

    @Override
    public void act(World world) {
        isNight = world.isNight();
        if(!world.getEmptySurroundingTiles().isEmpty() && !isNight) {
            world.move(this, getEmptyRandomLocations(world));
            foodLevel--;
            if(burrow == null) {
                digHole(world);
            }    
        } 
        if(isNight) {
            age += 1;
            if(burrow != null) {
                if(world.isTileEmpty(loc)) {
                    world.move(this, loc);
                }
            }
        }
    }
    

    public void digHole(World world) {
        loc = world.getLocation(this); 
        if(!world.containsNonBlocking(loc)) {
            burrow = new Burrow();
            world.setTile(loc, burrow);
        }
    }

    public void eat(World world) {
        Location foodLoc = world.getLocation(this);
        if(world.containsNonBlocking(foodLoc)) {
            if(world.getNonBlocking(foodLoc) instanceof Grass) {
                world.delete(world.getNonBlocking(foodLoc));
                foodLevel += 5;
            }
        }
    }

    /* IKKE KLAR ENDNU
    public void reproduce(World world) {
        Location birthLocation = getEmptyRandomLocations(world);
            if(birthLocation != null) {
                world.setTile(birthLocation, new Rabbit());
            }
        }
    */

    @Override
    public DisplayInformation getInformation() {
        if(!isNight) {
            return new DisplayInformation(Color.DARK_GRAY, "rabbit-small");
        } else {
            return new DisplayInformation(Color.DARK_GRAY, "rabbit-small-sleeping");
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
    
}
