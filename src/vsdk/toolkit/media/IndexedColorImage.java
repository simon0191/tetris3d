//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 21 2006 - Oscar Chavarro: Original base version                =
//===========================================================================

package vsdk.toolkit.media;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.ColorRgb;

public class IndexedColorImage extends Image
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060821L;

    private byte data[];
    private int xSize;
    private int ySize;
    private RGBColorPalette colorTable;

    private ColorRgb _static_color;

    /**
    Check the general signature contract in superclass method
    Image.init.
    If the recieved colorTable is null, this method creates a 256 level
    grayscale palette.
    */
    public IndexedColorImage(RGBColorPalette colorTable)
    {
        xSize = 0;
        ySize = 0;
        data = null;
        if ( colorTable != null ) {
            this.colorTable = colorTable;
        }
        else {
            this.colorTable = new RGBColorPalette();
        }
    }

    /**
    Check the general signature contract in superclass method
    Image.init.
    This method creates a 256 level grayscale palette.
    */
    public IndexedColorImage()
    {
        xSize = 0;
        ySize = 0;
        data = null;
        this.colorTable = new RGBColorPalette();
    }

    /**
    This is the class destructor.
    */
    public void finalize()
    {
        if ( data != null ) {
            xSize = 0;
            ySize = 0;
            data = null;
        }
    }

    public boolean init(int width, int height)
    {
        try {
          data = new byte[width * height];
          for ( int i = 0; i < width*height; i++ ) {
              data[i] = 0;
          }
        }
        catch ( Exception e ) {
          data = null;
          return false;
        }
        xSize = width;
        ySize = height;
        return true;
    }

    public boolean initNoFill(int width, int height)
    {
        try {
          data = new byte[width * height];
          for ( int i = 0; i < width*height; i++ ) {
              data[i] = 0;
          }
        }
        catch ( Exception e ) {
          data = null;
          return false;
        }
        xSize = width;
        ySize = height;
        return true;
    }

    public int getXSize()
    {
        return xSize;
    }

    public int getYSize()
    {
        return ySize;
    }

    public byte[] getRawImage()
    {
        return data;
    }

    public void putPixel(int x, int y, byte val)
    {
        int index = xSize*y + x;
        data[index] = val;
    }

    public void putPixel(int x, int y, int val)
    {
        int index = xSize*y + x;
        data[index] = VSDK.unsigned8BitInteger2signedByte(val);
    }

    public byte getPixel8bitGrayScale(int x, int y)
    {
        return data[xSize*y + x];
    }

    public int getPixel(int x, int y)
    {
        int index = xSize*y + x;
        return VSDK.signedByte2unsignedInteger(data[index]);
    }

    public RGBPixel getPixelRgb(int x, int y)
    {
        int index = xSize*y + x;
        double val;
        val = (double)(VSDK.signedByte2unsignedInteger(data[index])) / 255.0;

        _static_color = colorTable.evalLinear(val);
        RGBPixel p = new RGBPixel();
        p.r = VSDK.unsigned8BitInteger2signedByte((int)(_static_color.r*255.0));
        p.g = VSDK.unsigned8BitInteger2signedByte((int)(_static_color.g*255.0));
        p.b = VSDK.unsigned8BitInteger2signedByte((int)(_static_color.b*255.0));
        return p;
    }

    public void getPixelRgb(int x, int y, RGBPixel p)
    {
        int index = xSize*y + x;
        double val;
        val = (double)(VSDK.signedByte2unsignedInteger(data[index])) / 255.0;

        _static_color = colorTable.evalLinear(val);
        p.r = VSDK.unsigned8BitInteger2signedByte((int)(_static_color.r*255.0));
        p.g = VSDK.unsigned8BitInteger2signedByte((int)(_static_color.g*255.0));
        p.b = VSDK.unsigned8BitInteger2signedByte((int)(_static_color.b*255.0));
    }

    public RGBColorPalette getColorTable()
    {
        return colorTable;
    }

    public void setColorTable(RGBColorPalette p)
    {
        colorTable = p;
    }

    public void putPixelRgb(int x, int y, RGBPixel p)
    {
        ColorRgb c = new ColorRgb();
        c.r = (double)(VSDK.signedByte2unsignedInteger(p.r)) / 255.0;
        c.g = (double)(VSDK.signedByte2unsignedInteger(p.g)) / 255.0;
        c.b = (double)(VSDK.signedByte2unsignedInteger(p.b)) / 255.0;

        int index = colorTable.selectNearestIndexToRgb(c);
        putPixel(x, y, index);
    }

    public RGBImage exportToRgbImage()
    {
        RGBImage copy;
        int xSize = getXSize();
        int ySize = getYSize();
        int x, y;
        int source;
        RGBPixel target = new RGBPixel();

        copy = new RGBImage();
        copy.init(xSize, ySize);
        for ( x = 0; x < xSize; x++ ) {
            for ( y = 0; y < ySize; y++ ) {
                //source = getPixel(x, y);
                //target.r = target.g = target.b = VSDK.unsigned8BitInteger2signedByte(source);
                getPixelRgb(x, y, target);
                copy.putPixel(x, y, target);
            }
        }
        return copy;
    }

    public RGBAImage exportToRgbaImage()
    {
        RGBAImage copy;
        int xSize = getXSize();
        int ySize = getYSize();
        int x, y;
        int source;
        RGBAPixel target = new RGBAPixel();

        copy = new RGBAImage();
        copy.init(xSize, ySize);
        target.a = VSDK.unsigned8BitInteger2signedByte(128);
        for ( x = 0; x < xSize; x++ ) {
            for ( y = 0; y < ySize; y++ ) {
                source = getPixel(x, y);
                target.r = target.g = target.b = VSDK.unsigned8BitInteger2signedByte(source);
                copy.putPixel(x, y, target);
            }
        }

        return copy;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
