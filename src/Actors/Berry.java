package Actors;

import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Berry extends Foliage implements NonBlocking, Eatable{
    private boolean eaten;
    private int maxBerrys;
    private int regrowTime = 12;

    /**
     * Creates a new berry bush with a random number of berrys between 3 and 5
     * @param world the world the berry bush is in
     */
    public Berry(World world) {
        super(world);
        this.maxBerrys = ran.nextInt(3) + 3;  //random number of berrys between 3 and 5
        this.eaten = false;
        this.energy = 5;
    }

    @Override
    public void act(World world){
        if (eaten){
            regrowTime--;
            if (regrowTime == 0){
                eaten = false;
                regrowTime = 12;
            }
        }
    }

    //makes the berry disappear when eaten
    public int consumed() {
        eatBerry();
        return energy;
    }

    /**
     * Decreases the amount of berrys on the bush by 1 and sets eaten to true if there are no more berrys left
     */
    private void eatBerry(){
        if(maxBerrys > 0){
            maxBerrys--;
        } else {
            eaten = true;
        }
    }

    /**
     * Returns whether or not the berry has been eaten
     * @return true if the berry has been eaten, false otherwise
     */
    public boolean isEaten() {
        return eaten;
    }

    @Override
    public DisplayInformation getInformation(){
        if (!isEaten()) {
            return new DisplayInformation(Color.blue, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
        
    }

}
