package itumulator.display.animation;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import itumulator.display.animation.components.Animation;
import itumulator.display.animation.components.AnimationSet;
import itumulator.display.animation.components.AppearAnimation;
import itumulator.display.animation.components.DayNightAnimation;
import itumulator.display.animation.components.HideAnimation;
import itumulator.display.animation.components.MoveAnimation;
import itumulator.display.animation.components.StillAnimation;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

public class AnimationFactory {
    private World world;
    private boolean isDay;
    private boolean isDayPrime;
    private Map<Object, Location> e;
    private Map<Object, Location> ePrime;
    private Map<Object, ObjectInformation> objectMap;
    private Map<Class, DisplayInformation> displayMap;

    public AnimationFactory(World world){
        this.world = world;
        objectMap = new HashMap<>();
        displayMap = new HashMap<>();
        e = world.getEntities();
        isDay = world.isDay();
    }

    public void setDisplayInformation(Class cl, DisplayInformation di){
        displayMap.put(cl, di);
    }

    public void requestUpdate(){
        e = world.getEntities();
        isDay = world.isDay();
    }

    /**
     * Generates renders of animation sets for the next simulation step
     * @param length is the amount of images to generate
     * @return a list of callable bufferedimages, generatable
     */
    public List<Callable<BufferedImage>> getImages(int length){
        ePrime = world.getEntities();
        isDayPrime = world.isDay();
        
        List<Animation> animations = new ArrayList<>();
        for (Entry<Object, Location> kvp : e.entrySet()) {
            Object k = kvp.getKey();
            Location l = kvp.getValue();
            ObjectInformation oi = getObjectInformation(k);
            
            if (!ePrime.containsKey(k)){
                // put in list deleted animation
            } else {
                Location lPrime = ePrime.get(k);
                if (l == null && lPrime != null){
                    animations.add(new AppearAnimation(oi, l, length));
                    continue;
                }
                if (lPrime == null){
                    if (l == null){
                        continue;
                    }
                    animations.add(new HideAnimation(oi, l, length));
                    continue;
                }
                if (l.equals(lPrime)){
                    animations.add(new StillAnimation(oi, l, length));
                    continue;
                }
                animations.add(new MoveAnimation(oi, l, length, lPrime));
            }
        }

        for (Entry<Object, Location> kvp : ePrime.entrySet()){
            Object k = kvp.getKey();
            Location l = kvp.getValue();
            ObjectInformation oi = getObjectInformation(k);
            if (!e.containsKey(k)){
                animations.add(new AppearAnimation(oi, l, length));
            }
        }

        animations.add(new DayNightAnimation(length, isDay, isDayPrime, world.getCurrentTime()));

        Collections.sort(animations);

        List<Callable<BufferedImage>> sets = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            AnimationSet set = new AnimationSet();
            for (Animation animation : animations) {
                set.add(animation.next());
            }
            sets.add(set);
        }

        e = ePrime;
        isDay = isDayPrime;
        return sets;
    }

    private ObjectInformation getObjectInformation(Object obj){
        ObjectInformation oi;
        if (objectMap.containsKey(obj)){
            oi = objectMap.get(obj);
        } else {
            if (obj instanceof DynamicDisplayInformationProvider){
                oi = new ObjectInformation(obj);
            } else {
                if (!displayMap.containsKey(obj.getClass())){
                    throw new IllegalStateException("Missing DisplayInformation for " + obj.getClass());
                }
                oi = new ObjectInformation(obj, displayMap.get(obj.getClass()));
            }
            objectMap.put(obj, oi);
        }
        return oi;
    }
}
