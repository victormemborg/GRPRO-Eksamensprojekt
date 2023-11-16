import itumulator.executable.DisplayInformation;
import itumulator.executable.Program;
import itumulator.world.World;
import itumulator.world.Location;
import java.awt.Color;

public class Main {

    public static void main(String[] args) {

        int size = 4; // størrelsen af vores 'map' (dette er altid kvadratisk)
        int delay = 2000; // forsinkelsen mellem hver skridt af simulationen (i ms)
        int display_size = 1000; // skærm opløsningen (i px)

        Program p = new Program(size, display_size, delay); // opret et nyt program

        World world = p.getWorld(); // hiv verdenen ud, som er der hvor vi skal tilføje ting!z

        Person person = new Person();
        Location place = new Location(1, 1);
        world.setTile(place, person);

        DisplayInformation di = new DisplayInformation(Color.pink);
        p.setDisplayInformation(Person.class, di);

        p.show(); // viser selve simulationen
        for (int i = 0; i < 200; i++) {
            p.simulate();
        } // kører 200 runder, altså kaldes 'act' 200 gange for alle placerede aktører

    }

}