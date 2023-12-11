package Actors;

import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Berry extends Foliage implements NonBlocking, Eatable{
    private boolean eaten;
    private int maxBerrys;
    private int regrowTime = 12;

    public Berry(World world) {
        super(world);
        this.maxBerrys = ran.nextInt(3) + 3; 
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

    private void eatBerry(){
        if(maxBerrys > 0){
            maxBerrys--;
        } else {
            eaten = true;
        }
    }

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
