package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.world.*;

public class Rabbit extends Animal {
    public Rabbit(World world) {
        super(world);
        super.max_hp = 100;
        super.current_hp = max_hp;
        super.max_energy = 100;
        super.current_energy = max_energy;
        super.damage = 1;
        super.maturity_age = 3;
        super.vision_range = 2;
        super.move_range = 2;
        super.diet = Set.of("Grass");
        super.home = null;
    }

    void dayTimeBehaviour() {
        if (!wakeUp()) { return; }
        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
        // Escape all visible threats
        ArrayList<Object> threats = getObjectsWithInterface("Predator", visible_tiles);
        if (escape(threats)) { return; }
        // Look for a home
        tryInhabitEmptyBurrow();
        // If hungry search for food
        if (getEnergyPercentage() < 0.5) {
            if (searchForFoodWthin(visible_tiles)) { return; }
        }
        // Move random and reproduce
        moveRandom();
        reproduce();
    }

    void nightTimeBehaviour() {
        if (!is_sleeping) {
            moveToHome(); // Hvis det skal sættes sådan her op, så skal createHome() garantere at skabe et home. Ellers får vi NullPointerException
        }
        if (getHome() == null && !is_sleeping) {
            moveRandom();
        }
    }

    @Override // Make it so the Rabbit dissapears into the burrow
    public void sleep() {
        super.sleep();
        world.remove(this);
    }

    @Override
    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (getIsMature()) {
            image = is_sleeping ? "rabbit-sleeping" : "rabbit-large";
        } else {
            image = is_sleeping ? "rabbit-small-sleeping" : "rabbit-small";
        }
        return new DisplayInformation(Color.DARK_GRAY, image);
    }
}
