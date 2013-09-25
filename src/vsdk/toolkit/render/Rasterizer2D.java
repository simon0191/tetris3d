//===========================================================================
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [BRES1965] Bresenham, J.E. "Algorithm for computer control of a digital =
//=            plotter" IBM Syst. J. 4, 1 (1965), 25-30.                    =
//= [FOLE1992] Foley, vanDam, Feiner, Hughes. "Computer Graphics, princi-   =
//=            ples and practice" - second edition, Addison Wesley, 1992.   =
//===========================================================================

package vsdk.toolkit.render;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.ArrayListOfDoubles;
import vsdk.toolkit.common.Vertex2D;
import vsdk.toolkit.media.RGBPixel;
import vsdk.toolkit.media.Image;
import vsdk.toolkit.environment.geometry.Polygon2D;
import vsdk.toolkit.environment.geometry._Polygon2DContour;

public class Rasterizer2D extends RenderingElement
{
    /**
    This algorithm implements the Bresenham line algoritm with NO CLIPPING!
    See [BRES1965].
    Note that this is a currently naive implementation that makes use
    of double floating point arithmetic, while original Bresenham algorithm
    make use of most efficient integer line arithmetic.
    */
    public static void drawLine(Image img, int x0, int y0, int x1, int y1, RGBPixel p)
    {
        double dx, dy;
        double dxdy;
        double dydx;
        int x, y;
        double xx, yy;

        dx = (double)(x1-x0);
        dy = (double)(y1-y0);

        if ( Math.abs(dx) > VSDK.EPSILON && Math.abs(dy/dx) <= 1 && x1 > x0 ) {
            // Pendiente entre -1 y 1
            dydx = dy/dx;
            for ( x = x0, yy = (double)y0; x <= x1; x++ ) {
                y = (int)yy;
                if ( x >= 0 && x < img.getXSize() &&
                     y >= 0 && y < img.getYSize() ) {
                    img.putPixelRgb(x, y, p);
                }
                yy += dydx;
            }
          }
          else if ( Math.abs(dx) > VSDK.EPSILON && Math.abs(dy/dx) <= 1 && x1 < x0 ) {
            // Pendiente entre -1 y 1
            dydx = dy/dx;
            for ( x = x1, yy = (double)y1; x <= x0; x++ ) {
                y = (int)yy;
                if ( x >= 0 && x < img.getXSize() &&
                     y >= 0 && y < img.getYSize() ) {
                    img.putPixelRgb(x, y, p);
                }
                yy += dydx;
            }
          }
          else if ( Math.abs(dy) > VSDK.EPSILON && y1 > y0 ) {
            // Pendiente mayor a 1 o menor a -1
            dxdy = dx/dy;
            for ( y = y0, xx = (double)x0; y <= y1; y++ ) {
                x = (int)xx;
                if ( x >= 0 && x < img.getXSize() &&
                     y >= 0 && y < img.getYSize() ) {
                    img.putPixelRgb(x, y, p);
                }
                xx += dxdy;
            }
          }
          else if ( Math.abs(dy) > VSDK.EPSILON && y1 < y0 ) {
            // Pendiente mayor a 1 o menor a -1
            dxdy = dx/dy;
            for ( y = y1, xx = (double)x1; y <= y0; y++ ) {
                x = (int)xx;
                if ( x >= 0 && x < img.getXSize() &&
                     y >= 0 && y < img.getYSize() ) {
                    img.putPixelRgb(x, y, p);
                }
                xx += dxdy;
            }
          }
        ;
    }

    /**
    Draws a lines outline from a given polygon.
    */
    public static void drawPolygon(Image img, Polygon2D p, RGBPixel color)
    {
        _Polygon2DContour l;
        Vertex2D va = null;
        Vertex2D vb = null;
        int i;
        int j;

        for ( i = 0; i < p.loops.size(); i++ ) {
            for ( j = 0; j < p.loops.get(i).vertices.size() - 1; j++ ) {
                va = p.loops.get(i).vertices.get(j);
                vb = p.loops.get(i).vertices.get(j + 1);
                drawLine(img, (int)va.x, (int)va.y,
                              (int)vb.x, (int)vb.y, color);
            }
            va = vb;
            vb = p.loops.get(i).vertices.get(0);
            drawLine(img, (int)va.x, (int)va.y,
                          (int)vb.x, (int)vb.y, color);
        }
    }

