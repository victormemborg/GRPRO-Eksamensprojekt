import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class test {

    public static void main(String[] args) {
        try {
            createWorldFromFile("data/test.txt");
        } catch (FileNotFoundException fnfe) {
            System.out.println("Check path!");
        }
        
    }

    /*
     dadadaaw
    */
    private static void createWorldFromFile(String path) throws FileNotFoundException{
        Scanner s = new Scanner(new File(path));
        int size = Integer.parseInt(s.nextLine());
        Program p = new Program(size, 800, 500);
        World world = p.getWorld();
        ArrayList<Location> loclistNonBlock = getAllLocations(size);
        ArrayList<Location> loclistBlock = getAllLocations(size);

        while (s.hasNext()) {
            try {
                String input = s.next();
                String class_name = input.substring(0, 1).toUpperCase() + input.substring(1, input.length());
                Class<?> class_type = Class.forName(class_name);

                int input2 = Integer.parseInt(s.next());
                for (int i = 0 ; i < input2 ; i++) {
                    if (Arrays.toString(class_type.getInterfaces()).contains("NonBlocking")) {
                        setTileFromLocList(world, loclistNonBlock, class_type);
                    } else {
                        setTileFromLocList(world, loclistBlock, class_type);
                    }
                }

            } catch (ClassNotFoundException cnfe) {
                System.out.println(cnfe.getMessage());
            }
        }
        s.close();
        p.show();
        p.run();
        
        /* 
        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
        */
    }

    private static ArrayList<Location> getAllLocations(int size) {
        ArrayList<Location> loclist = new ArrayList<>();
        for (int x = 0 ; x < size ; x++) {
            for (int y = 0 ; y < size ; y++) {
                loclist.add(new Location(x, y));
            }
        }
        return loclist;
    }

    private static Class<?> getClassTypeFromInput(Scanner s) {

    }

    private static void setTileFromLocList(World world, ArrayList<Location> loclist, Class<?> class_type) {
        try {
            Random ran = new Random();
            int r = ran.nextInt(loclist.size());
            world.setTile(loclist.get(r), class_type.getDeclaredConstructor().newInstance());
            loclist.remove(r);
        } catch (Exception e) {
            System.out.println(e.getMessage() + " Fejl i setTileFromLocList(). Skipper dette step");
        }

    }
}