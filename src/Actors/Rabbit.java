package Actors;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Rabbit extends Animal implements DynamicDisplayInformationProvider {
    public Rabbit(World world) {
        super(world);

        super.max_hp = 100;
        super.current_hp = 100;
        super.max_energy = 100;
        super.current_energy = 100;
        super.maturity_age = 3;
        super.damage = 1;
        super.diet = Set.of("Grass");

        super.req_energy_reproduction = 0.6;
        super.move_range = 2;
        super.vision_range = 2;

    }

    // Needs all rabbit behaviour
    @Override
    public void act(World placeholder) {
        if (world.isNight()) {
            //sleep();
        }
        ArrayList<Animal> threats = checkForCarnivore();
        if(threats.isEmpty()) {
            if (current_energy < 50) {
                moveToFood();
            } else {
                System.out.println("Moving Random");
                moveRandom();
            }
        } else {
            System.out.println("Escape!");
            escape(threats);
        }
        reproduce();
        // sleep();
    }


    // NOT DONE NEEDS TO MOVE TO ITS BURROW
    public void sleep() {
        is_sleeping = true;
        while (current_energy < max_energy) {
            current_energy += 10;
        }
    }

    @Override
    public DisplayInformation getInformation() {
        String image;
        if (age > maturity_age) {
            image = is_sleeping ? "rabbit-sleeping" : "rabbit-large";
        } else {
            image = is_sleeping ? "rabbit-small-sleeping" : "rabbit-small";
        }
        return new DisplayInformation(Color.DARK_GRAY, image);
    }

    /*
     * @Override
     * public void act(World world) {
     * isNight = world.isNight();
     * if(!isNight) {
     * increaseAgeIfMorning(world);
     * moveAndEat(world);
     * reproduce(world);
     * } else {
     * moveToBurrow(world);
     * }
     * }
     * 
     * //Age increments everyday
     * private void increaseAgeIfMorning(World world) {
     * if(world.getCurrentTime() == 0) {
     * age++;
     * }
     * }
     * 
     * private int getMaxEnergy() {
     * int MAX_ENERGY = 100 - age * 2;
     * return MAX_ENERGY;
     * }
     * 
     * 
     * //move the rabbit to its burrow
     * private void moveToBurrow(World world) {
     * if(burrow != null && world.isTileEmpty(burrowLoc)) {
     * world.move(this, burrowLoc);
     * isSleeping = true;
     * while(energyLevel < getMaxEnergy()) {
     * energyLevel += 10;
     * }
     * }
     * }
     * 
     * private void killRabbit(World world) {
     * world.delete(this);
     * amountOfRabbits--;
     * }
     * 
     * private void moveAndEat(World world) {
     * isSleeping = false;
     * if(!world.getEmptySurroundingTiles().isEmpty()) {
     * if(foodLevel > 0 && energyLevel > 0) {
     * world.move(this, getEmptyRandomLocations(world));
     * digHole(world);
     * eat(world);
     * foodLevel--;
     * energyLevel--;
     * } else {
     * killRabbit(world);
     * }
     * }
     * }
     * 
     * private void digHole(World world) {
     * if(burrow == null) {
     * burrowLoc = world.getLocation(this);
     * if(!world.containsNonBlocking(burrowLoc)) {
     * burrow = new Burrow();
     * world.setTile(burrowLoc, burrow);
     * }
     * }
     * }
     * 
     * private void eat(World world) {
     * Location foodLoc = world.getLocation(this);
     * if(world.containsNonBlocking(foodLoc) && world.getNonBlocking(foodLoc)
     * instanceof Grass) {
     * Grass grass = (Grass) world.getNonBlocking(foodLoc);
     * if(!grass.getDying()) {
     * world.delete(world.getNonBlocking(foodLoc));
     * foodLevel += FOOD_GAIN;
     * }
     * }
     * }
     * 
     * private void reproduce(World world) {
     * if(age > MIN_AGE_ADULT && foodLevel > REQUIRED_FOOD_REPRODUCTION &&
     * amountOfRabbits >= 2) {
     * Location birthLocation = getEmptyRandomLocations(world);
     * if(birthLocation != null && world.isTileEmpty(birthLocation)) {
     * world.setTile(birthLocation, new Rabbit());
     * foodLevel -= FOOD_LOSS_REPRODUCTION;
     * amountOfRabbits++;
     * }
     * }
     * }
     * 
     * @Override
     * public DisplayInformation getInformation() {
     * String image;
     * if(age > MIN_AGE_ADULT) {
     * image = isSleeping ? "rabbit-sleeping" : "rabbit-large";
     * } else {
     * image = isSleeping ? "rabbit-small-sleeping" : "rabbit-small";
     * }
     * return new DisplayInformation(Color.DARK_GRAY, image);
     * }
     * 
     * private Location getEmptyRandomLocations(World world) {
     * Random r = new Random();
     * Set<Location> neighbours = world.getEmptySurroundingTiles();
     * if (neighbours.isEmpty()) {
     * return null;
     * }
     * List<Location> list = new ArrayList<>(neighbours);
     * return list.get(r.nextInt(list.size()));
     * }
     */
}
