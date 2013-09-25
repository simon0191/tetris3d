//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.common.linealAlgebra;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.FundamentalEntity;

public class Quaternion extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    public Vector3D direction;
    public double magnitude;

    public Quaternion()
    {
        direction = new Vector3D(0, 0, 0);
        magnitude = 0;
    }

    public double length()
    {
        return magnitude * magnitude + direction.dotProduct(direction);
    }

    public void normalize()
    {
        double l;

        l = length();
        magnitude *= 1/l;
        direction = direction.multiply(1/l);
    }

    public String toString()
    {
        String msg;
        msg = direction + " / " + VSDK.formatDouble(magnitude);
        return msg;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
