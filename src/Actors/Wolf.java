package Actors;

import java.awt.Color;
import java.util.Set;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Wolf extends Animal implements DynamicDisplayInformationProvider, Carnivore {
    
    public Wolf(World world) {
        super(world);
        super.max_hp = 100;
        super.max_energy = 100;
        super.damage = 50;
        super.maturity_age = 3;
        super.vision_range = 3;
        super.move_range = 2;
        super.diet = Set.of("Carcass");
        //super.home = ....
        //ArrayList<Wolf> pack = ....
    }

    public void act(World w) {
        super.act(w);
    }

    @Override
    public void attacked(int dmg, Animal agressor) {
        super.attacked(dmg, agressor); //Decrases hp
        alertPack(agressor);
        if (age < maturity_age || this.getHp() < agressor.getHp()) { //All social animals must override .getHp() to return the combined health of the pack
            escape(checkForCarnivore());
        } else {
            attack(agressor);
        }
    }

    public void sleep() {
        //todo
    }

    public DisplayInformation getInformation() {
        if (age > maturity_age) {
            return new DisplayInformation(Color.gray, "wolf");
        } else {
            return new DisplayInformation(Color.gray, "wolf-small");
        }
        
    }



}