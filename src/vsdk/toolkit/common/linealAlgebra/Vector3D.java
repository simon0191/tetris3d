//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//= - November 15 2005 - Oscar Chavarro: quality check                      =
//= - March 17 2006 - Oscar Chavarro: added exportToFloatArrayVect method   =
//===========================================================================

package vsdk.toolkit.common.linealAlgebra;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.FundamentalEntity;

/**
Class Vector3D represents a one dimensional array of three values, usually
to be interpreted as:
  - A column vector of 1x3 positions, useful in linear algebra computations.
  - A point in the R3 euclidean space
As current class is supposed to be used in the context of computer graphics,
array elements are not indiced, to say from 0 to 2, but are instead named
with the usual 3D axis labels `x`, `y` and `z`.
This is one of the most fundamental classes in VitralSDK toolkit, and its
attributes are usually accessed in the inner loops of computational intensive
calculations. As such, the attributes are promoted to be public, yes, 
breaking encaptulation and converting current class to a mere non-evolvable
structure.
Lack of get/set method enforces a direct attribute access programming style
which will lend to shorter code.
*/
public class Vector3D extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    /// Yes, they are public due to efficiency issues
    public double x, y, z;

    /**
    The default Vector3D value is the zero value
    */
    public Vector3D() {
        x = 0;
        y = 0;
        z = 0;
    }

    /**
    @param x X coordinate
    @param y Y coordinate
    @param z Z coordinate
    */
    public Vector3D(double x, double y, double z) {
        this.x = x; this.y = y; this.z = z;
    }

    public Vector3D(Vector3D B) {
        this.x = B.x; this.y = B.y; this.z = B.z;
    }

    public final void multiply(Matrix4x4 M, Vector3D E)
    {
        this.x = M.M[0][0] * E.x + M.M[0][1] * E.y + M.M[0][2] * E.z + M.M[0][3];
        this.y = M.M[1][0] * E.x + M.M[1][1] * E.y + M.M[1][2] * E.z + M.M[1][3];
        this.z = M.M[2][0] * E.x + M.M[2][1] * E.y + M.M[2][2] * E.z + M.M[2][3];
    }

    public final Vector3D multiply(double a) {
        return new Vector3D(a * x, a * y, a * z);
    }

    /**
    @param B the second vector in cross product
    @return Vector3D, the result of the operation
    */
    public final Vector3D crossProduct(Vector3D B) {
        return new Vector3D(y*B.z - z*B.y, z*B.x - x*B.z, x*B.y - y*B.x);
    }

    public final Vector3D modulate(Vector3D B) {
        return new Vector3D(x*B.x, y*B.y, z*B.z);
    }

    /**
    Make this vector internal values equal to the other's.
    */
    public final void clone(Vector3D other)
    {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
    @param B Vector3D
    @return double
    */
    public final double dotProduct(Vector3D B) {
        return (x*B.x + y*B.y + z*B.z);
    }

    /**
     *
     */
    public final void normalize() {
        double t = x*x + y*y + z*z;
        if ( Math.abs(t) < VSDK.EPSILON ) return;
        if (t != 0 && t != 1) t = (1.0 / Math.sqrt(t));
        x *= t;
        y *= t;
        z *= t;
    }

    /**
    @return double
    */
    public final double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    public final Vector3D add(Vector3D b)
    {
        return new Vector3D(x + b.x, y + b.y, z + b.z);
    }

    /**
    Stores in `this` Vector3D the result of adding the operands `a` and `b`
    */
    public final void add(Vector3D a, Vector3D b)
    {
        this.x = a.x + b.x;
        this.y = a.y + b.y;
        this.z = a.z + b.z;
    }

    public final Vector3D substract(Vector3D b)
    {
        return new Vector3D(x - b.x, y - b.y, z - b.z);
    }

    /**
    Stores in `this` Vector3D the result of substracting the operands `a` and `b`
    */
    public final void substract(Vector3D a, Vector3D b)
    {
        this.x = a.x - b.x;
        this.y = a.y - b.y;
        this.z = a.z - b.z;
    }

    public float[] exportToFloatArrayVect()
    {
        float[] ret={(float)x, (float)y, (float)z, 1};
        return ret;
    }

    public double[] exportToDoubleArrayVect()
    {
        double[] ret={x, y, z, 1};
        return ret;
    }

    /**
    Provides an object to text report convertion, optimized for human
    readability and debugging. Do not use for serialization or persistence
    purposes.
    */
    public String toString()
    {
        String msg;

        msg = "<" + VSDK.formatDouble(x) + ", " + VSDK.formatDouble(y) +
              ", " + VSDK.formatDouble(z) + ">";

        return msg;
    }

    /**
    Taking current vector as tail-anchored to the origin, this method
    calculates the theta angle (in radians) of the tip, corresponding
    to tip coordinate <x, y, z>, in spheric coordinates <r, theta, phi>.
    Note that theta goes from 0 to 2*PI, and correspond to an axis of
    rotation <0, 0, 1>.
    POST: 0 <= theta <= 2*PI
    */
    public double obtainSphericalThetaAngle()
    {
        double val;
        if ( Math.abs(x) > VSDK.EPSILON ) {
            if ( x > 0 ) val = Math.atan(y/x);
            else val = Math.PI + Math.atan(y/x);
        }
        else if ( y > 0 ) {
            val = Math.PI/2;
        }
        else {
            val = Math.PI + Math.PI/2;
        }
        while ( val < 0 ) val += 2*Math.PI;
        while ( val > 2*Math.PI ) val -= 2*Math.PI;
        return val;
    }

    /**
    Taking current vector as tail-anchored to the origin, this method
    calculates the phi angle (in radians) of the tip, corresponding
    to tip coordinate <x, y, z>, in spheric coordinates <r, theta, phi>.
    Note phi goes from 0 to PI.
    */
    public double obtainSphericalPhiAngle()
    {
        double r = length();

        if ( r < VSDK.EPSILON ) return 0;

        return Math.acos(z/r);
    }

    /**
    Given a point <r, theta, phi> in spherical coordinates, this method sets
    current vector to corresponding cartesian coordinates <x, y, z>. Angles
    are in radians.
    */
    public void setSphericalCoordinates(double r, double theta, double phi)
    {
        x = r * Math.sin(phi) * Math.cos(theta);
        y = r * Math.sin(phi) * Math.sin(theta);
        z = r * Math.cos(phi);
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
