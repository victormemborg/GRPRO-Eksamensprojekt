package itumulator.display.overlay;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.Random;

import itumulator.display.utility.ImageResourceCache;
import itumulator.display.utility.ImageUtility;

public class Cloud {
    private final int IMAGE_HEIGHT = 80;
    private final int SPAWN_MIN_Y = 0;
    private final int SPAWN_MAX_Y = 350;
    private final double SPAWN_MIN_SPEED = 0.2; 
    private final double SPAWN_MAX_SPEED = 2.4; 
    private int pixelSize;
    private int currentY;
    private double currentSpeed;
    private double currentX;
    private BufferedImage img;
    private Random rnd;
    
    private String[] possibleClouds = new String[]{
        "cloud-1",
        "cloud-2",
        "cloud-3",
        "cloud-4",
        "cloud-5"
    };
    
    public Cloud(int pixelSize){
        this.pixelSize = pixelSize;
        rnd = new Random();
        reset();
        currentX = rnd.nextDouble() * pixelSize;
    }
    
    public void draw(Graphics g){
        if (currentX >= pixelSize){
            reset();
        }
        currentX += currentSpeed;
        g.drawImage(img, (int)currentX, currentY, null);
    }

    private void reset(){
        img = ImageResourceCache.Instance().getImage(
            possibleClouds[rnd.nextInt(possibleClouds.length)]);
        double ratio = (IMAGE_HEIGHT * 1.0) / img.getHeight();
        img = ImageUtility.getScaledImage(img, (int)(ratio * img.getWidth()), IMAGE_HEIGHT);

        currentY = SPAWN_MIN_Y + (rnd.nextInt(SPAWN_MAX_Y));
        currentX = -img.getWidth();
        currentSpeed = SPAWN_MIN_SPEED + (rnd.nextDouble()*SPAWN_MAX_SPEED);
    }
}
