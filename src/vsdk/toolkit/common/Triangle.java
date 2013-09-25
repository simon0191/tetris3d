//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Gabriel Sarmiento / Lina Rojas: Original base version =
//= - November 19 2006 - Oscar Chavarro: re-structured and tested           =
//===========================================================================

package vsdk.toolkit.common;
import vsdk.toolkit.common.linealAlgebra.Vector3D;

public class Triangle extends FundamentalEntity 
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    public int p0;
    public int p1;
    public int p2;

    /**
     */
    public Vector3D normal;

    /**
     */
    public Triangle() {
        normal = new Vector3D(0, 0, 0);
    }

    /**
     */
    public Triangle(int p0, int p1, int p2) {
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        normal = new Vector3D(0, 0, 0);
    }

    /**
     */
    public int getPoint0() {
        return this.p0;
    }

    /**
     */
    public int getPoint1() {
        return this.p1;
    }

    /**
     */
    public int getPoint2() {
        return this.p2;
    }

    /**
     */
    public void setPoint0(int p0) {
        this.p0 = p0;
    }

    /**
     */
    public void setPoint1(int p1) {
        this.p1 = p1;
    }

    /**
     */
    public void setPoint2(int p2) {
        this.p2 = p2;
    }

    /**
    Provides an object to text report convertion, optimized for human
    readability and debugging. Do not use this method for serialization
    or persistence purposes.
    */
    public String toString() {

        return "f < " + p0 + ", " + p1 + ", " + p2 + " >";
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
