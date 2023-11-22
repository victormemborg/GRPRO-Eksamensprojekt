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
    static int amountOfRabbits;
    int age = 0;
    int foodLevel = 20; 
    Burrow burrow = null;
    Location loc;
    boolean isNight = false;

    Rabbit() {
        amountOfRabbits += 1;
    }

    @Override
    public void act(World world) {
        isNight = world.isNight();
        age(world);
        if(!world.getEmptySurroundingTiles().isEmpty() && !isNight) {
            moveAndEat(world);
            if(burrow == null) {
                digHole(world);
            }    
        } 
        if(isNight) {
            if(burrow != null  && world.isTileEmpty(loc)) {
                world.move(this, loc);
            }
        }
    }

    public void age(World world) {
        if(world.getCurrentTime() == 0) {
            age++;
        }
    }

    public void moveAndEat(World world) {
        if(foodLevel > 0) {
            world.move(this, getEmptyRandomLocations(world));
            eat(world);
            foodLevel--;
            reproduce(world);
        } else {
            world.delete(this);
            amountOfRabbits--;
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
        if(world.containsNonBlocking(foodLoc) && world.getNonBlocking(foodLoc) instanceof Grass) {
            Grass grass = (Grass) world.getNonBlocking(foodLoc);
            if(!grass.getDying()) {
                world.delete(world.getNonBlocking(foodLoc));
                foodLevel += 5;
                System.out.println("Spiste mad");
            }
        }
    }

    public void reproduce(World world) {
        if(age > 3 && foodLevel > 20 && amountOfRabbits >= 2) {
            Location birthLocation = getEmptyRandomLocations(world);
            if(birthLocation != null && world.isTileEmpty(birthLocation)) {
                world.setTile(birthLocation, new Rabbit());
                foodLevel -= 15;
                System.out.println("Kanin født");
                amountOfRabbits++;
            }
        }
    } 

    @Override
    public DisplayInformation getInformation() {
        String image;
        if(age > 3) {
            image = isNight ? "rabbit-sleeping" : "rabbit-large";
        } else {
            image = isNight ? "rabbit-small-sleeping" : "rabbit-small";
        }
        return new DisplayInformation(Color.DARK_GRAY, image);
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
