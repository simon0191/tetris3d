//===========================================================================
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [FOLE1992] Foley, vanDam, Feiner, Hughes. "Computer Graphics, princi-   =
//=            ples and practice" - second edition, Addison Wesley, 1992.   =
//= [.SUN2006] Java 1.5.0 API, available at http://java.sun.com             =
//= [.FONT2006] The FontForge project home page, available at               =
//=            http://fontforge.sourceforge.net/bezier.html                 =
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - April 20 2006 - Gina Chiquillo / David Camello: Original base version =
//= - April 28 2006 - Gina Chiquillo / Oscar Chavarro: quality check        =
//= - July 25 2006 - Oscar Chavarro: quality check, documentation added     =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import java.util.ArrayList;

import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.Ray;
import vsdk.toolkit.environment.geometry.Geometry;

/**
This class represents a set of curve segments, forming a poly-line.
The represented poly-line can be straight or curved, can be integral 
(continous) or broken in pieces (discontinous), and each piece can be 
open or closed. Usually, a poly-line is co-planar, all of its pieces are 
closed, and the first closed piece conforms the outer contour, while the 
remaining close pieces make holes inside the first piece.  In this scheme,
the order in which pieces and segments are defined is important.

Each segment is a parametric cubic curve with an infinite set of points in
the form <x(t), y(t), z(t)> for t in the interval [0.0, 1.0]. Only the
starting (t = 0) and ending (t = 1) points are explicity represented, 
and the remaining intermediate points can be calculated via interpolation 
using the `evaluate` method.

The behavior of each segment can vary depending of the type of its defining
control points. For each type of curve supported, there is a public constant
defined in this class that specify it, and a static matrix that represents the
interpolation equations used. 

Note that each control point is stored in the `points` ArrayList, and depending
of its type (which is stored in the `types` ArrayList), the number of 
Vector3D's that forms it varies, and its specific interpretation also varies.
Check the documentation of each identification type value constant to 
understand the exact interpretation of control points. The curve interpolation
scheme can be:
  - Reset interpolation sequence: the control point is not really a point,
    but a command that indicate to algorithms to break the poly-line in two
    pieces. Note that for this type of command, there is no Vector3D positions
    associated to array list, so the corresponding static vector is null or
    empty.
  - First order in respect to t: the curve segment is "linear", that is
    straight, the control points are "corners" between lines and each control
    point has just one Vector3D (the position of a line end)
  - Second order in respect to t: the curve segment is "quadratic"
  - Third order in respect to t: the curve segment is "cubic". There are
    multiple cubic curve interpretations based on the specific interpolation
    method used, and in specific control point interpretation.

From a design point of view, this class is similar to Sun Microsystems'
java.awt.geom.PathIterator class [.SUN2006], only that this one supports 
curves in 3D space, and not just in a 2D plane. The implementation follows the
parametric curve development derived in Chapter 11 of [FOLE1992].

@todo Document in detail the interpretation of point data for each curve type.
@todo Pending to implement some other curve types, like Catmull-Rom.
@todo Include some curve examples and its corresponding data structure in
      this documentation.
@todo Fix the definition of UNRBSPLINE_MATRIX, to have it divided by 6.
@todo Add evaluateFirstDerivative and evaluateSecondDerivative functions
*/

public class ParametricCurve extends Curve {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    // Model's "basis matrices" for evaluating curve parametric equations like
    // described in [FOLE1992].11.2.
    public static Matrix4x4 LINEAR_MATRIX = null;
    public static Matrix4x4 HERMITE_MATRIX = null;
    public static Matrix4x4 BEZIER_MATRIX = null;
    public static Matrix4x4 UNRBSPLINE_MATRIX = null;
    public static Matrix4x4 CATMULL_ROM_MATRIX = null;

