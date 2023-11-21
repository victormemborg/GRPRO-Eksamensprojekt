import itumulator.world.World;
import itumulator.world.*;

import java.util.Random;

import itumulator.simulator.*;

public class Rabbit implements Actor{
    @Override
    public void act(World world) {
        Random ran = new Random();
        Location l = new Location(ran.nextInt(5), ran.nextInt(5));
        world.move(this, l);
    }
}
