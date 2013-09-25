//===========================================================================
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [WAYN1990] Knapp Wayne. "Ray with Bicubic Patch Intersection Problem",  =
//=            Ray Tracing News, volume 3, number 3, july 13 1990.          =
//=            available at                                                 =
//=         http://jedi.ks.uiuc.edu/~johns/raytracer/rtn/rtnv3n3.html#art19 =
//= [FOLE1992] Foley, vanDam, Feiner, Hughes. "Computer Graphics, princi-   =
//=            ples and practice" - second edition, Addison Wesley, 1992.   =
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - April 27 2006 - Gina Chiquillo / David Camello: Original base version =
//= - April 28 2006 - Gina Chiquillo / Oscar Chavarro: quality check        =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import java.util.ArrayList;

import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.Ray;
import vsdk.toolkit.environment.geometry.Geometry;

public class ParametricBiCubicPatch extends Surface {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    public Matrix4x4 Gx_MATRIX = new Matrix4x4();
    public Matrix4x4 Gy_MATRIX = new Matrix4x4();
    public Matrix4x4 Gz_MATRIX = new Matrix4x4();

    private Matrix4x4 S_MATRIX;
    private Matrix4x4 Tt_MATRIX;
    private Matrix4x4 S_MATRIX_DS;
    private Matrix4x4 Tt_MATRIX_DT;
    private Matrix4x4 M_MATRIX;
    private Matrix4x4 Mt_MATRIX;
    private Matrix4x4 M_Gx_Mt_MATRIX;
    private Matrix4x4 M_Gy_Mt_MATRIX;
    private Matrix4x4 M_Gz_Mt_MATRIX;

    public static final int FERGUSON = 7;

    /// Note that the contourCurve must have 4 points with its respective
    /// control parameters.
    public ParametricCurve contourCurve;
    private Vector3D controlMeshPoints[][];
    public int type;

    // Number of steps for curve approximation
    private static final int INITIAL_APPROXIMATION_STEPS = 12;
    private int approximationSteps;

    public ParametricBiCubicPatch() {
        approximationSteps = INITIAL_APPROXIMATION_STEPS;
        this.type = ParametricCurve.HERMITE;
        contourCurve = null;
        S_MATRIX = null;
        Tt_MATRIX = null;
        S_MATRIX_DS = null;
        Tt_MATRIX_DT = null;
        M_MATRIX = null;
        Mt_MATRIX = null;
        M_Gx_Mt_MATRIX = null;
        M_Gy_Mt_MATRIX = null;
        M_Gz_Mt_MATRIX = null;
    }

    /**
    Given a contour curve, this constructor builds a corresponding
    FERGUSON type patch.

    PRE: Contour curve must has at least 4 control points. The first
    4 control points (the only one used) must be of type HERMITE.

    Note that a Ferguson patch is an Hermite patch with zero valued
    twist vectors.
    */
    public void buildFergusonPatch(ParametricCurve curve) {
        this.contourCurve = curve;
        approximationSteps = INITIAL_APPROXIMATION_STEPS;
        this.type = FERGUSON;
        calculateMatrices();
    }

    public void buildBezierPatch(Vector3D controlMeshPoints[][])
    {
        this.controlMeshPoints = controlMeshPoints;
        approximationSteps = INITIAL_APPROXIMATION_STEPS;
        this.type = ParametricCurve.BEZIER;
        calculateMatrices();
    }

    /**
    PRE: Some of the "build*Patch" methods should be called before calling
    this method.
    */
    private void calculateMatrices()
    {
        //- Build matrices M, Gx, Gy, Gz and Mt ---------------------------
        if ( this.type == ParametricCurve.BEZIER ) {
            buildGeometryMatricesXYZ_Bezier();
            M_MATRIX = ParametricCurve.BEZIER_MATRIX;
        }
        else if ( this.type == ParametricCurve.HERMITE ) {
            buildGeometryMatricesXYZ_Hermite();
            M_MATRIX = ParametricCurve.HERMITE_MATRIX;
        }
        else if ( this.type == ParametricBiCubicPatch.FERGUSON ) {
            buildGeometryMatricesXYZ_Ferguson();
            M_MATRIX = ParametricCurve.HERMITE_MATRIX;
        }
        Mt_MATRIX = new Matrix4x4(M_MATRIX);
        Mt_MATRIX.transpose();
        M_Gx_Mt_MATRIX = M_MATRIX.multiply(Gx_MATRIX).multiply(Mt_MATRIX);
        M_Gy_Mt_MATRIX = M_MATRIX.multiply(Gy_MATRIX).multiply(Mt_MATRIX);
        M_Gz_Mt_MATRIX = M_MATRIX.multiply(Gz_MATRIX).multiply(Mt_MATRIX);
        S_MATRIX = new Matrix4x4();
        Tt_MATRIX = new Matrix4x4();
        S_MATRIX_DS = new Matrix4x4();
        Tt_MATRIX_DT = new Matrix4x4();
    }

