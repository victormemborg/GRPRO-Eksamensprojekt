package Actors;

import itumulator.world.*;
import itumulator.simulator.Actor;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.DisplayInformation;

import java.util.ArrayList;

abstract class Home implements Actor, DynamicDisplayInformationProvider, NonBlocking {
    World world;
    ArrayList<Animal> occupants;

    abstract DisplayInformation geInformation();

    

    public ArrayList<Animal> getOccupants() {
        return occupants;
    }

    public Location getLocation() {
        return world.getCurrentLocation();
    }
}