    static {
        //- Base matrices initialization ----------------------------------
        // All the matrices are computed only for the first time this
        // class is instantiated. Note that this makes the Matrix4x4
        // creation efficient, but also makes this class non-re-entrant,
        // and not thread-safe.
        double[][] m;

        // This equation was derived as the answer of exercise 11.3 in
        // [FOLE1992], from equation base 11.11
        m = new double[][] { { 0.0,  0.0,  0.0,  1.0},
                             { 0.0,  0.0,  1.0,  1.0},
                             {-1.0,  1.0,  0.0,  0.0},
                             { 1.0,  0.0,  0.0,  0.0} };
        LINEAR_MATRIX = new Matrix4x4();
        LINEAR_MATRIX.M = m;

        // Equation 11.19 in [FOLE1992]
        m = new double[][] { { 2.0, -2.0,  1.0,  1.0},
                             {-3.0,  3.0, -2.0, -1.0},
                             { 0.0,  0.0,  1.0,  0.0},
                             { 1.0,  0.0,  0.0,  0.0} };
        HERMITE_MATRIX = new Matrix4x4();
        HERMITE_MATRIX.M = m;

        // Equation 11.28 in [FOLE1992]
        m = new double[][] { {-1.0,  3.0, -3.0,  1.0},
                             { 3.0, -6.0,  3.0,  0.0},
                             {-3.0,  3.0,  0.0,  0.0},
                             { 1.0,  0.0,  0.0,  0.0} };
        BEZIER_MATRIX = new Matrix4x4();
        BEZIER_MATRIX.M = m;

        // Equation 11.34 in [FOLE1992], but NOT multiplied by 1/6. (why?)
        m = new double[][] { {-1.0,  3.0, -3.0,  1.0},
                             { 3.0, -6.0,  3.0,  0.0},
                             {-3.0,  0.0,  3.0,  0.0},
                             { 1.0,  4.0,  1.0,  0.0} };
        UNRBSPLINE_MATRIX = new Matrix4x4();
        UNRBSPLINE_MATRIX.M = m;

        // Equation 11.47 in [FOLE1992], but NOT multiplied by T. (why?)
        m = new double[][] { {-0.5,  1.5, -1.5,  0.5},
                             { 1.0, -2.5,  2.0, -0.5},
                             {-0.5,  0.0,  0.5,  0.0},
                             { 0.0,  1.0,  0.0,  0.0} };
        CATMULL_ROM_MATRIX = new Matrix4x4();
        CATMULL_ROM_MATRIX.M = m;
    }

    // Constants used for identification type values for controlling points

    /// Identification type value for specifying a discontinuity in the curve.
    /// A control point of this type doesn't have any Vector3D, but uses a
    /// slot (possibly null) in the `points` attribute.
    public static final int BREAK = 1; 
    /// Identification type value for straight lines. A control point of this
    /// type has one Vector3D, representing and end of a straight line.
    public static final int CORNER = 2; 
    /// Identification type value for quadratic curves. A control point of this
    /// type has two Vector3D.
    public static final int QUAD = 3; 
    /// Identification type value for cubic curve with Hermite interpolation.
    /// A control point of this type has three Vector3D.
    public static final int HERMITE = 4;
    /// Identification type value for cubic curve with Bezier interpolation
    /// A control point of this type has three Vector3D.
    public static final int BEZIER = 5;
    /// Identification type value for cubic curve with Uniform Non Rational
    /// B Spline interpolation
    /// A control point of this type has three Vector3D.
    public static final int UNRBSPLINE = 6;
    /// Identification type value for cubic curve with Non Uniform Non 
    /// Rational B Spline interpolation
    /// A control point of this type has three Vector3D.
    public static final int NUNRBSPLINE = 7;
    /// Identification type value for cubic curve with Catmull-Rom 
    /// interpolation
    /// A control point of this type has three Vector3D.
    public static final int CATMULLROM = 8;

    // Curve model: a set of points, each having a type of data interpretation
    /// Controlling points array
    public ArrayList<Vector3D[]> points;
    /// Controlling points types array. This array always has the same
    /// dimension of the `points` array. The values always correspond to one
    /// of the identification type values defined in this class (i.e.
    /// CORNER, HERMITE, BEZIER, etc.).
    public ArrayList<Integer> types;

    // Number of steps for curve approximation
    private static final int INITIAL_APPROXIMATION_STEPS = 50;
    private int approximationSteps;

    /**
    This constructor takes care for the initialization of the matrices needed
    for the definition of the different types of curve model supported. Note
    that the matrices values are formally derived in [FOLE1992].11.2 section.
    <P>

    The resulting curve created is the empty curve.

    @todo Leave the UNRBSPLINE_MATRIX and CATMULL_ROM_MATRIX matrices 
    initialized to standard teoretical values, with their scalar constants 
    applied. This implies updating the evaluation methods.
    */
    public ParametricCurve() {
        //- Empty curve model creation ------------------------------------
        points = new ArrayList<Vector3D[]> ();
        types = new ArrayList<Integer> ();
        approximationSteps = INITIAL_APPROXIMATION_STEPS;
    }

