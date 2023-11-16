package itumulator.display.animation.components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import itumulator.display.animation.ObjectInformation;
import itumulator.display.utility.IsomorphicCoordinateFactory;
import itumulator.display.utility.IsomorphicUtility;
import itumulator.display.utility.Point2DInt;

public class AnimationFrame{
    private final float SHADOW_OPACITY = 0.5f;
    private ObjectInformation oi;
    private Point2DInt pixelPoint;
    private AlphaComposite alphaComp;
    private BufferedImage img;

    public AnimationFrame(BufferedImage img, Point2DInt pixelPoint){
        this.img = img;
        this.pixelPoint = pixelPoint;
    }

    public AnimationFrame(ObjectInformation oi, Point2DInt pixelPoint){
        this(oi, pixelPoint, 255);
    }

    public AnimationFrame(ObjectInformation oi, Point2DInt pixelPoint, int opacity){
        this.oi = oi;
        this.pixelPoint = pixelPoint;
        alphaComp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)((opacity * 1.0)/255));
    }

    public void draw(Graphics g){
        int correctionPixels = IsomorphicCoordinateFactory.Instance().getIsoReduction();

        if (alphaComp != null){
            ((Graphics2D)g).setComposite(alphaComp);
        }

        if (oi == null){
            // We assume draw to exact location (probably UI)
            g.drawImage(img, pixelPoint.getX(), pixelPoint.getY(), null);
        } else {
            if (oi.getImage() != null){
                BufferedImage scaledImg = IsomorphicCoordinateFactory.Instance().getScaledImage(oi.getImage());
                // Draw shadow oval
                ((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, SHADOW_OPACITY));
                g.setColor(Color.BLACK);
                g.fillOval(
                    pixelPoint.getX()+(scaledImg.getWidth()/2),
                    pixelPoint.getY()+(scaledImg.getHeight()/2), 
                    scaledImg.getWidth(),
                    scaledImg.getHeight()/5);

                if (alphaComp != null){
                    ((Graphics2D)g).setComposite(alphaComp);
                } else {
                    ((Graphics2D)g).setComposite(AlphaComposite.SrcOver);
                }

                // Draw image
                g.drawImage(
                    scaledImg,
                    pixelPoint.getX()+(correctionPixels/4),
                    pixelPoint.getY()- (correctionPixels/3),
                    null);
            } else {
                // Draw polygon with color
                int isoSize = IsomorphicCoordinateFactory.Instance().getTileSize()/2;
                g.setColor(oi.getColor());
                g.fillPolygon(IsomorphicUtility.getIsoPolygon(pixelPoint.getX() + correctionPixels - (correctionPixels/4), pixelPoint.getY() + (correctionPixels/2), isoSize, isoSize));
            }
        }
        ((Graphics2D)g).setComposite(AlphaComposite.SrcOver);
    }
}