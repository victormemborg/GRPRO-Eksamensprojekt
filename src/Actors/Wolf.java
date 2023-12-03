package Actors;

import java.awt.Color;
import java.util.Set;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Wolf extends Animal implements DynamicDisplayInformationProvider, Carnivore {
    
    public Wolf() {
        super.max_hp = 100;
        super.current_hp = 100;
        super.max_energy = 100;
        super.current_energy = 100;
        super.maturity_age = 3;
        super.damage = 1;
        super.diet = Set.of("Grass");

        super.req_energy_reproduction = 0.6;
        super.move_range = 2;
    }

    public void act(World world) {
        this.world = world;
        moveRandom();
    }

    public void sleep() {
        //todo
    }

    public DisplayInformation getInformation() {
        return new DisplayInformation(Color.gray, "wolf");
    }



}