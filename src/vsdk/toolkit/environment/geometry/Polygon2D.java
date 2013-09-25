//===========================================================================

package vsdk.toolkit.environment.geometry;

import java.util.ArrayList;

import vsdk.toolkit.common.Vertex2D;
import vsdk.toolkit.common.Ray;

public class Polygon2D extends Surface
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20090816L;

    public ArrayList<_Polygon2DContour> loops;
    private _Polygon2DContour currentLoop;

    public Polygon2D()
    {
        loops = new ArrayList<_Polygon2DContour>();
        nextLoop();
    }

    public void addVertex(double x, double y, double r, double g, double b)
    {
        currentLoop.addVertex(x, y, r, g, b);
    }

    public void addVertex(double x, double y)
    {
        currentLoop.addVertex(x, y);
    }

    public void nextLoop()
    {
        currentLoop = new _Polygon2DContour();
        loops.add(currentLoop);
    }

    public double[] getMinMax()
    {
        double minMax[];
        _Polygon2DContour l;
        Vertex2D v = null;
        int i;
        int j;
        minMax = new double[6];

        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for ( i = 0; i < loops.size(); i++ ) {
            for ( j = 0; j < loops.get(i).vertices.size() - 1; j++ ) {
                v = loops.get(i).vertices.get(j);
                if ( v.x > maxX ) {
                    maxX = v.x;
                }
                if ( v.x < minX ) {
                    minX = v.x;
                }
                if ( v.y > maxY ) {
                    maxY = v.y;
                }
                if ( v.y < minY ) {
                    minY = v.y;
                }
            }
        }

        minMax[0] = minX;
        minMax[1] = minY;
        minMax[2] = 0;
        minMax[3] = maxX;
        minMax[4] = maxY;
        minMax[5] = 0;

        return minMax;
    }

    public boolean doIntersection(Ray inOut_ray)
    {
        return false;
    }

    public void
    doExtraInformation(Ray inRay, double intT, 
                       GeometryIntersectionInformation outData)
    {
        ;
    }
}
