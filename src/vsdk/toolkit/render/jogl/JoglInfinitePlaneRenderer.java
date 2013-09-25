//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - November 13 2007 - Oscar Chavarro: Original base version              =
//===========================================================================

package vsdk.toolkit.render.jogl;

// JOGL clases
import javax.media.opengl.GL2;
//import com.jogamp.opengl.cg.CgGL;
//import com.jogamp.opengl.cg.CGparameter;

// VitralSDK classes
import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.geometry.InfinitePlane;

public class JoglInfinitePlaneRenderer extends JoglRenderer {

    /**
    Generate OpenGL/JOGL primitives needed for the rendering of recieved
    Geometry object.
    */
    public static void draw(GL2 gl, InfinitePlane s, Camera c, RendererConfiguration q)
    {
        draw(gl, s, c, q, 10, 10);
    }

    public static void
    drawInfinitePlaneElements(GL2 gl, InfinitePlane s, int nx, int ny)
    {
        int i, j;
        double x, y;
        double dx, dy;
        dx = 20.0/((double)nx);
        dy = 20.0/((double)ny);

        Matrix4x4 R = new Matrix4x4();

        Vector3D n = s.getNormal();
        double yaw = n.obtainSphericalThetaAngle();
        double pitch = Math.PI/2 - n.obtainSphericalPhiAngle();

        R.eulerAnglesRotation(yaw, pitch, 0);
        n.normalize();
        n = n.multiply(-s.getD());

        gl.glPushMatrix();
        gl.glTranslated(n.x, n.y, n.z);
        JoglMatrixRenderer.activate(gl, R);
        gl.glNormal3d(1, 0, 0);
        gl.glBegin(gl.GL_QUADS);
        for ( i = 0, x = -5*dx; i < nx; i++, x+=dx ) {
            for ( j = 0, y = -5*dy; j < ny; j++, y+=dy ) {
                gl.glVertex3d(0, x, y);
                gl.glVertex3d(0, x+dx, y);
                gl.glVertex3d(0, x+dx, y+dy);
                gl.glVertex3d(0, x, y+dy);
            }
        }
        gl.glEnd();
        gl.glPopMatrix();
    }

    public static void draw(GL2 gl, InfinitePlane s, Camera c, RendererConfiguration q,
                            int slices, int stacks)
    {
        JoglGeometryRenderer.activateShaders(gl, s, c);
        if ( q.isSurfacesSet() ) {
            JoglGeometryRenderer.prepareSurfaceQuality(gl, q);
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
            gl.glEnable(gl.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);
            drawInfinitePlaneElements(gl, s, slices, stacks);
        }
        if ( q.isWiresSet() ) {
            JoglRenderer.disableNvidiaCgProfiles();
            gl.glDisable(gl.GL_LIGHTING);
            gl.glDisable(gl.GL_CULL_FACE);
            gl.glShadeModel(gl.GL_FLAT);

            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
            gl.glDisable(gl.GL_POLYGON_OFFSET_LINE);
            gl.glLineWidth(1.0f);

            // Warning: Change with configured color for borders
            gl.glColor3d(1, 1, 1);
            gl.glDisable(gl.GL_TEXTURE_2D);

            drawInfinitePlaneElements(gl, s, slices, stacks);
        }

        if ( q.isPointsSet() ) {
                //drawPoints(gl, s, slices, stacks);
        }
        if ( q.isNormalsSet() ) {
                //drawVertexNormals(gl, s, slices, stacks);
        }
        if ( q.isBoundingVolumeSet() ) {
                //JoglGeometryRenderer.drawMinMaxBox(gl, s, q);
        }
        if ( q.isSelectionCornersSet() ) {
                //JoglGeometryRenderer.drawSelectionCorners(gl, s, q);
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
