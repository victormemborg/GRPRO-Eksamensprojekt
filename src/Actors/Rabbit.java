package Actors;

import java.util.ArrayList;
import java.util.Set;

import HelperMethods.Help;

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
        if (getHome() == null) {
            createHome();
        }
        if (!is_sleeping) {
            moveToHome();
        }
        if (getHome() == null && !is_sleeping) {
            moveRandom();
        }
        return;
    }

    private void dayTimeBehaviour() {
        wakeUp();
        if (home == null) {
            findSurroundingBurrows(this);
        }
        ArrayList<Animal> threats = checkForCarnivore();
        if (threats.isEmpty()) {
            if ((double) current_energy / max_energy < 0.5) {
                // System.out.println("energy lvl: " + (double) current_energy/max_energy +
                // "looking for food");
                moveToFood();
            } else {
                moveRandom();
            }
        } else {
            escape(threats);
        }
        reproduce();
    }

    /**
     * Checks for surrounding burrows and sets home to the first burrow found.
     * Not quite happy with this method, but it works for now - please take a look at it
     * @param animal the animal that is looking for a burrow
     */
    private void findSurroundingBurrows(Animal animal) {
        Set<Location> nearby_tiles = world.getSurroundingTiles(getLocation(), vision_range);
        for (Location l : nearby_tiles) {
            if (world.getTile(l) instanceof Burrow) {
                Burrow burrow = (Burrow) world.getTile(l);
                if (!burrow.isBigHole() && !burrow.isFull() && animal instanceof Rabbit) {
                    setHome(burrow);
                    return;
                } else if (burrow.isBigHole() && !burrow.isFull() && animal instanceof Wolf) {
                    setHome(burrow);
                    return;
                }
            }
        }
    }

    @Override
    public DisplayInformation getInformation() {
        if (dead) {
            return new DisplayInformation(Color.DARK_GRAY, "ghost");
        }
        String image;
        if (age > maturity_age) {
            image = is_sleeping ? "rabbit-sleeping" : "rabbit-large";
        } else {
            image = is_sleeping ? "rabbit-small-sleeping" : "rabbit-small";
        }
        return new DisplayInformation(Color.DARK_GRAY, image);
    }
}
