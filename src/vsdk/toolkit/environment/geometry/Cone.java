//===========================================================================
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [MANT1988] Mantyla Martti. "An Introduction To Solid Modeling",         =
//=     Computer Science Press, 1988.                                       =
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 13 2006 - Oscar Chavarro: Original base version              =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.environment.geometry.Geometry;
import vsdk.toolkit.environment.geometry.GeometryIntersectionInformation;
import vsdk.toolkit.common.Ray;

public class Cone extends Solid {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    private double r1; // Radius at the base
    private double r2; // Radius at the top
    private double h;  // Height

    private GeometryIntersectionInformation lastInfo;

    public Cone(double r1, double r2, double h) {
        this.r1 = r1;
        this.r2 = r2;
        this.h = h;
    }

    public double getBaseRadius()
    {
        return r1;
    }

    public double getTopRadius()
    {
        return r2;
    }

    public double getHeight()
    {
        return h;
    }

    public void setBaseRadius(double val)
    {
        r1 = val;
    }

    public void setTopRadius(double val)
    {
        r2 = val;
    }

    public void setHeight(double val)
    {
        h = val;
    }

    private boolean
    doIntersectionCylinder(Ray inOutRay) {
        return false;
    }

    private boolean
    doIntersectionCylinder(Ray inOutRay, double inR, double inH,
                      GeometryIntersectionInformation outInfo) 
    {
        double A, B, C, discriminant, t0;

        //- Translacion para concordar con la interpretacion AQUYNZA --------
        Ray r = new Ray(inOutRay);
        r.direction.normalize();

        //- Calcula el termino A --------------------------------------------
        A = VSDK.square(r.direction.x) +
            VSDK.square(r.direction.y);

        //- Calcula el termino B --------------------------------------------
        B = 2 *
            ( (r.direction.x * r.origin.x) +
              (r.direction.y * r.origin.y) );

        //- Calcula el termino C --------------------------------------------
        C = VSDK.square(r.origin.x) +
            VSDK.square(r.origin.y) -
            VSDK.square(inR);

        //- Calcula el discriminant. Si el discriminant no es positivo el -
        //- rayo no intersecta el cilindro. retorna t = 0                      
        discriminant = VSDK.square(B) - 4*A*C;
        if ( discriminant <= VSDK.EPSILON ) return false;

        //- Resuelve la ecuacion cuadratica para las raices de la ecuacion. -
        //- (-B +/- sqrt(B^2 - 4*A*C)) / 2A.                                -
        discriminant = Math.sqrt(discriminant);
        t0 = (-B-discriminant) / (2 * A);

        //- Si t0 es > 0 listo. Si no debemos calcular la otra raiz t1. -----
        if ( t0 > VSDK.EPSILON ) {
            // OJO: Aqui va el calculo del punto y la normal!
            outInfo.p = r.origin.add(r.direction.multiply(t0));
            if ( outInfo.p.z > inH || outInfo.p.z < 0 ) {
                return false;
            }

            // Se calcula la normal como el gradiente de la formula del cono,
            // notese que aqui se obtiene un vector escalado en 1/2 respecto al
            // gradiente (para ahorrar multiplicaciones)
            outInfo.n.x = outInfo.p.x;
            outInfo.n.y = outInfo.p.y;
            outInfo.n.z = 0;
            outInfo.n.normalize();
            inOutRay.t = t0;

            return true;
        }

        return false;
    }

    private boolean
    doIntersectionCone(Ray inOutRay, double inR, double inH,
                      GeometryIntersectionInformation outInfo) 
    {
        double A, B, C, discriminant, t0;

        //- Translacion para concordar con la interpretacion AQUYNZA --------
        Ray r = new Ray(inOutRay);
        r.origin.z -= inH;
        r.direction.normalize();

        //- Calcula el termino A --------------------------------------------
        A = VSDK.square(r.direction.x) +
            VSDK.square(r.direction.y) -
            VSDK.square(r.direction.z * inR / inH);

        //- Calcula el termino B --------------------------------------------
        B = 2 *
            ((r.direction.x * r.origin.x) +
             (r.direction.y * r.origin.y) -
             (r.direction.z * r.origin.z * VSDK.square(inR)) / 
             VSDK.square(inH)
             );

        //- Calcula el termino C --------------------------------------------
        C = VSDK.square(r.origin.x) +
            VSDK.square(r.origin.y) -
            VSDK.square(r.origin.z * inR / inH);

        //- Calcula el discriminant. Si el discriminant no es positivo el -
        //- rayo no intersecta la esfera. retorna t = 0                      
        discriminant = VSDK.square(B) - 4*A*C;
        if ( discriminant <= VSDK.EPSILON ) return false;

        //- Resuelve la ecuacion cuadratica para las raices de la ecuacion. -
        //- (-B +/- sqrt(B^2 - 4*A*C)) / 2A.                                -
        discriminant = Math.sqrt(discriminant);
        t0 = (-B-discriminant) / (2 * A);

        //- Si t0 es > 0 listo. Si no debemos calcular la otra raiz t1. -----
        if ( t0 > VSDK.EPSILON ) {
            // OJO: Aqui va el calculo del punto y la normal!
            outInfo.p = r.origin.add(r.direction.multiply(t0));
            if ( outInfo.p.z > 0 || outInfo.p.z < -inH ) {
                return false;
            }

            // Se calcula la normal como el gradiente de la formula del cono,
            // notese que aqui se obtiene un vector escalado en 1/2 respecto al
            // gradiente (para ahorrar multiplicaciones)
            outInfo.n.x = outInfo.p.x;
            outInfo.n.y = outInfo.p.y;
            outInfo.n.z = -outInfo.p.z * VSDK.square(inR/inH);
            outInfo.n.normalize();
    
            outInfo.p.z += inH;
            inOutRay.t = t0;

            return true;
        }

        return false;

    }

