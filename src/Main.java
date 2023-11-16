import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import java.awt.Color;

public class Main {
    public static void main(String[] args) {
        int size = 3;
        int display_size = 800;
        int delay = 1000;

        Program p = new Program(size, display_size, delay);
        World world = p.getWorld();

        Person person = new Person();
        Location place = new Location(0,1);
        world.setTile(place, person);

        DisplayInformation di = new DisplayInformation(Color.red);
        p.setDisplayInformation(Person.class, di);

        p.show();
        for (int i = 0 ; i < 200; i++) {
            p.simulate();
        }
    }
}