package Actors;

import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.NonBlocking;
import itumulator.world.World;

public class Berry extends Foliage implements Actor, DynamicDisplayInformationProvider, NonBlocking{
    boolean eaten;
    int regrowTime = 6;
    

    public Berry(){
        this.eaten = false;
    }

    public Berry(boolean eaten){
        this.eaten = eaten;
    }

    @Override
    public void act(World world){

        //Regrow mechanic
        if(eaten && world.isDay()){ 
            regrowTime--;
        }
        if(regrowTime == 0){
            this.eaten = false;
            regrowTime = 6;
        }
    }

    @Override
    public DisplayInformation getInformation(){
        if (!eaten) {
            return new DisplayInformation(Color.blue, "bush-berries");
        } else {
            return new DisplayInformation(Color.green, "bush");
        }
        
    }

}
