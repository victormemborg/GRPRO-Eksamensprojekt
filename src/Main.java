import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import java.awt.Color;

public class Main {

    public static void main(String[] args) {
        int size = 3; // størrelsen af vores 'map' (dette er altid kvadratisk)
        int delay = 1000; // forsinkelsen mellem hver skridt af simulationen (i ms)
        int display_size = 800; // skærm opløsningen (i px)
        Program p = new Program(size, display_size, delay); // opret et nyt program
        World world = p.getWorld(); // hiv verdenen ud, som er der hvor vi skal tilføje ting!
        
        Person person = new Person();
        Location place = new Location(0,1); 
        world.setTile(place, person);

        DisplayInformation di = new DisplayInformation(Color.red);
        p.setDisplayInformation(Person.class, di);

        p.show(); // viser selve simulationen
    }
}