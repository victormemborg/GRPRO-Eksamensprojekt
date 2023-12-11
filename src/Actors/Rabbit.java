package Actors;

import java.util.ArrayList;
import java.util.Set;
import java.awt.Color;

import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.*;

public class Rabbit extends Animal implements DynamicDisplayInformationProvider {
    public Rabbit(World world) {
        super(world);
        super.max_hp = 100;
        super.current_hp = max_hp;
        super.max_energy = 100;
        super.current_energy = max_energy;
        super.maturity_age = 3;
        super.damage = 1;
        super.diet = Set.of("Grass");
        super.req_energy_reproduction = 0.6;
        super.move_range = 2;
        super.vision_range = 2;
        super.home = null;
        super.afraid_of = null;
        super.mad_at = null;
    }

    // Needs all rabbit behaviour
    @Override
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

    private void nightTimeBehaviour() {
        if (!is_sleeping) {
            moveToHome(); // Hvis det skal sættes sådan her op, så skal createHome() garantere at skabe et home. Ellers får vi NullPointerException
        }
        if (getHome() == null && !is_sleeping) {
            moveRandom();
        }
    }

    private void dayTimeBehaviour() {
        if (!wakeUp()) { return; }
        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
        // Escape all visible threats
        ArrayList<Object> threats = getObjectsWithInterface("Carnivore", visible_tiles);
        if (escape(threats)) { return; }
        // Look for a home
        tryInhabitEmptyBurrow();
        // If hungry search for food
        if (getEnergyPercentage() < 0.5) {
            if (searchForFoodWthin(visible_tiles)) {return;}
        }
        // Move random and reproduce
        moveRandom();
        reproduce();
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
            image = is_sleeping ? "wombat-sleeping" : "wombat";
        }
        return new DisplayInformation(Color.DARK_GRAY, image);
    }
}
