package Actors;

import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import java.util.Random;

public class Burrow extends Home implements DynamicDisplayInformationProvider, NonBlocking  {
    private String image;
    Random r = new Random();

    /**
     * Creates a burrow in the world
     * @param world the world the burrow is in
     * @param animal the animal that is going to live in the burrow
     * If animal is provided as an argument, then burrow is big or small depending on animal
     */
    public Burrow(World world, Animal animal) {
        super(world);
        occupants.add(animal);
        image = animal.getHomeImage();
    }

    /**
     * Creates a burrow in the world
     * @param world the world the burrow is in
     * If animal isn't provided as an argument, then burrow is randomly big or small
     */
    public Burrow(World world) {
        super(world);
        if (r.nextBoolean()) {
            image = "hole";
        } else {
            image = "hole-small";
        }
    }

    public boolean isAvailableTo(Animal animal) {
        if ( !image.equals(animal.getHomeImage()) ) { return false; }
        if ( isFull() ) { return false; }
        if ( occupants.isEmpty() ) { return true; } // 
        if ( occupants.get(0).getClass() != animal.getClass() ) { return false; }
        return true;
    }

    @Override
    public DisplayInformation getInformation() {
        if (occupants.isEmpty()) {
            return new DisplayInformation(Color.black, image);
        }
        String image_key = occupants.get(0).getHomeImage(); // 
        return new DisplayInformation(Color.black, image_key);
    }
}