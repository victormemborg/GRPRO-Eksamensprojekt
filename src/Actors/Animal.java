package Actors;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
import java.util.HashSet;

import HelperMethods.Help;
import itumulator.world.*;
import itumulator.simulator.Actor;

public abstract class Animal implements Actor {
    Random r = new Random();
    World world;
    int max_hp;
    int max_energy;
    int current_hp;
    int current_energy;
    int damage;
    int maturity_age;
    double req_energy_reproduction;
    int vision_range;
    int move_range;
    Set<String> diet;

    int age = 0;
    int energy_loss_move = 1;
    double energy_loss_reproduction = 0.6;
    double hp_reproduction = 0.6;
    boolean has_reproduced_today = false;

    // Home home;
    boolean is_sleeping = false;

    abstract void sleep();
    
    abstract public void act(World world);


    void attack(Animal victim) {
        victim.attacked(damage);
    }

    public void attacked(int dmg) {
        if (current_hp - dmg < 0) {
            die();
        } else {
            current_hp -= dmg;
        }
    }

    void increaseAge() {
        if (world.getCurrentTime() == 0) {
            age++;
            has_reproduced_today = false; //might have to be moved to a different place
        }
    }

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

    ArrayList<Animal> checkForCarnivore() {
        // check if there is a carnivore nearby
        Set<Location> visible_tiles = world.getSurroundingTiles(this.getLocation(), vision_range);
        ArrayList<Animal> carnivore_list = new ArrayList<>();
        for (Location l : visible_tiles) {
            try {
                if (Arrays.toString(world.getTile(l).getClass().getInterfaces()).contains("Carnivore")) {
                    carnivore_list.add( (Animal) world.getTile(l) );
                }
            } catch (NullPointerException npe) {
                //Gets here if world.getTile(l) returns null. Does nothing. Just skips the step
            }
        }
        System.out.println("Carnovores nearby: " + carnivore_list.size());
        return carnivore_list;
    }

    Animal getNearestCarnivore() {
        ArrayList<Animal> carnivore_list = checkForCarnivore();
        int shortest_dist = Integer.MAX_VALUE;
        Animal nearest_carnivore = null;
        for (Animal a : carnivore_list) {
            int dist = Help.getDistance(this.getLocation(), a.getLocation());
            if (dist < shortest_dist) {
                shortest_dist = dist;
                nearest_carnivore = a;
            }
        }
        return nearest_carnivore;
    }

    Eatable findNearestEatable() {
        //Checks for all surrounding Eatable objects
        Set<Location> neighbours = world.getSurroundingTiles(vision_range); 
        ArrayList<Eatable> food_list = new ArrayList<>();
        for (Location l : neighbours) {
            if (l != null && Arrays.toString(world.getTile(l).getClass().getInterfaces()).contains("Eatable")) {
                food_list.add( (Eatable) world.getTile(l));
            } 
        } 
        //Remove all eatables not on dietlist
        for(Eatable e : food_list) {
            if(!diet.contains(e.getClass().getName())) {
                food_list.remove(e);
            }
        }
        //Find the nearest eatable
        Eatable nearestEatable = null;
        int closestLocation = Integer.MAX_VALUE;
        for(Eatable e : food_list) {
            Location foodLoc = world.getLocation(e);
            Location animalLoc = this.getLocation();
            int distance = Help.getDistance(foodLoc, animalLoc);
            if(distance < closestLocation) {
                nearestEatable = e;
                closestLocation = distance;
            }
        }
        return nearestEatable;
    }

    void die() {
        world.setTile(world.getLocation(this), new Carcass(max_energy));
        world.delete(this);
    }
    
    void moveToFood() {
        Eatable food = findNearestEatable();
        if (food != null) {
            Location food_loc = world.getLocation(food);
            Location move_loc = shortestRoute(food_loc);
            if (food_loc.getX() == move_loc.getX() && food_loc.getY() == move_loc.getY()) {
                world.move(this, move_loc);
                increaseEnergy(food.consumed());
            } else {
                world.move(this, move_loc);
            }
        } else {
            moveRandom();
        }
    }

    // Returns the next location in the shortest route to the target
    Location shortestRoute(Location target) {
        Set<Location> shortest_route = world.getSurroundingTiles(move_range);
        //Removes locations that contains a blocking object
        for (Location l : shortest_route) {
            if(!world.isTileEmpty(l)) {
                shortest_route.remove(l);
            }
        }
        // check which of the surrounding tiles is closest to the target
        Location closest_location = null;
        int closest_distance = Integer.MAX_VALUE;
        for(Location l : shortest_route) {
            int distance = Help.getDistance(l, target);
            if(distance < closest_distance) {
                closest_location = l;
                closest_distance = distance;
            }
        }
        return closest_location;
    }
        

    // Generates an escape route for the animal
    void escape(Location threat_loc) {
        Set<Location> reachable_tiles = world.getSurroundingTiles(this.getLocation(), move_range);
        ArrayList<Location> escape_routes = new ArrayList<>();
        for (Location l : escape_routes) {
            if (Arrays.toString(world.getTile(l).getClass().getInterfaces()).contains("Nonblocking") || world.getTile(l) == null) {
                escape_routes.add(l);
            }
        }
        //Find the escape route with max distance to threat
        int max_distance = 0;
        Location escape_route = this.getLocation();
        for (Location l : escape_routes2) {
            int distance = Math.abs(this.getLocation().getX() - threat_loc.getX()) + Math.abs(this.getLocation().getX() - threat_loc.getX());
            if (distance > max_distance) {
                max_distance = distance;
                escape_route = l;
            }
        }
        //Move to the escape route
        System.out.println("Escape!");
        world.move(this, escape_route);
    }

    void increaseEnergy(int energy) {
        int potential_energy = current_energy + energy;
        if (potential_energy > max_energy) {
            current_energy = max_energy;
        } else {
            current_energy = potential_energy;
        }
    }

    void moveRandom() {
        Location ran_loc = Help.getRandomNearbyEmptyTile(world, this.getLocation(), move_range);
        if (ran_loc != null) {
            world.move(this, ran_loc);
        }
    }

    // void setHome(Home home)

    int getHp() {
        return current_hp;
    }

    int getEnergy() {
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

    Location getLocation() {
        return world.getCurrentLocation();
    }

}
