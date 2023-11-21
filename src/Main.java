import java.util.Random;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;

public class Main {

    public static void main(String[] args) {
        createWorld();
    }

    /*
     dadadaaw
    */
    private static void createWorld() {
        try {
            Scanner s = new Scanner(new File("data/t1-1a.txt"));
            int size = Integer.parseInt(s.nextLine());
            Program p = new Program(size, 800, 500);
            World world = p.getWorld();

            while (s.hasNextLine()) {
                //Lav object af klasse
                String input = s.next();
                String class_name = input.substring(0, 1).toUpperCase() + input.substring(1, input.length());
                Class<?> class_type = Class.forName(class_name);
                Object obj = class_type.getDeclaredConstructor().newInstance();
                //lav x instancer i world
                Random ran = new Random();
                for (int i = 0 ; i < Integer.parseInt(s.next()) ; i++) {
                    Location l = new Location(ran.nextInt(size), ran.nextInt(size));
                    world.setTile(l, obj);
                }
            }

        } catch (FileNotFoundException fnfe) {
            System.out.println(fnfe.getMessage());
        } catch (ClassNotFoundException cnfe) {
            System.out.println(cnfe.getMessage());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}