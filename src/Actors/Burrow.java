package Actors;

import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import java.awt.Color;
import java.util.Random;

public class Burrow extends Home implements DynamicDisplayInformationProvider, NonBlocking  {
    private Random r = new Random();
    private String image;

    /**
     * Creates a burrow in the world
     * @param world the world the burrow is in
     * @param animal the animal that is going to live in the burrow.
     * If animal is provided as an argument, then the burrow takes the size specified by the animals home_image
     */
    public Burrow(World world, Animal animal) {
        super(world);
        occupants.add(animal);
        image = animal.getHomeImage();
    }

    /**
     * Creates a burrow in the world
     * @param world the world the burrow is in.
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

    /**
     * A method to check whether any given animal is allowed to make this burrow its home
     * @param animal The animal for which you want to check
     * @return True if, and <strong>only</strong> if, all of the following statements are true aswell: <p>
     * 1. The burrow has the right size <p>
     * 2. The burrow is empty or only occupied by animals of the same spicies <P>
     * 3. isFull() returns false
     */
    public boolean isAvailableTo(Animal animal) {
        if ( !image.equals(animal.getHomeImage()) ) { return false; }
        if ( isFull() ) { return false; }
        if ( occupants.isEmpty() ) { return true;  } // 
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