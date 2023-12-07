package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;
import java.util.Arrays;

import HelperMethods.Help;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.World;
import itumulator.world.Location;

public class Bear extends Animal implements Carnivore, DynamicDisplayInformationProvider {
    Animal baby;
    ArrayList<Location> test_home; //test
    int counter; // test

    public Bear(World world, Territory home) {
        super(world);
        super.max_hp = 800;
        super.current_hp = max_hp;
        super.max_energy = 300;
        super.current_energy = max_energy;
        super.maturity_age = 4;
        super.damage = 100;
        super.diet = Set.of("Berry", "Carcass");
        super.req_energy_reproduction = 0.6;
        super.move_range = 1;
        super.vision_range = 3;
        super.home = home;
        this.baby = null; //reproduction needs to be overwritten

        counter = 0; // test

    }

    // Needs all Bear behaviour
    @Override
    public void act(World w) {
        if (dead) {
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
        if (world.isDay()) {
            is_sleeping = false; // testing purposes
            dayTimeBehaviour();
        } else {
            nightTimeBehaviour();
        }
    }


    private void dayTimeBehaviour() {
        if (!wakeUp()) { return; }

        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);

        // Check if the "afraid_of-animal" is nearby
        if (checkForAfraidOfAnimals(visible_tiles)) { return; }

        //Check if the "mad_at-animal" is nearby
        if (checkForMadAtAnimals(visible_tiles)) { return; }

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
            if (searchForFoodWthin(visible_tiles)) { return; }

            ArrayList<Object> target_list = getObjectsOfClass("Animal", visible_tiles);
            if (approachAndAttackNearest(target_list)) { return; }
        }
        moveRandom();
        reproduce();
    }

    private void nightTimeBehaviour() {
        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
        checkForAfraidOfAnimals(visible_tiles);
        checkForMadAtAnimals(visible_tiles);
        is_sleeping = true; // testing purposes
        //Go towards territory and sleep
    }

    @Override
    void moveRandom() {
        moveTo(Help.getRandomNearbyEmptyTile(world, new Location(2, 2), 2)); // new Location(2,2) must be replaced with home.getLocation() once Territory.java has been made
    }

    @Override
    public void attacked(int dmg, Animal agressor){
        super.attacked(dmg, agressor);
        if ( ((double) current_hp / agressor.getHp()) < 0.75) { //if the agressor has around 30-35 % more health than the bear
            afraid_of.add(agressor);
            ArrayList<Object> threat = new ArrayList<>(Arrays.asList(agressor));
            escape(threat);
        } else {
            // Do not attack back instantly! Must wait until next act(). Otherwise we might get an infinite loop of attacking.
            mad_at.add(agressor);
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (getIsMature()) {
            image = is_sleeping ? "bear-sleeping" : "bear";
        } else {
            image = is_sleeping ? "bear-small-sleeping" : "bear-small";
        }
        return new DisplayInformation(Color.red, image);
    }
}