package itumulator.display.animation.components;

import itumulator.display.animation.ObjectInformation;
import itumulator.display.utility.IsomorphicCoordinateFactory;
import itumulator.world.Location;

public class HideAnimation extends Animation{
    public HideAnimation(ObjectInformation oi, Location location, int animationLength) {
        super(oi, location, animationLength);
        step = 255/animationLength;
    }

    int step;

    @Override
    protected AnimationFrame getFrame(int index) {
        return new AnimationFrame(oi, IsomorphicCoordinateFactory.Instance().getIsoLocation(location), index * step);
    }
}
