//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 13 2006 - Oscar Chavarro: Original base version              =
//= - August 7 2006 - Oscar Chavarro: Accounting functionality started      =
//===========================================================================

package vsdk.toolkit.common;

import java.text.DecimalFormat;
import java.text.FieldPosition;

import vsdk.toolkit.common.linealAlgebra.Vector3D;

/**
\mainpage Vitral SDK Toolkit documentation.

Vitral SDK stands for "VITRAL Software Development Kit", and it is a software
platform for computer graphics, virtual reality and augmented reality
application development. Here is the main software documentation for current
Vitral SDK implementation.  VITRAL is a trademark of the TAKINA research
group, and identifies the computer graphics laboratory in the Pontificia
Universidad Javeriana of Bogot&aacute; Colombia.

\section intro Introduction: the Vitral SDK Architecture and design specification

This is an automatically generated page from the Vitral SDK source code. It is not
editable.  You can browse the literate programming style documentation, but
remember to take a look first at the document describing the Vitral SDK toolkit:<P>

<A HREF="../ArchitectureAndDesignSpecification.html">Architecture and design specification of the Vitral SDK toolkit</A>.

\section model The Vitral SDK toolkit data model

At some point in the future, the data model implemented on the toolkit will be 
described in an external document, and that document will be linked here. At
the moment such a detailed description is not available.

\section next What to do next?

The recomended steps are: 1. Install the Vitral SDK toolkit. 2. Take a look at the
samples in the testsuite directory. 3. Have this API documentation available
and look at specific method description for help. 4. Have in hand a good
computer graphics textbook.

Some of the samples in the testsuite directory are also available as applets,
and are currently viewable here (you need a web browser with a java plugin
version 1.5 or newer):

<A HREF="../applets_index.html">Applets examples for the Vitral SDK toolkit</A>.

\section VSDK The vsdk.toolkit.common.VSDK utility class

All classes in the Vitral SDK toolkit have access to an static class called VSDK,
which is governed by the SINGLETON design pattern behavior. That is, it is
an static class that lives only once per virtual machine.  Some operations
in this class are not thread safe, so thread partitioning of Vitral SDK toolkit
based applications is not recommended. Instead, heavy process partition is
recommended, as specified by the Vitral SDK framework.

The VSDK class provides the following services:
  - It contains common mathematical constants used by Vitral SDK, but not
    defined in Java's Math class
  - It provides common mathematical methods useful for Vitral SDK, not
    defined in Java's Math class, as such vector distance, data type
    conversions and data formatting, tuned for Vitral SDK
  - It provides a basic error reporting mechanism, for easing the
    error handling and internationalization of messages
  - It provides to a heavy process (i.e. a JVM) the ability to count
    primitives and ray intersection on various types of objects. This
    service is to be used by rendering operations (specially those on
    toolkit.render and toolkit.environment.geometry packages), useful
    for benchmarking purposes
*/

public class VSDK
{
    // Common mathematical constants
    public static final double EPSILON = 1e-6;

    // Error reporting levels
    public static final int WARNING = 1;
    public static final int ERROR = 2;
    public static final int FATAL_ERROR = 3;
    public static final int DEBUG = 4;
    public static final int VERBOSE = 5;

    // Primitive types
    public static final int POINT = 0;
    public static final int LINE = 1;
    public static final int TRIANGLE = 2;
    public static final int TRIANGLE_STRIP = 3;
    public static final int QUAD = 4;
    public static final int QUAD_STRIP = 5;
    public static final int PRIMITIVE_TYPE_COUNT = 6;

    // Ray intersection count types
    public static final int PLANE = 0;
    public static final int SPHERE = 1;
    public static final int CONE = 2;
    public static final int INTERSECTION_TYPE_COUNT = 3;

    // Sizeof sizes
    public static final int sizeofInt = 4;
    public static final int sizeofReference = 4;

    // Primitive accounting data structures (not thread safe)
    private static int primitiveCount[];
    private static int intersectionCount[];

    static {
        primitiveCount = new int[PRIMITIVE_TYPE_COUNT];
        intersectionCount = new int[INTERSECTION_TYPE_COUNT];
        resetPrimitiveCounters();
        resetIntersectionCounters();
    }

    public static void resetPrimitiveCounters()
    {
        int i;

        for ( i = 0; i < PRIMITIVE_TYPE_COUNT; i++ ) {
            primitiveCount[i] = 0;
        }
    }

    public static void resetIntersectionCounters()
    {
        int i;

        for ( i = 0; i < INTERSECTION_TYPE_COUNT; i++ ) {
            intersectionCount[i] = 0;
        }
    }

