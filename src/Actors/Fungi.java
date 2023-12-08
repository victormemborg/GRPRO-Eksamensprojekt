package Actors;

import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;

import java.util.Set;

import java.awt.Color;

public class Fungi extends Foliage implements DynamicDisplayInformationProvider, NonBlocking {
    private int spreadRange = 1;
    
    public Fungi(World world, int energy) {
        super(world);
        this.energy = energy;
        this.spread_chance = 0.00; //chance doesn't matter since fungi can only spread via carcasses
        this.wither_chance = 0.00; //fungi can't wither
    }

    @Override
    public void act(World w){
        super.act(w);
        spread();
        checkEnergy();
    }
    
    private void spread() {
        Set<Location> surrounding_tiles = world.getSurroundingTiles(world.getCurrentLocation(), spreadRange);
        int infectedCarcasses = 0;
        for(Location tile : surrounding_tiles) {
            if (world.getTile(tile) instanceof Carcass) {
                if(!((Carcass) world.getTile(tile)).getIsInfected()) {
                    ((Carcass) world.getTile(tile)).setInfected(true);
                    energy += 10; //fungi gains energy if it spreads
                    infectedCarcasses++; 
                }
            }
        }
        if(infectedCarcasses == 0) {
            energy -= 2; //fungi loses energy if it doesn't spread
        }
    }

    private void checkEnergy() {
        if(energy <= 0) {
            world.delete(this);
        }
    }

    @Override
    public DisplayInformation getInformation(){
        if(energy > 50) {
            return new DisplayInformation(Color.magenta, "fungi");
        } else {
            return new DisplayInformation(Color.magenta, "fungi-small");
        }
    }
}
