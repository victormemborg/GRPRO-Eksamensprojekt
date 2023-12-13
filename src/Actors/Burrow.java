package Actors;

import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import java.util.Random;

public class Burrow extends Home implements DynamicDisplayInformationProvider, NonBlocking  {
    private boolean big_hole;
    private Animal owner_of_burrow;
    Random r = new Random();

    /**
     * Creates a burrow in the world
     * @param world the world the burrow is in
     * @param animal the animal that is going to live in the burrow
     * If animal is provided as an argument, then burrow is big or small depending on animal
     */
    public Burrow(World world, Animal animal) {
        super(world);
        setHoleType(animal);
    }

    /**
     * Creates a burrow in the world
     * @param world the world the burrow is in
     * If animal isn't provided as an argument, then burrow is randomly big or small
     */
    public Burrow(World world) {
        super(world);
        setRandomHole();
    }

    /**
     * Sets the size of the burrow depending on the animal
     * @param animal the animal that is going to live in the burrow
     */
    private void setHoleType(Animal animal) {
        if(animal instanceof Rabbit) {
            big_hole = false;
        } else {
            big_hole = true;
        }
        animal.setHome(this);
        setOwnerOfBurrow(animal);
    }

    /**
     * Sets a random hole size for a burrow without having to provide an animal
     */
    private void setRandomHole() {
        if(r.nextBoolean()) {
            big_hole = true;
        } else {
            big_hole = false;
        }
    }

    /**
     * Returns whether or not the burrow is big
     * @return true if the burrow is big, false if not
     */
    public boolean isBigHole() {
        return big_hole;
    }

    /**
     * 
     * @return Returns the animal that made the burrow or set to live in the burrow
     */
    public Animal getOwnerOfBurrow() {
        return owner_of_burrow;
    }

    /**
     * Sets the animal that made the burrow or set to live in the burrow
     * @param animal the animal that made the burrow or set to live in the burrow
     */
    public void setOwnerOfBurrow(Animal animal) {
        owner_of_burrow = animal;
    }

    public DisplayInformation getInformation() {
        if(big_hole) {
            Object owner = getOwnerOfBurrow();
            if(owner instanceof Wombat) {
                Wombat wombat = (Wombat) owner;
                if(wombat.isScaredWhilstSleeping()) {
                    return new DisplayInformation(Color.black, "wombat-hole-scared");
                }
            }
            return new DisplayInformation(Color.black, "hole");
        } else {
            return new DisplayInformation(Color.black, "hole-small");
        }
    }
}