//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 29 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.geometry.Arrow;

public class JoglArrowRenderer extends JoglRenderer {

    private static GLU glu = null;
    private static GLUquadric gluQuadric = null;

    private static void drawParts(GL2 gl, Arrow arrow)
    {
        double r1, r2, h1, h2;

        h1 = arrow.getBaseLength();
        h2 = arrow.getHeadLength();
        r1 = arrow.getBaseRadius();
        r2 = arrow.getHeadRadius();

        glu.gluCylinder(gluQuadric, r1, r1, h1, 16, 1);
        gl.glPushMatrix();
        gl.glRotated(180, 1, 0, 0);
        glu.gluDisk(gluQuadric, 0, r1, 16, 1);
        gl.glPopMatrix();

        gl.glPushMatrix();
        gl.glTranslated(0, 0, h1);
        glu.gluCylinder(gluQuadric, r2, 0, h2, 16, 1);
        gl.glRotated(180, 1, 0, 0);
        glu.gluDisk(gluQuadric, r1, r2, 16, 1);

        gl.glPopMatrix();
    }

    /**
    Generate OpenGL/JOGL primitives needed for the rendering of recieved
    Geometry object.
    */
    public static void draw(GL2 gl, Arrow arrow, Camera c, RendererConfiguration q)
    {
        if (glu == null) {
            glu = new GLU();
            gluQuadric = glu.gluNewQuadric();
        }

        if ( q.isSurfacesSet() ) {
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
            gl.glEnable(gl.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);
            drawParts(gl, arrow);
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

            drawParts(gl, arrow);
        }

        if ( q.isPointsSet() ) {
            JoglRenderer.disableNvidiaCgProfiles();
            gl.glDisable(gl.GL_LIGHTING);
            gl.glDisable(gl.GL_CULL_FACE);
            gl.glShadeModel(gl.GL_FLAT);

            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_POINTS);
            gl.glEnable(gl.GL_POLYGON_OFFSET_LINE);
            gl.glPolygonOffset(-0.5f, 0.0f);
            gl.glLineWidth(1.0f);

            // Warning: Change with configured color for borders
            gl.glColor3d(1, 1, 1);
            gl.glDisable(gl.GL_TEXTURE_2D);

            drawParts(gl, arrow);
        }

        if ( q.isBoundingVolumeSet() ) {
            JoglGeometryRenderer.drawMinMaxBox(gl, arrow, q);
        }
        if ( q.isSelectionCornersSet() ) {
            JoglGeometryRenderer.drawSelectionCorners(gl, arrow, q);
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
