//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 29 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.Ray;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.environment.geometry.Cone;
import vsdk.toolkit.environment.geometry.Geometry;

public class Arrow extends Solid {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    private double baseLength;
    private double headLength;
    private double baseRadius;
    private double headRadius;

    private Cone baseCylinder;
    private Cone headCone;
    private Cone lastElement;

    public Arrow(double baseLength, double headLength, double baseRadius, double headRadius) {
        this.baseLength = baseLength;
        this.headLength = headLength;
        this.baseRadius = baseRadius;
        this.headRadius = headRadius;
        baseCylinder = new Cone(baseRadius, baseRadius, baseLength);
        headCone = new Cone(headRadius, 0, headLength);
        lastElement = baseCylinder;
    }

    public double getBaseLength()
    {
        return baseLength;
    }

    public void setBaseLength(double val)
    {
        baseLength = val;
        baseCylinder.setHeight(val);
    }

    public double getHeadLength()
    {
        return headLength;
    }

    public void setHeadLength(double val)
    {
        headLength = val;
        headCone.setHeight(val);
    }

    public double getBaseRadius()
    {
        return baseRadius;
    }

    public void setBaseRadius(double val)
    {
        baseRadius = val;
        baseCylinder.setBaseRadius(val);
        baseCylinder.setTopRadius(val);
    }

    public double getHeadRadius()
    {
        return headRadius;
    }

    public void setHeadRadius(double val)
    {
        headRadius = val;
        headCone.setBaseRadius(val);
    }

    /**
     Check the general interface contract in superclass method
     Geometry.doIntersection.
    */
    public boolean
    doIntersection(Ray inOutRay) {
        boolean headTest, baseTest;
        Ray headRay, baseRay;
        GeometryIntersectionInformation headInfo, baseInfo;
        Vector3D tr = new Vector3D(0, 0, -baseLength);

        headRay = new Ray(inOutRay.origin.add(tr), inOutRay.direction);
        baseRay = new Ray(inOutRay);

        baseTest = baseCylinder.doIntersection(baseRay);
        headTest = headCone.doIntersection(headRay);

        if ( (baseTest && !headTest) || 
             (baseTest && headTest && (baseRay.t < headRay.t) ) ) {
            inOutRay.origin = baseRay.origin;
            inOutRay.direction = baseRay.direction;
            inOutRay.t = baseRay.t;
            lastElement = baseCylinder;
            return true;
        }
        else if ( (!baseTest && headTest) || 
                  (baseTest && headTest && (headRay.t < baseRay.t) ) ) {
            inOutRay.origin = baseRay.origin;
            inOutRay.direction = headRay.direction;
            inOutRay.t = headRay.t;
            lastElement = headCone;
            return true;
        }

        return false;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doExtraInformation.
    */
    public void
           doExtraInformation(Ray inRay, double inT, 
           GeometryIntersectionInformation outData) {
        lastElement.doExtraInformation(inRay, inT, outData);
        if ( lastElement == headCone ) {
            // Modify answer!
            outData.p.z += baseLength;
        }
    }

    public double[] getMinMax()
    {
        double [] minmax = new double[6];
        double r = Math.max(baseRadius, headRadius);

        minmax[0] = -r;
        minmax[1] = -r;
        minmax[2] = 0;
        minmax[3] = r;
        minmax[4] = r;
        minmax[5] = baseLength + headLength;

        return minmax;
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
