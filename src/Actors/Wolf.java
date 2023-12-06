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
    private ArrayList<Animal> enemies;
    
    public Wolf(World world, ArrayList<Wolf> pack_members) {
        super(world);
        super.max_hp = 100;
        super.current_hp = max_hp;
        super.max_energy = 100;
        super.current_energy = max_energy;
        super.damage = 50;
        super.maturity_age = 3;
        super.vision_range = 2;
        super.move_range = 2;
        super.diet = Set.of("Carcass");
        super.home = null;

        this.pack_members = pack_members;
        this.enemies = new ArrayList<>();
    }

    public void act(World w) {
        if (dead) {
            die();
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
        if (!wakeUp()) { return; }
        // Check for nearby enemies
        ArrayList<Location> visible_tiles = getAllLocsVisibleToPack();
        if (checkForEnemiesOfPack(visible_tiles)) { return; }
        // 
        if (getEnergyPercentage() < 0.7) {
            if (searchForFoodWthin(visible_tiles)) { return; }
        }
        //
        if (getEnergyPercentage() < 0.4) {
            ArrayList<Object> target_list = getObjectsOfClass("Animal", visible_tiles);
            target_list.removeAll(pack_members);
            if (approachAndAttackNearest(target_list)) { return; }
        }
        if (moveToNearestMember()) { 
            reproduce();
            return; 
        }
        moveRandom();
        reproduce(); // In case it has no packmembers it can still reproduce with wolfs from other packs
    }

    private void nightTimeBehaviour() {
        
    }

    private boolean checkForEnemiesOfPack(ArrayList<Location> area) {
        ArrayList<Object> visible_animals = getObjectsOfClass("Animal", area);
        ArrayList<Object> visible_enimies = new ArrayList<>();
        for (Object a : visible_animals) {
            if (enemies.contains(a)) {
                visible_enimies.add(a);
            }
        }
        return approachAndAttackNearest(visible_enimies) ? true : false;
    }

    private boolean moveToNearestMember() {
        if (pack_members.size() > 1) {
            Wolf nearest_member = (Wolf) getNearestObject(Help.castArrayList(pack_members));
            moveTo(nearest_member.getLocation());
            return true;
        } else {
            return false;
        }
    }

    @Override
    void die() {
        notifyDeath();
        super.die();
    }

    @Override
    public void attacked(int dmg, Animal agressor) {
        alertPack(agressor);
        super.attacked(dmg, agressor); //Decrases hp
        if (!getIsMature() || this.getHp() < agressor.getHp()) { //All social animals must override .getHp() to return the combined health of the pack
            ArrayList<Animal> threat_list = new ArrayList<>(Arrays.asList(agressor));
            escape(threat_list);
        } else {
            attack(agressor);
        }
    }

    @Override
    void attack(Animal victim) {
        alertPack(victim);
        super.attack(victim);
    }

    @Override
    public int getHp() {
        int pack_hp = 0;
        for (Wolf member : pack_members) {
            pack_hp += member.getIndividualHp();
        }
        return pack_hp;
    }

    @Override
    public Animal reproduce() {
        Wolf baby = (Wolf) super.reproduce();
        if (baby != null) {
            for (Wolf member : pack_members) {
                member.addPackMember(baby);
            }
        }
        return baby;
    }

    public void addPackMember(Wolf new_member) {
        pack_members.add(new_member);
    }

    public int getIndividualHp() {
        return current_hp;
    }

    private void alertPack(Animal animal) {
        for (Wolf member : pack_members) {
            member.addEnemy(animal);
        }
    }

    private void notifyDeath() {
        for (Wolf member : pack_members) {
            member.removeMember(this);
        }
    }

    public void addEnemy(Animal enemy) {
        enemies.add(enemy);
    }

    public void removeMember(Wolf member) {
        pack_members.remove(member);
    }

    private ArrayList<Location> getAllLocsVisibleToPack() {
        HashSet<Location> loc_set = new HashSet<>(); // We dont want overlap
        for (Wolf member : pack_members) {
            for (Location l : member.getSurroundingTilesAsList(vision_range)) {
                loc_set.add(l);
            }
        }
        ArrayList<Location> loc_list = new ArrayList<>();
        loc_list.addAll(loc_set);
        return loc_list;
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