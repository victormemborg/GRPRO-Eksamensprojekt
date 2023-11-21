package itumulator.display;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.swing.*;

import itumulator.display.animation.AnimationFactory;
import itumulator.display.utility.ImageResourceCache;
import itumulator.display.utility.ImageUtility;
import itumulator.display.utility.IsomorphicCoordinateFactory;
import itumulator.display.utility.IsomorphicUtility;
import itumulator.display.utility.Point2DInt;
import itumulator.executable.DisplayInformation;
import itumulator.executable.DynamicDisplayInformationProvider;
import itumulator.world.Location;
import itumulator.world.World;

/**
 * Provides a canvas painting the various objects within our world. This is not relevant to continue the project.
 */
public class Canvas extends JPanel {
    private final static Color COLOR_EMPTY = Color.WHITE;
    private final static Color COLOR_NON_PAINTABLE = Color.GRAY;
    private final int MS_PER_FRAME = 4;
    private World world;
    private Graphics graphics;
    private Image img;
    private int size;
    private boolean isomorphic;
    private BufferedImage isoBackgroundImage;
    private AnimationFactory af;
    private java.util.Map<Class, Color> colorMap;
    private ExecutorService executor;
    
    public Canvas(World world, int size, boolean startIso) {
        super();
        setLayout(new BorderLayout());
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMaximumSize(d);
        setMinimumSize(d);
        setSize(d);
        this.world = world;
        this.size = size;
        this.af = new AnimationFactory(world);
        new Random();
        colorMap = new java.util.HashMap<>();
        BufferedImage img = ImageResourceCache.Instance().getImage("base");
        isoBackgroundImage = ImageUtility.getScaledImage(img, IsomorphicCoordinateFactory.Instance().getDisplaySize(), IsomorphicCoordinateFactory.Instance().getDisplaySize());
        setIsomorphic(startIso);
    }

    /**
     * @return if using isomorphic renderer
     */
    public boolean isIsomorphic() {
        return isomorphic;
    }

    /**
     * Boolean to control whether to render isomorphic or topdown
     * @param isomorphic sets the boolean
     */
    public void setIsomorphic(boolean isomorphic) {
        this.isomorphic = isomorphic;
        if (isomorphic){
            af.requestUpdate();
        }
    }

    private void setColor(Class c, Color color) {
        colorMap.put(c, color);
    }

    /**
     * Decide on the graphical representation of objects when shown within the GUI.
     * @param cl is the class which to associate a given display type with (can be accessed by writing [ClassName].class).
     * @param di the {@link DisplayInformation} to associate the type of object with.
     */
    public void setDisplayInformation(Class cl, DisplayInformation di){
        setColor(cl, di.getColor());
        af.setDisplayInformation(cl, di);
    }

    public void paintImage(){
        this.paintImage(0);
    }

    public void paintImage(int delay) {
        if (isomorphic){
            // 1 main thread
            // 1 simulator thread
            // 1 java thread
            // 1 overlay rendering thread
            // 1 buffer for safety
            // rest for rendering
            executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()-5);
            try {
                List<Future<BufferedImage>> queue = executor.invokeAll(af.getImages(delay == 0 ? 1 : delay/MS_PER_FRAME));
                
                for (Future<BufferedImage> future : queue) {
                    // Potentially could be moved out
                    // ------
                    img = createImage(size, size);
                    graphics = img.getGraphics();
                    graphics.drawImage(isoBackgroundImage, 0,IsomorphicCoordinateFactory.Instance().getDisplaySize()/2, null);
                    graphics.setColor(new Color(150, 210, 131));
                    graphics.fillPolygon(IsomorphicUtility.getIsoPolygon((IsomorphicCoordinateFactory.Instance().getDisplaySize()/2), IsomorphicCoordinateFactory.Instance().getDisplaySize()/2, IsomorphicCoordinateFactory.Instance().getDisplaySize()/2, IsomorphicCoordinateFactory.Instance().getDisplaySize()/4));
                    // -------
                    
                    graphics.drawImage(future.get(), 0, 0,null);
                    
                    // Currently sleeping delays the rendering a LOT
                    // would need to sleep a "MAXIMUM" amount of time
                    // and make sure to have a catch-up mechanism
                    //Thread.sleep(MS_PER_FRAME);
                    repaint();
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else {
            img = createImage(size, size);
            graphics = img.getGraphics();

            graphics.setColor(COLOR_EMPTY);
            graphics.fillRect(0, 0, size, size);
            int tiles = world.getSize();
            for (int y = tiles-1; y >= 0; y--) {
                for (int x = 0; x < tiles; x++) {
                    Location l = new Location(x, y);
                    Object o = world.getTile(l);
                    drawDebugView(l, o);
                }
            }
            repaint();
        }
    }

    private void drawDebugView(Location l, Object o) {
        int tiles = world.getSize();
        int pixelSize = size / tiles;
        Point2DInt pixelPoint;
        pixelPoint = new Point2DInt(pixelSize * l.getX(), pixelSize * l.getY());

        int[][] locations = new int[][] {
                { l.getX(), l.getY() - 1 },
                { l.getX(), l.getY() + 1 },
                { l.getX() - 1, l.getY() },
                { l.getX() + 1, l.getY() },
        };

        graphics.setColor(new Color(250, 250, 250));
        graphics.fillRect(pixelPoint.getX(), pixelPoint.getY(), pixelSize, pixelSize);


        
        if (world.containsNonBlocking(l) && world.getNonBlocking(l) != o)
            drawDebugView(l, world.getNonBlocking(l));

        // for (int[] loc : locations) {
        //     if (loc[0] < 0 || loc[0] >= tiles || loc[1] < 0 || loc[1] >= tiles)
        //         continue;
        //     Object on = world.getLowestTile(new Location(loc[0], loc[1]));
        //     if (on != null && o != null && on.getClass() == o.getClass()) {
        //         break;
        //     }
        // }

        if (o instanceof DynamicDisplayInformationProvider){
                setDisplayInformation(o.getClass(), ((DynamicDisplayInformationProvider)o).getInformation());
        }

        if (o != null){
            if (colorMap.containsKey(o.getClass())) {
                graphics.setColor(colorMap.get(o.getClass()));
            } else {
                graphics.setColor(COLOR_NON_PAINTABLE);
            }
            int x = pixelPoint.getX();
            int y = pixelPoint.getY();
            int width = pixelSize;
            int height = pixelSize;
            graphics.fillRect(x, y, width, height);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (img != null)
            g.drawImage(img, (this.getWidth()/2)-(size/2), (this.getHeight()/2)-(size/2), null);
    }

}
