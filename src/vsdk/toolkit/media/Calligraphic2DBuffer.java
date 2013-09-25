//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - November 5 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.media;

import java.util.ArrayList;

import vsdk.toolkit.common.ArrayListOfDoubles;
import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.render.Rasterizer2D;

/**
The Calligraphic2DBuffer class represents a set of elements suitable for a
vector graphics device, like calligraphic CRT, vectorized postscript
and conventional/legacy pen-plotters.

This class is to calligraphic devices like the Image class is to raster
devices.

The nature of this class is structurally 2D, so must not be treated as a
Geometry as doesn't live in 3D space. Nevertheless, could be use as an
argument or modifier for 3D Geometry, in the same way an Image could be
used as a map (i.e. texture or colormap, depthmap, bumpmap, etc).

This class doen' t impose any interpretation on coordinates, but it is
suggested that internal double coordinates be mapped to the range 
<-1, -1, -1> to <1, 1, 1>.
*/
public class Calligraphic2DBuffer extends MediaEntity {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    private ArrayListOfDoubles lineData;

    public Calligraphic2DBuffer()
    {
        lineData = new ArrayListOfDoubles(80000); // 10000 lines
    }

    /**
    Erases all of internal calligraphy contents
    */
    public void init()
    {
        lineData.clean();
    }

    /**
    Adds a 2D line to the calligraphic buffer. Note that z components in
    Vector coordinates are discarged.
    */
    public void add2DLine(Vector3D p0, Vector3D p1) {
        add2DLine(p0.x, p0.y, p1.x, p1.y);
    }

    /**
    Adds a 2D line to the calligraphic buffer.
    */
    public void add2DLine(double x0, double y0, double x1, double y1) {
        lineData.add(x0);
        lineData.add(y0);
        lineData.add(x1);
        lineData.add(y1);
        lineData.add(0);  // R
        lineData.add(0);  // G
        lineData.add(0);  // B
        lineData.add(1.0); // Width
    }

    public void get2DLine(int i, Vector3D outP0, Vector3D outP1)
    {
        outP0.x = lineData.get(8*i);
        outP0.y = lineData.get(8*i+1);
        outP0.z = 0.0;
        outP1.x = lineData.get(8*i+2);
        outP1.y = lineData.get(8*i+3);
        outP1.z = 0.0;
    }

    public int getNumLines()
    {
        return lineData.size()/8;
    }

    public void exportRgbImage(RGBImage inOutRasterViewport)
    {
        double xt = inOutRasterViewport.getXSize();
        double yt = inOutRasterViewport.getYSize();

        Vector3D e0 = new Vector3D();
        Vector3D e1 = new Vector3D();
        int x0, y0, x1, y1;
        RGBPixel pixel = new RGBPixel();

        pixel.r = (byte)255;
        pixel.g = (byte)255;
        pixel.b = (byte)255;

        for ( int j = 0; j < getNumLines(); j++ ) {
            get2DLine(j, e0, e1);
            x0 = (int)((xt-1)*((e0.x+1)/2));
            y0 = (int)((yt-1)*(1-((e0.y+1)/2)));
            x1 = (int)((xt-1)*((e1.x+1)/2));
            y1 = (int)((yt-1)*(1-((e1.y+1)/2)));
            Rasterizer2D.drawLine(inOutRasterViewport, x0, y0, x1, y1, pixel);
        }
    }

    public void finalize()
    {
        init();
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
