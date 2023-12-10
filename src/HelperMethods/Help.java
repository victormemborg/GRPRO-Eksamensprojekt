package HelperMethods;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import Actors.Animal;
import itumulator.world.*;

public class Help {
    /*
     * Returns a random location that is not occupied by any object of
     * the given type (NonBlocking: 0 - Blocking: 1)
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

    public static int getDistance(Location l1, Location l2) {
        //System.out.println(Math.abs(l1.getX() - l2.getX()));
        //System.out.println(Math.abs(l1.getY() - l2.getY()));
        return Math.abs(l1.getX() - l2.getX()) + Math.abs(l1.getY() - l2.getY());
    }

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

    public static Object createNewInstanceWithArg(Object object, World world) {
        try {
            Class<?>[] cArg = new Class[1];
            cArg[0] = World.class;
            return object.getClass().getDeclaredConstructor(cArg).newInstance(world);
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException ignore) {
            return null;
        }
    }

    public static <newType, oldType> ArrayList<newType> castArrayList(ArrayList<oldType> list){ // Very sketchy
        ArrayList<newType> newlyCastedArrayList = new ArrayList<newType>();
        for(oldType listObject : list){
            newlyCastedArrayList.add( (newType) listObject );
        }
        return newlyCastedArrayList;
    }

    public static void removeNonExistent(World world, ArrayList<Object> list) {
        ArrayList<Object> temp = new ArrayList<>();
        for (Object o : list) {
            if (!world.contains(o)) {
                temp.add(o);
            }
        }
        list.removeAll(temp);
    }

    public static Location strToLoc(String loc_str) {
        String temp_str = loc_str.replaceAll("\\(|\\)", "");
        int x = Integer.parseInt(temp_str.split(",")[0]);
        int y = Integer.parseInt(temp_str.split(",")[1]);
        return new Location(x, y);
    }

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