    public int getApproximationSteps() {
        return approximationSteps;
    }

    public void setApproximationSteps(int n) {
        approximationSteps = n;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        type = type;
    }

    private void buildGeometryMatricesXYZ_Bezier() {
        double[][] mx = new double[4][4];
        double[][] my = new double[4][4];
        double[][] mz = new double[4][4];
        int i;
        int j;

        for ( i = 0; i < 4; i++ ) {
            for ( j = 0; j < 4; j++ ) {
                Vector3D vp = controlMeshPoints[i][j];
                mx[i][j] = vp.x;
                my[i][j] = vp.y;
                mz[i][j] = vp.z;
            }
        }
        Gx_MATRIX.M = mx;
        Gy_MATRIX.M = my;
        Gz_MATRIX.M = mz;
        //printGeometryMatrices();
    }

    private void buildGeometryMatricesXYZ_Hermite() {
        double[][] mx = new double[4][4];
        double[][] my = new double[4][4];
        double[][] mz = new double[4][4];

        /* The upper-left 2x2 matrix portion of Gh contains the x coordinates
           of the four corners of the patch.
           The upper-right and lower-left 2x2 matrix portion of Gh contains
           the x coordiates of the tangent vectors four corners of the patch.
        */
        int p = 0;
        int i = 0;
        for (int j = 0; j < 2; j++) {
            Vector3D[] vp = contourCurve.getPoint(p);

            mx[i][j] = vp[0].x;
            my[i][j] = vp[0].y;
            mz[i][j] = vp[0].z;

            mx[i][j + 2] = vp[2 - j].x;
            my[i][j + 2] = vp[2 - j].y;
            mz[i][j + 2] = vp[2 - j].z;

            mx[i + 2][j] = vp[1 + j].x;
            my[i + 2][j] = vp[1 + j].y;
            mz[i + 2][j] = vp[1 + j].z;

            Vector3D vn = new Vector3D(0, 0, 0);

            mx[i + 2][j + 2] = vn.x;
            my[i + 2][j + 2] = vn.y;
            mz[i + 2][j + 2] = vn.z;
            p++;
        }
        p = 2;
        i = 1;
        for (int j = 0; j < 2; j++) {
            Vector3D[] vp = contourCurve.getPoint(p);

            mx[i][j] = vp[0].x;
            my[i][j] = vp[0].y;
            mz[i][j] = vp[0].z;

            mx[i][j + 2] = vp[j + 1].x;
            my[i][j + 2] = vp[j + 1].y;
            mz[i][j + 2] = vp[j + 1].z;

            mx[i + 2][j] = vp[2 - j].x;
            my[i + 2][j] = vp[2 - j].y;
            mz[i + 2][j] = vp[2 - j].z;

            Vector3D vn = new Vector3D(0, 0, 0);

            mx[i + 2][j + 2] = vn.x;
            my[i + 2][j + 2] = vn.y;
            mz[i + 2][j + 2] = vn.z;
            p++;

        }

        Gx_MATRIX.M = mx;
        Gy_MATRIX.M = my;
        Gz_MATRIX.M = mz;
    }

