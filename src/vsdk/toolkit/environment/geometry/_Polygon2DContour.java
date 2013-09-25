//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.FundamentalEntity;
import vsdk.toolkit.common.Vertex2D;

import java.util.ArrayList;

public class _Polygon2DContour extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20090816L;

    public ArrayList<Vertex2D> vertices;

    public _Polygon2DContour()
    {
        vertices = new ArrayList<Vertex2D>();
    }

    public void addVertex(double x, double y, double r, double g, double b)
    {
        vertices.add(new Vertex2D(x, y, r, g, b));
    }

    public void addVertex(double x, double y)
    {
        vertices.add(new Vertex2D(x, y));
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
