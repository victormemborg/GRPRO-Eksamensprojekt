package HelperMethods;

import java.util.ArrayList;
import java.util.Random;

import itumulator.executable.Program;
import itumulator.world.Location;

public class Help {
    /*
     Returns a random location that is not occupied by any object of 
     the given type (NonBlocking: 0 - Blocking: 1)
    */
    public static Location getRanLocWithoutType(int type, Program p) {
        ArrayList<Location> empty_location_list = new ArrayList<>();
        Object[][][] tiles = p.getWorld().getTiles();
        for (int i = 0; i < p.getSize() ; i++) {
            for (int j = 0; j < p.getSize() ; j++) {
                if (tiles[i][j][type] == null) {
                    empty_location_list.add(new Location(i, j));
                }
            }
        }
        return empty_location_list.get((new Random()).nextInt(empty_location_list.size()));
    }
}
