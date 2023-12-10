package Unittests;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.*;

import Actors.Carcass;
import Actors.Fungi;
import Actors.Rabbit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import app.Main;

public class thirdWeek {

    //Opgave K3-1a
    @Test
    public void placeCarcass() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week3_carcass.txt", 800, 100); // CARCASS AMOUNT: 1, WORLD SIZE: 1x1 (by our txt file)
        World world = p.getWorld();
        p.simulate();
        Assertions.assertTrue(world.getNonBlocking(new Location(0,0)) instanceof Carcass); // if there is one grass tile, the test passes
    }

    //Opgave K3-1b
    @Test
    public void animalsLeaveCarcass() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Rabbit rabbit = new Rabbit(world);
        world.setTile(loc, rabbit);
        rabbit.die();
        p.simulate(); // carcasses are created in the next tick
        Assertions.assertTrue(world.getTile(loc) instanceof Carcass);
    }

    //Opgave K3-1c
    @Test
    public void carcassDecays() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        world.setTile(loc, new Carcass(world, 50));
        int carcassCount = 1;
        while (world.getTile(loc) instanceof Carcass) {  // it would be an infinite loop if carcass doesn't decay
            p.simulate();
        }
        carcassCount--;
        Assertions.assertEquals(0, carcassCount);
    }

    //Opgave K3-1d
    @Test
    public void fungiGrowsInCarcass() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        world.setTile(loc, new Carcass(world, 50));
        int fungiCount = 0;
        while (world.getTile(loc) instanceof Carcass) { //Will run until the fungi overtakes the carcass
            p.simulate();
            if (world.getTile(loc) instanceof Fungi) {
                fungiCount++;
            }
        }
        Assertions.assertEquals(1, fungiCount);
    }

    //Opgave K3-1e
    @Test
    public void fungiNeedsCarcassToSurvive() {
        Program p = new Program(1, 800, 100); 
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        world.setTile(loc, new Fungi(world, 50)); 
        int fungiCount = 1;
        while (world.getTile(loc) instanceof Fungi) { //Will run until the fungi dies - if the fungi doesn't die, the loop will run forever
            p.simulate();
        }
        if(!(world.getTile(loc) instanceof Fungi)) { //If the fungi dies, the while loop will stop and the fungiCount will be decremented
            fungiCount--;
        }
        Assertions.assertEquals(0, fungiCount);
    }
}