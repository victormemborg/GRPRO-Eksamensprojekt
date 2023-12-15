package Actors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;

import itumulator.executable.DisplayInformation;
import itumulator.world.*;

public class Wolf extends SocialAnimal implements Predator {
    /**
     * Creates a new Wolf
     * @param world The world the Wolf is in
     */
    public Wolf(World world) {
        super(world);
        super.max_hp = 250;
        super.current_hp = max_hp;
        super.max_energy = 200;
        super.current_energy = max_energy;
        super.damage = 75;
        super.maturity_age = 3;
        super.vision_range = 2;
        super.move_range = 2;
        super.diet = Set.of("Carcass");
        super.home = null;
        super.home_image = "hole";
    }

    @Override
    void dayTimeBehaviour() {
        if (!wakeUp()) { return; }
        // Check for afraid_of-animals within vision range
        if (checkForAfraidOfAnimals(getSurroundingTilesAsList(vision_range))) { return; }
        
        ArrayList<Location> visible_tiles = getAllLocsVisibleToPack(); // Pack shares vision for the rest of dayTimeBehaviour()

        // Check for mad_at-animals within packs combined vision range
        if (checkForMadAtAnimals(visible_tiles)) { return; }
        // if it has no home, search for one
        tryInhabitEmptyBurrow();
        // If hungry, search for food
        if (getEnergyPercentage() < 0.75) {
            if (searchForFoodWthin(visible_tiles)) { return; }
        }
        // If even more hungry, and find no food, hunt animals
        if (getEnergyPercentage() < 0.5) {
            ArrayList<Object> target_list = getObjectsOfClass("Animal", visible_tiles);
            target_list.removeAll(pack_members);
            if (approachAndAttackNearest(target_list)) { return; }
        } 
        // If not hungry, or cant find find animals nor food, move closer to pack
        if (moveToNearestMember()) { 
            reproduce(); // So it prefers to reproduce with pack members
            return; 
        }
        // If none of the above, move random
        moveRandom();
        reproduce(); // In case it has no packmembers it can still reproduce with wolfs from other packs
    }

    @Override
    void nightTimeBehaviour() {
        if (!is_sleeping) {
            // // Check for afraid_of-animals within vision range
            if (checkForAfraidOfAnimals(getSurroundingTilesAsList(vision_range))) { return; }
            // Hunts alone at night if too hungry
            if (getEnergyPercentage() < 0.3) {
                ArrayList<Object> target_list = getObjectsOfClass("Animal", getSurroundingTilesAsList(vision_range));
                target_list.removeAll(pack_members);
                if (approachAndAttackNearest(target_list)) { return; }
            }
            // Else go home and sleep
            if (moveToHome()) { return; }
            moveRandom();
        }
    }

    @Override // Make it so the pack is afraid of, or mad at, the agressor depending on their relative hp
    public void attacked(int dmg, Animal agressor) {
        super.attacked(dmg, agressor); //Decrases hp
        if (this.getHp() < ( agressor.getHp() / 2 )) {
            makePackAfraidOf(agressor);
        } else {
            // Do not attack back instantly! Must wait until next act(). Otherwise we might get an infinite loop of attacking.
            makePackMadAt(agressor);
        }
    }

    @Override // Make it so all packmembers will be mad at the victim
    void attack(Animal victim) {
        makePackMadAt(victim);
        super.attack(victim);
    }

    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (getIsMature()) {
            image = is_sleeping ? "wolf-sleeping" : "wolf";
        } else {
            image = is_sleeping ? "wolf-small-sleeping" : "wolf-small";
        }
        return new DisplayInformation(Color.red, image);
    }
}