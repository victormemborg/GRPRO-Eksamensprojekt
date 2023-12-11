package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import HelperMethods.Help;

import itumulator.executable.DisplayInformation;
import itumulator.world.World;
import itumulator.world.Location;

public class Bear extends Animal implements Predator {
    Animal baby;
    Territory territory; // Have to convince the compiler thta home is indeed of the class Territory

    public Bear(World world, String loc_str) {
        this(world);
        if (loc_str != null) {
            this.territory = new Territory(world, Help.strToLoc(loc_str));
            this.home = territory;
        }
    }

    // currently used if the input file does not contain a location for the bear
    public Bear(World world) { 
        super(world);
        super.max_hp = 800;
        super.current_hp = max_hp;
        super.max_energy = 300;
        super.current_energy = max_energy;
        super.damage = 100;
        super.maturity_age = 4;
        super.vision_range = 3;
        super.move_range = 1;
        super.diet = Set.of("Berry", "Carcass");
        this.baby = null; //reproduction needs to be overwritten
        this.territory = new Territory(world, Help.getRanLocWithoutType(0, world)); 
        this.home = territory;
    }
    
    // Needs all Bear specific behaviour
    @Override
    public void act(World w) {
        if (world.isDay()) { is_sleeping = false; } // Has to be done manually beacause Bear does not use the WakeUp() method, as it does not dissapear from the map when sleeping
        super.act(w);
    }

    void dayTimeBehaviour() {
        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);

        // Check if the "afraid_of-animal" is nearby
        if (checkForAfraidOfAnimals(visible_tiles)) { return; }

        //Check if the "mad_at-animal" is nearby
        if (checkForMadAtAnimals(visible_tiles)) { return; }

        // Check territory for intruders and attack them
        ArrayList<Object> intruders = getObjectsWithInterface("Predator", territory.getArea());
        intruders.remove(this); // Removes itself from the list of intruders
        intruders.remove(baby); // Removes its baby (if it has one) from the list of intruders
        if (approachAndAttackNearest(intruders)) { return; }

        // If hungry, search for food within territory
        if (getEnergyPercentage() < 0.7 ) {
            if (searchForFoodWthin(territory.getArea())) { return; }
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

    void nightTimeBehaviour() {
        if (!is_sleeping) {
            ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
            if (checkForAfraidOfAnimals(visible_tiles)) { return; }
            if (checkForMadAtAnimals(visible_tiles)) { return; }
            moveToHome();
        }
    }

    @Override
    void moveRandom() {
        moveTo(Help.getRandomNearbyEmptyTile(world, home.getLocation(), territory.getRadius() + 1)); // Magic number
    }

    @Override
    public void attacked(int dmg, Animal agressor){
        super.attacked(dmg, agressor);
        if ( ((double) current_hp / agressor.getHp()) < 0.75) { //if the agressor has around 30-35 % more health than the bear
            afraid_of.add(agressor);
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