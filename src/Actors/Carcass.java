package Actors;

import itumulator.world.*;
import itumulator.simulator.Actor;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.DisplayInformation;

import java.awt.Color;

public class Carcass implements Actor, DynamicDisplayInformationProvider, NonBlocking, Eatable {
    private int energy;
    private int age;
    private World world;

    public Carcass(World world, int energy) {
        this.world = world;
        this.energy = energy;
        this.age = 0;
    }

    public int consumed() {
        world.delete(this);
        return energy;
    }

    private void decay() {
        age++;
        if (age > 40) { // magic number
            world.delete(this);
        }
    }

    @Override
    public void act(World world) {
        this.world = world;
        decay();
    }

    @Override 
    public DisplayInformation getInformation() {
        if (energy > 150) { //another magic number :(
            return new DisplayInformation(Color.red, "carcass");
        } else {
            return new DisplayInformation(Color.red, "carcass-small");
        }
    }
}