    private boolean
    doIntersectionTap(Ray inOutRay, double inR, double inH,
                      GeometryIntersectionInformation outInfo) {
        double t;
        Vector3D p;
        Vector3D proy;
        Vector3D o = new Vector3D(0, 0, 0);

        if ( Math.abs(inOutRay.direction.z) > VSDK.EPSILON ) {
            t = (inH - inOutRay.origin.z) / inOutRay.direction.z;
            if ( t > VSDK.EPSILON ) {
                p = inOutRay.origin.add(inOutRay.direction.multiply(t));
                proy = new Vector3D(p.x, p.y, 0);
                if ( VSDK.vectorDistance(proy, o) < inR ) {
                    inOutRay.t = t;
                    outInfo.n.x = 0;
                    outInfo.n.y = 0;
                    outInfo.n.z = 1;
                    outInfo.p = p;
                    return true;
                }
            }
        }
        return false;
    }

    /**
     Check the general interface contract in superclass method
     Geometry.doIntersection.
    */
    public boolean
    doIntersection(Ray inOutRay) {
        Ray bodyRay;
        Ray tap1Ray;
        Ray tap2Ray;
        GeometryIntersectionInformation infoTap1;
        GeometryIntersectionInformation infoTap2;
        GeometryIntersectionInformation infoBody;
        boolean tap1, tap2, body;

        bodyRay = new Ray(inOutRay);
        tap1Ray = new Ray(inOutRay);
        tap2Ray = new Ray(inOutRay);
        infoTap1 = new GeometryIntersectionInformation();
        infoTap2 = new GeometryIntersectionInformation();
        infoBody = new GeometryIntersectionInformation();

        //- Cone case -----------------------------------------------------
        if ( r2 < VSDK.EPSILON && r1 > VSDK.EPSILON ) {
            body = doIntersectionCone(bodyRay, r1, h, infoBody);
            tap1 = doIntersectionTap(tap1Ray, r1, 0, infoTap1);
            if ( (tap1 && !body) ||
                 (tap1 && body && (tap1Ray.t < bodyRay.t)) ) {
                inOutRay.origin = tap1Ray.origin;
                inOutRay.direction = tap1Ray.direction;
                inOutRay.t = tap1Ray.t;
                infoTap1.n = infoTap1.n.multiply(-1);
                lastInfo = infoTap1;
                return true;
            }
            if ( (!tap1 && body) ||
                 (tap1 && body && (tap1Ray.t > bodyRay.t)) ) {
                inOutRay.origin = bodyRay.origin;
                inOutRay.direction = bodyRay.direction;
                inOutRay.t = bodyRay.t;
                lastInfo = infoBody;
                return true;
            }
        }

        //- Cylinder case -------------------------------------------------
        int cercano = -1;

        if ( VSDK.equals(r1, r2) ) {
            body = doIntersectionCylinder(bodyRay, r1, h, infoBody);
            tap1 = doIntersectionTap(tap1Ray, r1, 0, infoTap1);
            tap2 = doIntersectionTap(tap2Ray, r1, h, infoTap2);
    
            if ( body && 
                 ((tap1 && (bodyRay.t < tap1Ray.t)) || !tap1) && 
                 ((tap2 && (bodyRay.t < tap2Ray.t)) || !tap2) ) {
                cercano = 1;
            }
            else if ( tap1 && 
                      (body && (tap1Ray.t < bodyRay.t) || !body) && 
                      (tap2 && (tap1Ray.t < tap2Ray.t) || !tap2) ) {
                cercano = 3;
            }
            else if ( tap2 ) {
                cercano = 2;
            }

            if ( cercano == 1 ) {
                inOutRay.origin = bodyRay.origin;
                inOutRay.direction = bodyRay.direction;
                inOutRay.t = bodyRay.t;
                lastInfo = infoBody;
                return true;
              }
              else if ( cercano == 2 )  {
                inOutRay.origin = tap2Ray.origin;
                inOutRay.direction = tap2Ray.direction;
                inOutRay.t = tap2Ray.t;
                lastInfo = infoTap2;
                return true;
              }
              else if ( cercano == 3 ) {
                inOutRay.origin = tap1Ray.origin;
                inOutRay.direction = tap1Ray.direction;
                inOutRay.t = tap1Ray.t;
                lastInfo = infoTap1;
                return true;
            }
        }
    
        return false;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doExtraInformation.
    */
    public void
    doExtraInformation(Ray inRay, double inT, 
                                  GeometryIntersectionInformation outData)
    {
        outData.p = lastInfo.p;
        outData.n = lastInfo.n;
        outData.n.normalize();
    }

    public double[] getMinMax()
    {
        // TODO!
        double [] minmax = new double[6];

        double r = Math.max(r1, r2);

        minmax[0] = -r;
        minmax[1] = -r;
        minmax[2] = 0;
        minmax[3] = r;
        minmax[4] = r;
        minmax[5] = h;

        return minmax;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
