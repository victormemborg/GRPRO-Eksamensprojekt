import itumulator.simulator.Actor;
import itumulator.world.World;

public class Person implements Actor{
    @Override
    public void act(World world){
        System.out.println("big bruh");
    }
}
