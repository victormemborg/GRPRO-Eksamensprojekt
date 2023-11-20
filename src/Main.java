import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int size = 3; // størrelsen af vores 'map' (dette er altid kvadratisk)
        int delay = 500; // forsinkelsen mellem hver skridt af simulationen (i ms)
        int display_size = 800; // skærm opløsningen (i px)
    
        Program p = new Program(size, display_size, delay); // opret et nyt program
        World world = p.getWorld(); // hiv verdenen ud, som er der hvor vi skal tilføje ting!
    
        Random r = new Random();

        //Tilføjer en Person på alle steder undtagen en plads.
        for (int i = 0 ; i < (size*size)-1 ; i++) {
            Location random = new Location(r.nextInt(size), r.nextInt(size));
            while(!world.isTileEmpty(random)) {
                random = new Location(r.nextInt(size), r.nextInt(size));
            }
            world.setTile(random, new Person());
        }
        p.show(); // viser selve simulationen
        p.run();
    }
}