import itumulator.world.NonBlocking;
import itumulator.world.World;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;
import itumulator.world.Location;

import java.awt.Color;
import java.util.Random;
import java.util.ArrayList;

public class Grass implements Actor, DynamicDisplayInformationProvider, NonBlocking{
    boolean dying = false;
    int time_dying = 0;
    @Override
    public void act(World world){
        Random ran = new Random();
        //Maybe dø
        if (ran.nextInt(11) >= 8) {
            dying = true;
        }
        if (dying) {
            time_dying = time_dying + 1;
        }
        if (time_dying > 8) {
            world.delete(this);
        }

        //Maybe spread
        ArrayList<Location> list = new ArrayList<>();
        for (Location l : world.getSurroundingTiles()) {
            if (!world.containsNonBlocking(l)) {
                list.add(l);
            }
        }
        if (!list.isEmpty() && ran.nextBoolean() && !dying) {
                world.setTile(list.get(ran.nextInt(list.size())), new Grass());
        }
    }

    @Override
    public DisplayInformation getInformation(){
        if (!dying) {
            return new DisplayInformation(Color.green, "better-grass");
        } else {
            return new DisplayInformation(Color.green, "grass-dying");
        }
        
    }

}
