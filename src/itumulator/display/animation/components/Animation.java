package itumulator.display.animation.components;

import itumulator.display.animation.ObjectInformation;
import itumulator.world.Location;

public abstract class Animation implements Comparable<Animation>{
    protected ObjectInformation oi;
    protected Location location;
    protected int frameCount;
    protected int animationLength;
    protected boolean overrideSort;

    public Animation(ObjectInformation oi, Location location, int animationLength, boolean overrideSort){
        this.oi = oi;
        this.location = location;
        this.animationLength = animationLength;
        this.overrideSort = overrideSort;
    }

    public Animation(ObjectInformation oi, Location location, int animationLength){
        this(oi, location, animationLength, false);
    }

    public AnimationFrame next(){
        if (frameCount == animationLength){
            throw new IllegalStateException("Animation out of frames");
        }
        return getFrame(frameCount++);
    }

    @Override
    public int compareTo(Animation other) {
        if (this.overrideSort && !other.overrideSort){
            return 1;
        } else if (!this.overrideSort && other.overrideSort) {
            return -1;
        }
        if (this.oi.isGroundObject() && !other.oi.isGroundObject()){
            return -1;
        } else if (!this.oi.isGroundObject() && other.oi.isGroundObject()){
            return 1;
        }
        return 0;
    }

    protected abstract AnimationFrame getFrame(int index);
}