    private static void fillPolygonProcessLine(Vertex2D va, Vertex2D vb,
        double h,
        ArrayListOfDoubles spanBuffer)
    {
        //-----------------------------------------------------------------
        double dx;
        double dy;
        double dxdy;
        double dydx;
        double b;
        double x;

        dx = vb.x - va.x;
        dy = vb.y - va.y;

        if ( Math.abs(dx) > VSDK.EPSILON && 
             Math.abs(dy/dx) <= 1 + VSDK.EPSILON ) {
            // Slop between -1 and 1
            dydx = dy/dx;
            b = va.y - dydx*va.x;

            if ( Math.abs(dydx) < VSDK.EPSILON ) {
                // Horizontal line case
                if ( Math.abs(va.y - h) <= 0.5 ) {
                    // Horizontal line is at "h" height
                    spanBuffer.add(va.x);
                    spanBuffer.add(vb.x);
                }
            }
            else {
                // Non-horizontal line with slope between -1 and 1
                x = (h - b) / dydx;
                if ( (va.y <= h && vb.y >= h) ||
                     (va.y >= h && vb.y <= h) ) {
                    spanBuffer.add(x);
                }
            }
        }

        if ( Math.abs(dy) > VSDK.EPSILON && 
             Math.abs(dx/dy) <= 1 ) {
            // Slop between -1 and 1 with respect to y
            dxdy = dx/dy;
            b = va.x - dxdy*va.y;

            if ( Math.abs(dxdy) < VSDK.EPSILON ) {
                // Vertical line case
                if ( (va.y <= h && vb.y >= h) ||
                     (va.y >= h && vb.y <= h)  ) {
                    // Horizontal line is at "h" height
                    spanBuffer.add(va.x);
                }
            }
            else {
                // Non-vertical line with slope between -1 and 1
                x = dxdy*h + b;
                if ( (va.y <= h && vb.y >= h) ||
                     (va.y >= h && vb.y <= h) ) {
                    spanBuffer.add(x);
                }
            }
        }
    }

