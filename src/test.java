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
        Program p = createProgramFromFile("data/test.txt");
        p.show();
        p.run();
    }

    /*
     dadadaaw
    */
    private static Program createProgramFromFile(String path){
        Scanner s = getScanner(path);
        int size = Integer.parseInt(s.nextLine());
        Program p = new Program(size, 800, 1000);
        ArrayList<Location> loclistNonBlock = getAllLocations(size);
        ArrayList<Location> loclistBlock = getAllLocations(size);

        int line_counter = 1;
        while (s.hasNextLine() && line_counter++ < Integer.MAX_VALUE) {
            try {
                createElementsFromLine(s, p, loclistNonBlock, loclistBlock);
            } catch (Exception e) {
                System.out.println("Fejl i input, skipper linje " + line_counter + " i inputfil");
            }
        }

        s.close();
        return p;
    }

    private static Scanner getScanner(String path) {
        try {
            Scanner s = new Scanner(new File(path));
            return s;
        } catch (FileNotFoundException fnfe) {
            System.out.println("Check path!");
            System.exit(-1);
            return null;
        }
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

    private static void createElementsFromLine(Scanner s, Program p, ArrayList<Location> loclistNonBlock, ArrayList<Location> loclistBlock) throws Exception{
        World world = p.getWorld();
        //get class from input
        String input1 = s.next();
        int input2 = Integer.parseInt(s.next());
        String class_name = input1.substring(0, 1).toUpperCase() + input1.substring(1, input1.length());
        Class<?> class_type = Class.forName(class_name);

        //create specified number of instances
        Random ran = new Random();
        for (int i = 0 ; i < input2 ; i++) {
            if (Arrays.toString(class_type.getInterfaces()).contains("NonBlocking")) {
                int r = ran.nextInt(loclistNonBlock.size());
                world.setTile(loclistNonBlock.get(r), class_type.getDeclaredConstructor().newInstance());
                loclistNonBlock.remove(r);
            } else {
                int r = ran.nextInt(loclistBlock.size());
                world.setTile(loclistBlock.get(r), class_type.getDeclaredConstructor().newInstance());
                loclistBlock.remove(r);
            }
        }
    }
}