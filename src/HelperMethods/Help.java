package HelperMethods;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import itumulator.executable.Program;
import itumulator.world.*;

public class Help {
    /*
     * Returns a random location that is not occupied by any object of
     * the given type (NonBlocking: 0 - Blocking: 1)
     */
    public static Location getRanLocWithoutType(int type, World world) {
        ArrayList<Location> empty_location_list = new ArrayList<>();
        Object[][][] tiles = world.getTiles();
        for (int i = 0; i < world.getSize(); i++) {
            for (int j = 0; j < world.getSize(); j++) {
                if (tiles[i][j][type] == null) {
                    empty_location_list.add(new Location(i, j));
                }
            }
        }
        return empty_location_list.get((new Random()).nextInt(empty_location_list.size()));
    }

    public static Location getRandomNearbyEmptyTile(World world, Location l, int radius) {
        ArrayList<Location> emptyTiles = new ArrayList<>();
        int startX = Math.max(0, l.getX() - radius);
        int endX = Math.min(world.getSize() - 1, l.getX() + radius);
        int startY = Math.max(0, l.getY() - radius);
        int endY = Math.min(world.getSize() - 1, l.getY() + radius);

        for (int x = startX; x <= endX; x++) {
            for (int y = startY; y <= endY; y++) {
                if (world.isTileEmpty(new Location(x, y)) ) {
                    emptyTiles.add(new Location(x, y));
                }
            }
        }

        if (emptyTiles.isEmpty()) {
            return null; // No empty tile found
        }

        Random random = new Random();
        return emptyTiles.get(random.nextInt(emptyTiles.size()));
    }

    public static int getDistance(Location l1, Location l2) {
        return Math.abs(l1.getX() - l2.getX()) + Math.abs(l1.getY() - l2.getY());
    }

    public static boolean doesInterfaceContain(Object o, String target) {
        if (o == null) {
            return false;
        }
        if (Arrays.toString(o.getClass().getInterfaces()).contains(target)) {
            return true;
        } else {
            return false;
        }
    }

}
