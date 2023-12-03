package Actors;

import itumulator.simulator.Actor;
import itumulator.world.World;

public abstract class Foliage implements Actor {
    World world;
    double spread_rate;
    int energy;

    Foliage(World world) {
        this.world = world;
    }
}