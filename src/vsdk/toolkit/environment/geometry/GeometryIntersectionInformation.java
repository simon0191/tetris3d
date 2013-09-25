//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 9 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.FundamentalEntity;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.media.Image;
//import vsdk.toolkit.media.NormalMap;
//import vsdk.toolkit.environment.Material;

/**
Note: contains public attributes so this structure is critical for various
performance compute intensive algorithms.
*/
public class GeometryIntersectionInformation extends FundamentalEntity {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    public Vector3D p; // Intersection point coordinates
    public Vector3D n; // Surface normal at intersection point
    public Vector3D t; // Surface tangent at intersection point
    // Note that surface binormal at intersection point must be calculated
    // by the application as the cross product (n x t).

    public double u; // Texture coordinate of intersection point
    public double v;

    // This can be null.
    //public Material material; // Internal geometry selected material

    // This can be null.
    public Image texture; // Internal geometry selected texture map

    // This can be null.
    //public NormalMap normalMap; // Internal geometry selected texture map

    public GeometryIntersectionInformation() 
    {
        p = new Vector3D();
        n = new Vector3D();
        t = new Vector3D();
        //material = null;
        texture = null;
        //normalMap = null;
    }

    public GeometryIntersectionInformation(GeometryIntersectionInformation b) 
    {
        clone(b);
    }

    public void clone(GeometryIntersectionInformation other)
    {
        this.p = new Vector3D(other.p);
        this.n = new Vector3D(other.n);
        this.u = other.u;
        this.v = other.v;
        //this.material = other.material;
        this.texture = other.texture;
        //this.normalMap = other.normalMap;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
