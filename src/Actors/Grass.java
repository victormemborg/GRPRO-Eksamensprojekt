package Actors;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.NonBlocking;

import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class Grass extends Foliage implements DynamicDisplayInformationProvider, NonBlocking {
    boolean dying;
    int time_dying;

    public Grass(World world) {
        super(world);
        spread_rate = 0.5;
        energy = 50;

        dying = false;
        time_dying = 0;
    }

    @Override
    public void act(World placeholder){
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
                world.setTile(list.get(ran.nextInt(list.size())), new Grass(world));
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
