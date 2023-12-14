package Actors;

import itumulator.world.*;
import java.util.ArrayList;

abstract public class Home {
    World world;
    ArrayList<Animal> occupants;
    int max_occupants;

    /**
     * Creates a new home
     * @param world the world to create the home in
     */
    public Home(World world) {
        this.world = world;
        this.occupants = new ArrayList<Animal>();
        this.max_occupants = 4;
    }
    
    /**
     * Returns the occupants of the home
     * @return an ArrayList of Animals representing the occupants of the home
     */
    public ArrayList<Animal> getOccupants() {
        return occupants;
    }

    /**
     * Adds the given occupant to the home
     * @param occupant the animal to add
     */
    public void addOccupant(Animal occupant) {
        //if(!isFull()) {
            occupants.add(occupant);
        //}
    }

    /**
     * Removes the given occupant from the home
     * @param occupant the animal to remove
     */
    public void removeOccupant(Animal occupant) {
        if(occupants.contains(occupant)) {
            occupants.remove(occupant);
        }
    }

    /**
     * Checks if the home is full
     * @return true if the home is full, false if not
     */
    public boolean isFull() {
        return occupants.size() >= max_occupants;
    }

    /**
     * Returns the location of the home
     * @return a Location object representing the location of the home
     */
    public Location getLocation() {
        return world.getLocation(this);
    }
}