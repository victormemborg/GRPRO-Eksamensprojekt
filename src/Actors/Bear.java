package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.World;

public class Bear extends Animal implements Carnivore, DynamicDisplayInformationProvider {

    public Bear(World world) {

        super(world);

        super.max_hp = 1000;
        super.current_hp = 1000;
        super.max_energy = 800;
        super.current_energy = 300;
        super.maturity_age = 4;
        super.damage = 600;
        super.diet = Set.of("Berry", "Carcass");

        super.req_energy_reproduction = 0.6;
        super.move_range = 2;
        super.vision_range = 3;

    }

    // Needs all Bear behaviour
    @Override
    public void act(World w) {
        super.act(w);
        System.out.println("Health: " + current_hp + "    Energy: " + current_energy);
        if (world.isNight()) {
            sleep();
        }

        if ((double) current_energy / max_energy < 0.7 ) {
            System.out.println("energy lvl: " + (double) current_energy / max_energy + "looking for food");
            moveToFood();
        } else if(!world.isNight()) {
            moveRandom();
        }

        //Attacking behavior ::OBS:: needs to react to being attacked as well
        if(current_energy / max_energy < 0.8 && getNearestAnimal() != null){
            attack(getNearestAnimal());
        }
        reproduce();
    }

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
            image = is_sleeping ? "bear-sleeping" : "bear-large";
        } else {
            image = is_sleeping ? "bear-small-sleeping" : "bear-small";
        }
        return new DisplayInformation(Color.red, image);
    }
}