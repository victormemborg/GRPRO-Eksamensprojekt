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
    boolean isSleeping = false;
    World world;

    Rabbit() {
        amountOfRabbits += 1;
    }

    @Override
    public void act(World world) {
        isNight = world.isNight();
        increaseAgeIfMorning(world);
        if(!isNight) {
            moveAndEat(world);
            reproduce(world); 
        } else {
            moveToBurrow(world);
        }
    }

    public void increaseAgeIfMorning(World world) {
        if(world.getCurrentTime() == 0) {
            age++;
        }
    }

    public void moveToBurrow(World world) {
        if(burrow != null  && world.isTileEmpty(loc)) {
            world.move(this, loc);
            isSleeping = true;
        }
    }

    public void killRabbit(World world) {
        world.delete(this);
        amountOfRabbits--;
    }

    public void moveAndEat(World world) {
        isSleeping = false;
        if(!world.getEmptySurroundingTiles().isEmpty()) {
            if(foodLevel > 0) {
                world.move(this, getEmptyRandomLocations(world));
                digHole(world);
                eat(world);
                foodLevel--;
            } else {
                killRabbit(world);
            }
        }
    }

    public void digHole(World world) {
        if(burrow == null) {
            loc = world.getLocation(this); 
            System.out.println(loc);
            if(!world.containsNonBlocking(loc)) {
                burrow = new Burrow();
                world.setTile(loc, burrow);
            }
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
            image = isSleeping ? "rabbit-sleeping" : "rabbit-large";
        } else {
            image = isSleeping ? "rabbit-small-sleeping" : "rabbit-small";
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
