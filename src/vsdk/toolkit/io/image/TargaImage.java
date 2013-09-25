//===========================================================================
package vsdk.toolkit.io.image;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.awt.Dimension;
import java.awt.image.DirectColorModel;

import vsdk.toolkit.io.PersistenceElement;
import vsdk.toolkit.media.RGBImage;
import vsdk.toolkit.media.RGBAImage;

/**
The TargaImage class represents a targa image (the image files with .tga
extension)
*/

public class TargaImage extends PersistenceElement
{
    private static final int NO_TRANSPARENCY = 255;
    private static final int FULL_TRANSPARENCY = 0;
    
    private short idLength;
    private short colorMapType;
    private short imageType;
    private int cMapStart;
    private int cMapLength;
    private short cMapDepth;
    private int xOffset;
    private int yOffset;
    private int xSize;
    private int ySize;
    private short pixelDepth;
    private short imageDescriptor;
    private DirectColorModel cm;
    public byte[] pixels;

    /**
    Constructs a TGAImage given the File image
    @param srcFile The tga image file
    */
    public TargaImage(File srcFile) throws ImageNotRecognizedException {
        try
        {
            open(srcFile);
        }
        catch(IOException ioe)
        {
            throw new ImageNotRecognizedException("I/O Error while reading the image", srcFile);
        }
    }
    
    private void open(File srcFile) throws ImageNotRecognizedException, IOException {
        byte red = 0;
        byte green = 0;
        byte blue = 0;
        byte alpha = FULL_TRANSPARENCY;
        int srcLine = 0;
        
        FileInputStream fis = new FileInputStream(srcFile);
        BufferedInputStream bis = new BufferedInputStream(fis, 8192);
        DataInputStream dis = new DataInputStream(bis);
        
        idLength = (short) dis.read();
        colorMapType = (short) dis.read();
        imageType = (short) dis.read();
        cMapStart = (int) flipEndian(dis.readShort());
        cMapLength = (int) flipEndian(dis.readShort());
        cMapDepth = (short) dis.read();
        xOffset = (int) flipEndian(dis.readShort());
        yOffset = (int) flipEndian(dis.readShort());
        xSize = (int) flipEndian(dis.readShort());
        ySize = (int) flipEndian(dis.readShort());
        pixelDepth = (short) dis.read();
        
        if (pixelDepth == 24) 
        {
            cm = new DirectColorModel(24, 0xFF0000, 0xFF00, 0xFF);
            pixels = new byte[xSize * ySize*3];
        } 
        else { 
            if ( pixelDepth == 32 ) {
                cm = new DirectColorModel(32, 0xFF000000, 0xFF0000, 0xFF00, 0xFF);
                pixels = new byte[xSize * ySize*4];
            }
            else {
                System.err.println("TargaImage - cannot read file!");       
            }
        }
        
        imageDescriptor = (short) dis.read();
        
        if ( idLength > 0 ) {
            bis.skip(idLength);
        }

        //printInfo();
        
        switch ( imageType ) {
        case 1:
            throw new ImageNotRecognizedException("Cannot load color indexed uncompressed images", srcFile);
        case 2: //descomprimidas, true type
                                
            //System.out.println("Imagen descomprimida usando true color");           
                    
            //System.out.println((ySize - 1)*xSize);

            for ( int i = 0; i < (ySize - 1); i++ ) {
                srcLine = i * xSize;
                for (int j = 0; j < xSize; j++) {
                    blue = dis.readByte();
                    green = dis.readByte();
                    red = dis.readByte();
                    if ( pixelDepth == 32 ) {
                        alpha = dis.readByte();
                        pixels[srcLine*4 + 4*j + 0]=red;
                        pixels[srcLine*4 + 4*j + 1]=green;
                        pixels[srcLine*4 + 4*j + 2]=blue;
                        pixels[srcLine*4 + 4*j + 3]=alpha;
                                        
                    } 
                    else {
                        pixels[srcLine*3 + 3*j + 0]=red;
                        pixels[srcLine*3 + 3*j + 1]=green;
                        pixels[srcLine*3 + 3*j + 2]=blue;
                    }
                }
            }
            break;
                
        case 3:
            throw new ImageNotRecognizedException("Cannot load uncompressed images in black and white", srcFile);
        case 9:
            throw new ImageNotRecognizedException("Por el momento no se puede cargar imagenes comprimidas que usan color map", srcFile);
        case 10:
            System.out.println("Imagen comprimida usando true color");
            int pixel=0;
            int pixIndex=0;
            int tam = this.ySize*this.xSize;

            byte trunc=0x7f;
            byte repetitionCount;
            while( pixel < tam ) {
                repetitionCount = dis.readByte();
                if( repetitionCount <= 0 ) {       
                    // RLE PACKET
                    repetitionCount=(byte)(repetitionCount & trunc);
                    blue = dis.readByte();
                    green = dis.readByte();
                    red = dis.readByte();

                    if ( pixelDepth == 32 ) {
                        alpha = dis.readByte();
                    }
                    for ( int i = 0; i <= repetitionCount; i++ ) {
                        this.pixels[pixIndex]=red;
                        pixIndex++;
                        this.pixels[pixIndex]=green;
                        pixIndex++;
                        this.pixels[pixIndex]=blue;
                        pixIndex++;
                        if( this.pixelDepth == 32 ) {
                            this.pixels[pixIndex]=alpha;
                            pixIndex++;
                        }
                                                        
                        pixel++;
                                                        
                        if ( pixel == tam ) {
                            break;
                        }
                    }
                }
                else {
                    //RAW PACKET
                    for( int i = 0; i <= repetitionCount; i++ ) {
                        blue = dis.readByte();
                        green = dis.readByte();
                        red = dis.readByte();
                        if ( pixelDepth == 32 ) {
                            alpha = dis.readByte();
                        }
                        this.pixels[pixIndex]=red;              
                        pixIndex++;
                        this.pixels[pixIndex]=green;
                        pixIndex++;
                        this.pixels[pixIndex]=blue;
                        pixIndex++;
                        if ( pixelDepth == 32 ) {
                            this.pixels[pixIndex]=alpha;
                            pixIndex++;
                        }
                        pixel++;
                        if ( pixel == tam ) {
                            break;
                        }
                    }
                }
            }
            //System.out.println("Termino de cargar la imagen");
            break;

        case 11:
            throw new ImageNotRecognizedException("Por el momento no se puede cargar imagenes comprimidas en blanco y negro", srcFile);
        default:
            throw new ImageNotRecognizedException("No se reconoce el tipo de imagen en el archivo.", srcFile);
        }
        fis.close();
    }

