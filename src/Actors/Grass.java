package Actors;
import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;

import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class Grass extends Foliage implements Actor, DynamicDisplayInformationProvider, NonBlocking {
    World world;
    boolean dying = false;
    int time_dying = 0;

    @Override
    public void act(World world){
        this.world = world;
        Random ran = new Random();
        //Death mechanic
        if (ran.nextInt(20) == 19) {
            dying = true;
        }
        if (dying) {
            time_dying++;
        }
        if (time_dying > 4) {
            world.delete(this);
        }

        //Spread mechanic
        ArrayList<Location> list = new ArrayList<>();
        for (Location l : world.getSurroundingTiles()) {
            if (!world.containsNonBlocking(l)) {
                list.add(l);
            }
        }
        if (!list.isEmpty() && ran.nextBoolean() && !dying && world.isDay()) {
                world.setTile(list.get(ran.nextInt(list.size())), new Grass());
        }
    }

    @Override
    public DisplayInformation getInformation(){
        if (!dying) {
            return new DisplayInformation(Color.green, "grass-better");
        } else {
            return new DisplayInformation(Color.green, "grass-dying");
        }
        
    }

    public boolean getDying() {
        return dying;
    }

}