    /**
    Current polygon filling rasterizer (scan-line) algorithm is a NAIVE
    implementation of the general macro-algorithm outlined at [FOLE1992].3.6.

    This implementation is working but inefficient, due to the fact that
    neither scanline coherence nor edge coherence is taken into account. This
    means that for each scanline, all polygon edges are intersected 
    analitically. This is the "brute-force technique" recommended to be
    avoided in [FOLE1992].3.6.3, but it is provided as reference to
    compare efficiency with other (clever) aproaches that makes use of
    mid-point algoritms for intersection finding, or "active edge tables"
    (AETs) for efficient edge-coherence based traversals.
    */
    public static void fillPolygon(Image img, Polygon2D p, RGBPixel color)
    {
        int x;
        int y;
        _Polygon2DContour l;
        Vertex2D va = null;
        Vertex2D vb = null;
        int i;
        int j;

        //- Calculate polygon's min-max (keep clipping border) ------------
        int minx = img.getXSize();
        int miny = img.getYSize();
        int maxx = 0;
        int maxy = 0;

        for ( i = 0; i < p.loops.size(); i++ ) {
            for ( j = 0; j < p.loops.get(i).vertices.size(); j++ ) {
                va = p.loops.get(i).vertices.get(j);
                if ( va.x < minx && va.x >= 0 ) {
                    minx = (int)va.x;
                }
                if ( va.x > maxx && va.x < img.getXSize() ) {
                    maxx = (int)va.x;
                }
                if ( va.y < miny && va.y >= 0 ) {
                    miny = (int)va.y;
                }
                if ( va.y > maxy && va.y < img.getYSize() ) {
                    maxy = (int)va.y;
                }
            }
        }

        //- Build spanbuffer (for each span) ------------------------------
        ArrayListOfDoubles spanBuffer;
        double h;

        for ( y = miny; y <= maxy; y++ ) {
            //-------------------------------------------------------------
            h = y;
            spanBuffer = new ArrayListOfDoubles(p.loops.size());

            //- Brute force - analytical search of scan line intersections 
            for ( i = 0; i < p.loops.size(); i++ ) {
                for ( j = 0; j < p.loops.get(i).vertices.size() - 1; j++ ) {
                    va = p.loops.get(i).vertices.get(j);
                    vb = p.loops.get(i).vertices.get(j + 1);
                    fillPolygonProcessLine(va, vb, h, spanBuffer);
                }
                va = vb;
                vb = p.loops.get(i).vertices.get(0);
                fillPolygonProcessLine(va, vb, h, spanBuffer);
            }

            //-------------------------------------------------------------
            int s = 0;

            //- X-order quick sort of span line intersections distances ---
            spanBuffer.sort();

            //- Scan convert current span ---------------------------------
            double xs1, xs2;
            boolean state = false;

            for ( s = 0; s < spanBuffer.size()-1; s++ ) {
                xs1 = spanBuffer.get(s);
                xs2 = spanBuffer.get(s+1);
                state = state?false:true;

                // Clipping...
                if ( xs2 < minx || xs1 > maxx ) {
                    continue;
                }
                else if ( xs2 < minx ) {
                    xs2 = minx;
                }
                if ( xs2 > maxx ) {
                    xs2 = maxx;
                }

                // Draw from xs1 to xs2 if "interior" state flagged
                for ( x = (int)xs1; state == true && x < (int)xs2; x++ ) {
                    img.putPixelRgb(x, y, color);
                }
            }

            //-------------------------------------------------------------
            //spanBuffer.reset();
            spanBuffer = null;
        }
    }

    /**
    Given the current polygon `p` and the coordinate of a pixel (x, y),
    this method gives the interpolated color `outColor`.
    */
    public static void
    fillSmoothPolygonCalculateColor(Polygon2D p, double x, double y, ColorRgb outColor)
    {
        Vertex2D va = null;
        Vertex2D vb = null;
        int i;
        int j;
        double distance;
        double totaldistance = 0.0;

        outColor.r = 0.0;
        outColor.g = 0.0;
        outColor.b = 0.0;

        for ( i = 0; i < p.loops.size(); i++ ) {
            for ( j = 0; j < p.loops.get(i).vertices.size(); j++ ) {
                va = p.loops.get(i).vertices.get(j);
                distance = 1.0/(1.0+Math.sqrt((va.x-x)*(va.x-x) + 
                                              (va.y-y)*(va.y-y)));
                totaldistance += distance;
                outColor.r += va.color.r * distance;
                outColor.g += va.color.g * distance;
                outColor.b += va.color.b * distance;
            }
        }

        outColor.r /= totaldistance;
        outColor.g /= totaldistance;
        outColor.b /= totaldistance;
    }

