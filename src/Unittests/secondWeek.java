package Unittests;

import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.*;

import Actors.Bear;
import Actors.Rabbit;
import Actors.Berry;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import app.Main;

public class secondWeek {
    
    //Opgave K2-1a


    //Opgave K2-1b


    //Opgave K2-1c


    //Opgave K2-2a


    //Opgave K2-3a


    //Opgave K2-4a


    //Opgave K2-5a
    @Test
    public void placeBear() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_bear.txt", 800, 100); // BEAR AMOUNT: 1, WORLD SIZE: 1x1 (by our txt file)
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Assertions.assertTrue(world.getTile(loc) instanceof Bear);
    }


    //Opgave K2-5b
    @Test
    public void bearHunts() {
        Program p = new Program(5, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit = new Rabbit(world);
        Bear bear = new Bear(world);
        world.setTile(new Location(0,1), bear);
        world.setTile(new Location(3,1), rabbit);
        bear.setEnergy(100);
        for(int i = 0; i < 10; i++) {
            p.simulate();
        }
        int totalRabbit = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Rabbit) {
                    totalRabbit++; // if a rabbit is found, add 1 to the totalRabbit
                }
            }
        }
        Assertions.assertEquals(0, totalRabbit); // if the rabbit has been eaten, there should be no rabbits left
    }


    //Opgave K2-5c
    @Test
    public void RabbitFearsBear(){
        Program p = new Program(5, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit = new Rabbit(world);
        Bear bear = new Bear(world);
        Location loc_bear = new Location(0, 0);
        Location loc_rabbit = new Location(1,1);
        world.setTile(loc_rabbit, rabbit);
        world.setTile(loc_bear, bear);
        p.simulate();
        Assertions.assertFalse(world.getTile(loc_rabbit) instanceof Rabbit);
    }

    //Opgave K2-6a


    //Opgave K2-7a
    @Test
    public void bearCanEatBerrys() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_berry.txt", 800, 100); // BEAR AMOUNT: 1, WORLD SIZE: 1x1 (by our txt file)
        World world = p.getWorld();
        Bear bear = new Bear(world);
        Berry berry = (Berry) world.getNonBlocking(new Location(0,0)); //Since the world is 1x1, we know that the berry is placed at 0,0
        world.setTile(new Location(0, 0), bear);
        bear.setEnergy(0);
        bear.eat(berry);
        assertTrue(bear.getEnergy() > 0); // if the bear has eaten, it should have more energy than before
    }
    

    
    //Opgave K2-8a







}
