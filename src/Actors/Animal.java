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
            has_reproduced_today = false; //might have to be moved to a different place
        }
    }

    //curently it breeds with itself
    public void reproduce() {
        if (!getIsMature()) {
            return;
        }
        Set<Location> neighbours = world.getSurroundingTiles();
        for (Location n : neighbours) {
            Object nearbyObject = world.getTile(n);
            if (!(nearbyObject instanceof Animal)) {
                continue;
            }
            Animal partner = (Animal) nearbyObject;
            if (this == partner) {
                continue;
            }
            if (this.getClass() != partner.getClass()) {
                continue;
            }
            if (!partner.getIsMature() || partner.has_reproduced_today) {
                continue;
            }
            createBaby();
            setReproduceProperties(this, partner);
        }
    }

    private void setReproduceProperties(Animal engager, Animal partner) {
        engager.current_energy *= energy_loss_reproduction;
        partner.current_energy *= energy_loss_reproduction;
        engager.has_reproduced_today = true;
        partner.has_reproduced_today = true;
    }

    private void createBaby() {
        Location locationForBaby = Help.getRandomNearbyEmptyTile(world, world.getCurrentLocation(), 2);
        try {
            Animal baby = this.getClass().getDeclaredConstructor().newInstance();
            if (locationForBaby != null) {
                world.setTile(locationForBaby, baby);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            System.out.println(e.getMessage());
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
