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
    private final int FOOD_GAIN = 5;
    private final int FOOD_LOSS_REPRODUCTION = 25;
    private final int REQUIRED_FOOD_REPRODUCTION = 40;
    private final int MIN_AGE_ADULT = 3;

    private int age = 0;
    private int foodLevel = 20; 

    private Burrow burrow = null;
    private Location burrowLoc = null;

    private boolean isNight = false;
    private boolean isSleeping = false;

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

    private void increaseAgeIfMorning(World world) {
        if(world.getCurrentTime() == 0) {
            age++;
        }
    }

    private void moveToBurrow(World world) {
        if(burrow != null  && world.isTileEmpty(burrowLoc)) {
            world.move(this, burrowLoc);
            isSleeping = true;
        }
    }

    private void killRabbit(World world) {
        world.delete(this);
        amountOfRabbits--;
    }

    private void moveAndEat(World world) {
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

    private void digHole(World world) {
        if(burrow == null) {
            burrowLoc = world.getLocation(this); 
            if(!world.containsNonBlocking(burrowLoc)) {
                burrow = new Burrow();
                world.setTile(burrowLoc, burrow);
            }
        }
    }

    private void eat(World world) {
        Location foodLoc = world.getLocation(this);
        if(world.containsNonBlocking(foodLoc) && world.getNonBlocking(foodLoc) instanceof Grass) {
            Grass grass = (Grass) world.getNonBlocking(foodLoc);
            if(!grass.getDying()) {
                world.delete(world.getNonBlocking(foodLoc));
                foodLevel += FOOD_GAIN;
            }
        }
    }

    private void reproduce(World world) {
        if(age > MIN_AGE_ADULT && foodLevel > REQUIRED_FOOD_REPRODUCTION && amountOfRabbits >= 2) {
            Location birthLocation = getEmptyRandomLocations(world);
            if(birthLocation != null && world.isTileEmpty(birthLocation)) {
                world.setTile(birthLocation, new Rabbit());
                foodLevel -= FOOD_LOSS_REPRODUCTION;
                amountOfRabbits++;
            }
        }
    } 

    @Override
    public DisplayInformation getInformation() {
        String image;
        if(age > MIN_AGE_ADULT) {
            image = isSleeping ? "rabbit-sleeping" : "rabbit-large";
        } else {
            image = isSleeping ? "rabbit-small-sleeping" : "rabbit-small";
        }
        return new DisplayInformation(Color.DARK_GRAY, image);
    }    

    private Location getEmptyRandomLocations(World world) {
        Random r = new Random();
        Set<Location> neighbours = world.getEmptySurroundingTiles();
        if (neighbours.isEmpty()) {
            return null; 
        }
        List<Location> list = new ArrayList<>(neighbours);
        return list.get(r.nextInt(list.size())); 
    } 
    
}
