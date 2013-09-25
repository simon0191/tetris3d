//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 19 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.render.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.media.Image;
import vsdk.toolkit.media.RGBImage;
import vsdk.toolkit.media.RGBPixel;

public class AwtRGBImageRenderer extends AwtRenderer
{
    public static void draw(Graphics dc, RGBImage img, int x0, int y0)
    {
/*
        int x, y;
        RGBPixel pixel;

        for ( y = 0; y < img.getYSize(); y++ ) {
            for ( x = 0; x < img.getXSize(); x++ ) {
                pixel = img.getPixelRgb(x, y);
                dc.setColor(
                    new Color( VSDK.signedByte2unsignedInteger(pixel.r), 
                               VSDK.signedByte2unsignedInteger(pixel.g), 
                               VSDK.signedByte2unsignedInteger(pixel.b) )
                );
                dc.drawLine(x+x0, y+y0, x+x0, y+y0);
            }
        }
*/
        BufferedImage i = exportToAwtBufferedImage(img);
        dc.drawImage(i, x0, y0, null);
    }

    public static void draw(Graphics dc, RGBImage img)
    {
        draw(dc, img, 0, 0);
    }

    /**
    Given an input BufferedImage, this method copies its contents to the
    specified output image. If output image currently exists, this method
    doesn't initialize its contents, but merely copies pixels. If the
    output image previously had a different size that the input's size,
    then is initialized.
    */
    public static boolean importFromAwtBufferedImage(
        BufferedImage input, RGBImage output
    )
    {
        int w = input.getWidth();
        int h = input.getHeight();
        int w2 = output.getXSize();
        int h2 = output.getYSize();

        if ( w != w2 || h != h2 ) {
            if ( !output.initNoFill(w, h) ) {
                return false;
            }
        }

        ColorModel cm = input.getColorModel();
        int x, y;
        int pixel;
        RGBPixel p = new RGBPixel();

        if ( cm.getNumColorComponents() == 3 ) {
            for ( y = 0; y < h; y++ ) {
                for ( x = 0; x < w; x++ ) {
                    // Warning: This method call is so slow...
                    pixel = input.getRGB(x, y);
                    p.r = (byte)((pixel & 0x00FF0000) >> 16);
                    p.g = (byte)((pixel & 0x0000FF00) >> 8);
                    p.b = (byte)((pixel & 0x000000FF));
                    output.putPixelRgb(x, y, p);
                }
            }
        }
        else if ( cm.getNumColorComponents() == 1 ) {
            DataBuffer db = input.getData().getDataBuffer();
            int i;
            for ( i = 0, y = 0; y < h; y++ ) {
                for ( x = 0; x < w; x++, i++ ) {
                    p.r = p.g = p.b = VSDK.unsigned8BitInteger2signedByte((db.getElem(i) & 0x000000FF));
                    // Warning: This method call is so slow...
                    output.putPixelRgb(x, y, p);
                }
            }
        }
        else {
            VSDK.reportMessage(null, VSDK.FATAL_ERROR, "importFromAwtBufferedImage", "ColorSpace encoding not supported!");
        }
        return true;
    }

    public static BufferedImage exportToAwtBufferedImage(RGBImage img)
    {
        BufferedImage bi;
        RGBPixel p;
        int x, y;
        int val;
        int r, g, b;

        bi = new BufferedImage(img.getXSize(), img.getYSize(), 
             BufferedImage.TYPE_INT_ARGB);
        for ( y = 0; y < img.getYSize(); y++ ) {
            for ( x = 0; x < img.getXSize(); x++ ) {
                p = img.getPixelRgb(x, y);
                r = VSDK.signedByte2unsignedInteger(p.r);
                g = VSDK.signedByte2unsignedInteger(p.g);
                b = VSDK.signedByte2unsignedInteger(p.b);
                val = 0xFF000000 | (r << 16) | (g << 8) | b;
                bi.setRGB(x, y, val);
            }
        }
        return bi;
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
