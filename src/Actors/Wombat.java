package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.world.*;

public class Wombat extends SocialAnimal {
    public Wombat(World world) {
        super(world);
        super.max_hp = 200;
        super.current_hp = max_hp;
        super.max_energy = 200;
        super.current_energy = max_energy;
        super.damage = 1;
        super.maturity_age = 3;
        super.vision_range = 2;
        super.move_range = 2;
        super.diet = Set.of("Grass");
        super.home = null;
    }

    @Override // Make it so food is shared between all members of the pack
    public void eat(Eatable food) {
        int energy_split = Math.round(food.consumed() / pack_members.size());
        for (SocialAnimal member : pack_members) {
            member.increaseEnergy(energy_split);
        }
        poopBricks();
    }

    @Override
    void dayTimeBehaviour() {
        //A wombat has 0.25% (2.5% for the whole day) chance of waking up each daytimetick. If it wakes up, it stays awake for 10 ticks
        double awakeProbability = 0.0025;
        int awakeDuration = 10; //Duration in ticks
        //Check if the wombat should wake up
        if (r.nextDouble() < awakeProbability) {
            for (int i = 0; i < awakeDuration; i++) {
                nightTimeBehaviour();
                System.out.println("Wombat is awake");
            }
        } else {
            if (!is_sleeping) {
                moveToHome();
            }
            if (getHome() == null && !is_sleeping) {
                moveRandom();
            }
        }
    }

    //Wombats are nocturnal animals, so their activity is at night
    @Override
    void nightTimeBehaviour() {
        if (!wakeUp()) { return; }
        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
        // Escape all visible threats
        ArrayList<Object> threats = getObjectsWithInterface("Predator", visible_tiles);
        if (escape(threats)) { return; }
        // Look for a home
        tryInhabitEmptyBurrow();
        // If hungry search for food
        if (getEnergyPercentage() < 0.75) {
            if (searchForFoodWthin(visible_tiles)) {return;}
        }
        // If not hungry, or cant find find animals nor food, move closer to pack
        if (moveToNearestMember()) { 
            /*if(isMatingSeason()) {
                reproduce();
            } */
            return; 
        }
        moveRandom();
        reproduce(); // In case it has no packmembers it can still reproduce with wolfs from other packs
    }

    /* 
    private boolean isMatingSeason() {
        //Logic for a wombat mating in the summer months
    }  */

    //Wombats will poop bricks if they are full. The bricks act as fertilizer for the foliage
    private void poopBricks() {
        if(getEnergy() > 0.75) { 
            //Poop poop = new Poop(world);
            //world.setTile(this.getLocation(), poop);
        }
    }

    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (getIsMature()) {
            image = is_sleeping ? "wombat-sleeping" : "wombat";
            //we need to add an image for a wombat baby
        } else {
            image = is_sleeping ? "wombat-sleeping" : "wombat";
        }
        return new DisplayInformation(Color.red, image);
    }
}