package itumulator.display.utility;

import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;

public class ImageResourceCache {
    /* This is Singleton pattern */
    private static ImageResourceCache instance;

    public static ImageResourceCache Instance(){
        if (instance == null){
            instance = new ImageResourceCache();
        }
        return instance;
    }
    /* This is Singleton pattern */


    private Map<String, BufferedImage> cache;

    public BufferedImage getImage(String cacheString){
        BufferedImage result = cache.get(cacheString);
        if (result == null){
            System.out.println(cacheString + " is null");
        }
        return result;
    }

    private ImageResourceCache(){
        cache = new HashMap<String, BufferedImage>();

        loadFolder("./resources/");
        loadFolder("../resources");
    }

    private void loadFolder(String folderPath){
        try {
            File folder = new File(folderPath);
            
            if (folder.listFiles() == null) {
                return;
            }
            for (File element : folder.listFiles()) {
                if (element.getName().endsWith(".png") || 
                element.getName().endsWith(".jpg") || 
                element.getName().endsWith(".jpeg")){
                    String elementName = element.getName().substring(0, element.getName().lastIndexOf('.'));
                    if (cache.containsKey(elementName)){
                        throw new IllegalArgumentException("Image names in resource folder must be unique - " + elementName);
                    } else {
                        cache.put(elementName, ImageIO.read(element));
                    }
                } else {
                    loadFolder(folderPath+element.getName()+"/");
                }
            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
