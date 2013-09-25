//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//= - November 28 2005 - Oscar Chavarro: Quality check                      =
//= - March 19 2006 - Oscar Chavarro: VSDK integration                      =
//===========================================================================

package vsdk.toolkit.media;

// Note that on (old or incomplete) Java implementations as such those found
// on mobile devices, this class can be disabled, by commenting out following
// line and all dependant methods, and using the basic version of this class
// (not the direct buffer optimized).
import java.nio.ByteBuffer;

//#define WITH_JAVA_DIRECT_BUFFERS

//#ifdef WITH_JAVA_DIRECT_BUFFERS
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import vsdk.toolkit.io.PersistenceElement;
//#endif

import vsdk.toolkit.common.VSDK;

/**
Current class is an specific low level implementation of an uncompressed
24 bits per pixel RGB image over a byte array (ordered in a sequential array
of RGB bytes, row by row from upper left pixel, and left to right on each
row).

Note that this class implements two version of vector access operations:
one simple basic one, and one optimized for using Java's "Direct Buffers".

If Java would have C/C++ - like preprocessor directives, the two
implementations could be selected using conditional compilation. As
conditional compilation is not supported on Java, manual comments are
provided. It was choosen not to use hierarchy to keep a simple class
easy to understand at a design level, and to use it for teaching purposes.
Another reason for using this "comment-based conditional compilation" is
to keep this class conceptually consistent with non-java VSDK realizations
(particulary the corresponding C++ AQUYNZA class).
*/
public class RGBImage extends Image
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//    private byte[] data;
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
    transient private ByteBuffer data;
