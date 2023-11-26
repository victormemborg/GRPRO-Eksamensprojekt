import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;

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
        }
    }

    /*
     dadadaaw
    */
    private static Program createProgramFromFile(String path, int display_size, int delay) throws FileNotFoundException{
        Scanner scan = new Scanner(new File(path));
        Program p = new Program(Integer.parseInt(scan.nextLine()), display_size, delay);

        int line_counter = 1;
        while (scan.hasNextLine()) {
            line_counter++;
            try {
                //get input
                String line = scan.nextLine();
                String input1 = line.split(" ")[0];
                int input2 = Integer.parseInt(line.split(" ")[1]);
                //Determine class
                String class_name = input1.substring(0, 1).toUpperCase() + input1.substring(1, input1.length());
                Class<?> class_type = Class.forName(class_name);

                //create specified number of instances
                World world = p.getWorld();
                for (int i = 0 ; i < input2 ; i++) {
                    int type = Arrays.toString(class_type.getInterfaces()).contains("NonBlocking") ? 0 : 1;
                    world.setTile(getRanLocWithoutType(type, p), class_type.getDeclaredConstructor().newInstance());
                }

            } catch (Exception e) {
                System.out.println("Error: " + e.getClass() + ", skipping line " + line_counter + " in " + path);
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
        return empty_location_list.get((new Random()).nextInt(empty_location_list.size()));
    }
}