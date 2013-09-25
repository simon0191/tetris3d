//===========================================================================

package vsdk.toolkit.common;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.ColorRgb;

public class Vertex2D extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20090816L;

    public double x;
    public double y;
    public ColorRgb color;

    public Vertex2D(double x, double y)
    {
        this.x = x;
        this.y = y;
        this.color = new ColorRgb();
    }

    public Vertex2D(double x, double y, double r, double g, double b)
    {
        this.x = x;
        this.y = y;
        this.color = new ColorRgb(r, g, b);
    }

    public String toString()
    {
        String msg = "<" + VSDK.formatDouble(x) + ", " +
            VSDK.formatDouble(y) + ">";
        return msg;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