    private void printGeometryMatrices()
    {
        double[][] mx = Gx_MATRIX.M;
        double[][] my = Gy_MATRIX.M;
        double[][] mz = Gz_MATRIX.M;

        System.out.printf("[ <%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> | " +
                          "<%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> ]\n",
                          mx[0][0], my[0][0], mz[0][0],
                          mx[0][1], my[0][1], mz[0][1],
                          mx[0][2], my[0][2], mz[0][2],
                          mx[0][3], my[0][3], mz[0][3]
        );
        System.out.printf("[ <%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> | " +
                          "<%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> ]\n",
                          mx[1][0], my[1][0], mz[1][0],
                          mx[1][1], my[1][1], mz[1][1],
                          mx[1][2], my[1][2], mz[1][2],
                          mx[1][3], my[1][3], mz[1][3]
        );
        System.out.printf("[ <%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> | " +
                          "<%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> ]\n",
                          mx[2][0], my[2][0], mz[2][0],
                          mx[2][1], my[2][1], mz[2][1],
                          mx[2][2], my[2][2], mz[2][2],
                          mx[2][3], my[2][3], mz[2][3]
        );
        System.out.printf("[ <%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> | " +
                          "<%.2f, %.2f, %.2f> | <%.2f, %.2f, %.2f> ]\n",
                          mx[3][0], my[3][0], mz[3][0],
                          mx[3][1], my[3][1], mz[3][1],
                          mx[3][2], my[3][2], mz[3][2],
                          mx[3][3], my[3][3], mz[3][3]
        );
    }

    /**
    @todo verify that current contour curve have at least 4 control points
    and that first 4 control points are of type HERMITE.
    */
    private void buildGeometryMatricesXYZ_Ferguson() {
        double[][] mx = new double[4][4];
        double[][] my = new double[4][4];
        double[][] mz = new double[4][4];
        Vector3D[] vp00 = contourCurve.getPoint(0);
        Vector3D[] vp10 = contourCurve.getPoint(1);
        Vector3D[] vp11 = contourCurve.getPoint(2);
        Vector3D[] vp01 = contourCurve.getPoint(3);

        // Positions with respect to contour curve
        mx[0][0] = vp00[0].x;
        my[0][0] = vp00[0].y;
        mz[0][0] = vp00[0].z;
        mx[0][1] = vp01[0].x;
        my[0][1] = vp01[0].y;
        mz[0][1] = vp01[0].z;
        mx[1][0] = vp10[0].x;
        my[1][0] = vp10[0].y;
        mz[1][0] = vp10[0].z;
        mx[1][1] = vp11[0].x;
        my[1][1] = vp11[0].y;
        mz[1][1] = vp11[0].z;

        // Partial derivatives with respect to S direction
        // For Hermite contour
        mx[2][0] = (vp00[2].x);
        my[2][0] = (vp00[2].y);
        mz[2][0] = (vp00[2].z);
        mx[2][1] = -(vp01[1].x);
        my[2][1] = -(vp01[1].y);
        mz[2][1] = -(vp01[1].z);
        mx[3][0] = (vp10[1].x);
        my[3][0] = (vp10[1].y);
        mz[3][0] = (vp10[1].z);
        mx[3][1] = -(vp11[2].x);
        my[3][1] = -(vp11[2].y);
        mz[3][1] = -(vp11[2].z);

        // Partial derivatives with respect to T direction
        // For Hermite contour
        mx[0][2] = -(vp00[1].x);
        my[0][2] = -(vp00[1].y);
        mz[0][2] = -(vp00[1].z);
        mx[0][3] = -(vp01[2].x);
        my[0][3] = -(vp01[2].y);
        mz[0][3] = -(vp01[2].z);
        mx[1][2] = (vp10[2].x);
        my[1][2] = (vp10[2].y);
        mz[1][2] = (vp10[2].z);
        mx[1][3] = (vp11[1].x);
        my[1][3] = (vp11[1].y);
        mz[1][3] = (vp11[1].z);

        // Ferguson patch: twist vectors (second order partial derivatives)
        // are all 0, as noted on [FOLE1992].11.3.1, equation [FOLE1992].11.84
        mx[2][2] = 0;
        my[2][2] = 0;
        mz[2][2] = 0;
        mx[2][3] = 0;
        my[2][3] = 0;
        mz[2][3] = 0;
        mx[3][2] = 0;
        my[3][2] = 0;
        mz[3][2] = 0;
        mx[3][3] = 0;
        my[3][3] = 0;
        mz[3][3] = 0;

        // Final result
        Gx_MATRIX.M = mx;
        Gy_MATRIX.M = my;
        Gz_MATRIX.M = mz;
        //printGeometryMatrices();
    }

