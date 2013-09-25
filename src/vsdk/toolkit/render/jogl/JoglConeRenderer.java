//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 18 2006 - Oscar Chavarro: Original base version                 =
//= - March 28 2006 - Oscar Chavarro: First complete version with cylinder  =
//=   case support                                                          =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.geometry.Cone;

public class JoglConeRenderer extends JoglRenderer {

    private static GLU glu = null;
    private static GLUquadric gluQuadric = null;

    private static void drawParts(GL2 gl, Cone cone)
    {
        double r1, r2, h;
        int slices = 16;

        r1 = cone.getBaseRadius();
        r2 = cone.getTopRadius();
        h = cone.getHeight();

        VSDK.acumulatePrimitiveCount(VSDK.TRIANGLE, 4*slices);

        glu.gluCylinder(gluQuadric, r1, r2, h, slices, 1);
        gl.glPushMatrix();
        gl.glRotated(180, 1, 0, 0);
        glu.gluDisk(gluQuadric, 0, r1, slices, 1);
        gl.glPopMatrix();

        if ( r2 > 0.0 ) {
            gl.glPushMatrix();
            gl.glTranslated(0, 0, h);
            glu.gluDisk(gluQuadric, 0, r1, slices, 1);
            gl.glPopMatrix();
        }

    }

    /**
    Generate OpenGL/JOGL primitives needed for the rendering of recieved
    Geometry object.
    */
    public static void draw(GL2 gl, Cone cone, Camera c, RendererConfiguration q)
    {
        if ( glu == null ) {
            glu = new GLU();
            gluQuadric = glu.gluNewQuadric();
        }

        if ( q.isSurfacesSet() ) {
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
            drawParts(gl, cone);
        }

        if ( q.isWiresSet() ) {
            gl.glLineWidth(1);
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
            drawParts(gl, cone);
        }

        if ( q.isBoundingVolumeSet() ) {
            JoglGeometryRenderer.drawMinMaxBox(gl, cone, q);
        }
        if ( q.isSelectionCornersSet() ) {
            JoglGeometryRenderer.drawSelectionCorners(gl, cone, q);
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
