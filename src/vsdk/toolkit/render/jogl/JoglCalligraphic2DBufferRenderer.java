//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - November 5 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;

import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.media.Calligraphic2DBuffer;

public class JoglCalligraphic2DBufferRenderer extends JoglRenderer
{
    public static void draw(GL2 gl, Calligraphic2DBuffer vectors)
    {
        int i;
        Vector3D p0 = new Vector3D();
        Vector3D p1 = new Vector3D();

        gl.glBegin(gl.GL_LINES);
        for ( i = 0; i < vectors.getNumLines(); i++ ) {
            vectors.get2DLine(i, p0, p1);
            gl.glVertex3d(p0.x, p0.y, p0.z);
            gl.glVertex3d(p1.x, p1.y, p1.z);
        }
        gl.glEnd();
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
