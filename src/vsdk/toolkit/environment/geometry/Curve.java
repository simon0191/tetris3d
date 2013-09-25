//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - May 2 2006 - Oscar Chavarro: Original base version                    =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.Ray;

public abstract class Curve extends Geometry {
    /**
    This method is provided to ease the integration with generic operation
    Geometry.doIntersection.  A default behavior of not responding the test
    is provided here for 1-dimensional forms. Note that a Loft creation
    between current curve and a circle curve for emulating a tube like
    structure, gives as a result a 2-dimensional Surface, from which a
    Loft.doIntersection operation will give similar results to that
    expected from this 1-dimensional case. However, as real mathematical
    1-dimensional objects are infinitively thin, a doIntersection operation
    will always fail as the operation is regularized for constructive
    solid modelling compatible interpretation.
    */
    public boolean doIntersection(Ray r)
    {
        return false;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doExtraInformation.
    */
    public void
    doExtraInformation(Ray inRay, double intT, 
                                      GeometryIntersectionInformation outData)
    {
        return;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
