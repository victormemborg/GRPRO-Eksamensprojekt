package Actors;

import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import java.util.Random;

public class Burrow extends Home implements DynamicDisplayInformationProvider {
    private boolean big_hole;
    Random r = new Random();

    //if animal is provided as an argument, then burrow is big or small depending on animal
    public Burrow(World world, Animal animal) {
        super(world);
        setBigHole(animal);
    }

    //if animal isnt provided as an argument, then burrow is randomly big or small
    public Burrow(World world) {
        super(world);
        setRandomHole();
    }

    private void setBigHole(Animal animal) {
        if(animal instanceof Rabbit) {
            big_hole = false;
        } else {
            big_hole = true;
        }
    }

    private void setRandomHole() {
        if(r.nextBoolean()) {
            big_hole = true;
        } else {
            big_hole = false;
        }
    }

    public boolean isBigHole() {
        return big_hole;
    }

    public DisplayInformation getInformation() {
        if(big_hole) {
            return new DisplayInformation(Color.black, "hole");
        } else {
            return new DisplayInformation(Color.black, "hole-small");
        }
    }
}