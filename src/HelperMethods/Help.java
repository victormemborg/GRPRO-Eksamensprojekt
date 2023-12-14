package HelperMethods;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import Actors.Animal;
import itumulator.world.*;

public class Help {
    /**
     * Returns a random location that is not occupied by any object of
     * the given type in the given world
     * @param type 0 = NonBlocking, 1 = Blocking
     * @param world The world to search in
     */
    public static Location getRanLocWithoutType(int type, World world) {
        ArrayList<Location> empty_location_list = new ArrayList<>();
        Object[][][] tiles = world.getTiles();
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (tiles[i][j][type] == null) {
                    empty_location_list.add(new Location(i, j));
                }
            }
        }
        return empty_location_list.get((new Random()).nextInt(empty_location_list.size()));
    }

    /**
     * Returns a random location that is not occupied by any object 
     * @param world The world to search in
     * @param l The location to search around
     * @param radius The radius to search for empty tiles
     */
    public static Location getRandomNearbyEmptyTile(World world, Location l, int radius) {
        ArrayList<Location> emptyTiles = new ArrayList<>();
        int startX = Math.max(0, l.getX() - radius);
        int endX = Math.min(world.getSize() - 1, l.getX() + radius);
        int startY = Math.max(0, l.getY() - radius);
        int endY = Math.min(world.getSize() - 1, l.getY() + radius);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (world.isTileEmpty(new Location(x, y)) ) {
                    emptyTiles.add(new Location(x, y));
                }
            }
        }

        if (emptyTiles.isEmpty()) {
            return null; // No empty tile found
        }

        Random random = new Random();
        return emptyTiles.get(random.nextInt(emptyTiles.size()));
    }

    /**
     * Returns the absolute distance between two locations
     * @param l1 first location
     * @param l2 second location
     */
    public static int getDistance(Location l1, Location l2) {
        //System.out.println(Math.abs(l1.getX() - l2.getX()));
        //System.out.println(Math.abs(l1.getY() - l2.getY()));
        return Math.abs(l1.getX() - l2.getX()) + Math.abs(l1.getY() - l2.getY());
    }

    /**
     * Returns whether the underlying class of a object implements a given interface
     * @param object The object to check
     * @param target The interface to check for
     * @return Returns true if the interface is implemented - false otherwise
     */
    public static boolean doesInterfacesInclude(Object object, String target) {
        if (object == null) {
            return false;
        }
        if (Arrays.toString(object.getClass().getInterfaces()).contains(target)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the truth state between l1 and l2 being on the same location
     * @param l1 The first objects location
     * @param l2 The second objects location
     */
    public static boolean isSameLocations(Location l1, Location l2) {
        if (l1 == null || l2 == null) {
            System.out.println("One (or more) of your locations are null");
            return false;
        }
        if (l1.getX() == l2.getX() && l1.getY() == l2.getY()) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Returns an ArrayList of all tiles with no NonBlocking objects
     * @param world The world to search in
     * @param loc The location to search around
     * @param range The radius to search for empty tiles
     */
    public static ArrayList<Location> getNearbyTileWithoutNonBlocking(World world, Location loc, int range) {
        Set<Location> surrounding_tiles = world.getSurroundingTiles(loc, range);
        ArrayList<Location> empty_tiles = new ArrayList<>();
        for (Location l : surrounding_tiles) {
            if (!world.containsNonBlocking(l)) {
                empty_tiles.add(l); 
            }
        }
        return empty_tiles;
    }

    /**
     * Creates a new instance of a class, similar to the provided object, 
     * while invoking a constructor that takes a World parameter.
     * @param object The original object for which a new instance is to be created.
     * @param world The World instance to be passed as an argument to the constructor.
     * @return A new instance of the same class as 'object' with the 'world' parameter
     *         passed to its constructor, or null if any exceptions occur during the process.
     */
    public static Object createNewInstanceWithArg(Object object, World world) {
        try {
            Class<?>[] cArg = new Class[1];
            cArg[0] = World.class;
            return object.getClass().getDeclaredConstructor(cArg).newInstance(world);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ignore) {
            return null;
        }
    }

    /**
     * A generic function for casting an ArrayList of any type into an ArrayList of any other type. Should be used with caution
     * @param <newType> The type casted to
     * @param <oldType> The type casted from <p>
     * @param list The ArrayList that you want to cast
     * @return A new casted ArrayList containing the same elements
     */
    public static <newType, oldType> ArrayList<newType> castArrayList(ArrayList<oldType> list){
        ArrayList<newType> casted_list = new ArrayList<newType>();
        for(oldType obj : list){
            casted_list.add( (newType) obj ); // Well...
        }
        return casted_list;
    }

    /**
     * Removes all objects in the given list that are not contained in the given world (as by world.conatins(Object object))
     * @param world The world from which to check from
     * @param list The list of elements to check
     */
    public static void removeNonExistent(World world, ArrayList<Object> list) {
        ArrayList<Object> temp = new ArrayList<>();
        for (Object o : list) {
            if (!world.contains(o)) {
                temp.add(o);
            }
        }
        list.removeAll(temp);
    }

    /**
     * Returns a Location converted from a String
     * @param loc_str Takes a String in the format "(x,y)"
     * @return Returns an instance of Location if the string is valid
     */
    public static Location strToLoc(String loc_str) {
        String temp_str = loc_str.replaceAll("\\(|\\)", "");
        int x = Integer.parseInt(temp_str.split(",")[0]);
        int y = Integer.parseInt(temp_str.split(",")[1]);
        return new Location(x, y);
    }

    /**
     * Returns an integer containing the energy of the animal
     * @param animal_str The name of the animal
     * @param world Can be any instance of the World class. Is used only to create a temporary instance of the animal
     * @return An integer representing the max_energy of the given animal, or a random integer in the range 100-200 if no animal is found
     */
    public static int strToEnergy(String animal_str, World world) {
        try {
            String class_name = animal_str.substring(0, 1).toUpperCase() + animal_str.substring(1, animal_str.length());
            Class<?> class_type = Class.forName("Actors." + class_name);
            Animal animal = (Animal) class_type.getDeclaredConstructor(World.class).newInstance(world);
            int energy = animal.getEnergy();
            return energy;
        } catch (Exception e) {
            Random ran = new Random();
            System.out.println("There exists no Animal with name: " + animal_str);
            int energy = ran.nextInt(100,200);
            System.out.println("Setting energy to random number: " + energy);
            return energy;
        }
    }



    //Depricated getTerritory. Might become useful later
    public static Location getTerritory(ArrayList<String> str_array) {
        for (String str : str_array) {
            if (str.matches("\\([0-9]+,[0-9]+\\)")) {
                String temp_str = str.replaceAll("\\(|\\)", "");
                int x = Integer.parseInt(temp_str.split(",")[0]);
                int y = Integer.parseInt(temp_str.split(",")[1]);
                str_array.remove(str);
                return new Location(x, y);
            }
        }
        return null; // This will just happen whenever the class is not bear
    }
}
