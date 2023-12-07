package Unittests;

import static org.junit.Assert.assertTrue;
import java.io.FileNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.*;

import Actors.Burrow;
import Actors.Grass;
import Actors.Rabbit;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import app.Main;

public class firstWeek {

    // Opgave k1-1a
    @Test
    public void placeGrass() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week1_grass.txt", 800, 100); // GRASS AMOUNT: 1, WORLD SIZE: 3x3 (by our txt file)
        World world = p.getWorld();
        int totalGrass = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Grass) {
                    totalGrass++;
                }
            }
        }
        Assertions.assertEquals(1, totalGrass); // if there is one grass tile, the test passes
    }

    /* Opgave k1-1b
     * it would be stuck in an infinite loop if the grass never withered hence we use a while loop to simulate until the grass withers
     * if the grass withers, the test passes
     * the chance of the grass withering is 2% at the moment, so it might take a while for the grass to wither
     */
    @Test
    public void grassCanWither() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        world.setTile(loc, new Grass(world));
        int grassCount = 1;
        while (world.getTile(loc) instanceof Grass) { 
            p.simulate();
        }
        grassCount--;
        Assertions.assertEquals(0, grassCount);
    }

    // Opgave k1-1c
    @Test
    public void grassCanSpread() {
        Program p = new Program(5, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        world.setTile(loc, new Grass(world));
        for (int i = 0; i < 10; i++) {
            p.simulate();
        }
        int totalGrass = 1;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Grass) {
                    totalGrass++;
                }
            }
        }
        Assertions.assertTrue(totalGrass > 1); // if the grass has spread, there should be more than the initial amount of grass
    }

    // Opgave k1-1d
    @Test
    public void animalCanStandOnGrass() {
        Assertions.assertDoesNotThrow(() -> { // its gonna throw an exception if an animal cant stand on grass, so if it doesnt throw an exception, the test passes
            World world = new World(1);
            Location loc = new Location(0, 0);
            world.setTile(loc, new Grass(world));
            world.setTile(loc, new Rabbit(world));
        });
    }

    //Opgave k1-2a
    @Test
    public void placeRabbit() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week1_rabbit.txt", 800, 100); // RABBIT AMOUNT: 1, WORLD SIZE: 3x3 (by our txt file)
        World world = p.getWorld();
        int totalRabbit = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Rabbit) {
                    totalRabbit++;
                }
            }
        }
        Assertions.assertEquals(1, totalRabbit); // if there is one tile with a rabbit, the test passes
    }

    //Opgave k1-2b
    @Test
    public void killRabbit() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Rabbit rabbit = new Rabbit(world);
        world.setTile(loc, rabbit);
        rabbit.die();
        p.simulate();

        Assertions.assertFalse(world.getTile(loc) instanceof Rabbit); // if the rabbit is dead, the tile should not contain a rabbit
    }

    //Opgave k1-2c
    @Test
    public void rabbitCanEatGrass() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Location loc = new Location(0, 0);
        Rabbit rabbit = new Rabbit(world);
        rabbit.setEnergy(0);
        Grass grass = new Grass(world);
        world.setTile(loc, rabbit);
        world.setTile(loc, grass);
        rabbit.eat(grass);
        assertTrue(rabbit.getEnergy() > 0); // if the rabbit has eaten, it should have more energy than before
    }

    //Opgave k1-2d
    @Test
    public void rabbitAgeDeterminesEnergy() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();;
        Rabbit rabbit = new Rabbit(world);
        int initialMaxEnergy = rabbit.getMaxEnergy();
        rabbit.setAge(15);
        rabbit.changeMaxEnergy();
        int maxEnergy = rabbit.getMaxEnergy();
        Assertions.assertTrue(initialMaxEnergy > maxEnergy); // if the rabbit is older, it should have less energy
    }

    //Opgave k1-2e
    @Test
    public void rabbitsCanReproduce() {
        Program p = new Program(2, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit1 = new Rabbit(world);
        Rabbit rabbit2 = new Rabbit(world);
        world.setTile(new Location(0, 0), rabbit1);
        world.setTile(new Location(1, 0), rabbit2);
        rabbit1.setAge(3);
        rabbit2.setAge(3);
        rabbit1.reproduce();
        int totalRabbit = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Rabbit) {
                    totalRabbit++;
                }
            }
        }
        Assertions.assertEquals(3, totalRabbit); // if the rabbits have reproduced, there should be 3 rabbits
    }

    //Opgave k1-2f
    @Test
    public void rabbitCanDigBurrow() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit = new Rabbit(world);
        world.setTile(new Location(0, 0), rabbit);
        rabbit.createHome();
        Assertions.assertTrue(rabbit.getHome() != null); // if the rabbit has dug a burrow, it should have a home
    }

    //Opgave k1-2g
    @Test
    public void rabbitCanEnterBurrow() {
        Program p = new Program(1, 800, 100);
        World world = p.getWorld();
        Rabbit rabbit = new Rabbit(world);
        world.setTile(new Location(0, 0), rabbit);
        rabbit.createHome();
        world.setNight();
        p.simulate();
        int totalRabbit = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Rabbit) {
                    totalRabbit++;
                }
            }
        }
        
        Assertions.assertEquals(0,totalRabbit); // if the rabbit has entered the burrow, then the map should not contain any rabbits
    }

    
    //Opgave K1-3a
    @Test
    public void placeBurrow() throws FileNotFoundException {
        Program p = Main.createProgramFromFile("data/Unittest/week1_burrow.txt", 800, 100); // BURROW AMOUNT: 1, WORLD SIZE: 3x3 (by our txt file)
        World world = p.getWorld();
        int totalBurrow = 0;
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (world.getTile(new Location(i, j)) instanceof Burrow) {
                    totalBurrow++;
                }
            }
        }
        Assertions.assertEquals(1, totalBurrow); // if there is one tile with a rabbit, the test passes
    }


    //K1-3b
    @Test
    public void animalCanStandOnBurrow() {
        Assertions.assertDoesNotThrow(() -> { // its gonna throw an exception if an animal cant stand on a burrow, so if it doesnt throw an exception, the test passes
            World world = new World(1);
            Location loc = new Location(0, 0);
            world.setTile(loc, new Burrow(world));
            world.setTile(loc, new Rabbit(world));
        });
    }
}