    /**
    This method evaluates current patch position in the parameter space 
    position (s, t), computing the equation set 11.76 in [FOLE1992].

    The following class attributes are used:
    <UL>
      <LI> S_MATRIX  Column vector for storing s parameter polynomial as
      explain in section [FOLE1992].11.3
      <LI> Tt_MATRIX Row vector for storing t parameter polynomial
      <LI> M_MATRIX  Patch's blending function
      <LI> Mt_MATRIX M's transpose
      <LI> Gx_MATRIX Geometry matrix for x
      <LI> Gy_MATRIX Geometry matrix for y
      <LI> Gz_MATRIX Geometry matrix for z
    </UL>

    PRE: calculateMAtrices() should be called before calling this method.
    */
    public void evaluate(Vector3D p, double s, double t)
    {
        S_MATRIX.M[0][0] = s * s * s;
        S_MATRIX.M[0][1] = s * s;
        S_MATRIX.M[0][2] = s;
        S_MATRIX.M[0][3] = 1;

        Tt_MATRIX.M[0][0] = t * t * t;
        Tt_MATRIX.M[1][0] = t * t;
        Tt_MATRIX.M[2][0] = t;
        Tt_MATRIX.M[3][0] = 1;

        Matrix4x4 S_M_Gx_Mt_MATRIX = S_MATRIX.multiply(M_Gx_Mt_MATRIX);
        Matrix4x4 S_M_Gy_Mt_MATRIX = S_MATRIX.multiply(M_Gy_Mt_MATRIX);
        Matrix4x4 S_M_Gz_Mt_MATRIX = S_MATRIX.multiply(M_Gz_Mt_MATRIX);
        Matrix4x4 Qx_MATRIX = S_M_Gx_Mt_MATRIX.multiply(Tt_MATRIX);
        Matrix4x4 Qy_MATRIX = S_M_Gy_Mt_MATRIX.multiply(Tt_MATRIX);
        Matrix4x4 Qz_MATRIX = S_M_Gz_Mt_MATRIX.multiply(Tt_MATRIX);

        // The result is a 1x1 matrix.
        p.x = Qx_MATRIX.M[0][0];
        p.y = Qy_MATRIX.M[0][0];
        p.z = Qz_MATRIX.M[0][0];
    }

    public void evaluateTangent(Vector3D dQds, double s, double t)
    {
        S_MATRIX_DS.M[0][0] = 3 * s * s;
        S_MATRIX_DS.M[0][1] = 2 * s;
        S_MATRIX_DS.M[0][2] = 1;
        S_MATRIX_DS.M[0][3] = 0;
        Tt_MATRIX.M[0][0] = t * t * t;
        Tt_MATRIX.M[1][0] = t * t;
        Tt_MATRIX.M[2][0] = t;
        Tt_MATRIX.M[3][0] = 1;

        Matrix4x4 S_M_Gx_Mt_MATRIX = S_MATRIX_DS.multiply(M_Gx_Mt_MATRIX);
        Matrix4x4 S_M_Gy_Mt_MATRIX = S_MATRIX_DS.multiply(M_Gy_Mt_MATRIX);
        Matrix4x4 S_M_Gz_Mt_MATRIX = S_MATRIX_DS.multiply(M_Gz_Mt_MATRIX);
        Matrix4x4 Qx_MATRIX = S_M_Gx_Mt_MATRIX.multiply(Tt_MATRIX);
        Matrix4x4 Qy_MATRIX = S_M_Gy_Mt_MATRIX.multiply(Tt_MATRIX);
        Matrix4x4 Qz_MATRIX = S_M_Gz_Mt_MATRIX.multiply(Tt_MATRIX);

        // The result is a 1x1 matrix.
        Vector3D result = new Vector3D();

        result.x = Qx_MATRIX.M[0][0];
        result.y = Qy_MATRIX.M[0][0];
        result.z = Qz_MATRIX.M[0][0];
        result.normalize();

        dQds.clone(result);
    }

