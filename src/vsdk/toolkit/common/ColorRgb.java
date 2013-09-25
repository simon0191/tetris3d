//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - September 15 2005 - David Diaz: Original base version                 =
//= - November 1 2005 - Oscar Chavarro: Quality check - comments added      =
//= - November 15 2005 - Oscar Chavarro: set/get methods added              =
//= - March 17 2006 - Oscar Chavarro: added toFloatVect method              =
//===========================================================================

package vsdk.toolkit.common;

import vsdk.toolkit.common.VSDK;

/**
Respect to data representation:

The `r`, `g`, and `b` class attributes represent red, green and blue 
components in a color specification, with values in the range [0, inf) when
used in High Dinamic Range Imaginery (HDRI). Note that no restriction as been
specified regarding to units to be used, and as of this revision the units
must be application defined. When not used in HDRI, the values must be
application-clamped to the range [0, 1]. A value of 0 always will represent
'no contribution' or 'black', and a value of 1 will be 'white' in non HDRI
applications. Interpretation in HDRI applications is pending to be defined.

Note that the `r`, `g`, and `b` class attributes are PUBLIC, converting this
class in an not evolvable structure, and IT MUST BE KEEP AS IS, due to
performance issues in a lot of algorithms, as this avoids indirections.
Nevertheless, get and set methods are provided.
*/
public class ColorRgb extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    /// Contains the red component of a color 
    public double r;
    public double g;
    public double b;

    /**
    Note that default assumed color in the toolkit is black. It is
    important to note that changing this default could impact some
    algorithms. Do not change it.
    */
    public ColorRgb()
    {
        r = 0;
        g = 0;
        b = 0;
    }

    /**
    This constructor builds a ColorRgb from another one.
    */
    public ColorRgb(ColorRgb c)
    {
        r = c.r;
        g = c.g;
        b = c.b;
    }

    /**
    This constructor builds a ColorRgb from individual component values.
    */
    public ColorRgb(double r, double g, double b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    /**
    Given other color, this color gets combined with the another one.
    */
    public void modulate(ColorRgb other)
    {
        /*
        this.r = (this.r + other.r) / 2.0;
        this.g = (this.g + other.g) / 2.0;
        this.b = (this.b + other.b) / 2.0;
        */
        this.r = (this.r * other.r);
        this.g = (this.g * other.g);
        this.b = (this.b * other.b);
    }

    /** This method returns a copy of the value r. Note that this method does
    NOT constitues an encapsulation of the value, as the original attribute
    is public. This method is supplied for puritans that like to see a lot
    of long get/set code, and for testing the performance of different
    algorithms, as the access technique is changed between the direct access
    to the attribute and this intermediated use of get/set methods. */
    public double getR()
    {
        return r;
    }

    /** This method returns a copy of the value g. Note that this method does
    NOT constitues an encapsulation of the value, as the original attribute
    is public. This method is supplied for puritans that like to see a lot
    of long get/set code, and for testing the performance of different
    algorithms, as the access technique is changed between the direct access
    to the attribute and this intermediated use of get/set methods. */
    public double getG()
    {
        return g;
    }

    /** This method returns a copy of the value b. Note that this method does
    NOT constitues an encapsulation of the value, as the original attribute
    is public. This method is supplied for puritans that like to see a lot
    of long get/set code, and for testing the performance of different
    algorithms, as the access technique is changed between the direct access
    to the attribute and this intermediated use of get/set methods. */
    public double getB()
    {
        return b;
    }

    /** This method sets the value r, as a copy of the parameter. Note that 
    this method does NOT constitues an encapsulation of the value, as the 
    original attribute is public. This method is supplied for puritans that
    like to see a lot of long get/set code, and for testing the performance of
    different algorithms, as the access technique is changed between the direct
    access to the attribute and this intermediated use of get/set methods. */
    public void setR(double r)
    {
        this.r = r;
    }

    /** This method sets the value g, as a copy of the parameter. Note that 
    this method does NOT constitues an encapsulation of the value, as the 
    original attribute is public. This method is supplied for puritans that
    like to see a lot of long get/set code, and for testing the performance of
    different algorithms, as the access technique is changed between the direct
    access to the attribute and this intermediated use of get/set methods. */
    public void setG(double g)
    {
        this.g = g;
    }

    /** This method sets the value b, as a copy of the parameter. Note that 
    this method does NOT constitues an encapsulation of the value, as the 
    original attribute is public. This method is supplied for puritans that
    like to see a lot of long get/set code, and for testing the performance of
    different algorithms, as the access technique is changed between the direct
    access to the attribute and this intermediated use of get/set methods. */
    public void setB(double b)
    {
        this.b = b;
    }

    /**
    This method exports the color components to an static array of float
    values. It is supposed to help operations in APIs like OpenGL/JOGL where
    this representation form is commonly used.
    */
    public float[] exportToFloatArrayVect()
    {
        float[] ret={(float)r, (float)g, (float)b, 1};
        return ret;
    }

    public double[] exportToDoubleArrayVect()
    {
        double[] ret={r, g, b, 1};
        return ret;
    }

    /**
    This method return a String representation of current color. In its
    current implementation it is biased for human readability, not for
    precision, so the use of an approximation formating.
    */
    public String toString()
    {
        return "<" + VSDK.formatDouble(r) + ", " + 
                     VSDK.formatDouble(g) + ", " + 
                     VSDK.formatDouble(b) + ">";
    }

    /**
    Given current color space (RGB coordinates), this method returns the
    euclidean distance between two points in such space: `this` and `other`.
    */
    public double distance(ColorRgb other) {
        return Math.sqrt((this.r - other.r)*(this.r - other.r) +
                         (this.g - other.g)*(this.g - other.g) +
                         (this.b - other.b)*(this.b - other.b));
    }

    public void clone(ColorRgb other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
    }

    /**
    Given a 8 bit signed RGB integer triplet, this method computes the
    corresponding [0.0, 1.0] clamped interval values for color.
    */
    public void importFromSigned8bitPixel(byte r, byte g, byte b)
    {
	this.r = ((double)VSDK.signedByte2unsignedInteger(r)) / 255.0;
	this.g = ((double)VSDK.signedByte2unsignedInteger(g)) / 255.0;
	this.b = ((double)VSDK.signedByte2unsignedInteger(b)) / 255.0;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
