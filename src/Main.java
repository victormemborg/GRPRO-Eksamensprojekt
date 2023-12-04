import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import HelperMethods.Help;
import Actors.*;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {
    public static void main(String[] args) {
        try {
            Program p = createProgramFromFile("data/test.txt", 800, 1000);
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
    private static Program createProgramFromFile(String path, int display_size, int delay) throws FileNotFoundException {
        Scanner scan = new Scanner(new File(path));
        Program p = new Program(Integer.parseInt(scan.nextLine()), display_size, delay);

        int line_counter = 1;
        while (scan.hasNextLine()) {
            line_counter++;
            try {
                //Get input
                String line = scan.nextLine();
                String name = line.split(" ")[0];
                int amount = getAmount(line.split(" ")[1]);
                Location territory = getTerritory(line);

                //Determine class
                String class_name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
                Class<?> class_type = Class.forName("Actors." + class_name);

                //Create specified number of instances
                if (class_name.equals("Wolf")) {
                    System.out.println("Laver en ulv");
                    World world = p.getWorld();
                    ArrayList<Wolf> pack = new ArrayList<>();
                    for (int i = 0 ; i < amount ; i++) {
                        Wolf wolf = new Wolf(world);
                        pack.add(wolf);
                        world.setTile(Help.getRanLocWithoutType(1, world), wolf);
                    }
                    System.out.println(pack);
                }
                for (int i = 0 ; i < amount ; i++) {
                    createInstance(p, class_type, territory);
                }

            } catch (ClassNotFoundException e) {
                System.out.println("Error, cant find class: " + e.getClass() + ", skipping line " + line_counter + " in " + path);
            }
        }
        scan.close();
        return p;
    }

    private static int getAmount(String amount_str) {
        Random ran = new Random();
        if (amount_str.contains("-")) {
            int lower_bound = Integer.parseInt(amount_str.split("-")[0]);
            int upper_bound = Integer.parseInt(amount_str.split("-")[1]);
            return ran.nextInt(lower_bound, upper_bound+1);
        }
        return Integer.parseInt(amount_str);
    }

    private static Location getTerritory(String line) {
        try {
            String temp_str = line.split(" ")[2].replaceAll("\\(|\\)", "");
            int x = Integer.parseInt(temp_str.split(",")[0]);
            int y = Integer.parseInt(temp_str.split(",")[1]);
            return new Location(x, y);
        } catch (IndexOutOfBoundsException iobe) {
            //System.out.println("This actor has no territory");
            return null;
        }  
    }

    private static void createInstance(Program p, Class<?> class_type, Location territory){
        try {
            World world = p.getWorld();
            int type = Arrays.toString(class_type.getInterfaces()).contains("NonBlocking") ? 0 : 1;
            if (territory == null) {
                Class<?>[] cArg = new Class[1];
                cArg[0] = World.class;
                world.setTile(Help.getRanLocWithoutType(type, world), class_type.getDeclaredConstructor(cArg).newInstance(world));
            } else {
                Class<?>[] cArg = new Class[2];
                cArg[0] = World.class;
                cArg[1] = Location.class;
                world.setTile(Help.getRanLocWithoutType(type, world), class_type.getDeclaredConstructor(cArg).newInstance(world, territory));
            }
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            System.out.println(e.getClass() + ", trying to initialize the next object");
        } catch (IllegalArgumentException iae) {
            System.out.println(iae.getMessage());
        }
    }
}