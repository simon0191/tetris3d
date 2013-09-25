//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - February 22 2006 - Oscar Chavarro: Original base version              =
//===========================================================================

package vsdk.toolkit.render.jogl;

// JOGL clases
import javax.media.opengl.GL2;

// VitralSDK classes
import vsdk.toolkit.environment.geometry.VoxelVolume;
import vsdk.toolkit.environment.geometry.Box;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;

public class JoglVoxelVolumeRenderer extends JoglRenderer
{
    private static Box geometryInstance = null;

    private static int threshold = 127;

    public static void setThreshold(int t)
    {
        threshold = t;
    }

    public static int getThreshold()
    {
        return threshold;
    }

    public static void drawBinaryCubes(GL2 gl, VoxelVolume v,
        Camera c, RendererConfiguration q)
    {
        int xSize, ySize, zSize, x, y, z;
        double dx, dy, dz;
        RendererConfiguration qsub = q.clone();

        qsub.setBoundingVolume(false);
        qsub.setSelectionCorners(false);

        xSize = v.getXSize();
        ySize = v.getYSize();
        zSize = v.getZSize();
        dx = 2 / ((double)xSize);
        dy = 2 / ((double)ySize);
        dz = 2 / ((double)zSize);

        if ( geometryInstance == null ) {
            geometryInstance = new Box(dx, dy, dz);
        }

        //-----------------------------------------------------------------
        gl.glPushMatrix();
        int voxelValue;
        for ( z = 0; z < zSize; z++ ) {
            for ( y = 0; y < ySize; y++ ) {
                for ( x = 0; x < xSize; x++ ) {
                    voxelValue = v.getVoxel(x, y, z);
                    if ( voxelValue >= threshold ) {
                        gl.glPushMatrix();
                        gl.glTranslated(dx/2+x*dx-1, dy/2+y*dy-1, dz/2+z*dz-1);
                        JoglGeometryRenderer.draw(gl, geometryInstance, c, qsub);
                        gl.glPopMatrix();
                    }
                }
            }
        }
        gl.glPopMatrix();

        //-----------------------------------------------------------------

        if ( q.isBoundingVolumeSet() ) {
            JoglGeometryRenderer.drawMinMaxBox(gl, v, q);
        }
        if ( q.isSelectionCornersSet() ) {
            JoglGeometryRenderer.drawSelectionCorners(gl, v, q);
        }

    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================

