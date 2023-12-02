package Actors;

import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.World;

public class Berry extends Foliage implements DynamicDisplayInformationProvider {
    private boolean eaten;
    private int regrowTime = 12;

    public Berry() {
        this.eaten = false;
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

    public void eatBerry(){
        eaten = true;
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
