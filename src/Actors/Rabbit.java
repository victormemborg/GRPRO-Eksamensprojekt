package Actors;

import HelperMethods.Help;
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

    }

    // Needs all rabbit behaviour
    @Override
    public void act(World w) {
        if (dead) {
            die();
            return;
        }
        super.act(w);
        if(world.isDay()) {
            dayTimeBehaviour();
        } else {
            nightTimeBehaviour();
        }
    }

    private void nightTimeBehaviour() {
/*         if (getHome() == null) {
            createHome();
        } */
        if (!is_sleeping) {
            moveToHome(); // Hvis det skal sættes sådan her op, så skal createHome() garantere at skabe et home. Ellers får vi NullPointerException
        }
        return; // stop further execution if it's night
    }

    private void dayTimeBehaviour() {
        wakeUp();
        ArrayList<Location> visible_tiles = getSurroundingTilesAsList(vision_range);
        ArrayList<Animal> threats = Help.castArrayList(getObjectsWithInterface("Carnivore", visible_tiles));
        if (!threats.isEmpty()) {
            escape(threats);
            return;
        }
        if (getEnergyPercentage() < 0.5) {
            if (searchForFoodWthin(visible_tiles)) {return;}
        }
        moveRandom();
        reproduce();
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
