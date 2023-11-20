import itumulator.world.Location;
import itumulator.world.World;
import org.junit.*;

public class JUnitTest {
    @Test
    public void noAvailableSpace() {
        int size = 3;
        World world = new World(size); 

        //Placerer personer på alle mulige tiles
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Location loc = new Location(i, j);
                world.setTile(loc, new Person(world));
            }
        }
        //Sætter lokationen til (0,0) og får Person objektet på denne lokation
        Location testLocation = new Location(0,0);
        world.setCurrentLocation(testLocation);
        Person person = (Person) world.getTile(testLocation);

        //Forventer null i og med at alle tiles er occupied omkring den
        Assert.assertNull("Forventer null", person.getEmptyRandomLocations(world));
    }
}

