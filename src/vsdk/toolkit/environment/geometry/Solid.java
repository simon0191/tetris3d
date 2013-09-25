//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - May 2 2006 - Oscar Chavarro: Original base version                    =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.linealAlgebra.Vector3D;

public abstract class Solid extends Geometry {
    /**
    Given current solid, the method `doCenterOfMass` returns a vector
    containing the center of mass for current solid, assuming that all the
    solid interior is filled with a material of constant density.
    */
    public Vector3D doCenterOfMass() {
        return new Vector3D(0, 0, 0);
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
