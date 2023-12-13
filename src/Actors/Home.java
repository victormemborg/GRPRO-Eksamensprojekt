package Actors;

import itumulator.world.*;
import java.util.ArrayList;

abstract public class Home {
    World world;
    ArrayList<Animal> occupants;
    int max_occupants;

    public Home(World world) {
        this.world = world;
        this.occupants = new ArrayList<Animal>();
        this.max_occupants = 4;
    }
    

    public ArrayList<Animal> getOccupants() {
        return occupants;
    }

    public void addOccupant(Animal occupant) {
        if(!isFull()) {
            occupants.add(occupant);
        }
    }

    public void removeOccupant(Animal occupant) {
        if(occupants.contains(occupant)) {
            occupants.remove(occupant);
        }
    }

    public boolean isFull() {
        return occupants.size() >= max_occupants;
    }

    public Location getLocation() {
        return world.getLocation(this);
    }
}