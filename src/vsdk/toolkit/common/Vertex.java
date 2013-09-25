//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Gabriel Sarmiento / Lina Rojas: Original base version =
//= - November 19 2006 - Oscar Chavarro: re-structured and tested           =
//===========================================================================

package vsdk.toolkit.common;

import java.util.ArrayList;
import vsdk.toolkit.common.linealAlgebra.Vector3D;

/**
A vertex is a basic data pack for tipically used operations in computer
graphics, as polygon mesh representations and basic visualization algorithms
and shaders.

This class is meant as a modeling facility for polygon meshes, and a common
structure for operation signatures.

Note that this is NOT a class, but merely a data structure, and as such,
all its attributes are public.  This structure is not supposed to evolve or
change in time radically.

This class is supposed to be used as basic element for building polygons in
three dimensional space. Note that is being currently used in polygon meshes
(surfaces as such TriangleMesh) and on structured solid polygons, as such
BinarySpacePartitioningTreeSolid.
*/
public class Vertex extends FundamentalEntity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    ///
    public Vector3D position;

    ///
    public Vector3D normal;

    ///
    public Vector3D binormal;

    ///
    public Vector3D tangent;

    /// Texture coordinates
    public double u;
    public double v;

    ///
    public ArrayList<Triangle> incidentTriangles;

    public Vertex(double x, double y, double z) {
        position = new Vector3D(x, y, z);
        normal = new Vector3D(1, 0, 0);
        binormal = new Vector3D(0, 1, 0);
        tangent = new Vector3D(0, 0, 1);
        incidentTriangles = null;
        u = 0.0;
        v = 0.0;
    }

    public Vertex(Vector3D position) {
        this.position = position;
        normal = new Vector3D(1, 0, 0);
        binormal = new Vector3D(0, 1, 0);
        tangent = new Vector3D(0, 0, 1);
        incidentTriangles = null;
        u = 0.0;
        v = 0.0;
    }

    public Vertex(Vector3D position, Vector3D normal) {
        this.position = new Vector3D(position);
        this.normal = new Vector3D(normal);
        this.normal.normalize();

        binormal = new Vector3D(0, 1, 0);
        tangent = new Vector3D(0, 0, 1);
        incidentTriangles = null;
        u = 0.0;
        v = 0.0;
    }

    public Vertex(Vector3D position, Vector3D normal, double u, double v) {
        this.position = new Vector3D(position);
        this.normal = new Vector3D(normal);
        this.normal.normalize();

        binormal = new Vector3D(0, 1, 0);
        tangent = new Vector3D(0, 0, 1);
        incidentTriangles = null;
        this.u = u;
        this.v = v;
    }

    public Vertex(Vector3D position, Vector3D normal, Vector3D binormal,
                  Vector3D tangent) {
        this.position = position;
        this.normal = normal;
        this.binormal = binormal;
        this.tangent = tangent;
        incidentTriangles = null;
        u = 0.0;
        v = 0.0;
    }

    public Vertex(Vertex vertex) {
        position = vertex.position;
        normal = vertex.normal;
        binormal = vertex.binormal;
        tangent = vertex.tangent;
        incidentTriangles=vertex.getIncidentTriangles();
        u = vertex.u;
        v = vertex.v;
    }

    public Vector3D getPosition() {
        return this.position;
    }

    public Vector3D getNormal() {
        return normal;
    }

    public Vector3D getBinormal() {
        return this.binormal;
    }

    public Vector3D getTangent() {
        return this.tangent;
    }

    public double getU() {
        return this.u;
    }

    public double getV() {
        return this.v;
    }

    public ArrayList<Triangle> getIncidentTriangles() {
        return this.incidentTriangles;
    }

    public void setPosition(Vector3D position) {
        this.position = new Vector3D(position);
    }

    public void setNormal(Vector3D normal) {
        this.normal = new Vector3D(normal);
        this.normal.normalize();
    }

    public void setBinormal(Vector3D binormal) {
        this.binormal = binormal;
    }

    public void setTangent(Vector3D tangent) {
        this.tangent = tangent;
    }

    public void setU(double u) {
        this.u = u;
    }

    public void setV(double v) {
        this.v = v;
    }

    public void setIncidentTriangles(ArrayList<Triangle> incidentTriangles) {
        this.incidentTriangles = incidentTriangles;
    }

    public Triangle getIncidentTriangleAt(int index) {
        return incidentTriangles.get(index);
    }

    /**
    Provides an object to text report convertion, optimized for human
    readability and debugging. Do not use for serialization or persistence
    purposes.
    */
    public String toString() {
        return "v " + position +" n " + normal + " UV<" +
            VSDK.formatDouble(u) + ", " + VSDK.formatDouble(v) + ">";
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