    public int getApproximationSteps()
    {
        return approximationSteps;
    }

    public void setApproximationSteps(int n)
    {
        approximationSteps = n;
    }

    public void addPoint(Vector3D[] point, int type) {
        if ( type == BREAK && points.size() < 1 ) {
            // It has no sense to insert a break command as the first segment
            // in the curve. A `BREAK` is only inserted after a first curve
            // segment is available in the curve model.
            return;
        }
        points.add(point);
        types.add(new Integer(type));
    }

    /**
    Calculates and return the number of pieces in current curve.
    */
    public int getNumPieces()
    {
        int sum = 1, i;

        for ( i = 1; i < types.size(); i++ ) {
            if ( types.get(i).intValue() == BREAK ) {
                sum++;
            }
        }

        return sum;
    }

    public void addPointAt(Vector3D[] point, int type, int position) {
        if ( type == BREAK && position < 1 ) {
            // It has no sense to insert a break command as the first segment
            // in the curve. A `BREAK` is only inserted after a first curve
            // segment is available in the curve model.
            return;
        }
        points.add(position, point);
        types.add(position, new Integer(type));
    }

    public Vector3D[] getPoint(int pos) {
        return points.get(pos);
    }

    public int getPointSize() {
       return this.points.size();
    }

    /**
    @todo should check after removal of element 0 that if next command is a
    BREAK, that command should be also be removed.
    */
    public void removePoint(int pos) {
        points.remove(pos);
        types.remove(pos);
    }

    public void setPointAt(Vector3D[] p, int pos) {
       points.set(pos, p);
    }

    /**
    Given current ParametricCurve, this method takes the scalar 
    parameter t, which must be inside the interval [0.0, 1.0], and 
    returns the Vector3D resulting from the evaluation of the 
    segment of the polycurve ending in the segment `endingSegment`.
    The ending segment type determines the interpretation of
    interpolants.

    Returns null if current endingSegment is not a segment but a BREAK
    command.
    */
    public Vector3D evaluate(int endingSegment,  double t) {
        if ( types.get(endingSegment).intValue() == CORNER ) {
            return evaluateLinear(endingSegment, t);
        }
        else if ( types.get(endingSegment).intValue() == QUAD ) {
            return evaluateQuadratic(endingSegment, t);
        }
        else if ( types.get(endingSegment).intValue() == HERMITE ) {
            return evaluateHermite(endingSegment, t);
        }
        else if ( types.get(endingSegment).intValue() == BEZIER ) {
            return evaluateBezier(endingSegment, t);
        }
        else if ( types.get(endingSegment).intValue() == UNRBSPLINE ) {
            return evaluateBspline(endingSegment, t);
        }
        return null;
    }

    private Vector3D evaluateLinear(int nseg, double t) {
        Vector3D[] startingSegmentControl = points.get(nseg - 1);
        Vector3D[] endingSegmentControl = points.get(nseg);
        Vector3D p;

        // p1
        Vector3D result = startingSegmentControl[0];
        double vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += LINEAR_MATRIX.M[i][0] * (Math.pow(t, 3 - i));
        }
        result = result.multiply(vt);

