package Actors;

import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.NonBlocking;

import java.awt.Color;

public class Grass extends Foliage implements DynamicDisplayInformationProvider, NonBlocking, Eatable {
    public Grass(World world) {
        super(world);
        this.energy = 30;
        this.spread_chance = 0.05;
        this.wither_chance = 0.02;
    }

    @Override
    public void act(World w){
        super.act(w);
    }

    @Override
    public DisplayInformation getInformation(){
        if (!withering) {
            return new DisplayInformation(Color.green, "grass-better");
        } else {
            return new DisplayInformation(Color.green, "grass-dying");
        }
    }

    public int consumed() {
        world.delete(this);
        return energy;
    }
}
