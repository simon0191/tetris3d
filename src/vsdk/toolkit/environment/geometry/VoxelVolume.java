//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 22 2007 - Oscar Chavarro: Original base version              =
//=-------------------------------------------------------------------------=
//= References:                                                             =
//= [KAUF1987] Kaufman, Arie. "Efficient Algorithms for 3D Scan-Conversion  =
//=     of Parametric Curves, Surfaces, and Volumes", ACM SIGGRAPH Computer =
//=     Graphics, volume 21, number 4, July 1987.                           =
//===========================================================================

package vsdk.toolkit.environment.geometry;

import java.util.ArrayList;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.Ray;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.media.IndexedColorImage;

/**
VoxelVolume represents a voxelized paralelogram volume in memory, as the
"cubic frame buffer" proposed in [KAUF1987]. Note that this class is intended
for simpler applications in which data volume fix into main memory. Current
class doesn't support any caching or data storage optimization. When the
x/y/z sizes of the voxel volume are equal, the corresponding space covered
by the volume is the cube from the point <-1, -1, -1> to the point <1, 1, 1>.
The voxels are always assume to be square, and when dimensions are different,
the largest dimension fits in to the <-1, 1> interval (other dimensions are
proportional to maintain voxel sizes).

As current voxel volume is based in specific data samples of 8 bits per voxel,
it is not general and not well biased to processing applications. This is
just a placeholder for regions of interest in memory, as an aid to
other algorithms.

For general representation of N-dimensional images of arbitrary data sample
format, use another toolkit, like ITK or VTK.
*/
public class VoxelVolume extends Solid {
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20070222L;

    private ArrayList<IndexedColorImage> data;

    public VoxelVolume()
    {
        data = null;
    }

    public int getXSize()
    {
        if ( data == null || data.size() < 0 ) return 0;
        return data.get(0).getXSize();
    }

    public int getYSize()
    {
        if ( data == null || data.size() < 0 ) return 0;
        return data.get(0).getXSize();
    }

    public int getZSize()
    {
        if ( data == null || data.size() < 0 ) return 0;
        return data.size();
    }

    public boolean init(int xSize, int ySize, int zSize)
    {
        int z;
        ArrayList<IndexedColorImage> localData;
        IndexedColorImage slice;

        localData = new ArrayList<IndexedColorImage>();

        for ( z = 0; z < zSize; z++ ) {
            slice = new IndexedColorImage();
            if ( !slice.init(xSize, ySize) ) {
                return false;
            }
            localData.add(slice);
        }

        data = localData;
        return true;
    }

    public void putVoxel(int x, int y, int z, byte val)
    {
        try {
            if ( (x < 0 || x >= getXSize()) ||
                 (y < 0 || y >= getYSize()) ||
                 (z < 0 || z >= getZSize()) ) {
                return;
            }
            IndexedColorImage slice = data.get(z);
            slice.putPixel(x, y, val);
        }
        catch ( Exception e ) {
            //
        }
    }

    /**
    Given current voxelset geometric space (cube from <-1, -1, -1> to
    <1, 1, 1>), current voxelset size, and cell position to a voxel; this
    methods gives the position of the voxel center in world coordinates.
    */
    public Vector3D getVoxelPosition(int x, int y, int z)
    {
        Vector3D p = new Vector3D();
        p.x = ((double)x+0.5) / ((double)getXSize())*2 - 1;
        p.y = ((double)y+0.5) / ((double)getYSize())*2 - 1;
        p.z = ((double)z+0.5) / ((double)getZSize())*2 - 1;
        return p;
    }

    /**
    Partial coordinate convertion (X axis) for `x` voxel coordinate to
    corresponding voxel index.
    */
    public int getNearestIFromX(double x)
    {
        return (int)(((x + 1)/2) * ((double)getXSize()) - 0.5);
    }

    /**
    Partial coordinate convertion (Y axis) for `y` voxel coordinate to
    corresponding voxel index.
    */
    public int getNearestJFromY(double y)
    {
        return (int)(((y + 1)/2) * ((double)getYSize()) - 0.5);
    }

    /**
    Partial coordinate convertion (Z axis) for `z` voxel coordinate to
    corresponding voxel index.
    */
    public int getNearestKFromZ(double z)
    {
        return (int)(((z + 1)/2) * ((double)getZSize()) - 0.5);
    }

    /**
    Given current voxelset geometric space (cube from <-1, -1, -1> to
    <1, 1, 1>), current voxelset size, and cell position to a voxel; this
    methods gives the voxel value with a position corresponding to coordinate
    <x, y, z> (inside voxel space cube).
    */
    public int getVoxelAtPosition(double x, double y, double z)
    {
        if ( x < -1 || x > 1 || y < -1 || y > 1 || z < -1 || z > 1 ) return 0;
        int i, j, k;

        i = (int)(((x + 1)/2) * ((double)getXSize()) - 0.5);
        j = (int)(((y + 1)/2) * ((double)getYSize()) - 0.5);
        k = (int)(((z + 1)/2) * ((double)getZSize()) - 0.5);

        return getVoxel(i, j, k);
    }

    /**
    Given current voxelset geometric space (cube from <-1, -1, -1> to
    <1, 1, 1>), current voxelset size, and cell position to a voxel; this
    methods gives the voxel value with a position corresponding to coordinate
    <x, y, z> (inside voxel space cube).
    */
    public int getVoxelAtPosition(Vector3D p)
    {
        if ( p.x < -1 || p.x > 1 || p.y < -1 || p.y > 1 || p.z < -1 || p.z > 1 ) return 0;
        int i, j, k;

        i = (int)(((p.x + 1)/2) * ((double)getXSize()) - 0.5);
        j = (int)(((p.y + 1)/2) * ((double)getYSize()) - 0.5);
        k = (int)(((p.z + 1)/2) * ((double)getZSize()) - 0.5);

        return getVoxel(i, j, k);
    }

