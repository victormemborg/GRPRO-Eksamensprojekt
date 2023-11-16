package itumulator.simulator;

import itumulator.world.World;

/**
 * By implementing the {@link Actor} interface and adding an instance of such a class to a {@link World} will make the simulation call the
 * {@link act(World world) act} method during each step of the simulation. 
 */
public interface Actor {
    
    /**
     * The method called whenever the actor needs to simulate actions.
     * @param world providing details of the position on which the actor is currently located and much more.
     */
    public void act(World world);
    
}
