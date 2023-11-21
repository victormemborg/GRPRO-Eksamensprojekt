
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int size = 5;
        int display_size = 800;
        int delay = 500;

        Program p = new Program(size, display_size, delay);
        World world = p.getWorld();

        Random r = new Random();
        for (int i = 0 ; i < 10 ; i++) {
            Location l = new Location(r.nextInt(size), r.nextInt(size));
            while(!world.isTileEmpty(l)) {
                l = new Location(r.nextInt(size), r.nextInt(size));
            }
            world.setTile(l, new Person());
        }

        p.show();
        p.run();
    }
}