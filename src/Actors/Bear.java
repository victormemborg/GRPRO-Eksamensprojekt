package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.World;
import itumulator.world.Location;

public class Bear extends Animal implements Carnivore, DynamicDisplayInformationProvider {

    public Bear(World world, Territory home) {

        super(world);

        super.max_hp = 1000;
        super.current_hp = max_hp;
        super.max_energy = 800;
        super.current_energy = max_energy;
        super.maturity_age = 4;
        super.damage = 600;
        super.diet = Set.of("Berry", "Carcass");
        super.req_energy_reproduction = 0.6;
        super.move_range = 2;
        super.vision_range = 3;
        super.home = home;

    }

    // Needs all Bear behaviour
    @Override
    public void act(World w) {
        super.act(w);
        System.out.println("Health: " + current_hp + "    Energy: " + current_energy);

        // Sleep if night
        if (world.isNight()) {
            sleep();
            return;
        }
        // Check territory for intruders and attack them
        ArrayList<Object> intruders = getObjectsWithInterface("Carnivore", home.getArea()); // home.getArea returns() a list of all locations within the territorry
        if (!intruders.isEmpty()) {
            Animal target = (Animal) getNearestObject(intruders);
            if (moveTo(target.getLocation()) <= 1) { // moveTo() moves the animal towards the final location and returns the distance to this final location
                attack(target); 
            }
            return;
        }
        // If hungry, search for food within territory
        if (getEnergyPercentage() < 0.7 ) {
            ArrayList<Object> food_list = getObjectsInDiet(home.getArea()); // home.getArea() returns a list of all locations within the territorry
            if (!(food_list.isEmpty())) {
                Eatable food = (Eatable) getNearestObject(food_list);
                if (moveTo(world.getLocation(food)) == 0) { // moveTo() moves the animal towards the final location and returns the distance to this final location
                    eat(food);
                }
                return;
            }
        // If even more hungry, attack all animals within vision range
        } if (getEnergyPercentage() < 0.4 ) {
            ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
            ArrayList<Object> target_list = getObjectsOfClass("Animal", visible_tiles);
            if (!target_list.isEmpty()) {
                Animal target = (Animal) getNearestObject(target_list);
                if (moveTo(target.getLocation()) <= 1) {
                    attack(target);
                }
                return;
            }
        }
        moveRandom();
        reproduce();
    }

    @Override
    void moveRandom() {
        super.moveRandom();
        // Skal bevæge sig tilfældig, men stadig ikke for langt fra territoriet
    }

    @Override
    public void attacked(int dmg, Animal agressor){
        super.attacked(dmg, agressor);
        // Must be extended
    }

    void sleep() {

    }

    @Override
    public DisplayInformation getInformation() {
        String image;
        if (age > maturity_age) {
            image = is_sleeping ? "bear-sleeping" : "bear-large";
        } else {
            image = is_sleeping ? "bear-small-sleeping" : "bear-small";
        }
        return new DisplayInformation(Color.red, image);
    }
}