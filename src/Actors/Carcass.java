package Actors;

import HelperMethods.Help;

import itumulator.world.*;
import itumulator.simulator.Actor;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.executable.DisplayInformation;

import java.awt.Color;
import java.util.Random;

public class Carcass implements Actor, DynamicDisplayInformationProvider, NonBlocking, Eatable {
    Random r = new Random();
    private int energy;
    private int age;
    private World world;
    private boolean isInfected = false;
    private int fungiEnergy = 0;
    private int CARCASS_BIG_THRESHOLD = 150;

    // Constructor used by Animals
    public Carcass(World world, int energy) {
        this.world = world;
        this.energy = energy;
        this.age = 0;
    }

    // Constructor used by Main
    public Carcass(World world, String animal_str) {
        this.world = world;
        this.age = 0;
        this.energy = Help.strToEnergy(animal_str, world);
    }

    // Constructer used for testing
    public Carcass(World world) {
        this.world = world;
        this.energy = r.nextInt(100,200);
        this.age = 0;
    }

    public int consumed() {
        world.delete(this);
        return energy;
    }

    private void decay() {
        //age determines whether or not the carcass randomly gets infected
        if(!isInfected && r.nextFloat(0,1) < age / 100) {
            isInfected = true;
        } else if(isInfected) {
            growFungi();
        }
    }

    private void growFungi() {
        if(energy - fungiEnergy >= 0) { //if the fungi energy is less than the energy of the carcass, add 10 to the fungi energy
            fungiEnergy += 10;
            energy -= 10;
        } else {
            Location carcassLocation = world.getLocation(this);
            Fungi fungi = new Fungi(world, fungiEnergy);
            world.delete(this);
            world.setTile(carcassLocation, fungi);
        }
    }

    @Override
    public void act(World world) {
        this.world = world;
        age++;
        decay();
    }

    @Override 
    public DisplayInformation getInformation() {
        if (energy > CARCASS_BIG_THRESHOLD) { 
            return new DisplayInformation(Color.red, "carcass");
        } else {
            return new DisplayInformation(Color.red, "carcass-small");
        }
    }

    public boolean getIsInfected() {
        return isInfected;
    }

    public void setInfected(boolean isInfected) {
        this.isInfected = isInfected;
    }
}