package Actors;

import itumulator.simulator.Actor;
import itumulator.world.*;

public abstract class Foliage implements Actor, NonBlocking {
    World world;
    double spread_rate;
    int energy;
    
    public void act() {

    }
}