package Actors;

// Java lib
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;
// Custom lib
import HelperMethods.Help;
// Itumumulator lib
import itumulator.world.*;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.simulator.Actor;

public abstract class Animal implements Actor, DynamicDisplayInformationProvider {
    ///////////////////////////////////////////////////////////////////////
    /////////////////////           Fields:           /////////////////////

    Random r = new Random();          // A random number generator

    // Initialized by Animal:
    World world;                      // The world the animal lives in
    double req_hp_reproduction;       // A percentage value of the animals max HP, that must at least be present for reproduction
    double req_energy_reproduction;   // A percentage value of the animals max energy, that must at least be present for reproduction
    double energy_loss_reproduction;  // The amount of energy lost per reproduction
    int energy_loss_move;             // The amount of energy lost per move
    int age;                          // The age of the animal, measured in ingame ticks
    boolean has_reproduced_today;     // A boolean describing whether the animal has alredy reproduced today (resets every 10 ingame ticks)
    boolean is_sleeping;              // A boolean describing whether the animal is sleeping
    boolean dead;                     // Waiting frame for proper sync between carcass and animal
    ArrayList<Animal> mad_at;         // List of animals the animal would wanna attack
    ArrayList<Animal> afraid_of;      // List of animals the animal would want to flee from


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

    /**
     * The animals behaviour during day time
     */
    abstract void dayTimeBehaviour();

    /**
     * The animals behaviour during night time
     */
    abstract void nightTimeBehaviour();


    ///////////////////////////////////////////////////////////////////////
    /////////////////////         Constructor         /////////////////////
    /* 
        Initializes generic animal fields 
    */

    /**
     * Initializes generic animal fields
     * @param world The world the animal lives in
     */
    Animal(World world) {
        this.world = world;
        this.req_hp_reproduction = 0.6;   
        this.req_energy_reproduction = 0.6;
        this.energy_loss_reproduction = 0.5;
        this.energy_loss_move = 10;
        this.age = 0;
        this.has_reproduced_today = false;
        this.is_sleeping = false;
        this.dead = false;
        this.mad_at = new ArrayList<>();
        this.afraid_of = new ArrayList<>();
    }


    ///////////////////////////////////////////////////////////////////////
    /////////////    Methods to be overritten by subclass:    /////////////

    @Override
    public void act(World w) {
        if (dead) {   // A waiting frame to prevent a bug where the animals act is called even tho it has been deleted from the world
            die();
            return;
        }
        if (world.getCurrentTime() == 0) {
            has_reproduced_today = false;
            age++;
            changeMaxEnergy();
        }
        if (!is_sleeping) { passiveHpRegen(); }
        if (world.isDay()) {
            dayTimeBehaviour();
        } else {
            nightTimeBehaviour();
        }
        //Might be useful to extend here in subclass....  
    }

    /**
     * Reduce the victims health by the given amount
     * @param dmg The amount of damage to dealt by the agressor
     */
    public void attacked(int dmg, Animal agressor) {
        decreaseHp(dmg);
        //Might be useful to extend here in subclass....
    }