    public static void acumulatePrimitiveCount(int type, int count)
    {
        primitiveCount[type] += count;
    }

    public static void acumulateIntersectionCount(int type, int count)
    {
        intersectionCount[type] += count;
    }

    public static int getPrimitiveCount(int type)
    {
        return primitiveCount[type];
    }

    public static int getIntersectionCount(int type)
    {
        return intersectionCount[type];
    }

    public static boolean equals(double a, double b)
    {
        if ( Math.abs(a - b) < EPSILON ) {
            return true;
        }
        return false;
    }

    public static double vectorDistance(Vector3D a, Vector3D b)
    {
        return Math.sqrt((a.x-b.x)*(a.x-b.x) + 
                         (a.y-b.y)*(a.y-b.y) +
                         (a.z-b.z)*(a.z-b.z));
    }

    public static double colorDistance(ColorRgb a, ColorRgb b)
    {
        return Math.sqrt((a.r-b.r)*(a.r-b.r) + 
                         (a.g-b.g)*(a.g-b.g) +
                         (a.b-b.b)*(a.b-b.b));
    }

    public static double square(double a)
    {
        return a*a;
    }

    public static String formatNumberWithinZeroes(long a, int n)
    {
        int i;
        String z = "";

	for ( i = 0; i < n; i++ ) {
	    z += "0";
	}

        DecimalFormat f1 = new DecimalFormat(z);
        return f1.format(a, new StringBuffer(""), new FieldPosition(0)).toString();
    }

    /**
    Given a double number, it formats it to print in a given precision
    */
    public static String formatDouble(double a)
    {
        DecimalFormat f = new DecimalFormat("0.00");

        return f.format(a, new StringBuffer(""), new FieldPosition(0)).toString();
    }

    public static String formatDouble(double a, int digits)
    {
        int i;
        String ff = "0.";
        for ( i = 0; i < digits; i++ ) {
            ff = ff + "0";
        }
        DecimalFormat f = new DecimalFormat(ff);

        return f.format(a, new StringBuffer(""), new FieldPosition(0)).toString();
    }

    /**
    Given a byte, it formats it to print as two hexagesimal nibbles
    */
    public static String formatByteAsHex(byte a)
    {
        char guarismos[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int i = signedByte2unsignedInteger(a);
        byte nibbleH = (byte)(i >> 4), nibbleL = (byte)(i & 0x0F);

        return "" + guarismos[nibbleH] + guarismos[nibbleL];
    }

    /**
    Given an integer, it formats it to print as two hexagesimal nibbles
    */
    public static String formatIntAsHex(int a)
    {
        char guarismos[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        String msg = "";
        byte b;

        b = (byte)((a & 0xFF000000) >> 24);
        msg += formatByteAsHex(b);
        b = (byte)((a & 0x00FF0000) >> 16);
        msg += formatByteAsHex(b);
        b = (byte)((a & 0x0000FF00) >> 8);
        msg += formatByteAsHex(b);
        b = (byte)((a & 0x000000FF));
        msg += formatByteAsHex(b);

        return msg;
    }

    /** Converts integers in the domain [-128, 127] to integers in the range
    [0, 256] */
    public static int signedByte2unsignedInteger(byte in) {
        int a;

        a = (int)in;
        if ( a < 0 ) a += 256;
        return a;

    }

    public static byte unsigned8BitInteger2signedByte(int in) {
        if ( in > 255 ) in = 255;
        if ( in < 0 ) in = 0;
        if ( in > 127 ) in -= 256;
        return (byte)in;
    }

    public static void reportMessage(Object o, int level, String method, String message)
    {
        String msg;

        msg = "===========================================================================\n";
        msg = msg + "= VSDK Exception report                                                   =\n";
        if ( o != null ) {
            msg = msg + " - An exception has been thrown in the \"" + o.getClass().getName() + "\" class\n";
        }
        else {
            msg = msg + " - An exception has been thrown from a static context\n";
        }
        msg = msg + " - Exception located at method " + method + "\n";
        msg = msg + " - Exception message:\n" + message + "\n";
        msg = msg + "===========================================================================\n";
        if ( level == FATAL_ERROR ) {
            msg = msg + "Program excecution suspended!\n";
        }

        System.err.println(msg);

        if ( level == FATAL_ERROR ) {
            try {
                throw new Exception("VSDK.reportMessage(FATAL_ERROR)");
            }
            catch ( Exception e ) {
                e.printStackTrace();
            }
            System.exit(1);
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
