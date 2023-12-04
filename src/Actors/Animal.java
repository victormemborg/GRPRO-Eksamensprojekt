package Actors;

// Java lib
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;
import java.util.Arrays;
// Custom lib
import HelperMethods.Help;
// Itumumulator lib
import itumulator.world.*;
import itumulator.simulator.Actor;

public abstract class Animal implements Actor {
    ///////////////////////////////////////////////////////////////////////
    /////////////////////           Fields:           /////////////////////

    Random r = new Random();          // A random number generator

    // Initialized by Animal:
    World world;                      // The world the animal lives in
    //double fullness;                  // A percentage value describing how full the animal is
    double req_hp_reproduction;       // A percentage value of the animals max HP, that must at least be present for reproduction
    double req_energy_reproduction;   // A percentage value of the animals max energy, that must at least be present for reproduction
    double energy_loss_reproduction;  // The amount of energy lost per reproduction
    int energy_loss_move;             // The amount of energy lost per move
    int age;                          // The age of the animal, measured in ingame ticks
    boolean has_reproduced_today;     // A boolean describing whether the animal has alredy reproduced today (resets every 10 ingame ticks)
    boolean is_sleeping;              // A boolean describing whether the animal is sleeping
    boolean dead;                // Waiting frame for proper sync between carcass and animal

    // Initialized by a subclass:
    int max_hp;                       // The max HP for the animal
    int current_hp;                   // The current amount of HP the animal has
    int max_energy;                   // The max energy level for the animal
    int current_energy;               // The current amount of energy the animal has
    int damage;                       // The damage the animal is cabable of dealing
    int maturity_age;                 // The minimum age of the animal required for reproduction. Also determines the DisplayInformation 
    int vision_range;                 // The amount of tiles the animal can see arround itself
    int move_range;                   // The amount of tiles the animal can move per action
    Set<String> diet;                 // A unsorted set of strings containg all objects the animal can consume
    Home home;                        // The home of the animal. Burrow for Rabbits and Wolfs, territory for Bears


    ///////////////////////////////////////////////////////////////////////
    /////////////////////     Abstract functions:     /////////////////////

    abstract void sleep();    //Tænker umidelbart at denne skal stå for at regenere energy og ekstra liv, samt finde hjem til home?


    ///////////////////////////////////////////////////////////////////////
    /////////////////////         Constructor         /////////////////////
    /* 
        Initializes generic animal fields 
    */
    Animal(World world) {
        this.world = world;               
        //fullness = 1;
        this.req_hp_reproduction = 0.6;   
        this.req_energy_reproduction = 0.6;
        this.energy_loss_reproduction = 0.5;
        this.energy_loss_move = 10;
        this.age = 0;
        this.has_reproduced_today = false;
        this.dead = false;
    }


    ///////////////////////////////////////////////////////////////////////
    /////////////    Methods to be overritten by subclass:    /////////////

    @Override
    public void act(World w) {
        if (world.getCurrentTime() == 0) {
            has_reproduced_today = false;
            age++;
        }
        passiveHpRegen();
        //Must be extended by subclass here....    
    }

    public void attacked(int dmg, Animal agressor) {
        decreaseHp(dmg);
        //Must be extended by subclass here....
    }

