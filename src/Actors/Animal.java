package Actors;

// Java lib
import java.util.Set;
import java.util.ArrayList;
import java.util.Random;
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

    // No abstract functions atm


    ///////////////////////////////////////////////////////////////////////
    /////////////////////         Constructor         /////////////////////
    /* 
        Initializes generic animal fields 
    */
    Animal(World world) {
        this.world = world;
        this.req_hp_reproduction = 0.6;   
        this.req_energy_reproduction = 0.6;
        this.energy_loss_reproduction = 0.5;
        this.energy_loss_move = 10;
        this.age = 0;
        this.has_reproduced_today = false;
        this.dead = false;
        this.mad_at = new ArrayList<>();
        this.afraid_of = new ArrayList<>();
    }


    ///////////////////////////////////////////////////////////////////////
    /////////////    Methods to be overritten by subclass:    /////////////

    @Override
    public void act(World w) {
        if (world.getCurrentTime() == 0) {
            has_reproduced_today = false;
            age++;
            changeMaxEnergy();
        }
        if (!is_sleeping) { passiveHpRegen(); }
        //Must be extended by subclass here....    
    }

    public void attacked(int dmg, Animal agressor) {
        decreaseHp(dmg);
        //Must be extended by subclass here....
    }

    void attack(Animal victim) {
        if (Help.getDistance(this.getLocation(), victim.getLocation()) == 1) {
            victim.attacked(damage, this);
        }
        //Might be useful to extend here in subclass....
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////           General methods:           /////////////////

    void passiveHpRegen() {
        double energy_ratio = (double) current_energy / max_energy;
        int healing_factor = max_hp / 20;        // Magic number
        int heal_amount = (int) Math.round(healing_factor*energy_ratio);
        int actual_increase = increaseHp(heal_amount);
        decreaseEnergy(actual_increase / 2);     // Another magic number
    }

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

    //Moves the Animal to the empty tile closest to its target. Returns the distance from the new location to the target
    int moveTo(Location target) {
        Location moveLoc = getClosestEmptyLocation(target);
        if (!Help.isSameLocations(this.getLocation(), moveLoc)) {
            move(moveLoc);
        }
        return Help.getDistance(moveLoc, target);
    }

    void moveRandom() {
        Location ran_loc = Help.getRandomNearbyEmptyTile(world, this.getLocation(), move_range);
        move(ran_loc);
    }

    ////////////////////////////////////////////////////////////////////////
    ////////////////           Scouting methods:            ////////////////

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

    //does not include the Animals own location
    ArrayList<Location> getSurroundingTilesAsList(int range) {
        Set<Location> tiles_set = world.getSurroundingTiles(this.getLocation(), range);
        ArrayList<Location> tiles_list = new ArrayList<>();
        for (Location l : tiles_set) {
            tiles_list.add(l);
        }
        return tiles_list;
    }

    /**
     * Checks for surrounding burrows and sets home to the first burrow found.
     * Not quite happy with this method, but it works for now - please take a look at it
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

    private void setReproduceProperties(Animal engager, Animal partner) {
        engager.current_energy *= energy_loss_reproduction;
        partner.current_energy *= energy_loss_reproduction;
        engager.has_reproduced_today = true;
        partner.has_reproduced_today = true;
    }

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

    // Escapes any afraid_of-animal within given area. Can probably be combined with checkForMadAtAnimals() via reflection, but cant be bothered
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

    // Hunts down any mad_at-animal within given area. Can probably be combined with checkForAfraidOfAnimals via reflection, but cant be bothered
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

    // Generates an escape route for the animal
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

    public void eat(Eatable food) {
        increaseEnergy(food.consumed());
    }

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
    
    void moveToHome() {
        if (home == null) { // We also check for this in rabbit, no?
            //50 % chance to create a home or 50% to occupy one - NOT IMPLEMENTED YET
            setHome(createBurrow()); // Only Wolf and Rabbit can have home == null true, so create burrow
            return;
        }
        if (moveTo(home.getLocation()) == 0 && !dead) { // Anoying buffer frame because of world.delete()
            sleep();
        }
    }

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

    //maybe, maybe not an individual method for the subclasses?
    public void sleep() {
        is_sleeping = true;
        if (current_hp < max_hp) {
            current_hp += 5;
        }
    }

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

    public Home getHome() {
        return home;
    }

    public void setHome(Home home) {
        this.home = home;
        if (home != null) {
            home.addOccupant(this);
        }
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Set methods:             /////////////////

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

    void decreaseHp(int amount) {
        if (current_hp - amount <= 0) {
            die();
        } else {
            current_hp -= amount;
        }
    }

    public void changeMaxEnergy() {
        this.max_energy = max_energy - age * 2; 
    }


    ///////////////////////////////////////////////////////////////////////
    ////////////////             Get methods:             /////////////////

    public int getHp() {
        return current_hp;
    }

    public int getEnergy() {
        return current_energy;
    }

    public int getDamage() {
        return damage;
    }

    public int getAge() {
        return age;
    }

    public boolean getIsMature() {
        return age >= maturity_age;
    }

    public boolean getHasReproducedToday() {
        return has_reproduced_today;
    }

    public Location getLocation() {
        return world.getLocation(this);
    }

    public double getEnergyPercentage() {
        return (double) current_energy / max_energy;
    }
  
    public int getVisionRange() {
        return vision_range;
    }

    public int getMaxEnergy() {
        return max_energy;
    }

    public void setEnergy(int energy) {
        this.current_energy = energy;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean getIsSleeping() {
        return is_sleeping;
    }
}
