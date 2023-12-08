package Actors;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

import HelperMethods.Help;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Wolf extends Animal implements DynamicDisplayInformationProvider, Carnivore {
    private ArrayList<Wolf> pack_members;
    
    public Wolf(World world, ArrayList<Wolf> pack_members) {
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
        super.mad_at = new ArrayList<>();
        super.afraid_of = new ArrayList<>();
        this.pack_members = pack_members;
    }

    public Wolf(World world) { // For creating a Wolf without a pack, might delete this
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
        super.mad_at = new ArrayList<>();
        super.afraid_of = new ArrayList<>();
        this.pack_members = new ArrayList<>(); // Arrays.asList(this)
    }

    public void act(World w) {
        if (dead) {
            super.die();
            return;
        }
        super.act(w);
        if (world.isDay()) {
            dayTimeBehaviour();
        } else {
            nightTimeBehaviour();
        }
    }

    private void dayTimeBehaviour() {
        if (!wakeUp()/*its the first of the month*/) { return; }
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

    private void nightTimeBehaviour() {
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
            moveToHome();
        }
    }

    private boolean moveToNearestMember() {
        ArrayList<Wolf> awake_members = new ArrayList<>();
        for (Wolf member : pack_members) {
            if (!member.getIsSleeping()) {
                awake_members.add(member);
            }
        }
        Wolf nearest_member = (Wolf) getNearestObject(Help.castArrayList(awake_members));
        moveTo(nearest_member.getLocation());
        return true;
    }

    @Override // Make it so setHome() updates home for all members, but this sucks
    public void setHome(Home home) {
        for (Wolf member : pack_members) {
            member.setIndividualHome(home); // Very bad
        }
    }

    @Override // Make it so all packmembers know that the individual wolf has died
    public void die() {
        notifyDeath();
        super.die();
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

    @Override // Make it so getHp() returns the combined health of the pack
    public int getHp() {
        int pack_hp = 0;
        for (Wolf member : pack_members) {
            pack_hp += member.getIndividualHp();
        }
        return pack_hp;
    }

    @Override // Make it so the baby is added to the pack
    public Animal reproduce() {
        Wolf baby = (Wolf) super.reproduce();
        int pack_size = pack_members.size();
        if (baby != null) {
            for (int i = 0 ; i < pack_size ; i++) {
                pack_members.get(i).addPackMember(baby);
                baby.addPackMember(pack_members.get(i));
            }
            baby.addPackMember(baby);
        }
        return baby;
    }

    @Override // Make it so food is shared between all members of the pack
    public void eat(Eatable food) {
        int energy_split = Math.round(food.consumed() / pack_members.size());
        for (Wolf member : pack_members) {
            member.increaseEnergy(energy_split);
        }
    }

    @Override // Make it so the Wolf dissapears into the burrow
    public void sleep() {
        super.sleep();
        world.remove(this);
    }

    private ArrayList<Location> getAllLocsVisibleToPack() {
        HashSet<Location> loc_set = new HashSet<>(); // We dont want overlap
        for (Wolf member : pack_members) {
            if (!member.getIsSleeping()) {
                for (Location l : member.getSurroundingTilesAsList(vision_range)) {
                    loc_set.add(l);
                }
            }
        }
        ArrayList<Location> loc_list = new ArrayList<>();
        loc_list.addAll(loc_set);
        return loc_list;
    }

    public void addPackMember(Wolf new_member) {
        pack_members.add(new_member);
    }

    public int getIndividualHp() {
        return current_hp;
    }

    private void makePackMadAt(Animal animal) {
        for (Wolf member : pack_members) {
            member.makeMadAt(animal);
        }
    }

    private void makePackAfraidOf(Animal animal) {
        for (Wolf member : pack_members) {
            member.makeAfraidOf(animal);
        }
    }

    public void makeMadAt(Animal animal) {
        mad_at.add(animal);
    }

    public void makeAfraidOf(Animal animal) {
        afraid_of.add(animal);
    }

    private void notifyDeath() {
        int pack_size = pack_members.size();
        for (int i = 0 ; i < pack_size ; i++) {
            if (!pack_members.get(i).equals(this)) {
                pack_members.get(i).removeMember(this);
            }
        }
    }

    public void removeMember(Wolf member) {
        pack_members.remove(member);
    }

    public void setIndividualHome(Home home) { // :(
        super.setHome(home);
    }

    public ArrayList<Wolf> getPackMembers() {
        return pack_members;
    }

    public int getPackSize() {
        return pack_members.size();
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