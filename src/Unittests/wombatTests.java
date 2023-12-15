package Unittests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.*;

import app.Main;
import itumulator.world.*;
import Actors.Grass;
import Actors.Poop;
import Actors.Wolf;
import Actors.Wombat;
import itumulator.executable.Program;

public class wombatTests {

    /**
     * This test checks if a wombat can be loaded into the world from a txt file
     */
    @Test
    public void wombatExistsInWorld() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week4_wombat.txt", 800, 100); // WOMBAT AMOUNT: 1, WORLD SIZE: 1x1 (by our txt file)
        World world = p.getWorld();
        Assertions.assertTrue(world.getTile(new Location(0,0)) instanceof Wombat); //If a wombat is placed in the world, the test passes
    }

    /**
     * This test checks if the wombat is active during the night.
     * This is done by checking if the wombat moves around during the night.
     */
    @Test
    public void wombatIsNocturnal() {
        Program p = new Program(3, 800, 100);
        World world = p.getWorld();
        Wombat w = new Wombat(world);
        Location initialLocation = new Location(0,0);
        world.setTile(initialLocation, w);
        world.setNight(); //Sets the world to night
        while(world.getTile(initialLocation) instanceof Wombat && world.isNight()) { //While the wombat is still in the same location and it is still night
            p.simulate(); //Simulate the world
        }
        Assertions.assertFalse(world.getTile(initialLocation) instanceof Wombat); //If a wombat is not in the same location, the test passes
    }

    /**
     * This test checks if the wombat is capable of eating.
     * This is done by checking if the wombat has more energy after eating.
     */
    @Test
    public void wombatCanEat() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week4_grassandwombat.txt", 800, 100); // GRASS AMOUNT: 9, WORLD SIZE: 3x3 (by our txt file)
        World world = p.getWorld();
        Wombat w;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Wombat) {
                    w = (Wombat) world.getTile(new Location(i, j));
                    w.setEnergy(20);
                    while(w.getEnergy() <= 20) { //would be an infinite loop if the wombat was not able to eat
                        p.simulate();
                    }
                    assertTrue(w.getEnergy() > 20); // if the wombat has eaten, it should have more energy than before
                }
            }
        }
    }

    /**
     * This test checks if the wombat is capable of pooping.
     * This is done by checking if the world contains an instance of poop after eating.
     */
    @Test
    public void wombatCanPoop() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week4_grassandwombat.txt", 800, 100); // GRASS AMOUNT: 9, WORLD SIZE: 3x3 (by our txt file)
        World world = p.getWorld();
        int amountOfPoop = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Poop) {
                    amountOfPoop++;
                }
            }
        }        
        while(amountOfPoop == 0) { //would be an infinite loop if the wombat was not able to poop
            p.simulate();
            return;
        }
        assertTrue(amountOfPoop > 0); // if the wombat has pooped, there should be poop in the world
    }

    /**
     * This test checks if the wombat is capable of reproducing
     * This is done by checking if the world contains more than two instances of wombat after reproducing
     */
    @Test
    public void wombatCanReproduce() {
        Program p = new Program(2, 800, 100);
        World world = p.getWorld();
        Wombat wombat1 = new Wombat(world);
        Wombat wombat2 = new Wombat(world);
        world.setTile(new Location(0, 0), wombat1);
        world.setTile(new Location(1, 0), wombat2);
        wombat1.setAge(3);
        wombat2.setAge(3);
        wombat1.reproduce();
        int totalwombat = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Wombat) {
                    totalwombat++;
                }
            }
        }
        Assertions.assertEquals(3, totalwombat); // if the wombats have reproduced, there should be 3 wombats
    }

    /**
     * This test checks if the wombat is capable of dying
     */
    @Test
    public void wombatCanDie() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Wombat wombat = new Wombat(world);
        world.setTile(new Location(0, 0), wombat);
        wombat.die();
        p.simulate();
        Assertions.assertFalse(world.getTile(new Location(0, 0)) instanceof Wombat); // if the wombat has died, there should not be a wombat anymore
    }

    @Test
    public void wombatPoopFertilizesGrass() {
        Program p = new Program(2, 800, 100);
        World world = p.getWorld();
        Poop poop = new Poop(world);
        world.setTile(new Location(0, 0), poop);
        while(world.getTile(new Location(0, 0)) instanceof Poop) {
            p.simulate();
        }
        int totalGrass = 0;
        // if the poop has fertilized the grass, there should be grass in the world
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Grass) {
                    totalGrass++;
                }
            }
        }
        Assertions.assertTrue(totalGrass > 0); //The poop should have fertilized the grass hence the totalGrass should be more than 0
    }

    /**
     * When the wombat is sleeping and there are predators nearby, the wombat should be scared and activate its natural defense mechanism
     * (which is to turn its back to the predator)
     */
    @Test
    public void wombatIsScaredWhenSleeping() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week4_wombatandwolf.txt", 800, 100); // WOLF AMOUNT: 1, WOMBAT AMOUNT: 1, WORLD SIZE:5x5 (by our txt file)
        World world = p.getWorld();
        Wombat w = null;
        for(int i = 0; i < world.getSize(); i++) {
            for(int j=0; j < world.getSize(); j++) {
                if(world.getTile(new Location(i, j)) instanceof Wombat) {
                    w = (Wombat) world.getTile(new Location(i, j));
                    w.setEnergy(300); //energy is set to 300 so it will not die.
                } else if(world.getTile(new Location(i, j)) instanceof Wolf) {
                    Wolf wolf = (Wolf) world.getTile(new Location(i, j));
                    wolf.setEnergy(300); //energy is set to 300 so it will not die.
                }
            }
        }
        while(w.getHomeImage() != "wombat-hole-scared") { // if the wombat is not scared, keep simulating the world - would be an infinite loop if the wombat cannot be scared
            p.simulate();
        }
        Assertions.assertEquals("wombat-hole-scared", w.getHomeImage()); // if the wombat is scared, it should turn its back to the predator
    }
        

    
}
