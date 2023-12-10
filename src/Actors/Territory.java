package Actors;

import itumulator.world.World;
import itumulator.world.Location;
import java.util.ArrayList;
import java.util.Set;

public class Territory extends Home {
    Location center;
    int radius;
    ArrayList<Location> area;

    public Territory(World world, Location center) {
        super(world);
        this.center = center;
        this.radius = 1;
        this.area = createArea();
    }

    private ArrayList<Location> createArea() {
        Set<Location> set = world.getSurroundingTiles(center, radius);
        ArrayList<Location> list = new ArrayList<>();
        list.addAll(set);
        list.add(center);
        return list;
    }

    @Override
    public Location getLocation() {
        return center;
    }

    public int getRadius() {
        return radius;
    }

    public ArrayList<Location> getArea() {
        return area;
    }
}
