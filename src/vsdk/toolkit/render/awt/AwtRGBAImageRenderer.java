//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - November 28 2005 - Oscar Chavarro: Original base version              =
//===========================================================================

package vsdk.toolkit.render.awt;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.media.RGBAImage;
import vsdk.toolkit.media.RGBAPixel;

public class AwtRGBAImageRenderer extends AwtRenderer
{
    public static void draw(Graphics dc, RGBAImage img, int x0, int y0)
    {
        int x, y;
        RGBAPixel pixel;

        for ( y = 0; y < img.getYSize(); y++ ) {
            for ( x = 0; x < img.getXSize(); x++ ) {
                pixel = img.getPixel(x, y);
                dc.setColor(
                    new Color( VSDK.signedByte2unsignedInteger(pixel.r), 
                               VSDK.signedByte2unsignedInteger(pixel.g), 
                               VSDK.signedByte2unsignedInteger(pixel.b) )
                );
                dc.drawLine(x+x0, y+y0, x+x0, y+y0);
            }
        }
    }

    public static void draw(Graphics dc, RGBAImage img)
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
        BufferedImage input, RGBAImage output
    )
    {
        int w = input.getWidth();
        int h = input.getHeight();
        int w2 = output.getXSize();
        int h2 = output.getYSize();

        if ( w != w2 || h != h2 ) {
            if ( !output.init(w, h) ) {
                return false;
            }
        }

        int x, y;
        int pixel;
        RGBAPixel p = new RGBAPixel();

        for ( y = 0; y < h; y++ ) {
            for ( x = 0; x < w; x++ ) {
                // Warning: This method call is so slow...
                pixel = input.getRGB(x, y);
                p.a = (byte)((pixel & 0xFF000000) >> 24);
                p.r = (byte)((pixel & 0x00FF0000) >> 16);
                p.g = (byte)((pixel & 0x0000FF00) >> 8);
                p.b = (byte)((pixel & 0x000000FF));
                output.putPixel(x, y, p);
            }
        }
        return true;
    }

    public static BufferedImage exportToAwtBufferedImage(RGBAImage img)
    {
        BufferedImage bi;
        RGBAPixel p;
        int x, y;
        int val;
        int r, g, b, a;

        bi = new BufferedImage(img.getXSize(), img.getYSize(), 
             BufferedImage.TYPE_INT_ARGB);
        for ( y = 0; y < img.getYSize(); y++ ) {
            for ( x = 0; x < img.getXSize(); x++ ) {
                p = img.getPixel(x, y);
                a = VSDK.signedByte2unsignedInteger(p.a);
                r = VSDK.signedByte2unsignedInteger(p.r);
                g = VSDK.signedByte2unsignedInteger(p.g);
                b = VSDK.signedByte2unsignedInteger(p.b);
                val = (a << 24) | (r << 16) | (g << 8) | b;
                bi.setRGB(x, y, val);
            }
        }
        return bi;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
