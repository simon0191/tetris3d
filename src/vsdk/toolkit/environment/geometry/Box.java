//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 12 2006 - Oscar Chavarro: Original base version              =
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [MANT1988] Mantyla Martti. "An Introduction To Solid Modeling",         =
//=     Computer Science Press, 1988.                                       =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.environment.geometry.Geometry;
import vsdk.toolkit.environment.geometry.GeometryIntersectionInformation;
import vsdk.toolkit.common.Ray;

public class Box extends Solid {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    private Vector3D size;

    private GeometryIntersectionInformation lastInfo;
    private int lastPlane;

    public Box(double dx, double dy, double dz) {
        size = new Vector3D(dx, dy, dz);

        lastInfo = new GeometryIntersectionInformation();
        lastPlane = 0;
    }

    public Box(Vector3D s) {
        size = new Vector3D(s);

        lastInfo = new GeometryIntersectionInformation();
        lastPlane = 0;
    }

    public int getLastIntersectedPlane()
    {
        return lastPlane;
    }

    /**
     Check the general interface contract in superclass method
     Geometry.doIntersection.
    */
    public boolean
    doIntersection(Ray inOutRay) {
        double t, min_t = Double.MAX_VALUE;
        double x2 = size.x/2;  // OJO: Esto deberia venir precalculado
        double y2 = size.y/2;  // OJO: Esto deberia venir precalculado
        double z2 = size.z/2;  // OJO: Esto deberia venir precalculado
        Vector3D p = new Vector3D();
        GeometryIntersectionInformation info = 
            new GeometryIntersectionInformation();

        inOutRay.direction.normalize();

        // (1) Plano superior: Z = size.z/2
        if ( Math.abs(inOutRay.direction.z) > VSDK.EPSILON ) {
            // El rayo no es paralelo al plano Z=size.z/2
            t = (z2-inOutRay.origin.z)/inOutRay.direction.z;
            if ( t > -VSDK.EPSILON ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                if ( p.x >= -x2 && p.x <= x2 && 
                     p.y >= -y2 && p.y <= y2 ) {
                    info.p = new Vector3D(p);
                    min_t = t;
                    lastPlane = 1;
                }
            }
        }

        // (2) Plano inferior: Z = -size.z/2
        if ( Math.abs(inOutRay.direction.z) > VSDK.EPSILON ) {
            // El rayo no es paralelo al plano Z=-size.z/2
            t = (-z2-inOutRay.origin.z)/inOutRay.direction.z;
            if ( t > -VSDK.EPSILON && t < min_t ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                if ( p.x >= -x2 && p.x <= x2 && 
                     p.y >= -y2 && p.y <= y2 ) {
                    info.p = p;
                    min_t = t;
                    lastPlane = 2;
                }
            }
        }

        // (3) Plano frontal: Y = size.y/2
        if ( Math.abs(inOutRay.direction.y) > VSDK.EPSILON ) {
            // El rayo no es paralelo al plano Y=size.y/2
            t = (y2-inOutRay.origin.y)/inOutRay.direction.y;
            if ( t > -VSDK.EPSILON && t < min_t ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                if ( p.x >= -x2 && p.x <= x2 && 
                     p.z >= -z2 && p.z <= z2 ) {
                    info.p = p;
                    min_t = t;
                    lastPlane = 3;
                }
            }
        }

        // (4) Plano posterior: Y = -size.y/2
        if ( Math.abs(inOutRay.direction.y) > VSDK.EPSILON ) {
            // El rayo no es paralelo al plano Y=-size.y/2
            t = (-y2-inOutRay.origin.y)/inOutRay.direction.y;
            if ( t > -VSDK.EPSILON && t < min_t ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                if ( p.x >= -x2 && p.x <= x2 && 
                     p.z >= -z2 && p.z <= z2 ) {
                    info.p = p;
                    min_t = t;
                    lastPlane = 4;
                }
            }
        }

        // (5) Plano X = size.x/2
        if ( Math.abs(inOutRay.direction.x) > VSDK.EPSILON ) {
            // El rayo no es paralelo al plano X=size.x/2
            t = (x2-inOutRay.origin.x)/inOutRay.direction.x;
            if ( t > -VSDK.EPSILON && t < min_t ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                if ( p.y >= -y2 && p.y <= y2 && 
                     p.z >= -z2 && p.z <= z2 ) {
                    info.p = p;
                    min_t = t;
                    lastPlane = 5;
                }
            }
        }

        // (6) Plano X = -size.x/2
        if ( Math.abs(inOutRay.direction.x) > VSDK.EPSILON ) {
            // El rayo no es paralelo al plano X=-size.x/2
            t = (-x2-inOutRay.origin.x)/inOutRay.direction.x;
            if ( t > -VSDK.EPSILON && t < min_t ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                if ( p.y >= -y2 && p.y <= y2 && 
                     p.z >= -z2 && p.z <= z2 ) {
                    info.p = p;
                    min_t = t;
                    lastPlane = 6;
                }
            }
        }

        if ( min_t < Double.MAX_VALUE ) {
            inOutRay.t = min_t;
            lastInfo.clone(info);
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
        outData.p = lastInfo.p;

        switch ( lastPlane ) {
          case 1:
            outData.n.x = 0;
            outData.n.y = 0;
            outData.n.z = 1;
            outData.u = outData.p.y / size.y - 0.5;
            outData.v = 1-(outData.p.x / size.x - 0.5);
            outData.t.x = 0;
            outData.t.y = 1;
            outData.t.z = 0;
            break;
          case 2:
            outData.n.x = 0;
            outData.n.y = 0;
            outData.n.z = -1;
            outData.u = outData.p.y / size.y - 0.5;
            outData.v = outData.p.x / size.x - 0.5;
            outData.t.x = 0;
            outData.t.y = 1;
            outData.t.z = 0;
            break;
          case 3:
            outData.n.x = 0;
            outData.n.z = 0;
            outData.n.y = 1;
            outData.u = 1-(outData.p.x / size.x - 0.5);
            outData.v = outData.p.z / size.z - 0.5;
            outData.t.x = -1;
            outData.t.y = 0;
            outData.t.z = 0;
            break;
          case 4:
            outData.n.x = 0;
            outData.n.z = 0;
            outData.n.y = -1;
            outData.u = outData.p.x / size.x - 0.5;
            outData.v = outData.p.z / size.z - 0.5;
            outData.t.x = 1;
            outData.t.y = 0;
            outData.t.z = 0;
            break;
          case 5:
            outData.n.x = 1;
            outData.n.y = 0;
            outData.n.z = 0;
            outData.u = outData.p.y / size.y - 0.5;
            outData.v = outData.p.z / size.z - 0.5;
            outData.t.x = 0;
            outData.t.y = 1;
            outData.t.z = 0;
            break;
          case 6:
            outData.n.x = -1;
            outData.n.y = 0;
            outData.n.z = 0;
            outData.u = 1-(outData.p.y / size.y - 0.5);
            outData.v = outData.p.z / size.z - 0.5;
            outData.t.x = 0;
            outData.t.y = -1;
            outData.t.z = 0;
            break;
          default:
            outData.u = 0;
            outData.v = 0;
            break;
        }
    }

    public double[] getMinMax()
    {
        // TODO!
        double [] minmax = new double[6];

        minmax[0] = -size.x/2;
        minmax[1] = -size.y/2;
        minmax[2] = -size.z/2;
        minmax[3] = size.x/2;
        minmax[4] = size.y/2;
        minmax[5] = size.z/2;

        return minmax;
    }

    public Vector3D getSize()
    {
        return size;
    }

    public void setSize(double dx, double dy, double dz) {
        size = new Vector3D(dx, dy, dz);
    }

    public void setSize(Vector3D s) {
        size = new Vector3D(s);
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
