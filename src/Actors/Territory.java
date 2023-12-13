package Actors;

import itumulator.world.World;
import itumulator.world.Location;
import java.util.ArrayList;
import java.util.Set;

public class Territory extends Home {
    Location center;
    int radius;
    ArrayList<Location> area;

    /**
     * Creates a territory with the given center and in the given world
     * @param world the world the territory is in
     * @param center the location of the center of the territory
     */
    public Territory(World world, Location center) {
        super(world);
        this.center = center;
        this.radius = 1;
        this.area = createArea();
    }

    /**
     * Creates the area of the territory that is to be occupied
     * @return an ArrayList of Locations representing the area of the territory
     */
    private ArrayList<Location> createArea() {
        Set<Location> set = world.getSurroundingTiles(center, radius);
        ArrayList<Location> list = new ArrayList<>();
        list.addAll(set);
        list.add(center);
        return list;
    }

    /**
     * Returns the location of the center of the territory
     * @return A Location object representing the center of the territory
     */
    @Override
    public Location getLocation() {
        return center;
    }

    /**
     * Returns the radius of the territory
     * @return an int representing how far the territory extends from the center
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Returns the area of the territory
     * @return an ArrayList of Locations representing the area of the territory
     */
    public ArrayList<Location> getArea() {
        return area;
    }
}
