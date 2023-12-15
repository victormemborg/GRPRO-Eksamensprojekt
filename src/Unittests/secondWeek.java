package Unittests;

import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.*;

import Actors.Wolf;
import HelperMethods.Help;
import Actors.Bear;
import Actors.Rabbit;
import Actors.Berry;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import app.Main;

public class secondWeek {
    
    //Opgave K2-1a
    @Test
    public void placeWolf() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_wolf.txt", 800, 100); // WOLF AMOUNT: 1, WORLD SIZE: 1x1 (by our txt file)
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Assertions.assertTrue(world.getTile(loc) instanceof Wolf);
    }

    //Opgave K2-1b
    @Test
    public void killWolf() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Wolf wolf = new Wolf(world);
        world.setTile(loc, wolf);
        wolf.die();
        p.simulate();
        Assertions.assertFalse(world.getTile(loc) instanceof Wolf); // if the wolf is dead, the tile should not contain a rabbit
    }


    //Opgave K2-1c
    @Test
    public void wolfHunts() {
        Program p = new Program(2, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit = new Rabbit(world);
        Wolf wolf = new Wolf(world);
        world.setTile(new Location(0,1), wolf);
        world.setTile(new Location(1,1), rabbit);
        wolf.setEnergy(100);
        for(int i = 0; i < 40; i++) { 
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

    //Opgave K2-2a
    @Test
    public void wolvesAreInPacks() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_wolfpack.txt", 800, 100); // WOLF AMOUNT: 4, WORLD SIZE: 2x2 (by our txt file)
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Wolf wolf = (Wolf) world.getTile(loc);
        Assertions.assertTrue(wolf.getPackMembers().size() == 4); //would be true if the pack members are added correctly
    }


    //Opgave K2-3a
    @Test
    public void wolvesFightsOtherPacks() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_wolfpack2.txt", 800, 100); // WOLF AMOUNT: 8 (2x4 pack size), WORLD SIZE: 4x4 (by our txt file)
        World world = p.getWorld();
        //right now there are 2 packs of 4 wolves each, and we need to make them fight by simulating and then showing that the amount of wolves is less than 8
        for(int i = 0; i < 30; i++) {
            p.simulate();
        }
        int totalWolves = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Wolf) {
                    totalWolves++; // if a wolf is found, add 1 to the totalWolves
                }
            }
        }
        Assertions.assertFalse(totalWolves == 8); // if the wolves have fought, at least one wolf should be dead
    }    


    //Opgave K2-4a
    @Test
    public void RabbitFearsWolf(){
        Program p = new Program(5, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit = new Rabbit(world);
        Wolf wolf = new Wolf(world);
        Location loc_wolf = new Location(0, 0);
        Location loc_rabbit = new Location(1,1);
        world.setTile(loc_rabbit, rabbit);
        world.setTile(loc_wolf, wolf);
        p.simulate();
        Assertions.assertFalse(world.getTile(loc_rabbit) instanceof Rabbit); // if the rabbit has run away, there should not be a rabbit anymore
    }

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
    @Test
    public void bearsOnlyMoveNearTerritory() {
        Program p = new Program(5, 800, 100);
        World world = p.getWorld();
        String bearTerritory = "0,0";
        Bear bear = new Bear(world, bearTerritory);
        Location bearT = Help.strToLoc(bearTerritory);
        world.setTile(bearT, bear);
        for(int i = 0; i < 10; i++) {
            p.simulate();
        }
        int distance = Help.getDistance(bearT, bear.getLocation());
        Assertions.assertTrue(distance <= 2); //the max distance a bear can move away from its territory is a radius of 2 - if its not hunting. 
    }


    //Opgave K2-7a
    @Test
    public void bearCanEatBerrys() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_berry.txt", 800, 100); // BERRY AMOUNT: 1, WORLD SIZE: 1x1 (by our txt file)
        World world = p.getWorld();
        Bear bear = new Bear(world);
        Berry berry = null;
        //loop through the world to find the berry
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Berry) {
                    berry = (Berry) world.getNonBlocking(new Location(i, j));
                }
            }
        }
        world.setTile(new Location(0, 0), bear);
        bear.setEnergy(50);
        while(berry.isEaten() == false) {
            p.simulate();
        }
        assertTrue(berry.isEaten()); // since the bear has low energy, it should eat the berry and the berry should be eaten
    }

    
    //Opgave K2-8a
    @Test
    public void wolfsFightBears() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week2_wolfandbear.txt", 800, 100); // WOLF AMOUNT: 12 (1 pack), WORLD SIZE: 4x4 (by our txt file)
        World world = p.getWorld();
        //There is 12 wolfs and 1 bear, and we need to make them fight by simulating and then showing that the wolfs has killed the bear
        for(int i = 0; i < 30; i++) {
            p.simulate();
        }
        int totalBear = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Bear) {
                    totalBear++; // if a bear is found, add 1 to the totalBear
                }
            }
        }
        Assertions.assertFalse(totalBear == 1); //If the wolf pack has killed the bear within 30 ticks there should be no bears left
    }          
}
