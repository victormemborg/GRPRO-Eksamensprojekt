package Actors;

import itumulator.world.*;
import java.util.ArrayList;
import java.util.Set;

abstract public class Home implements NonBlocking {
    World world;
    ArrayList<Animal> occupants;
    int max_occupants = 5;

    public Home(World world) {
        this.world = world;
        occupants = new ArrayList<Animal>();
    }

    //if the home is a burrow, and the animal on the location is a rabbit, then the rabbit can occupy the burrow if it is not full and if the burrow is small
    //NEEDS HEAVY REFACTORING
    public void occupyHome(Animal animal) {
        if(animal instanceof Rabbit && this instanceof Burrow) {
            if(!isFull() && !((Burrow) this).isBigHole()) {
                addOccupant(animal);
            }
        } else if (animal instanceof Wolf && this instanceof Burrow) {
            if(!isFull() && ((Burrow) this).isBigHole()) {
                addOccupant(animal);
            }
        }
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