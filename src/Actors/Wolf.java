package Actors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Wolf extends Animal implements DynamicDisplayInformationProvider, Carnivore {
    private ArrayList<Wolf> pack_members;
    
    public Wolf(World world) {
        super(world);

        super.max_hp = 100;
        super.current_hp = max_hp;
        super.max_energy = 100;
        super.current_energy = max_energy;
        super.damage = 50;
        super.maturity_age = 3;
        super.vision_range = 3;
        super.move_range = 2;
        super.diet = Set.of("Carcass");
        //super.home = ....
        pack_members = new ArrayList<>();
    }

    public void act(World w) {
        super.act(w);
        moveRandom();
        System.out.println(pack_members);
        System.out.println(current_energy + "   " + max_energy);
    }

    @Override
    public void attacked(int dmg, Animal agressor) {
        super.attacked(dmg, agressor); //Decrases hp
        //alertPack(agressor);
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

    public void addtoPack(Wolf new_member) {
        for (Wolf member : pack_members) {
            member.add(new_member);
        }
    }


}