package app;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.List;

import HelperMethods.Help;
import Actors.SocialAnimal;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {
    public static void main(String[] args) {
        try {
            Program p = createProgramFromFile("data/test.txt", 800, 250);
            p.show();
            p.run();

        } catch (FileNotFoundException fnfe) {
            System.out.println("Check path!");
        } catch (Exception e) {
            System.out.println("Something unexpected happened! Message:" + e.getMessage() + " Class: " + e.getClass());
            System.out.println(e.getCause());
        }
    }

    /**
     * Returns an instance of Program with the specifications given by its arguments and the specified txt-file
     * @param path to the txt-file
     * @param display_size of the graphical window
     * @param delay inbetween simulation steps when running run()
     * @return An instance of the specified Program
     * @throws FileNotFoundException if the path is invalid
     */
    public static Program createProgramFromFile(String path, int display_size, int delay) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        Program p = new Program(Integer.parseInt(scan.nextLine()), display_size, delay);

        int line_counter = 1;
        while (scan.hasNextLine()) {
            line_counter++;
            try {
                //Get input
                ArrayList<String> line = new ArrayList<>(List.of(scan.nextLine().split(" ")));
                Class<?> class_type = getClassType(line);
                int amount = getAmount(line);
                Object[] args = getArgs(line, p);
                
                //Create specified number of instances
                ArrayList<Object> cluster = new ArrayList<>(); // Used for SocialAnimals
                for (int i = 0 ; i < amount ; i++) {
                    cluster.add(createInstance(p, class_type, args));
                }

                //Special case for SocialAnimals
                if (Class.forName("Actors.SocialAnimal").isAssignableFrom(class_type)) {
                    ArrayList<SocialAnimal> pack = Help.castArrayList(cluster);
                    for (SocialAnimal member : pack) {
                        member.addPackMembers(pack);
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + ", skipping line " + line_counter + " in " + path);
            }
        }
        scan.close();
        return p;
    }

    /**
     * Loops through an ArrayList of Strings until one matches the name of a known class. Also removes said String from the ArrayList
     * @param str_array The ArrayList in which to search for a classname
     * @return The first String that matches a class in the ../Actors - directory
     * @throws Exception If no valid class has been found after looping through the entire array
     */
    private static Class<?> getClassType(ArrayList<String> str_array) throws Exception{
        for (String str : str_array) {
            try {
                String class_name = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
                Class<?> class_type = Class.forName("Actors." + class_name);
                str_array.remove(str);
                return class_type;
            } catch (Exception ignore) {
                // Do nothing. Continue the loop
            }
        }
        throw new Exception("This line contains no valid class"); // Should make a custom exception later
    }

    /**
     * Loops through an ArrayList of Strings until one can be converted to an integer. Also removes said "integer" from the ArrayList
     * @param str_array The ArrayList in which to search for an integer
     * @return An integer representation of the first String that matches either one of the following regular expressions: <p>
     * 1. <Strong>(^[0-9]+-[0-9]+$)</strong>, a range of numbers, of which, one is randomly chosen <p>
     * 2. <Strong>(^[0-9]+$)</strong>, an integer
     * @throws Exception if no match has been found after looping through the entire array
     */
    private static int getAmount(ArrayList<String> str_array) throws Exception{
        Random ran = new Random();
        for (String str : str_array) {
            if (str.matches("^[0-9]+-[0-9]+$")) {
                int lower_bound = Integer.parseInt(str.split("-")[0]);
                int upper_bound = Integer.parseInt(str.split("-")[1]);
                str_array.remove(str);
                return ran.nextInt(lower_bound, upper_bound+1);
            }
            if (str.matches("^[0-9]+$")) {
                str_array.remove(str);
                return Integer.parseInt(str);
            }
        }
        throw new Exception("This line contains no valid amount"); // Should make a custom exception later
    }

    /**
     * Converts an ArrayList of Strings into an array of objects. Always adds the world of the given Program (as by Program.getWorld()) as the first element.
     * @param str_array The ArrayList that is to be converted
     * @param p The Program that contains the world which is to be added to the array
     * @return An array of Objects with p.getWorld() as its first element, followed by all the elements of <strong>str_array</strong>
     */
    private static Object[] getArgs(ArrayList<String> str_array, Program p) {
        Object[] args = new Object[str_array.size() + 1]; // One index reserved for world. All Actors have this argument
        args[0] = p.getWorld();
        for (int i = 0 ; i < str_array.size() ; i++) { // For now, all other arguments will be given to constructor as Strings
            args[i + 1] = str_array.get(i);
        }
        return args;
    }

    /**
     * Creates a new instance of a given class with the supplied arguments. Also places this new instance into the
     * world of the given Program (as by p.getWorld()) at a random valid location
     * @param p The Program that contains the world where the instance is to be placed
     * @param class_type The class of which to create a new instance from
     * @param args The arguments to be given to the constructor of the class
     * @return The instance created
     */
    private static Object createInstance(Program p, Class<?> class_type, Object[] args){
        try {
            World world = p.getWorld(); 
            int type = Arrays.toString(class_type.getInterfaces()).contains("NonBlocking") ? 0 : 1;
            Location loc = Help.getRanLocWithoutType(type, world);
            Object obj = class_type.getDeclaredConstructor(getArgTypes(args)).newInstance(args);
            world.setTile(loc, obj);
            return obj;
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException e) {
            System.out.println(e.getClass() + ", trying to initialize the next object. " + e.getMessage());
            return null;
        }
    }

    /**
     * Creates an array of classtypes corresponding to the classtypes of every element (as by Object.getClass()) in the given array. 
     * The order of the elements is preserved, and dublicates are allowed
     * @param args The array of objects from which you want their classes
     * @return A new array of classtypes corresponding to the classtypes of <strong>args</strong>
     */
    private static Class<?>[] getArgTypes(Object[] args) { 
        Class<?>[] arg_types = new Class[args.length];
        for (int i = 0 ; i < args.length ; i++) { 
            arg_types[i] = args[i].getClass();
        }
        return arg_types;
    }
}