    /**
    Given current voxelset geometric space (cube from <-1, -1, -1> to
    <1, 1, 1>), current voxelset size, and cell position to a voxel; this
    methods puts the voxel value with a position corresponding to coordinate
    <x, y, z> (inside voxel space cube).
    */
    public void putVoxelAtPosition(double x, double y, double z, byte val)
    {
        if ( x < -1 || x > 1 || y < -1 || y > 1 || z < -1 || z > 1 ) return;
        int i, j, k;

        i = (int)(((x + 1)/2) * ((double)getXSize()) - 0.5);
        j = (int)(((y + 1)/2) * ((double)getYSize()) - 0.5);
        k = (int)(((z + 1)/2) * ((double)getZSize()) - 0.5);

        putVoxel(i, j, k, val);
    }

    /**
    Given current voxelset geometric space (cube from <-1, -1, -1> to
    <1, 1, 1>), current voxelset size, and cell position to a voxel; this
    methods puts the voxel value with a position corresponding to coordinate
    <x, y, z> (inside voxel space cube).
    */
    public void putVoxelAtPosition(Vector3D p, byte val)
    {
        if ( p.x < -1 || p.x > 1 || p.y < -1 || p.y > 1 || p.z < -1 || p.z > 1 ) return;
        int i, j, k;

        i = (int)(((p.x + 1)/2) * ((double)getXSize()) - 0.5);
        j = (int)(((p.y + 1)/2) * ((double)getYSize()) - 0.5);
        k = (int)(((p.z + 1)/2) * ((double)getZSize()) - 0.5);

        putVoxel(i, j, k, val);
    }

    public int getVoxel(int x, int y, int z)
    {
        try {
            IndexedColorImage slice = data.get(z);
            return slice.getPixel(x, y);
        }
        catch ( Exception e ) {
            //
        }
        return 0;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.getMinMax.
    */
    public double[] getMinMax() {
        double minMax[];

        minMax = new double[6];
        minMax[0] = -1.0;
        minMax[1] = -1.0;
        minMax[2] = -1.0;
        minMax[3] = 1.0;
        minMax[4] = 1.0;
        minMax[5] = 1.0;
        return minMax;
    }

    /**
     Check the general interface contract in superclass method
     Geometry.doIntersection.

       Dado un Ray `inout_rayo`, esta operaci&oacute;n determina si el rayo se
       intersecta con alguna de las mallas de triangulos. Si el rayo no intersecta
       al objeto se retorna 0, y de lo contrario se retorna la distancia desde
       el origen del rayo hasta el punto de interseccion mas cercano de todas las mallas.
     */
    public boolean doIntersection(Ray inOut_Ray) {
        return false;
    }

    /**
    Check the general interface contract in superclass method
    Geometry.doExtraInformation.
    */
    public void
    doExtraInformation(Ray inRay, double inT,
                                   GeometryIntersectionInformation outData) {
        ;
    }

    /**
    Current method creates a transformation matrix that represent the
    coordinate change from voxel volume cube <-1, -1, -1>-<1, 1, 1> to
    the bounding box recieved in `minmax`.
    */
    public static Matrix4x4
    getTransformFromVoxelFrameToMinMax(double minmax[])
    {
        Matrix4x4 M;
        Matrix4x4 S, T1, T2;
        double greaterScale, sx, sy, sz;

        sx = minmax[3]-minmax[0];
        sy = minmax[4]-minmax[1];
        sz = minmax[5]-minmax[2];
        greaterScale = sx;
        if ( sy > greaterScale ) {
            greaterScale = sy;
        }
        if ( sz > greaterScale ) {
            greaterScale = sz;
        }

        S = new Matrix4x4();
        S.scale(greaterScale/2, greaterScale/2, greaterScale/2);

        T1 = new Matrix4x4();
        T1.translation(1, 1, 1);
        T2 = new Matrix4x4();
        T2.translation(minmax[0]-(greaterScale-sx)/2,
                       minmax[1]-(greaterScale-sy)/2,
                       minmax[2]-(greaterScale-sz)/2);

        M = T2.multiply(S.multiply(T1));
        return M;
    }

    /**
    Check the general interface contract in superclass method
    Solid.doCenterOfMass
    */
    public Vector3D doCenterOfMass() {
        Vector3D cm, p;
        cm = new Vector3D(0, 0, 0);
        double mi; // Maximum mass of one voxel (linear to voxel density)
        double M = 0;  // Total mass for current voxel volume
        int x, y, z;

        for ( x = 0; x < getXSize(); x++ ) {
            for ( y = 0; y < getYSize(); y++ ) {
                for ( z = 0; z < getZSize(); z++ ) {
                    // mi goes from 0 to 1
                    mi = (double)(getVoxel(x, y, z)) / 255.0;
                    M += mi;
                    p = getVoxelPosition(x, y, z);
                    cm.x += mi * p.x;
                    cm.y += mi * p.y;
                    cm.z += mi * p.z;
                }
            }
        }

        if ( Math.abs(M) < VSDK.EPSILON ) {
            return new Vector3D(0, 0, 0);
        }

        return cm.multiply(1/M);
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
