import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.Location;
import itumulator.world.World;
import java.awt.Color;

public class Test {
    public static void main(String[] args){
        int size = 3;
        int display_size = 600;
        int delay = 1000;

        Program p = new Program(size, display_size, delay);
        World world = p.getWorld();

        Person person = new Person();
        Location place = new Location(0,1);
        world.setTile(place, person);

        int count = 0;
        int bruh = world.getEmptySurroundingTiles(place).size();
        System.out.println();
        for (Location l : world.getEmptySurroundingTiles(place)) {
            if (count < bruh-1) {
                world.setTile(l, new Person());
                count += 1;
                System.out.println(count);
            }

        }

        DisplayInformation di = new DisplayInformation(Color.red);
        p.setDisplayInformation(Person.class, di);

        p.show();
        p.run(); 
    }
}
