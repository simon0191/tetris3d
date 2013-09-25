//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 20 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;

import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.geometry.Box;

public class JoglBoxRenderer extends JoglRenderer {

    private static GLUT glut = null;

    private static void drawSolidUnitCube(GL2 gl)
    {
        double l = 0.5;

        gl.glBegin(gl.GL_QUADS);
            // Down
            gl.glNormal3d(0, 0, -1);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-l, -l, -l);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(-l, l, -l);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(l, l, -l);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(l, -l, -l);

            // Up
            gl.glNormal3d(0, 0, 1);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-l, -l, l);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(l, -l, l);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(l, l, l);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(-l, l, l);

            // Front
            gl.glNormal3d(0, -1, 0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-l, -l, l);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-l, -l, -l);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(l, -l, -l);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(l, -l, l);

            // Left
            gl.glNormal3d(-1, 0, 0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(-l, l, l);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(-l, l, -l);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(-l, -l, -l);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(-l, -l, l);

            // Back
            gl.glNormal3d(0, 1, 0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(l, l, l);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(l, l, -l);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(-l, l, -l);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(-l, l, l);

            // Right
            gl.glNormal3d(1, 0, 0);
            gl.glTexCoord2d(0, 1);
            gl.glVertex3d(l, -l, l);
            gl.glTexCoord2d(0, 0);
            gl.glVertex3d(l, -l, -l);
            gl.glTexCoord2d(1, 0);
            gl.glVertex3d(l, l, -l);
            gl.glTexCoord2d(1, 1);
            gl.glVertex3d(l, l, l);
        gl.glEnd();
    }

    /**
    Generate OpenGL/JOGL primitives needed for the rendering of recieved
    Geometry object.
    */
    public static void draw(GL2 gl, Box box, Camera c, RendererConfiguration q)
    {
        if (glut == null) {
            glut = new GLUT();
        }

        Vector3D size = box.getSize();

        gl.glPushMatrix();
        gl.glEnable(gl.GL_NORMALIZE);
        gl.glScaled(size.x, size.y, size.z);
        if ( q.isSurfacesSet() ) {
            JoglGeometryRenderer.prepareSurfaceQuality(gl, q);
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
            drawSolidUnitCube(gl);
        }

        if ( q.isWiresSet() ) {
            gl.glLineWidth(1);
            glut.glutWireCube(1);
        }
        gl.glPopMatrix();

        if ( q.isBoundingVolumeSet() ) {
            JoglGeometryRenderer.drawMinMaxBox(gl, box, q);
        }
        if ( q.isSelectionCornersSet() ) {
            JoglGeometryRenderer.drawSelectionCorners(gl, box, q);
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
