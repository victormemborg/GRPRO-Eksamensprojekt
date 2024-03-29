package Actors;

import java.util.ArrayList;
import java.util.Random;

import HelperMethods.Help;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.World;
import itumulator.world.Location;


public abstract class Foliage implements Actor, DynamicDisplayInformationProvider {
    Random ran = new Random();

    // Initialized in Foliage
    World world;
    boolean withering; 
    int time_withering;
    int MAX_WITHER_TIME;

    // Initialized in subclasses
    int energy;
    double spread_chance;
    double wither_chance;

    /**
     * Constructor for the Foliage class
     * @param world The world object that the foliage is in
     */
    Foliage(World world) {
        this.world = world;
        this.withering = false;
        this.time_withering = 0;
        this.MAX_WITHER_TIME = 5;
    }

    @Override
    public void act(World w) {
        tryWithering();
        trySpread();
        //Might be useful to extend here in subclass....
    }

    /**
     * Tries to wither the given foliage based on the wither chance
     * @throws IllegalArgumentException if the foliage is already dead - can only happen if the tryWithering() method is called
     * after the act() method
     */
    void tryWithering() {
        try {
            if (ran.nextFloat(0,1) < wither_chance) {
                withering = true;
            }
            if (withering) {
                time_withering++;
            }
            if (time_withering > MAX_WITHER_TIME) {
                world.delete(this);
            }
        } catch (IllegalArgumentException ignore) { 
            // If the foliage is already dead do nothing
        }
    }
    
    /**
     * Tries to spread the given foliage based on the spread chance
     */
    void trySpread() {
        if (!(ran.nextFloat(0,1) < spread_chance)) {
            return;
        }
        ArrayList<Location> empty_tiles = Help.getNearbyTileWithoutNonBlocking(world, this.getLocation(), 1);
        //Check if empty tiles is empty
        if (empty_tiles.isEmpty()) {
            return;
        }
        //Create new instance at random location
        Location ran_loc = empty_tiles.get(ran.nextInt(empty_tiles.size()));
        Foliage new_foliage = (Foliage) Help.createNewInstanceWithArg(this, world);
        if (new_foliage != null) {
            world.setTile(ran_loc, new_foliage);
        }
    }

    /**
     * Returns the current location of the foliage
     * @return The location object of the foliage
     */
    public Location getLocation() {
        return world.getCurrentLocation();
    }
}