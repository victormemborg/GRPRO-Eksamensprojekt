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
    private boolean isInfected;
    private int fungiEnergy;
    private final int CARCASS_BIG_THRESHOLD = 150;

    /**
     * Constructor used for testing - creates a carcass with a random amount of energy between 100 and 200
     * @param world the world the carcass has to be created in
     */
    public Carcass(World world) {
        this.world = world;
        this.energy = r.nextInt(100,200);
        this.age = 0;
        this.isInfected = false;
        this.fungiEnergy = 0;
    }

    /**
     * Constructor used by Animal - takes an int energy and sets the energy of the carcass to the energy specified
     * @param world the world the carcass has to be created in
     * @param energy the energy of the carcass - associated with the energy of the animal that died
     */
    public Carcass(World world, int energy) {
        this(world);
        this.energy = energy;
    }

    /**
     * Constructor used by main - takes an input string and sets the energy of the carcass to the energy specified in the string
     * @param world the world the carcass has to be created in
     * @param animal_str the string that contains the energy of the carcass
     */
    public Carcass(World world, String str) {
        this(world);
        if (str.equals("fungi")) {
            this.isInfected = true;
        } else {
            this.energy = Help.strToEnergy(str, world);
        }
    }

    /**
     * When an animal eats a carcass, the carcass is deleted and the animal gains the energy of the carcass
     * @return the amount of energy the carcass has
     */
    public int consumed() {
        world.delete(this);
        return energy;
    }

    /**
     * Method that determines whether or not the carcass gets infected
     * The older the carcass is, the more likely it is to get infected
     */
    private void decay() {
        //age determines whether or not the carcass randomly gets infected
        if(!isInfected && r.nextFloat(0,1) < age / 100) {
            isInfected = true;
        } else if(isInfected) {
            growFungi();
        }
    }

    /**
     * Method that grows fungi on the carcass
     */
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
    public void act(World w) {
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

    /**
     * Getter for the energy of the carcass
     * @return the energy of the carcass
     */
    public boolean getIsInfected() {
        return isInfected;
    }

    /**
     * Setter for the infected state of the carcass
     * @param isInfected the infected state of the carcass
     */
    public void setInfected(boolean isInfected) {
        this.isInfected = isInfected;
    }
}