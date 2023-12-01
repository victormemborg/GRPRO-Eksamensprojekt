package Actors;

import java.util.Set;
import java.awt.Color;

import HelperMethods.Help;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Rabbit extends Animal implements Herbivore, DynamicDisplayInformationProvider {
    World world;

    public Rabbit() {
        super.max_hp = 100;
        super.current_hp = 100.00;
        super.max_energy = 100;
        super.current_energy = 100.00;
        super.maturity_age = 3;
        super.damage = 1;
        this.world = super.world;

        super.req_energy_reproduction = 0.6;

    }

    // Needs all rabbit behaviour
    @Override
    public void act(World world) {
        this.world = world;
        increaseAge(world);
        move();
        // reproduce();
        // sleep();
    }

    public void move() {
        if (current_energy > 0 && !world.getEmptySurroundingTiles().isEmpty()) {
            // check if there is a carnivore nearby
            Set<Location> neighbours = world.getSurroundingTiles(2); // magic number needs to be a field
            for (Location l : neighbours) {
                if (l != null && world.getTile(l) instanceof Carnivore) {
                    // get the location of the carnivore
                    Carnivore carnivore = (Carnivore) world.getTile(l);
                    Location carnivoreLoc = world.getLocation(carnivore);
                    // get location of the rabbit
                    Location rabbitLoc = world.getLocation(this);
                    // move opposite direction of the carnivore
                    getEscapeRoute(carnivoreLoc, rabbitLoc, 1);
                    System.out.println("Escape route");
                }
                Location loc = world.getLocation(this);
                if (Help.getRandomNearbyEmptyTile(world, loc, 1) != null) {
                    world.move(this, Help.getRandomNearbyEmptyTile(world, loc, 1));
                    System.out.println("Denne kørte");
                    // print the thread id of the current thread
                    System.out.println("Thread id: " + Thread.currentThread().getId());
                }
            }
        }
    }

    // NOT DONE NEEDS TO MOVE TO ITS BURROW
    public void sleep() {
        is_sleeping = true;
        while (current_energy < max_energy) {
            current_energy += 10;
        }
    }

    // NOT DONE
    public void attacked(int damage) {

    }

    // NOT DONE
    public void harvest() {

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
