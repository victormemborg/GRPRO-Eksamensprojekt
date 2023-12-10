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
import Actors.Wolf;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {
    public static void main(String[] args) {
        try {
            Program p = createProgramFromFile("data/test.txt", 800, 500);
            p.show();
            p.run();

        } catch (FileNotFoundException fnfe) {
            System.out.println("Check path!");
        } catch (NumberFormatException nfe) {
            System.out.println("The first line of your file should be an integer!");
        } catch (Exception e) {
            System.out.println("Something unexpected happened! Message:" + e.getMessage() + " Class: " + e.getClass());
            System.out.println(e.getCause());
        }
    }

    /*
     Returns an instances of Program with the specifications given by
     its arguments and the specified txt-file
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
                ArrayList<Object> cluster = new ArrayList<>(); // Only used by Wolf for now
                for (int i = 0 ; i < amount ; i++) {
                    cluster.add(createInstance(p, class_type, args));
                }

                //Special case for Wolfs
                if (class_type.getSimpleName().equals("Wolf")) {
                    ArrayList<Wolf> pack = Help.castArrayList(cluster);
                    for (Wolf wolf : pack) {
                        wolf.addPackMembers(pack);
                    }
                }
                
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage() + ", skipping line " + line_counter + " in " + path);
            }
        }
        scan.close();
        return p;
    }

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

    private static int getAmount(ArrayList<String> str_array) throws Exception{
        Random ran = new Random();
        for (String str : str_array) {
            if (str.contains("-")) {
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

    private static Object[] getArgs(ArrayList<String> str_array, Program p) {
        Object[] args = new Object[str_array.size() + 1]; // One index reserved for world. All Actors has this argument
        args[0] = p.getWorld();
        for (int i = 0 ; i < str_array.size() ; i++) { // For now, all other arguments will be given to constructor as Strings
            args[i + 1] = str_array.get(i);
        }
        return args;
    }

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

    private static Class<?>[] getArgTypes(Object[] args) { 
        Class<?>[] arg_types = new Class[args.length];
        for (int i = 0 ; i < args.length ; i++) { 
            arg_types[i] = args[i].getClass();
        }
        return arg_types;
    }
}