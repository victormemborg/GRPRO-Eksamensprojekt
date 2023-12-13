package Actors;


import java.awt.Color;
import java.util.Random;
import java.util.Set;

import HelperMethods.Help;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.*;

public class Poop implements Actor, DynamicDisplayInformationProvider, NonBlocking {
    Random r = new Random();
    private World world;
    private double age;
    private static final double GRASS_CHANCE = 0.7;
    private static final double FUNGI_CHANCE = 0.1;
    
    /**
     * Constructor for objects of class Poop
     * @param world the world in which the poop is created
     */
    public Poop(World world) {
        this.age = 0;
        this.world = world;
    }

    public void act(World world) {
        age++;
        if(age > 10 && world.isNight()) {
            fertilize();
        }
    }


    /**
     * Fertilizes the surrounding tiles
     */
    private void fertilize() {
        Set<Location> surrounding_tiles = world.getSurroundingTiles(world.getCurrentLocation(), 1);
        for (Location tile : surrounding_tiles) {
            Object object = world.getTile(tile);
            if(object instanceof Animal) {
                return; //we dont want to get the interface of an animal
            }
            if(!(Help.doesInterfacesInclude(object, "NonBlocking"))) { 
                spawnObjects(tile);
            }
        }
        Location poopLoc = world.getCurrentLocation();
        world.delete(this);
        world.setTile(poopLoc, new Grass(world));
    }

    /**
     * Randomly spawns a fungi, grass or nothing on a tile
     * @param tile the Location of a tile to spawn an object on
     */
    private void spawnObjects(Location tile) {
        double random = r.nextDouble();
        if (random < FUNGI_CHANCE) {  
            world.setTile(tile, new Fungi(world, 150));
        } else if (random < GRASS_CHANCE) { 
            world.setTile(tile, new Grass(world));
        }        
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.ORANGE, "bricks");
    }
}