        // p2
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += LINEAR_MATRIX.M[i][1] * (Math.pow(t, 3 - i));
        }
        p = endingSegmentControl[0];
        result = result.add(p.multiply(vt));

        // 0
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += LINEAR_MATRIX.M[i][2] * (Math.pow(t, 3 - i));
        }
        p = new Vector3D(0, 0, 0);
        result = result.add(p.multiply(vt));

        // 0
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += LINEAR_MATRIX.M[i][3] * (Math.pow(t, 3 - i));
        }
        p = new Vector3D(0, 0, 0);
        result = result.add(p.multiply(vt));

        return result;
    }

    /**
    Note that current implemented quadratic spline interpolation strategy
    is to first express the second order quadric as a third order Bezier
    spline, and later evaluate the Bezier spline, as noted on [.FONT2006].

    @todo Express this interpolation as a QUAD_MATRIX and similar scheme to
    other points in this class.
    */
    private Vector3D evaluateQuadratic(int nseg, double t)
    {
        Vector3D[] startingSegmentControl = points.get(nseg - 1);
        Vector3D[] endingSegmentControl = points.get(nseg);
        Vector3D p;

        Vector3D qp0, qp1, qp2;
        qp0 = startingSegmentControl[0];
        qp1 = endingSegmentControl[1];
        qp2 = endingSegmentControl[0];

        // p0
        Vector3D result = qp0;
        double vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += BEZIER_MATRIX.M[i][0] * (Math.pow(t, 3 - i));
        }
        result = result.multiply(vt);

        // p1
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += BEZIER_MATRIX.M[i][1] * (Math.pow(t, 3 - i));
        }
        p = qp0.add((qp1.substract(qp0)).multiply(2/3));
        result = result.add(p.multiply(vt));

        // p2
        vt = 0;

        for (int i = 0; i < 4; i++) {
            vt += BEZIER_MATRIX.M[i][2] * (Math.pow(t, 3 - i));
        }
        p = qp1.add((qp2.substract(qp0)).multiply(1/3));
        result = result.add(p.multiply(vt));

        // p3
        vt = 0;
        for (int i = 0; i < 4; i++) {
            vt += BEZIER_MATRIX.M[i][3] * (Math.pow(t, 3 - i));
        }
        p = endingSegmentControl[0];
        result = result.add(p.multiply(vt));

        return result;
    }

    private Vector3D evaluateHermite(int nseg, double t) {
        Vector3D[] startingSegmentControl = points.get(nseg - 1);
        Vector3D[] endingSegmentControl = points.get(nseg);

        // p1
        Vector3D result = startingSegmentControl[0];
        double vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += HERMITE_MATRIX.M[i][0] * (Math.pow(t, 3 - i));
        }
        result = result.multiply(vt);

        // p4
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += HERMITE_MATRIX.M[i][1] * (Math.pow(t, 3 - i));
        }
        Vector3D p = endingSegmentControl[0];
        result = result.add(p.multiply(vt));

        // r1
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += HERMITE_MATRIX.M[i][2] * (Math.pow(t, 3 - i));
        }
        p = startingSegmentControl[2];
        result = result.add(p.multiply(vt));

        // r4
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += HERMITE_MATRIX.M[i][3] * (Math.pow(t, 3 - i));
        }
        p = endingSegmentControl[1];
        result = result.add(p.multiply(vt));

        return result;
    }

    private Vector3D evaluateBezier(int nseg, double t) {
        Vector3D[] startingSegmentControl = points.get(nseg - 1);
        Vector3D[] endingSegmentControl = points.get(nseg);
        Vector3D p;

        // p1
        Vector3D result = startingSegmentControl[0];
        double vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += BEZIER_MATRIX.M[i][0] * (Math.pow(t, 3 - i));
        }
        result = result.multiply(vt);

        // p2
        vt = 0;
        for ( int i = 0; i < 4; i++ ) {
            vt += BEZIER_MATRIX.M[i][1] * (Math.pow(t, 3 - i));
        }
        p = startingSegmentControl[2];
        result = result.add(p.multiply(vt));

        // p3
        vt = 0;

        for (int i = 0; i < 4; i++) {
            vt += BEZIER_MATRIX.M[i][2] * (Math.pow(t, 3 - i));
        }
        p = endingSegmentControl[1];
        result = result.add(p.multiply(vt));

        // p4
        vt = 0;
        for (int i = 0; i < 4; i++) {
            vt += BEZIER_MATRIX.M[i][3] * (Math.pow(t, 3 - i));
        }
        p = endingSegmentControl[0];
        result = result.add(p.multiply(vt));

        return result;
    }

    private Vector3D evaluateBspline(int nseg, double t) {
        if (points.size() < 4) {
            return null;
        }
        Vector3D result = new Vector3D(0, 0, 0);
        for (int np = 0; np < 4; np++) {
            double vt = 0;
            for (int i = 0; i < 4; i++) {
                vt += UNRBSPLINE_MATRIX.M[i][np] * (Math.pow(t, 3 - i));
            }
            Vector3D p = points.get(nseg - np)[0];
            // Note the 1/6 multiplication!
            result = result.add(p.multiply(vt / 6));
        }
        return result;
    }

    private int
    calculatePointPosition(int pin)
    {
        int pout, i;

        pout = 0;
        for ( i = 0; i < types.size() && i < pin; i++ ) {
            if ( types.get(i) == BREAK ) {
                pout = -1;
            }
            else {
                pout++;
            }
        }
        return pout;
    }

    /**
    This method calculates the interpolation points for the segment that ends
    in the point `endingPointForSegment`.<P>

    Note that as evaluation is able to manage the linear/corner case under
    the same numerical schema that is used for other curve types, the polyline
    can be interpreted as multiple segments for applications requiring.
    However, in cases as simple drawing, this leads to unnecessary line
    primitives. Due to that situation, the user can specify if breaking
    rects or not.
    */
    public ArrayList<Vector3D> calculatePoints(int endingPointForSegment,
                                               boolean withBrokenRects) {
        ArrayList<Vector3D> pol = new ArrayList<Vector3D> ();

        // `relativePoint` is used to estimate current starting point
        // for curve. Starting points are -1 for break points (i.e. no curve),
        // and some curves needs previous N relative points, so this is
        // used to prevent computation of undefined curve segments.
        int relativePoint = calculatePointPosition(endingPointForSegment);

        if ( (relativePoint <= 2 &&
             types.get(endingPointForSegment) == UNRBSPLINE) 
             || relativePoint < 0 ) {
            // The Uniform Non Rational B Splines require a least 3 control
            // points, which do not form a curve segment.
            // A break control point doesn't define any points
            return pol;
        }

        if ( (types.get(endingPointForSegment) == CORNER && !withBrokenRects) 
             || relativePoint <= 0 ||
             (types.get(endingPointForSegment) == UNRBSPLINE &&
              relativePoint < 3)) {
            pol.add(points.get(endingPointForSegment - 1)[0]);
            pol.add(points.get(endingPointForSegment)[0]);
        }
        else {
            Vector3D q = evaluate(endingPointForSegment,  0);
            if (q == null) {
                return pol;
            }
            pol.add(q);
            for ( int i = 1; i <= approximationSteps; i++ ) {
                q = evaluate(endingPointForSegment, 
                             (double)i / (double)approximationSteps);
                if (q != null) {
                    pol.add(q);
                }
            }

        }
        return pol;
    }

    /**
    This method returns an aproximate minmax of current curve, based on a
    sampling (evaluation) of points.
    */
    public double[] getMinMax()
    {
        double minmax[] = new double[6];
        int i, j;

        for ( i = 0; i < 3; i++ ) minmax[i] = Double.MAX_VALUE;
        for ( ; i < 6; i++ ) minmax[i] = Double.MIN_VALUE;

        Vector3D p;

        for ( i = 1; i < types.size(); i++ ) {
            ArrayList polyline = calculatePoints(i, false);
            for ( j = 0; j < polyline.size(); j++ ) {
                p = (Vector3D)polyline.get(j);
                if ( p.x < minmax[0] ) minmax[0] = p.x;
                if ( p.y < minmax[1] ) minmax[1] = p.y;
                if ( p.z < minmax[2] ) minmax[2] = p.z;
                if ( p.x > minmax[3] ) minmax[3] = p.x;
                if ( p.y > minmax[4] ) minmax[4] = p.y;
                if ( p.z > minmax[5] ) minmax[5] = p.z;
            }
        }
        return minmax;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doContainmentTest.
    @todo Check efficiency for this implementation. Note that for the
    special application of volume rendering generation, it is better
    to provide another method, to add voxels after a path following
    over the line.
    */
    public int doContainmentTest(Vector3D p, double distanceTolerance)
    {
        int i, j;
        Vector3D vec;

        for ( i = 1; i < types.size(); i++ ) {
            if ( types.get(i).intValue() == BREAK ) {
                i++;
                continue;
            }

            // Build a polyline for approximating the [i] curve segment
            ArrayList polyline = calculatePoints(i, false);

            // Solve problem for the polyline
            for ( j = 0; j < polyline.size(); j++ ) {
                vec = (Vector3D) polyline.get(j);
                if ( VSDK.vectorDistance(vec, p) < distanceTolerance ) {
                    return LIMIT;
                }
            }
        }


        return OUTSIDE;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
