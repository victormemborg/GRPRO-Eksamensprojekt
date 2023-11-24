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
            Program p = createProgramFromFile("data/test.txt", 800, 500);
            p.show();
            p.run();

        } catch (FileNotFoundException fnfe) {
            System.out.println("Check path!");
        }
    }

    /*
     dadadaaw
    */
    private static Program createProgramFromFile(String path, int display_size, int delay) throws FileNotFoundException{
        Scanner scan = new Scanner(new File(path));
        Program p = new Program(Integer.parseInt(scan.nextLine()), display_size, delay);

        int line_counter = 2;
        while (scan.hasNextLine()) {
            try {
                //get input
                String input1 = scan.next();
                int input2 = Integer.parseInt(scan.next());
                //Determine class
                String class_name = input1.substring(0, 1).toUpperCase() + input1.substring(1, input1.length());
                Class<?> class_type = Class.forName(class_name);

                //create specified number of instances
                World world = p.getWorld();
                for (int i = 0 ; i < input2 ; i++) {
                    int type = Arrays.toString(class_type.getInterfaces()).contains("NonBlocking") ? 0 : 1;
                    world.setTile(getRanLocWithoutType(type, p), class_type.getDeclaredConstructor().newInstance());
                }
                line_counter++;

            } catch (Exception e) {
                System.out.println("Fejl i input, skipper linje " + line_counter + " i " + path);
            }
        }
        scan.close();
        return p;
    }

    private static Location getRanLocWithoutType(int type, Program p) {
        ArrayList<Location> empty_location_list = new ArrayList<>();
        Object[][][] tiles = p.getWorld().getTiles();
        for (int i = 0; i < p.getSize() ; i++) {
            for (int j = 0; j < p.getSize() ; j++) {
                if (tiles[i][j][type] == null) {
                    empty_location_list.add(new Location(i, j));
                }
            }
        }
        Random ran = new Random();
        return empty_location_list.get(ran.nextInt(empty_location_list.size()));
    }
}