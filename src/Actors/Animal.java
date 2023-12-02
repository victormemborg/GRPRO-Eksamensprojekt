package Actors;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.Set;

import HelperMethods.Help;
import itumulator.world.*;
import itumulator.executable.*;
import itumulator.simulator.Actor;

public abstract class Animal implements Actor {
    World world;
    int max_hp;
    int max_energy;
    double current_hp;
    double current_energy;
    int damage;
    int maturity_age;
    double req_energy_reproduction;

    int age = 0;
    int energy_loss_move = 1;
    double energy_loss_reproduction = 0.6;
    double hp_reproduction = 0.6;
    boolean has_reproduced_today = false;

    // Home home;
    boolean is_sleeping = false;

    abstract DisplayInformation getInformation();

    abstract void move();

    abstract void sleep();

    abstract public void attacked(int damage);

    void attack(Animal victim) {
        Animal attacker = this;
        victim.current_hp = victim.getHp() - attacker.getDamage();
        if (victim.getHp() <= 0) {
            die(victim);
        }
    }

    void increaseAge() {
        if (world.getCurrentTime() == 0) {
            age++;
            has_reproduced_today = false;
        }
    }

    // This method is responsible for the reproduction process of the animal.
    void reproduce() {
        if (canReproduce(this)) {
            // Get the surrounding locations.
            Set<Location> neighbours = world.getSurroundingTiles(1);
            for (Location n : neighbours) {
                // Try to find a partner at the given location.
                Optional<Animal> partner = getPartner(n);
                // If a partner is found, create a baby.
                partner.ifPresent(this::createBaby);
            }
        }
    }

    private boolean canReproduce(Animal animal) {
        return animal.getIsMature() && animal.current_energy > animal.req_energy_reproduction && !has_reproduced_today;
    }

    //This method tries to find a partner for reproduction at the given location.
    private Optional<Animal> getPartner(Location location) {
        Object nearbyObject = world.getTile(location);
        if (nearbyObject instanceof Animal && this.getClass() == nearbyObject.getClass()) {
            Animal partner = (Animal) nearbyObject;
            if (canReproduce(partner)) {
                return Optional.of(partner);
            }
        }
        return Optional.empty();
    }

    // This method creates a baby animal.
    private void createBaby(Animal partner) {
        //Loss of energy for both parents.
        partner.current_energy *= partner.energy_loss_reproduction;
        this.current_energy *= this.energy_loss_reproduction;
        try {
            // Create a new instance of the animal class.
            Animal baby = this.getClass().getDeclaredConstructor().newInstance();
            // Place the baby in a random nearby empty location.
            world.setTile(Help.getRandomNearbyEmptyTile(world, world.getLocation(partner), 2), baby);
            partner.has_reproduced_today = true;
            this.has_reproduced_today = true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException ignore) {
        }
    }

    void die(Animal animal) {
        // Instantier carcass
        // world.setTile(world.getLocation(this), 0), carcass);

        world.delete(animal);
    }

    // Generates an escape route for the prey
    void getEscapeRoute(Location hunter, Location prey, int radius) {
        Animal preyObject = (Animal) world.getTile(prey);
        Location newLocation = null;

        if (hunter.getX() > prey.getX()) {
            newLocation = new Location(prey.getX() - radius, prey.getY());
        } else if (hunter.getX() < prey.getX()) {
            newLocation = new Location(prey.getX() + radius, prey.getY());
        } else if (hunter.getY() > prey.getY()) {
            newLocation = new Location(prey.getX(), prey.getY() - radius);
        } else if (hunter.getY() < prey.getY()) {
            newLocation = new Location(prey.getX(), prey.getY() + radius);
        }
        if (newLocation != null && world.isTileEmpty(newLocation)) {
            world.move(preyObject, newLocation);
        }
    }

    // void setHome(Home home)

    double getHp() {
        return current_hp;
    }

    double getEnergy() {
        return current_energy;
    }

    int getDamage() {
        return damage;
    }

    int getAge() {
        return age;
    }

    boolean getIsMature() {
        return age >= maturity_age;
    }

}
