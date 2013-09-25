//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//= - March 14 2006 - Oscar Chavarro: Get/set interface                     =
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [MANT1988] Mantyla Martti. "An Introduction To Solid Modeling",         =
//=     Computer Science Press, 1988.                                       =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.Ray;

public class Sphere extends Solid {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    private double _radius;
    private double _radius_squared;
    private Vector3D _static_delta;
    private double [] _static_minmax;

    public Sphere(double r) {
        _radius = r;
        _radius_squared = _radius*_radius;
        _static_delta = new Vector3D();
        _static_minmax = new double[6];
    }

    /**
     Check the general interface contract in superclass method
     Geometry.doIntersection.

    Dado un Ray `inout_rayo`, esta operaci&oacute;n determina si el rayo se
    intersecta con la superficie de este objeto o no. Si el rayo no intersecta
    al objeto se retorna 0, y de lo contrario se retorna la distancia desde
    el origen del rayo hasta el punto de interseccion.

    En caso de intersecci&oacute;n, se modifica `inout_rayo.t` para que 
    contenga la distancia entre el punto de intersecci&oacute;n y el origen
    del `inout_rayo`.
    */
    public boolean
    doIntersection(Ray inout_rayo) {
        /* OJO: Como en Java, a diferencia de C no hay sino objetos por
                referencia, no se puede hacer una declaraci&oacute;n 
                est&aacute;tica de un objeto, y poder hacerla es importante 
                porque la constructora Vector3D::Vector3D se ejecuta muchas veces, 
                y no se debe gastar tiempo creando objetos (i.e. haciento 
                Vector3D delta; delta = new Vector3D(); ...).  El c&oacute;digo
                original de MIT resolvi&oacute; &eacute;sto usando unos 
                flotantes double dx, dy, dz; e implementando una versi&oacute;n
                adicional de Vector3D::dotProduct() que recibe 3 doubles.  
                Se considera que esa soluci&oacute;n es "fea" y que al ofrecer
                el nuevo m&eacute;todo `Vector3D::dotProduct` se le 
                desorganiza la mente al usuario/programador.  Por eso, se 
                resolvi&oacute; implementar otra soluci&oacute;n (tal vez 
                igual de fea): a&ntilde;adir un nuevo atributo de clase en
                Sphere, y utilizarlo como su fuese una variable est&aacute;tica
                de tipo Vector3D dentro del m&eacute;todo.  Esto no es bueno 
                porque gasta memoria, pero ... que m&aacute;s podr&aacute; 
                hacerse? Al menos el tiempo de ejecuci&oacute;n se mantiene 
                igual respecto al c&oacute;digo original de MIT.
                NOTA: Comparar este m&eacute;todo modificado con la 
                      versi&oacute;n original en la etapa 1, con la 
                      ayuda de un profiler. ... */
        _static_delta.x = -inout_rayo.origin.x;
        _static_delta.y = -inout_rayo.origin.y;
        _static_delta.z = -inout_rayo.origin.z;
        double v = inout_rayo.direction.dotProduct(_static_delta);

        // Test if the inout_rayo actually intersects the sphere
        double t = _radius_squared + v*v 
                  - _static_delta.x*_static_delta.x 
                  - _static_delta.y*_static_delta.y 
                  - _static_delta.z*_static_delta.z;
        if ( t < 0 ) {
            return false;
        }

        // Test if the intersection is in the positive
        // inout_rayo direction
        t = v - Math.sqrt(t);
        if ( t < 0 ) {
            return false;
        }

        inout_rayo.t = t;
        return true;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doExtraInformation.
    */
    public void
    doExtraInformation(Ray inRay, double inT, 
                                  GeometryIntersectionInformation outData) {
        //-----------------------------------------------------------------
        outData.p.x = inRay.origin.x + inT*inRay.direction.x;
        outData.p.y = inRay.origin.y + inT*inRay.direction.y;
        outData.p.z = inRay.origin.z + inT*inRay.direction.z;

        outData.n.x = outData.p.x;
        outData.n.y = outData.p.y;
        outData.n.z = outData.p.z;
        outData.n.normalize();

        //-----------------------------------------------------------------
        double theta;
        double phi;

        phi = Math.acos(outData.n.z);
        if ( outData.n.x > VSDK.EPSILON ) {
            theta = Math.atan(outData.n.y / outData.n.x) + 3*Math.PI/2;
          }
          else if ( outData.n.x < VSDK.EPSILON ) {
            // OJO: Habra una manera mas eficiente de lograr este intervalo?
            theta = Math.atan(outData.n.y / outData.n.x) + 3*Math.PI/2;
            theta += Math.PI;
            if ( theta > 2*Math.PI ) theta -= 2*Math.PI;
          }
          else {
            theta = 0.0;
        }
        // Suponiendo que theta esta en [0, 2*PI] y phi en [0, PI]...
        outData.u = ((theta+Math.PI/2)/(2*Math.PI));
        outData.v = 1 - (phi / Math.PI);

        //-----------------------------------------------------------------
        outData.t.x = Math.sin(theta-Math.PI/2);
        outData.t.y = -Math.cos(theta-Math.PI/2);
        outData.t.z = 0;

        //-----------------------------------------------------------------
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doContainmentTest.
    */
    public int doContainmentTest(Vector3D p, double distanceTolerance)
    {
        double l = p.length();
        if ( l < _radius - distanceTolerance ) {
            return INSIDE;
        }
        else if ( l > _radius + distanceTolerance ) {
            return OUTSIDE;
        }
        return LIMIT;
    }

    public double[] getMinMax()
    {
        for ( int i = 0; i < 3; i++ ) {
            _static_minmax[i] = -_radius;
        }
        for ( int i = 3; i < 6; i++ ) {
            _static_minmax[i] = _radius;
        }
        return _static_minmax;
    }

    public double getRadius()
    {
        return _radius;
    }

    public void setRadius(double r)
    {
        _radius = r;
        _radius_squared = r*r;
    }

    private static void
    spherePosition(Vector3D p, double theta, double t, double r)
    {
        double phi = (t-0.5)*Math.PI;
        p.x = Math.cos(phi) * Math.cos(theta) * r;
        p.y = Math.cos(phi) * Math.sin(theta) * r;
        p.z = Math.sin(phi) * r;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
