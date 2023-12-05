package Actors;

import java.util.ArrayList;
import java.util.Set;

import HelperMethods.Help;

import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.World;
import itumulator.world.Location;

public class Bear extends Animal implements Carnivore, DynamicDisplayInformationProvider {
    Animal afraid_of;
    int afraid_time;
    Animal mad_at;
    int mad_time;
    Animal baby;
    ArrayList<Location> test_home; //test
    int counter; // test

    public Bear(World world) {
        super(world);
        super.max_hp = 800;
        super.current_hp = max_hp;
        super.max_energy = 300; // test. Should be 800
        super.current_energy = max_energy;
        super.maturity_age = 4;
        super.damage = 100;
        super.diet = Set.of("Berry", "Carcass");
        super.req_energy_reproduction = 0.6;
        super.move_range = 1;
        super.vision_range = 3;
        //super.home = home;
        afraid_of = null;
        afraid_time = 0;
        mad_at = null;
        mad_time = 0;
        baby = null;

        counter = 0; // test

    }

    // Needs all Bear behaviour
    @Override
    public void act(World w) {
        if (dead) {
            world.move(this, this.getLocation());
            die();
            return;
        }

        /////////////////// test ////////////////////
        if (counter == 0) {
            test_home = getSurroundingTilesAsList(1);
            test_home.add(this.getLocation());
            System.out.println(test_home);
        }
        counter++;
        /////////////////////////////////////////////

        super.act(w);
        System.out.println("Health: " + current_hp + "    Energy: " + current_energy);
        //System.out.println("afraid of: " + afraid_of + "           dadadadadadadadadadadadadadadadadadad");
        //System.out.println("mad at: " + mad_at + "           efefefefefefefefefefefefefefefef");

        // Sleep if night
/*         if (world.isNight()) {
            sleep();
            return;
        } */

        // Check if the "afraid_of-animal" is nearby
        if (checkForAfraidOfAnimal()) { return; }

        //Check if the "mad_at-animal" is nearby
        if (checkForMadAtAnimal()) { return; }

        // Check territory for intruders and attack them
        ArrayList<Object> intruders = getObjectsWithInterface("Carnivore", test_home); // test_home must be replaced with (ArrayList<Location>) home.getArea() once Territory.java has been made
        intruders.remove(this); // Removes itself from the list of intruders
        intruders.remove(baby); // Removes its baby (if it has one) from the list of intruders
        if (approachAndAttackNearest(intruders)) { return; }

        // If hungry, search for food within territory
        if (getEnergyPercentage() < 0.7 ) {
            if (searchForFoodWthin(test_home)) { return; } // test_home must be replaced with (ArrayList<Location>) home.getArea() once Territory.java has been made
        }

        // If even more hungry, first check for food within vision range, if there is none, then attack all animals within vision range
        if (getEnergyPercentage() < 0.4 ) {
            ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
            if (searchForFoodWthin(visible_tiles)) { return; }

            ArrayList<Object> target_list = getObjectsOfClass("Animal", visible_tiles);
            System.out.println("Searching for prey withing vision range. Found: "+ target_list);
            if (approachAndAttackNearest(target_list)) { return; }
        }
        System.out.println("Moving random");
        moveRandom();
        reproduce();
    }

    boolean checkForAfraidOfAnimal() { // Kan nok samles med checkForMadAtAnimal vedhjælp af reflection, men ved ikke hvordan
        try {
            world.getLocation(afraid_of);
        } catch (IllegalArgumentException | NullPointerException e) {
            afraid_of = null;
            afraid_time = 0;
            return false;
        }
        if (afraid_time > 10) {
            afraid_of = null;
            afraid_time = 0;
            return false;
        }
        System.out.println(Help.getDistance(this.getLocation(), afraid_of.getLocation()));
        if (Help.getDistance(this.getLocation(), afraid_of.getLocation()) <= vision_range) { // Magic number
            ArrayList<Animal> threat = new ArrayList<>();
            threat.add(afraid_of);
            escape(threat);
            System.out.println("Escaping");
            return true;
        }
        return false;
    }

    boolean checkForMadAtAnimal() { // Kan nok samles med checkForAfraifOfAnimal vedhjælp af reflection, men ved ikke hvordan
        try {
            world.getLocation(mad_at);
        } catch (IllegalArgumentException | NullPointerException e) {
            mad_at = null;
            mad_time = 0;
            return false;
        }
        if (mad_time > 10) {
            mad_at = null;
            mad_time = 0;
            return false;
        }
        if (Help.getDistance(this.getLocation(), mad_at.getLocation()) <= vision_range) { // Magic number
            ArrayList<Object> target = new ArrayList<>();
            target.add(mad_at);
            approachAndAttackNearest(target);
            return true;
        }
        return false;
    }

    boolean searchForFoodWthin(ArrayList<Location> area) { // Maybe move to Animal.java
        ArrayList<Object> food_list = getObjectsInDiet(area);
        System.out.println("Searching for food within " + area.size() + " tiles. Found: "+ food_list);
        if (!(food_list.isEmpty())) {
            Eatable food = (Eatable) getNearestObject(food_list);
            if (moveTo(world.getLocation(food)) == 0) { // moveTo() moves the animal towards the final location and returns the distance to this final location
                System.out.println("Eating: " + food);
                eat(food);
            }
            return true;
        }
        return false;
    }

    boolean approachAndAttackNearest(ArrayList<Object> target_list) { // Maybe move to Animal.java
        if (target_list.isEmpty()) {
            return false;
        }
        Animal target = (Animal) getNearestObject(target_list);
        if (moveTo(target.getLocation()) == 1) {
            System.out.println("Attacking: " + target);
            attack(target);
        }
        return true;
    }

    @Override
    void moveRandom() {
        moveTo(Help.getRandomNearbyEmptyTile(world, new Location(2, 2), 2)); // new Location(2,2) must be replaced with home.getLocation() once Territory.java has been made
    }

    @Override
    public void attacked(int dmg, Animal agressor){
        super.attacked(dmg, agressor);
        if ( ((double) current_hp / agressor.getHp()) < 0.75) { //if the agressor has around 30-35 % more health than the bear
            ArrayList<Animal> threat = new ArrayList<>();
            threat.add(agressor);
            escape(threat);
            afraid_of = agressor;
            afraid_time = 0;
        } else {
            // Do not attack back instantly! Must wait until next act(). Otherwise we might get an infinite loop of attacking.
            mad_at = agressor;
            mad_time = 0;
        }
    }

/*     void sleep() {

    } */

    @Override
    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (age > maturity_age) {
            image = is_sleeping ? "bear-sleeping" : "bear";
        } else {
            image = is_sleeping ? "bear-small-sleeping" : "bear-small";
        }
        return new DisplayInformation(Color.red, image);
    }
}