//#endif

    private int xSize;
    private int ySize;

    /**
    Check the general signature contract in superclass method
    Image.init.
    */
    public RGBImage()
    {
        xSize = 0;
        ySize = 0;
        data = null;
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

    /**
    Experimental method. Used for rendering-only applications that has
    transfered image contents to a JOGL context (GPU's Video memory) */
    public void dettach() {
        if ( data != null ) {
            data = null;
        }
    }

    public int getSizeInBytes()
    {
        // Warning: it is not taking into account the internal occupancy of the
        // ByteBuffer
        return xSize*ySize*3 + 2*VSDK.sizeofInt + VSDK.sizeofReference;
    }

    /**
    Image initialize with black background fill.

    Given the desired width and height, this method asigns the needed memory
    to hold such image uncompressed.

    Returns true if memory allocation succeed, false if not.
    */
    public boolean init(int width, int height)
    {
        try {
//#ifndef WITH_JAVA_DIRECT_BUFFERS
//            data = new byte[width * height * 3];
//            for ( int i = 0; i < width*height*3; i++ ) {
//                data[i] = 0;
//            }
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
          //byte arr[] = new byte[width * height * 3];
          //data = ByteBuffer.wrap(arr);
          data = ByteBuffer.allocateDirect(width * height * 3);
          data.rewind();
          for ( int i = 0; i < width*height*3; i++ ) {
              data.put((byte)0);
          }
//#endif

        }
        catch ( Exception e ) {
            data = null;
            return false;
        }
        xSize = width;        
        ySize = height;        
        return true;
    }

    /**
    Image initialize.

    Given the desired width and height, this method asigns the needed memory
    to hold such image uncompressed.

    Returns true if memory allocation succeed, false if not.
    */
    public boolean initNoFill(int width, int height)
    {
        if ( data != null && width == xSize && height == ySize ) {
            data.rewind();
            return true;
	}

        try {

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//            data = new byte[width * height * 3];
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
          //byte arr[] = new byte[width * height * 3];
          //data = ByteBuffer.wrap(arr);
          data = ByteBuffer.allocateDirect(width * height * 3);
          data.rewind();
//#endif

        }
        catch ( Exception e ) {
            data = null;
            return false;
        }
        xSize = width;        
        ySize = height;        
        return true;
    }

    /**
    This method changes the pixel information for pixel (x, y) on the
    represented image matrix, to contain the values <r, g, b>.
    */
    public void putPixel(int x, int y, byte r, byte g, byte b)
    {
        int index = (xSize*(ySize-1-y) + x)*3;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        data[index] = r;
//        data[index+1] = g;
//        data[index+2] = b;
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.position(index);
        data.put(r);
        data.put(g);
        data.put(b);
//#endif

    }

    public void putPixel(int x, int y, RGBPixel p)
    {
        int index = (xSize*(ySize-1-y) + x)*3;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        data[index] = p.r;
//        data[index+1] = p.g;
//        data[index+2] = p.b;
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.position(index);
        data.put(p.r);
        data.put(p.g);
        data.put(p.b);
//#endif

    }

    /**
    Check the general signature contract in superclass method
    Image.putPixelRgb.
    */
    public void putPixelRgb(int x, int y, RGBPixel p)
    {
        int index = (xSize*(ySize-1-y) + x)*3;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        data[index] = p.r;
//        data[index+1] = p.g;
//        data[index+2] = p.b;
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.position(index);
        data.put(p.r);
        data.put(p.g);
        data.put(p.b);
//#endif

    }

    /**
    This method returns the color component <r, g, b> contained on the pixel
    <x, y> of current image.
    */
    public RGBPixel getPixel(int x, int y)
    {
        RGBPixel p = new RGBPixel();
        int index = (xSize*(ySize-1-y) + x)*3;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        p.r = data[index];
//        p.g = data[index+1];
//        p.b = data[index+2];
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.position(index);
        p.r = data.get();
        p.g = data.get();
        p.b = data.get();
//#endif

        return p;
    }

    /**
    Check the general signature contract in superclass method
    Image.getPixelRgb.
    */
    public RGBPixel getPixelRgb(int x, int y)
    {
        RGBPixel p = new RGBPixel();
        int index = (xSize*(ySize-1-y) + x)*3;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        p.r = data[index];
//        p.g = data[index+1];
//        p.b = data[index+2];
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.position(index);
        p.r = data.get();
        p.g = data.get();
        p.b = data.get();
//#endif

        return p;
    }

    /**
    Check the general signature contract in superclass method
    Image.getPixelRgb.
    */
    public void getPixelRgb(int x, int y, RGBPixel p)
    {
        int index = (xSize*(ySize-1-y) + x)*3;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        p.r = data[index];
//        p.g = data[index+1];
//        p.b = data[index+2];
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.position(index);
        p.r = data.get();
        p.g = data.get();
        p.b = data.get();
//#endif

    }

    /**
    Check the general signature contract in superclass method
    Image.getXSize.
    */
    public int getXSize()
    {
        return xSize;
    }

    /**
    Check the general signature contract in superclass method
    Image.getYSize.
    */
    public int getYSize()
    {
        return ySize;
    }

    public byte[] getRawImage()
    {
//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        return data;
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        if ( !data.hasArray() ) {
            VSDK.reportMessage(this, VSDK.FATAL_ERROR, "getRawImage", "cannot return raw bytes for a direct buffer optimized image, use getRawImageDirectBuffer instead.");
        }
        return data.array();
//#endif

    }

    public ByteBuffer getRawImageDirectBuffer()
    {

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        return ByteBuffer.wrap(data);
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
        data.rewind();
        return data;
//#endif

    }

    public void setRawImage(int xSize, int ySize, byte[] data)
    {
        this.xSize = xSize;
        this.ySize = ySize;

//#ifndef WITH_JAVA_DIRECT_BUFFERS
//        this.data = data;
//#endif

//#ifdef WITH_JAVA_DIRECT_BUFFERS
            VSDK.reportMessage(this, VSDK.FATAL_ERROR, "setRawImage", "NOT IMPLEMENTED! CHECK VSDK CODE!");
//#endif

    }

    /** Returns a copy of current image in its own memory */
    public RGBImage clone()
    {
        RGBImage copy;
        int xSize = getXSize();
        int ySize = getYSize();
        int x, y;

        copy = new RGBImage();
        copy.init(xSize, ySize);
        for ( x = 0; x < xSize; x++ ) {
            for ( y = 0; y < ySize; y++ ) {
                copy.putPixel(x, y, getPixel(x, y));
            }
        }
        return copy;
    }

    /** Returns a copy of current image in its own memory */
    public RGBAImage cloneToRgba()
    {
        RGBAImage copy;
        int xSize = getXSize();
        int ySize = getYSize();
        int x, y;
        RGBPixel source;
        RGBAPixel target = new RGBAPixel();

        copy = new RGBAImage();
        copy.init(xSize, ySize);
        for ( x = 0; x < xSize; x++ ) {
            for ( y = 0; y < ySize; y++ ) {
                source = getPixel(x, y);
                target.r = source.r;
                target.g = source.g;
                target.b = source.b;
                copy.putPixel(x, y, target);
            }
        }
        return copy;
    }

//#ifdef WITH_JAVA_DIRECT_BUFFERS
    private void writeObject(ObjectOutputStream out) throws IOException
    {
        try {
            int x, y;

            PersistenceElement.writeIntBE(out, xSize);
            PersistenceElement.writeIntBE(out, ySize);
            byte arr[] = new byte[3];

            data.rewind();
            for ( y = 0; y < ySize; y++ ) {
                for ( x = 0; x < xSize; x++ ) {
                    arr[0] = data.get();
                    arr[1] = data.get();
                    arr[2] = data.get();
                    PersistenceElement.writeBytes(out, arr);
                }
            }
        }
        catch ( Exception e ) {
            throw new IOException("Error in custom RGBAImage writeObject");
        }
    }

    private void readObject(ObjectInputStream in) throws IOException
    {
        try {
            int x, y;

            xSize = PersistenceElement.readIntBE(in);
            ySize = PersistenceElement.readIntBE(in);

            initNoFill(xSize, ySize);
            data.rewind();

            byte arr[] = new byte[3];
            for ( y = 0; y < ySize; y++ ) {
                for ( x = 0; x < xSize; x++ ) {
                    PersistenceElement.readBytes(in, arr);
                    data.put(arr[0]);
                    data.put(arr[1]);
                    data.put(arr[2]);
                }
            }
        }
        catch ( Exception e ) {
            e.printStackTrace();
            throw new IOException("Error in custom RGBAImage readObject");
        }
    }
//#endif

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