    /**
     * Attack the victim if it is within range
     * @param victim The animal to be attacked
     */
    void attack(Animal victim) {
        if (Help.getDistance(this.getLocation(), victim.getLocation()) == 1) {
            victim.attacked(damage, this);
        }
        //Might be useful to extend here in subclass....
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////           General methods:           /////////////////

    /**
     * Regenerates the animals health based on its current energy level compared to its max energy level
     */
    void passiveHpRegen() {
        double energy_ratio = (double) current_energy / max_energy;
        int healing_factor = max_hp / 20;        // Magic number
        int heal_amount = (int) Math.round(healing_factor*energy_ratio);
        int actual_increase = increaseHp(heal_amount);
        decreaseEnergy(actual_increase / 2);     // Another magic number
    }

    /**
     * Kills the animal and replaces it with a carcass 
     * @throws NullPointerException if the animal is already dead
     * @throws IllegalArgumentException if the animal is not located on a Home object
     */
    public void die() {
        Location l = this.getLocation();
        if (!dead) {
            dead = true;
            return;
        }
        try {
            Object object = world.getNonBlocking(l);
            if (object instanceof Home) {
                world.delete(this);
                return;
            }
            world.delete(object);
        } catch (IllegalArgumentException | NullPointerException ignore) {
            //do nothing
        }
        world.setTile(l, new Carcass(world, max_energy));
        world.delete(this);
    }

    /**
     * Checks if the given object is part of the animals diet
     * @param object The object to be checked
     * @return true if the object is part of the animals diet, false if not
     */
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

    /**
     * Moves the animal to the given location if it is not null and the animal has enough energy to move
     * @param loc The location to move the animal to
     */
    void move(Location loc) {
        if (loc == null) {
            return;
        }
        // If the animal does not have enough energy to move, then substract energy_loss_move from its health instead
        if (current_energy >= energy_loss_move) {
            world.move(this, loc);
            decreaseEnergy(energy_loss_move);
        } else {
            world.move(this, loc);
            decreaseHp(energy_loss_move); // Has to be done last so we dont move a dead animal
        }
    }

    /** 
     * Moves the Animal to the empty tile closest to its target. Returns the distance from the new location to the target
     * @param target The location to move the animal to
     * @return The distance from the new location to the target
     */
    int moveTo(Location target) {
        Location moveLoc = getClosestEmptyLocation(target);
        if (!Help.isSameLocations(this.getLocation(), moveLoc)) {
            move(moveLoc);
        }
        return Help.getDistance(moveLoc, target);
    }

    /**
     * Moves the animal to a random empty tile within its move range
     */
    void moveRandom() {
        Location ran_loc = Help.getRandomNearbyEmptyTile(world, this.getLocation(), move_range);
        move(ran_loc);
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////           Scouting methods:            ////////////////
    
    /**
     * Returns an ArrayList of objects within the given area that implements the given interface
     * @param target The interface to be checked for
     * @param area An ArrayList of locations within which to search for objects
     * @return An ArrayList of objects within the given area that implements the given interface
     */
    ArrayList<Object> getObjectsWithInterface(String target, ArrayList<Location> area) {
        ArrayList<Object> result_list = new ArrayList<>();
        for (Location l : area) {
            Object tile = world.getTile(l);
            if (Help.doesInterfacesInclude(tile, target)) {
                result_list.add(tile);
            }
        }
        return result_list;
    }

    /**
     * Returns an ArrayList of objects within the given area that are part of the animals diet
     * @param area An ArrayList of locations within which to search for objects
     * @return An ArrayList of objects within the given area that are part of the animals diet
     */
    ArrayList<Object> getObjectsInDiet(ArrayList<Location> area) {
        ArrayList<Object> diet_list = new ArrayList<>();
        for (Location l : area) {
            Object potential_food = world.getTile(l);
            if (isPartOfDiet(potential_food)) {
                diet_list.add(potential_food);
            }
        }
        return diet_list;
    }

    /**
     * Returns an ArrayList of objects within the given area that are of the given class
     * @param target The class to be checked for   
     * @param area An ArrayList of locations within which to search for objects
     * @return An ArrayList of objects within the given area that are of the given class
     */
    ArrayList<Object> getObjectsOfClass(String target, ArrayList<Location> area) {
        ArrayList<Object> class_list = new ArrayList<>();
        for (Location l : area) {
            try {
                Object tile = world.getTile(l);
                Class<?> class_type = Class.forName("Actors." + target);
                if (class_type.isInstance(tile)) {
                    class_list.add(tile);
                }
            } catch (ClassNotFoundException ignore) {
                // do nothing
            }
        }
        return class_list;
    }

    /**
     * Returns the nearest object from the given list of objects
     * @param object_list The list of objects to be checked
     * @return The nearest object from the given list of objects
     */
    Object getNearestObject(ArrayList<Object> object_list) {
        Object nearest_object = null;
        for (Object o : object_list) {
            int min_dist = Integer.MAX_VALUE;
            int dist = Help.getDistance(this.getLocation(), world.getLocation(o));
            if (dist < min_dist) {
                min_dist = dist;
                nearest_object = o;
            }
        }
        return nearest_object;
    }

    /**
     * Returns an ArrayList of empty tiles within the given range
     * @param range The range within which to search for empty tiles
     * @return An ArrayList of empty tiles within the given range
     */
    ArrayList<Location> getEmptyTilesWithinRange(int range) {
        Set<Location> tiles = world.getSurroundingTiles(this.getLocation(), range);
        ArrayList<Location> empty_tiles = new ArrayList<>();
        for (Location l : tiles) {
            if (world.isTileEmpty(l)) {
                empty_tiles.add(l);
            }
        }
        return empty_tiles;
    }

    /**
     * Returns the closest empty tile to the given target
     * @param target The target to which the closest empty tile is to be found
     * @return The closest empty tile to the given target
     */
    Location getClosestEmptyLocation(Location target) {
        ArrayList<Location> possible_paths = getEmptyTilesWithinRange(move_range);
        Location closest_path = this.getLocation(); // Let it stay if desirable. Set this to possible_paths.get(0) and add possible_paths.add(this.getLocation()) if you want randomization
        int min_dist = Help.getDistance(closest_path, target);
        for (Location path : possible_paths) {
            int dist = Help.getDistance(path, target);
            if (dist < min_dist) {
                min_dist = dist;
                closest_path = path;
            }
        }
        return closest_path;
    }

    /**
     * NOTE: does not include the Animals own location.
     * Returns an ArrayList of locations within the given range
     * @param range The range within which to search for locations
     * @return An ArrayList of locations within the given range
     */
    ArrayList<Location> getSurroundingTilesAsList(int range) {
        Set<Location> tiles_set = world.getSurroundingTiles(this.getLocation(), range);
        ArrayList<Location> tiles_list = new ArrayList<>();
        for (Location l : tiles_set) {
            tiles_list.add(l);
        }
        return tiles_list;
    }

    /**
     * Tries to inhabit an empty burrow within the animals vision range
     */
    void tryInhabitEmptyBurrow() {
        if (home != null) {
            return;
        }
        ArrayList<Burrow> burrow_list = Help.castArrayList(getObjectsOfClass("Burrow", getSurroundingTilesAsList(vision_range))); // get all burrows within vision range
        for (Burrow burrow : burrow_list) {
            if (!burrow.isBigHole() && !burrow.isFull() && this instanceof Rabbit) {
                setHome(burrow);
                return;
            } else if (burrow.isBigHole() && !burrow.isFull() && this instanceof Wolf) {
                setHome(burrow);
                return;
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////
    ///////////////          Reproduction methods:          ///////////////

    /**
     * Reproduces with a partner if one is found within the animals vision range
     * and the animal is mature and has not already reproduced today
     * @return The baby created - null if no partner was found or the animal is not mature or has already reproduced today
     */
    public Animal reproduce() {
        Animal baby = null;
        if (!getIsMature() || has_reproduced_today) {
            return baby;
        }
        ArrayList<Animal> partner_list = Help.castArrayList(getObjectsOfClass(this.getClass().getSimpleName(), getSurroundingTilesAsList(1)));
        for (Animal partner : partner_list) {
            if (partner.getIsMature() && !partner.getHasReproducedToday() && !this.getHasReproducedToday()) {
                baby = createBaby();
                setReproduceProperties(this, partner);
                return baby;
            }
        }
        return baby;
    }
    
    /**
     * Sets the energy and reproduction properties of the animals after they have reproduced
     * @param engager The animal that initiated the reproduction
     * @param partner The animal that was reproduced with
     */
    private void setReproduceProperties(Animal engager, Animal partner) {
        engager.current_energy *= energy_loss_reproduction;
        partner.current_energy *= energy_loss_reproduction;
        engager.has_reproduced_today = true;
        partner.has_reproduced_today = true;
    }

    /**
     * Places the baby at an empty tile near the engager if possible
     * @return The baby created - null if no empty tile was found near the engager
     */
    private Animal createBaby() {
        Location locationForBaby = Help.getRandomNearbyEmptyTile(world, this.getLocation(), 2);
        Animal baby = (Animal) Help.createNewInstanceWithArg(this, world);
        if (locationForBaby != null && baby != null) {
            world.setTile(locationForBaby, baby);
            return baby;
        }
        return null;
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////         Defence and Attack methods:         //////////////

    /**
     * Checks for afraid_of-animals within given area and escapes from the nearest one if found
     * @param area An ArrayList of locations within which to search for afraid_of-animals
     * @return true if an afraid_of-animal was found and escaped from - false if not
     */
    boolean checkForAfraidOfAnimals(ArrayList<Location> area) {
        // Updates afraid_of incase an element no longer exists in the world (has died)
        Help.removeNonExistent(world, Help.castArrayList(afraid_of));
        // Checks for visible afraid_of-animals within the given area
        ArrayList<Object> visible_animals = getObjectsOfClass("Animal", area);
        ArrayList<Object> visible_afraid_of = new ArrayList<>();
        for (Object a : visible_animals) {
            if (afraid_of.contains(a)) {
                visible_afraid_of.add(a);
            }
        }
        return escape(Help.castArrayList(visible_afraid_of));
    }

    /**
     * Checks for mad_at-animals within given area and attacks the nearest one if found
     * @param area An ArrayList of locations within which to search for mad_at-animals
     * @return true if a mad_at-animal was found and attacked - false if not
     */
    boolean checkForMadAtAnimals(ArrayList<Location> area) { 
        // Updates mad_at incase an element no longer exists in the world (has died)
        Help.removeNonExistent(world, Help.castArrayList(mad_at));
        // Checks for visible mad_at-animals within the given area
        ArrayList<Object> visible_animals = getObjectsOfClass("Animal", area);
        ArrayList<Object> visible_mad_at = new ArrayList<>();
        for (Object a : visible_animals) {
            if (mad_at.contains(a)) {
                visible_mad_at.add(a);
            }
        }
        return approachAndAttackNearest(visible_mad_at);
    }

    /**
     * Moves the animal away from its threat_list to the escape route with the least distance to the threat_list
     * @param threat_list The list of threats the animal will escape from
     * @return true if the animal has threats and has moved away from the nearest threat - false if not
     */
    boolean escape(ArrayList<Object> threat_list) {
        ArrayList<Location> escape_routes = getEmptyTilesWithinRange(move_range);
        if (threat_list.isEmpty() || escape_routes.isEmpty()) {
            return false;
        }
        //Dertermine the escape route with the highest minimal distance to all threats
        int max_dist = Integer.MIN_VALUE;
        Location best_route = escape_routes.get(r.nextInt(escape_routes.size()));
        for (Location route : escape_routes) {
            int min_dist = Integer.MAX_VALUE;
            for (Object threat : threat_list) {
                int dist = Help.getDistance(route, world.getLocation(threat));
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
        return true;
    }

    /**
     * Moves the animal towards its target and attacks it if it is within range of 1
     * @param target_list The list of targets the animal will attack
     * @return true if the animal has targets and has moved towards the nearest target - false if not
     */
    boolean approachAndAttackNearest(ArrayList<Object> target_list) { // Maybe move to Animal.java
        if (target_list.isEmpty()) {
            return false;
        }
        Animal target = (Animal) getNearestObject(target_list);
        if (moveTo(target.getLocation()) == 1) {
            attack(target);
        }
        return true;
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Food methods:            /////////////////

    /**
     * Eats the given food and increases the animals energy by the amount of energy the food contains.
     * It also calls the consumed() method of the Eatable object, which handles what should happen to the food after it has been eaten
     * @param food The food to be eaten
     */
    public void eat(Eatable food) {
        increaseEnergy(food.consumed());
    }
    
    /**
     * Searches for food within the given tiles and moves towards the nearest food if found
     * @param area An ArrayList of locations within which to search for food
     * @return true if food was found - false if not
     */
    boolean searchForFoodWthin(ArrayList<Location> area) {
        ArrayList<Object> food_list = getObjectsInDiet(area);
        if (!(food_list.isEmpty())) {
            Eatable food = (Eatable) getNearestObject(food_list);
            if (moveTo(world.getLocation(food)) == 0) { // moveTo() moves the animal towards the final location and returns the distance to this final location
                eat(food);
            }
            return true;
        }
        return false;
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Home methods:            /////////////////
    
    /**
     * Moves the animal towards its home, and makes it sleep. Creates a home if the animal has none. 
     * @return False if the animal has no home, and is unable to create one. Otherwise always true.
     */
    boolean moveToHome() {
        if (home == null) {
            return setHome(createBurrow());
        }
        if (moveTo(home.getLocation()) == 0 && !dead) { // Annoying waiting frame because act still gets called even though it is dead
            sleep();
        }
        return true;
    }

    /**
     * Creates a burrow at the animals location if there is no nonblocking object at the location
     * and sets the burrow as the animals home
     * @return the new home created - null if it was not possible to create a burrow
     */
    public Home createBurrow() { 
        Location loc = this.getLocation();
        try {
            if ( !(world.getNonBlocking(loc) instanceof Home) ) {
                world.delete(world.getNonBlocking(loc));
            }
        } catch (IllegalArgumentException ignore) { 
            //if there is no nonblocking object at the location, do nothing here
        }
        try {
            Home new_home = new Burrow(world, this);
            world.setTile(loc, new_home);
            return new_home;
        } catch (IllegalArgumentException iae) {
            return null;
        }
    }

    /**
     * Sets is_sleeping to true and increases the animals health by 5 if it is not already at max health
     */
    public void sleep() {
        is_sleeping = true;
        if (current_hp < max_hp) {
            current_hp += 5;
        }
    }

    /**
     * Wakes up the animal if it is sleeping and moves it to an empty tile near its home
     * @return true if the animal was sleeping and has now woken up, false if not
     */
    public boolean wakeUp() {
        try {
            if (is_sleeping) { //check first tick to move to an empty surrounding location near its home
                Location l = Help.getRandomNearbyEmptyTile(world, home.getLocation(), vision_range);
                world.setTile(l, this);
                is_sleeping = false;
            }
            return true;
        } catch (NullPointerException npe) {
            System.out.println("No empty tile found near home.. trying again next tick");
            return false;
        }
    }

    /**
     * Returns the home of the animal
     * @return a Home object containing the home of the animal - null if it has no home
     */
    public Home getHome() {
        return home;
    }

    /**
     * Sets the home of the animal
     * @param home The home to be set for the animal
     * @return False if home is null, true otherwise
     */
    public boolean setHome(Home home) {
        if (home == null) { return false; }
        this.home = home;
        home.addOccupant(this);
        return true;
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Set methods:             /////////////////

    /**
     * Increases the current energy of the animal by the given amount
     * @param energy The amount to increase the energy by
     */
    void increaseEnergy(int energy) {
        int potential_energy = current_energy + energy;
        if (potential_energy > max_energy) {
            current_energy = max_energy;
        } else {
            current_energy = potential_energy;
        }
    }

    /**
     * Decreases the current energy of the animal by the given amount
     * @param energy The amount to decrease the energy by
     */
    void decreaseEnergy(int energy) {
        int potential_energy = current_energy - energy;
        if (potential_energy < 0) {
            current_energy = 0;
        } else {
            current_energy = potential_energy;
        }
    }

    /**
     * Increases the current health of the animal by the given amount
     * @param amount The amount to increase the health by
     * @return The amount the health was increased by
     */
    int increaseHp(int amount) {
        int start_hp = current_hp; // for returning the amount healed
        int potential_hp = current_hp + amount;
        if (potential_hp > max_hp) {
            current_hp = max_hp;
        } else {
            current_hp = potential_hp;
        }
        return current_hp - start_hp;
    }

    /**
     * Decreases the current health of the animal by the given amount
     * @param amount The amount to decrease the health by
     */
    void decreaseHp(int amount) {
        if (current_hp - amount <= 0) {
            die();
        } else {
            current_hp -= amount;
        }
    }

    /**
     * Changes the maximum energy of the animal based on its age
     */
    public void changeMaxEnergy() {
        this.max_energy = max_energy - age * 2; 
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Get methods:             /////////////////

    /**
     * Returns the current health of the animal
     * @return an integer containing the current health of the animal
     */
    public int getHp() {
        return current_hp;
    }

    /**
     * Returns the current energy of the animal
     * @return an integer containing the current energy of the animal
     */
    public int getEnergy() {
        return current_energy;
    }

    /**
     * @return an integer containing the damage the animal is capable of dealing
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Returns the age of the animal
     * @return an integer containing the age of the animal
     */
    public int getAge() {
        return age;
    }

    /**
     * Returns the truth state of the animal being mature
     * @return true if the animal is mature, false if not
     */
    public boolean getIsMature() {
        return age >= maturity_age;
    }

    /**
     * Returns the truth state of the animal having reproduced today
     * @return true if the animal has reproduced today, false if not
     */
    public boolean getHasReproducedToday() {
        return has_reproduced_today;
    }

    /**
     * Returns the location of the animal
     * @return a Location object containing the location of the animal
     */
    public Location getLocation() {
        return world.getLocation(this);
    }

    /**
     * Returns the current energy of the animal as a double of its maximum energy
     * @return a double which is to be interpreted as a percentage of the animals maximum energy
     */
    public double getEnergyPercentage() {
        return (double) current_energy / max_energy;
    }
    
    /**
     * Returns the vision range of the animal
     * @return an integer containing the vision range of the animal
     */
    public int getVisionRange() {
        return vision_range;
    }

    /**
     * Returns the maximum energy of the animal
     * @return an integer containing the maximum energy of the animal
     */
    public int getMaxEnergy() {
        return max_energy;
    }

    /**
     * Sets the current energy of the animal
     * @param energy The desired energy of the animal
     */
    public void setEnergy(int energy) {
        this.current_energy = energy;
    }

    /**
     * Sets the age of the animal
     * @param age The desired age of the animal
     */
    public void setAge(int age) {
        this.age = age;
    }

    /**
     * Returns the truth state of the animal being asleep
     * @return true if the animal is sleeping, false if not
     */
    public boolean getIsSleeping() {
        return is_sleeping;
    }
}
