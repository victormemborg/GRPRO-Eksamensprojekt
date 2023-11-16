package itumulator.display.animation.components;

import java.awt.image.BufferedImage;
import java.lang.Math;

import itumulator.display.utility.ImageResourceCache;
import itumulator.display.utility.ImageUtility;
import itumulator.display.utility.IsomorphicCoordinateFactory;
import itumulator.display.utility.Point2DInt;
import itumulator.world.World;

public class DayNightAnimation extends Animation{
    private final int IMAGE_HEIGHT = 150;
    private final int Y_POS = 100;
    private BufferedImage img;
    private boolean isDay;
    private boolean isDayPrime;
    private int timePrime;

    public DayNightAnimation(int animationLength, boolean day, boolean dayPrime, int timePrime) {
        super(null, null, animationLength, true);
        this.isDay = day;
        this.isDayPrime = dayPrime;
        this.timePrime = timePrime;
        setNewImage(isDay);
    }

    @Override
    protected AnimationFrame getFrame(int index) {
        if (animationLength / 2 == index){
            if (isDay != isDayPrime){
                setNewImage(isDayPrime);
            }
        }
        
        int dayPixelWidth = IsomorphicCoordinateFactory.Instance().getTotalSize()/World.getDayDuration();

        int actualTime = timePrime % World.getDayDuration();
        Point2DInt startPixelPoint = new Point2DInt((dayPixelWidth * actualTime) - Y_POS/2, Y_POS*2);

        Point2DInt endPixelPoint = startPixelPoint.interpolate(new Point2DInt(startPixelPoint.getX() + dayPixelWidth, Y_POS*2), (1.00 * index) / animationLength);

        double y = -Math.sin((actualTime + ((1.00 * index) / animationLength))*(Math.PI/World.getDayDuration())) * Y_POS* 1.5;

        endPixelPoint.setY((int)y + (Y_POS * 2));

        return new AnimationFrame(img, endPixelPoint);
    }

    private void setNewImage(boolean day){
        if (day){
            img = ImageResourceCache.Instance().getImage("sun");
        } else {
            img = ImageResourceCache.Instance().getImage("moon");
        }
        double ratio = (IMAGE_HEIGHT * 1.0) / img.getHeight();
        img = ImageUtility.getScaledImage(img, (int)(ratio * img.getWidth()), IMAGE_HEIGHT);
    }
}
