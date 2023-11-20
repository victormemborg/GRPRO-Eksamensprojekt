import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import itumulator.simulator.Actor;
import itumulator.world.Location;
import itumulator.world.World;

public class Person implements Actor {
    @Override
    public void act(World world) {

        System.out.println("I ain't doing nothing!");

        Set<Location> neighbours = world.getEmptySurroundingTiles();
        List<Location> list = new ArrayList<>(neighbours);

        Random ran = new Random();

        Location move = list.get(ran.nextInt(list.size()));

        if (!list.isEmpty()) {
            world.move(this, move);
        }

        if (world.isNight()) {
            world.delete(this);
        }

    }

}