    /**
    This method returns the pixel depth, which can be 24 (a red green and blue 
    component per pixel each consisting of 8 bits) or 32 (a red green, blue 
    and alpha component per pixel each consisting of 8 bits).
        
    @return the pixel depth of this targa image
    */
    public int getPixelDepth() {
        return this.pixelDepth;
    }
    
    public void printInfo() {
        System.out.println("idLength: "+idLength);      
        System.out.println("colorMapType: "+colorMapType);      
        System.out.println("imageType: "+imageType);    
        System.out.println("cMapStart: "+cMapStart);    
        System.out.println("cMapLength: "+cMapLength);  
        System.out.println("cMapDepth: "+cMapDepth);    
        System.out.println("xOffset: "+xOffset);        
        System.out.println("yOffset: "+yOffset);        
        System.out.println("xSize: "+xSize);    
        System.out.println("ySize: "+ySize);  
        System.out.println("pixelDepth: "+pixelDepth);  
        System.out.println("imageDescriptor: "+imageDescriptor);        
    }
    
    /**
    Returns the ySize in pixels of this targa image
    @return The ySize of this tagra image
    */
    public int getYSize() {
        return this.ySize;     
    }

    /**
    Returns the xSize in pixels of this targa image
    @return The xSize of this tagra image
    */
    public int getXSize() {
        return this.xSize;
    }
        
    /**
    This method returns an array of bytes that represents the raw pixel data of
    this image. If the image's ySize is 32, the xSize is 64 and the depth
    value is 32, it returns an array of 32*64*4 bytes, because it has 32*64
    pixels and 32 bits (4 bytes) of data per pixel.

    @return The pixel data for this targa image
    */
    public byte[] getTexture() {
        return this.pixels;     
    }
        
    /**
    Returns the size of this tagra image in a Dimension object
    @return The size of this targa image
    */
    public Dimension getSize() {
        return new Dimension(xSize, ySize);
    }
       
    private short flipEndian(short signedShort) {
        int input = signedShort & 0xFFFF;
        return (short) (input << 8 | (input & 0xFF00) >>> 8);
    }

    public void exportRGB(RGBImage img)
    {
        int x, y;
        byte r, g, b;
        int i = 0;

        img.init(xSize, ySize);

        for ( y = 0; y < ySize; y++ ) {
            for ( x = 0; x < xSize; x++ ) {
                if ( pixelDepth == 32 ) {
                    r = pixels[i]; i++;
                    g = pixels[i]; i++;
                    b = pixels[i]; i++;
                    i++;
                }
                else if ( pixelDepth == 24 ) {
                    r = pixels[i]; i++;
                    g = pixels[i]; i++;
                    b = pixels[i]; i++;
                }
                else {
                    r = g = b = 0;
                }
                img.putPixel(x, ySize - 1 - y, r, g, b);
            }
        }
    }

    public void exportRGBA(RGBAImage img)
    {
        int x, y;
        byte r, g, b, a;
        int i = 0;

        img.init(xSize, ySize);

        for ( y = 0; y < ySize; y++ ) {
            for ( x = 0; x < xSize; x++ ) {
                if ( pixelDepth == 32 ) {
                    r = pixels[i]; i++;
                    g = pixels[i]; i++;
                    b = pixels[i]; i++;
                    a = pixels[i]; i++;
                }
                else if ( pixelDepth == 24 ) {
                    r = pixels[i]; i++;
                    g = pixels[i]; i++;
                    b = pixels[i]; i++;
                    a = -1;
                }
                else {
                    r = g = b = 0;
                    a = -1;
                }
                img.putPixel(x, ySize - 1 - y, r, g, b, a);
            }
        }
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
