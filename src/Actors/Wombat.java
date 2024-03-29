package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.world.*;

public class Wombat extends SocialAnimal {
    private int grassEaten;
    private double awakeProbability = 0.0025;
    private int awakeDuration = 10; //Duration in ticks
    private int ticks_awake = 0;

    /**
     * Constructor for Wombat
     * @param world The world the wombat is in
     */
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
        super.home_image = "hole";
        this.grassEaten = 0;
    }

    @Override // Make it so food is shared between all members of the pack
    public void eat(Eatable food) {
        super.eat(food);
        poopBricks();
    }

    @Override
    void dayTimeBehaviour() {
        //A wombat has 0.25% (2.5% for the whole day) chance of waking up each daytimetick. If it wakes up, it stays awake for 10 ticks
        //Check if the wombat should wake up randomly
        if (r.nextDouble() < awakeProbability) {
            ticks_awake = 0;
        }
        if (ticks_awake < awakeDuration) {
            nightTimeBehaviour();
            ticks_awake++;
            return;
        }
        if (!is_sleeping) {
            if (moveToHome()) { return; }
            moveRandom();
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
            if (searchForFoodWthin(visible_tiles)) { return; }
        }
        // If not hungry, or cant find find animals nor food, move closer to pack
        if (moveToNearestMember()) { 
            reproduce();
            return; 
        }
        moveRandom();
        reproduce(); // In case it has no packmembers it can still reproduce with wombats from other packs
    }

    @Override
    public String getHomeImage() {
        if (isScaredWhilstSleeping()) {
            return "wombat-hole-scared";
        }
        return super.getHomeImage();
    }

    /**
     * Makes the wombat poop for every 5 grass it eats
     */
    private void poopBricks() {
        grassEaten++;
        if(grassEaten % 5 == 0) { 
            Poop poop = new Poop(world);
            world.setTile(this.getLocation(), poop);
        }
    }

    /**
     * Method used when the wombat is sleeping. It checks whether there are any predators nearby
     * @return true if there is a predator nearby, false if not
     */
    public boolean isScaredWhilstSleeping() {
        if(!is_sleeping) { return false; }
        Set<Location> tiles_set = world.getSurroundingTiles(home.getLocation(), vision_range);
        ArrayList<Location> visible_tiles = new ArrayList<>();
        visible_tiles.addAll(tiles_set);
        ArrayList<Object> visible_predators = getObjectsWithInterface("Predator", visible_tiles);
        return !visible_predators.isEmpty(); 
    }

    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (getIsMature()) {
            image = is_sleeping ? "wombat-sleeping" : "wombat";
        } else {
            image = is_sleeping ? "wombat-small-sleeping" : "wombat-small";
        }
        return new DisplayInformation(Color.red, image);
    }
}