    public void evaluateBinormal(Vector3D dQdt, double s, double t)
    {
        S_MATRIX.M[0][0] = s * s * s;
        S_MATRIX.M[0][1] = s * s;
        S_MATRIX.M[0][2] = s;
        S_MATRIX.M[0][3] = 1;

        Tt_MATRIX_DT.M[0][0] = 3 * t * t;
        Tt_MATRIX_DT.M[1][0] = 2 * t;
        Tt_MATRIX_DT.M[2][0] = 1;
        Tt_MATRIX_DT.M[3][0] = 0;

        Matrix4x4 S_M_Gx_Mt_MATRIX = S_MATRIX.multiply(M_Gx_Mt_MATRIX);
        Matrix4x4 S_M_Gy_Mt_MATRIX = S_MATRIX.multiply(M_Gy_Mt_MATRIX);
        Matrix4x4 S_M_Gz_Mt_MATRIX = S_MATRIX.multiply(M_Gz_Mt_MATRIX);
        Matrix4x4 Qx_MATRIX = S_M_Gx_Mt_MATRIX.multiply(Tt_MATRIX_DT);
        Matrix4x4 Qy_MATRIX = S_M_Gy_Mt_MATRIX.multiply(Tt_MATRIX_DT);
        Matrix4x4 Qz_MATRIX = S_M_Gz_Mt_MATRIX.multiply(Tt_MATRIX_DT);

        // The results are 1x1 matrices.
        Vector3D result = new Vector3D();
        result.x = Qx_MATRIX.M[0][0];
        result.y = Qy_MATRIX.M[0][0];
        result.z = Qz_MATRIX.M[0][0];
        result.normalize();

        dQdt.clone(result);
    }

    /**
    This method evaluates current patch gradient in the parameter space 
    position (s, t), computing the patch normal as explain in section 
    [FOLE1992].11.3.4.

    The following class attributes are used:
    <UL>
      <LI> S_MATRIX  Column vector for storing s parameter polynomial as
      explain in section [FOLE1992].11.3
      <LI> Tt_MATRIX Row vector for storing t parameter polynomial
      <LI> M_MATRIX  Patch's blending function
      <LI> Mt_MATRIX M's transpose
      <LI> Gx_MATRIX Geometry matrix for x
      <LI> Gy_MATRIX Geometry matrix for y
      <LI> Gz_MATRIX Geometry matrix for z
    </UL>

    PRE: calculateMAtrices() should be called before calling this method.
    */
    public void evaluateNormal(Vector3D n, double s, double t)
    {
        Vector3D dQds = new Vector3D();
        Vector3D dQdt = new Vector3D();

        evaluateTangent(dQds, s, t);
        evaluateBinormal(dQdt, s, t);

        Vector3D nn = dQds.crossProduct(dQdt);
        nn.normalize();

        n.clone(nn);
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doIntersection.

    @todo implement the method
    */
    public boolean doIntersection(Ray r) {
        return false;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doExtraInformation.

    Check the discusion in [WAYN1990] about solving this problem. Two main
    strategies are known for solving this: a numeric root finding
    (trying different values for Ray.t until a given error tolerance is
    reached) and converting the patch to a mesh and test the mesh.

    This method implements the numerical approach, while an explicit
    convertion to a Mesh could be managed by the user/programmer directly.
    WARNING: The numerical approach is really, really slow... 

    @todo implement the method
    */
    public void
    doExtraInformation(Ray inRay, double intT, 
                                   GeometryIntersectionInformation outData) {
        return;
    }

    /** 
    Returns an approximate bounding volume minmax for current patch, from
    the minmax of its contour curve.

    @bug current contour curve asumption is not valid
    */
    public double[] getMinMax() {
        if ( contourCurve != null ) {
            return contourCurve.getMinMax();
        }
        else {
            // This gives convex hull's minmax
            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double minZ = Double.MAX_VALUE;
            double maxX = -Double.MAX_VALUE;
            double maxY = -Double.MAX_VALUE;
            double maxZ = -Double.MAX_VALUE;
            double minMax[] = new double[6];
            int i, j;

            for ( i = 0; i < 4; i++ ) {
                for ( j = 0; j < 4; j++ ) {
                    Vector3D p = controlMeshPoints[i][j];

                    if ( p.x < minX ) minX = p.x;
                    if ( p.y < minY ) minY = p.y;
                    if ( p.z < minZ ) minZ = p.z;
                    if ( p.x > maxX ) maxX = p.x;
                    if ( p.y > maxY ) maxY = p.y;
                    if ( p.z > maxZ ) maxZ = p.z;
                }
            }
            minMax[0] = minX;
            minMax[1] = minY;
            minMax[2] = minZ;
            minMax[3] = maxX;
            minMax[4] = maxY;
            minMax[5] = maxZ;
            return minMax;
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
