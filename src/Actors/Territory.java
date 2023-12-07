package Actors;

import itumulator.world.World;
import itumulator.world.Location;
import java.util.ArrayList;
import java.util.Set;

public class Territory extends Home {
    Set<Location> territorial_area;
    Location territorial_spot;

    public Territory(World world, Animal animal) {
        super(world);
        setTerritory(animal);
    }

    public void setTerritory(Animal animal){

        territorial_spot = world.getLocation(animal);
        territorial_area = world.getSurroundingTiles(world.getLocation(animal),3);

    }

    public ArrayList<Location> getTerritory(){
        ArrayList<Location> territory = new ArrayList<>();
        for(Location location : territorial_area){
            territory.add(location);
        }
        return territory;
    }

    public Location getBaseTerritory(){
        return territorial_spot;
    }
}