    void attack(Animal victim) {
        if (Help.getDistance(this.getLocation(), victim.getLocation()) <= 1) { // Magic number
            victim.attacked(damage, this);
        }
        //Might be useful to extend here in subclass....
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////           General methods:           /////////////////

    void passiveHpRegen() {
        float energy_ratio = current_energy / max_energy;
        int healing_factor = max_hp / 10;    // Magic number
        int heal_amount = Math.round(healing_factor*energy_ratio);
        increaseHp(heal_amount);
        decreaseEnergy(heal_amount / 2);     // Another magic number
    }

    void die() {
        //System.out.println(world.getLocation(this));
        //System.out.println(this);
/*         if (!dead) {
            dead = true;
            return;
        } */
        Location l = world.getLocation(this);
        try {
            world.delete(world.getNonBlocking(l));
        } catch (IllegalArgumentException | NullPointerException ignore) {
            //do nothing
        }
        world.setTile(l, new Carcass(world, max_energy));
        world.delete(this);
        world = null;
    }

    // Returns the next location in the shortest route to the target
    Location shortestRoute(Location target) {
        Set<Location> neighbors = world.getSurroundingTiles(move_range);
        //Removes locations that contains a blocking object
        ArrayList<Location> routes = new ArrayList<>();
        for (Location l : neighbors) {
            if(world.isTileEmpty(l)) {
                routes.add(l);
            }
        }
        // check which of the surrounding tiles is closest to the target
        Location closest_location = null;
        int closest_distance = Integer.MAX_VALUE;
        for(Location l : routes) {
            int distance = Help.getDistance(l, target);
            if(distance < closest_distance) {
                closest_location = l;
                closest_distance = distance;
            }
        }
        return closest_location;
    }

    void increaseEnergy(int energy) {
        int potential_energy = current_energy + energy;
        if (potential_energy > max_energy) {
            current_energy = max_energy;
        } else {
            current_energy = potential_energy;
        }
    }

    void decreaseEnergy(int energy) {
        int potential_energy = current_energy - energy;
        if (potential_energy < 0) {
            current_energy = 0;
        } else {
            current_energy = potential_energy;
        }
    }

    void increaseHp(int amount) {
        int potential_hp = current_hp + amount;
        if (potential_hp > max_hp) {
            current_hp = max_hp;
        } else {
            current_hp = potential_hp;
        }
    }

    void decreaseHp(int amount) {
        if (current_hp - amount <= 0) {
            die();
        } else {
            current_hp -= amount;
        }
    }

    void moveRandom() {
        Location ran_loc = Help.getRandomNearbyEmptyTile(world, this.getLocation(), move_range);
        if (ran_loc != null) {
            move(ran_loc);
        }
    }

    void move(Location loc) {
        if (current_energy >= energy_loss_move) {
            world.move(this, loc);
            decreaseEnergy(energy_loss_move);
        } else {
            System.out.println(this.getClass().getName() + " is too tired to move");
        }
    }

    boolean isPartOfDiet(Object object) {
        if (object == null) {
            return false;
        }
        if (diet.toString().contains(object.getClass().getSimpleName())) {
            return true;
        } else {
            return false;
        }
    }


    ///////////////////////////////////////////////////////////////////////
    ///////////////          Reproduction methods:          ///////////////

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
        Animal baby = (Animal) Help.createNewInstanceWithArg(this, world);
        if (locationForBaby != null && baby != null) {
            world.setTile(locationForBaby, baby);
        }
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////    Defence and Detection methods:    /////////////////
        
    ArrayList<Animal> checkForAnimal() {
        // check if there is an animal nearby
        Set<Location> visible_tiles = world.getSurroundingTiles(this.getLocation(), vision_range);
        ArrayList<Animal> animal_list = new ArrayList<>();
        for (Location l : visible_tiles) {
            if (world.getTile(l) instanceof Animal && world.getTile(l) != this) {
                animal_list.add( (Animal) world.getTile(l) );
            }
        }
        return animal_list;
    }

    Animal getNearestAnimal() {
        ArrayList<Animal> animal_list = checkForAnimal();
        int shortest_dist = Integer.MAX_VALUE;
        Animal nearest_animal = null;
        for (Animal a : animal_list) {
            int dist = Help.getDistance(this.getLocation(), a.getLocation());
            if (dist < shortest_dist) {
                shortest_dist = dist;
                nearest_animal = a;
            }
        }
        return nearest_animal;
    }

    ArrayList<Animal> checkForCarnivore() {
        // check if there is a carnivore nearby
        Set<Location> visible_tiles = world.getSurroundingTiles(this.getLocation(), vision_range);
        ArrayList<Animal> carnivore_list = new ArrayList<>();
        for (Location l : visible_tiles) {
            if (Help.doesInterfacesContain(world.getTile(l), "Carnivore")) {
                carnivore_list.add( (Animal) world.getTile(l) );
            }
        }
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
    
    //Arrays.toString(world.getTile(l).getClass().getInterfaces()).contains("NonBlocking"))
    // Generates an escape route for the animal
    void escape(ArrayList<Animal> threat_list) {
        //Find all possible escape routes
        Set<Location> reachable_tiles = world.getSurroundingTiles(this.getLocation(), move_range);
        ArrayList<Location> escape_routes = new ArrayList<>();
        for (Location l : reachable_tiles) {
            if (world.getTile(l) == null || Help.doesInterfacesContain(world.getTile(l), "NonBlocking")) {
                escape_routes.add(l);
            }
        }
        //Dertermine the escape route with the highest minimal distance to all threats
        int max_dist = Integer.MIN_VALUE;
        Location best_route = escape_routes.get(r.nextInt(escape_routes.size()));
        for (Location route : escape_routes) {
            int min_dist = Integer.MAX_VALUE;
            for (Animal threat : threat_list) {
                int dist = Help.getDistance(route, threat.getLocation());
                if (dist < min_dist) {
                    min_dist = dist;
                }
            }
            if (min_dist > max_dist) {
                max_dist = min_dist;
                best_route = route;
            }
        }

        //Move to the escape route
        move(best_route);
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Food methods:            /////////////////

    Eatable findNearestEatable() {
        //Check if it stands on something it can eat
        try {
            Object tile = world.getNonBlocking(this.getLocation());
            if (isPartOfDiet(tile)) {
                return (Eatable) tile;
            }
        } catch (IllegalArgumentException iae) {
            //do nothing
        }
        //Checks for all surrounding Eatable objects
        Set<Location> neighbours = world.getSurroundingTiles(vision_range);
        ArrayList<Eatable> food_list = new ArrayList<>();
        for (Location l : neighbours) {
            Object tile = world.getTile(l);
            if (isPartOfDiet(tile)) {
                food_list.add( (Eatable) world.getTile(l));
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
    
     void moveToFood() {
        Eatable food = findNearestEatable();
        if (food == null) {
            moveRandom();
            return;
        }
        Location food_loc = world.getLocation(food);
        if (Help.isSameLocations(food_loc, this.getLocation())) {
            eat(food);
            return;
        }
        Location move_loc = shortestRoute(food_loc);
        if (Help.isSameLocations(food_loc, move_loc)) {
            eat(food);
            world.move(this, move_loc);
        } else {
            move(move_loc);
        }
    }

    void eat(Eatable food) {
        increaseEnergy(food.consumed());
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Home methods:            /////////////////
    /*
        Shall contain home related methods when home is complete
    */


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Get methods:             /////////////////

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
