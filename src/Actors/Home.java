package Actors;

import itumulator.world.*;
import java.util.ArrayList;

abstract public class Home implements NonBlocking {
    World world;
    ArrayList<Animal> occupants;
    int max_occupants = 4;

    public Home(World world) {
        this.world = world;
        occupants = new ArrayList<Animal>();
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