package Actors;

import itumulator.world.*;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

import java.util.Set;
import java.awt.Color;

public class Fungi implements Actor, DynamicDisplayInformationProvider, NonBlocking {
    private World world;
    private int energy;
    private int spreadRange = 1;
    private int ENERGY_LOSS = 2; //fungi loses 2 energy if it doesn't spread
    private int ENERGY_GAIN = 10; //fungi gains 10 energy if it spreads

    /**
     * Constructor for Fungi
     * @param world the world the fungi is in
     * @param energy the energy of the fungi
     */
    public Fungi(World world, int energy) {
        this.world = world;
        this.energy = energy;
    }

    public void act(World w){
        spread();
        checkEnergy();
    }
    
    /**
     * Looks for carcasses in the surrounding tiles of its vision range and infects them if they are not already infected
     * If no carcasses are found, the fungi loses energy
     * If a carcass is found, the fungi gains energy
     */
    private void spread() {
        Set<Location> surrounding_tiles = world.getSurroundingTiles(world.getCurrentLocation(), spreadRange);
        int infectedCarcasses = 0;
        for(Location tile : surrounding_tiles) {
            if (world.getTile(tile) instanceof Carcass) {
                if(!((Carcass) world.getTile(tile)).getIsInfected()) {
                    ((Carcass) world.getTile(tile)).setInfected(true);
                    energy += ENERGY_GAIN; //fungi gains energy if it spreads
                    infectedCarcasses++; 
                }
            }
        }
        if(infectedCarcasses == 0) {
            energy -= ENERGY_LOSS; //fungi loses energy if it doesn't spread
        }
    }

    /**
     * Checks if the fungi has enough energy to survive, if not, it dies
     */
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
