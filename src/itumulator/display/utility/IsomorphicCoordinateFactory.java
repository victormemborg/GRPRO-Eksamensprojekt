package itumulator.display.utility;

import java.awt.image.BufferedImage;

import itumulator.world.Location;

public class IsomorphicCoordinateFactory {
        /* This is Singleton pattern */
        private static IsomorphicCoordinateFactory instance;

        public static IsomorphicCoordinateFactory Instance(){
            if (instance == null){
                throw new IllegalStateException("IsomorphicCoordinateFactory has not been setup yet, use setupFactory");
            }
            return instance;
        }
        /* This is Singleton pattern */

        public static void setupFactory(int isoCanvasReduction, int size, double scalingfactor, int worldSize){
            if (instance == null){
                instance = new IsomorphicCoordinateFactory();
            }
            instance.isoCanvasReduction = isoCanvasReduction;
            instance.size = size;
            instance.scalingFactor = scalingfactor;
            instance.worldSize = worldSize;
        }

        public Point2DInt getIsoLocation(Location l){
            return IsomorphicUtility.getIsoLocation(l, getTileSize(), getIsoPolygonSize());
        }

        public int getIsoSize(){
            return size - isoCanvasReduction;
        }
        
        public int getIsoPolygonSize(){
            return getIsoSize() - (isoCanvasReduction/2);
        }

        public int getIsoReduction(){
            return isoCanvasReduction;
        }

        public int getTileSize(){
            return getIsoSize()/worldSize;
        }

        public int getTotalSize(){
            return size;
        }

        public BufferedImage getScaledImage(BufferedImage image){
            return ImageUtility.getScaledImage(image, getTileSize()/2, getTileSize());
        }

        private int isoCanvasReduction;
        private int size;
        private double scalingFactor;
        private int worldSize;

        private IsomorphicCoordinateFactory(){
        }

}