    /**
    Current polygon filling rasterizer (scan-line) algorithm is a NAIVE
    implementation of the general macro-algorithm outlined at [FOLE1992].3.6.

    This implementation is working but inefficient, due to the fact that
    neither scanline coherence nor edge coherence is taken into account. This
    means that for each scanline, all polygon edges are intersected 
    analitically. This is the "brute-force technique" recommended to be
    avoided in [FOLE1992].3.6.3, but it is provided as reference to
    compare efficiency with other (clever) aproaches that makes use of
    mid-point algoritms for intersection finding, or "active edge tables"
    (AETs) for efficient edge-coherence based traversals.
    */
    public static void fillSmoothPolygon(Image img, Polygon2D p)
    {
        int x;
        int y;
        _Polygon2DContour l;
        Vertex2D va = null;
        Vertex2D vb = null;
        int i;
        int j;

        //- Calculate polygon's min-max (keep clipping border) ------------
        int minx = img.getXSize();
        int miny = img.getYSize();
        int maxx = 0;
        int maxy = 0;

        for ( i = 0; i < p.loops.size(); i++ ) {
            for ( j = 0; j < p.loops.get(i).vertices.size(); j++ ) {
                va = p.loops.get(i).vertices.get(j);
                if ( va.x < minx && va.x >= 0 ) {
                    minx = (int)va.x;
                }
                if ( va.x > maxx && va.x < img.getXSize() ) {
                    maxx = (int)va.x;
                }
                if ( va.y < miny && va.y >= 0 ) {
                    miny = (int)va.y;
                }
                if ( va.y > maxy && va.y < img.getYSize() ) {
                    maxy = (int)va.y;
                }
            }
        }

        //- Build spanbuffer (for each span) ------------------------------
        ArrayListOfDoubles spanBuffer;
        double h;

        for ( y = miny; y <= maxy; y++ ) {
            //-------------------------------------------------------------
            h = y;
            spanBuffer = new ArrayListOfDoubles(p.loops.size());

            //- Brute force - analytical search of scan line intersections 
            for ( i = 0; i < p.loops.size(); i++ ) {
                for ( j = 0; j < p.loops.get(i).vertices.size() - 1; j++ ) {
                    va = p.loops.get(i).vertices.get(j);
                    vb = p.loops.get(i).vertices.get(j + 1);
                    fillPolygonProcessLine(va, vb, h, spanBuffer);
                }
                va = vb;
                vb = p.loops.get(i).vertices.get(0);
                fillPolygonProcessLine(va, vb, h, spanBuffer);
            }

            //-------------------------------------------------------------
            int s = 0;

            //- X-order quick sort of span line intersections distances ---
            spanBuffer.sort();

            //- Scan convert current span ---------------------------------
            double xs1, xs2;
            boolean state = false;

            for ( s = 0; s < spanBuffer.size()-1; s++ ) {
                xs1 = spanBuffer.get(s);
                xs2 = spanBuffer.get(s+1);
                state = state?false:true;

                // Clipping...
                if ( xs2 < minx || xs1 > maxx ) {
                    continue;
                }
                else if ( xs2 < minx ) {
                    xs2 = minx;
                }
                if ( xs2 > maxx ) {
                    xs2 = maxx;
                }

                // Draw from xs1 to xs2 if "interior" state flagged
                RGBPixel color = new RGBPixel();
                ColorRgb floatColor = new ColorRgb();

                for ( x = (int)xs1; state == true && x < (int)xs2; x++ ) {
                    // Interpolate color
                    fillSmoothPolygonCalculateColor(p, x, y, floatColor);

                    // Convert given color to pixel
                    if ( floatColor.r > 1.0 ) floatColor.r = 1.0;
                    if ( floatColor.g > 1.0 ) floatColor.g = 1.0;
                    if ( floatColor.b > 1.0 ) floatColor.b = 1.0;
                    if ( floatColor.r < 0.0 ) floatColor.r = 0.0;
                    if ( floatColor.g < 0.0 ) floatColor.g = 0.0;
                    if ( floatColor.b < 0.0 ) floatColor.b = 0.0;
                    int rr;
                    int gg;
                    int bb;

                    rr = (int)(floatColor.r * 255.0);
                    gg = (int)(floatColor.g * 255.0);
                    bb = (int)(floatColor.b * 255.0);

                    color.r = VSDK.unsigned8BitInteger2signedByte(rr);
                    color.g = VSDK.unsigned8BitInteger2signedByte(gg);
                    color.b = VSDK.unsigned8BitInteger2signedByte(bb);

                    // Write resulting pixel into image
                    img.putPixelRgb(x, y, color);
                }
            }

            //-------------------------------------------------------------
            //spanBuffer.reset();
            spanBuffer = null;
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
