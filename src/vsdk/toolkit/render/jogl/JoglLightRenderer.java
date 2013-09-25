//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 17 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.render.jogl;

// Java basic
import java.util.ArrayList;

// JOGL classes
import javax.media.opengl.GL2;

// VitralSDK classes
import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.environment.Light;

public class JoglLightRenderer extends JoglRenderer {

    public static int supportedLightsInOpenGL = 8;

    public static void deactivate(GL2 gl, Light l)
    {
        int lightNumber = l.getId();

        if ( lightNumber >= supportedLightsInOpenGL || lightNumber < 0 ) {
            return;
        }
        gl.glDisable(gl.GL_LIGHT0 + lightNumber);
    }

    public static void activate(GL2 gl, Light l)
    {
        //-----------------------------------------------------------------
        float[] lightPosition=l.getPosition().exportToFloatArrayVect();
        float global_ambient[] = {0, 0, 0, 1};
        float global_twoside[] = {gl.GL_TRUE};  // WARNING: This is inefficient!
        int lightNumber = l.getId();

        if ( lightNumber >= supportedLightsInOpenGL || lightNumber < 0 ) {
            return;
        }

        lightPosition[3]=1.0f;
/*
        if ( l.getLightType() == Light.DIRECTIONAL ) { // Why?
            lightPosition[3]=0;
        }
*/
        gl.glPushMatrix();
        gl.glLoadIdentity();
        gl.glLightModelfv(gl.GL_LIGHT_MODEL_AMBIENT, global_ambient, 0);   // OJO! Esta
        gl.glLightModelfv(gl.GL_LIGHT_MODEL_TWO_SIDE, global_twoside, 0);  // cableado!
        gl.glLightModeli(gl.GL_LIGHT_MODEL_LOCAL_VIEWER, gl.GL_TRUE); // OJO: ?
        gl.glEnable(gl.GL_LIGHTING);  // Is it right to have this here and re-set all the time?
        gl.glEnable(gl.GL_LIGHT0 + lightNumber);

        gl.glLightfv(gl.GL_LIGHT0 + lightNumber, gl.GL_POSITION, lightPosition, 0);
        gl.glLightfv(gl.GL_LIGHT0 + lightNumber, gl.GL_AMBIENT, l.getAmbient().exportToFloatArrayVect(), 0);
        gl.glLightfv(gl.GL_LIGHT0 + lightNumber, gl.GL_DIFFUSE, l.getDiffuse().exportToFloatArrayVect(), 0);
        gl.glLightfv(gl.GL_LIGHT0 + lightNumber, gl.GL_SPECULAR, l.getSpecular().exportToFloatArrayVect(), 0);
        
/*
        gl.glLightf(gl.GL_LIGHT0 + lightNumber, gl.GL_CONSTANT_ATTENUATION, constantAtenuation);
        gl.glLightf(gl.GL_LIGHT0 + lightNumber, gl.GL_LINEAR_ATTENUATION, linearAtenuation);
        gl.glLightf(gl.GL_LIGHT0 + lightNumber, gl.GL_QUADRATIC_ATTENUATION, quadricAtenuation);
*/
        gl.glPopMatrix();
    }

    public static void draw(GL2 gl, Light l)
    {
        Vector3D p = l.getPosition();
        ColorRgb c = l.getSpecular();
        double delta = 0.1;

        gl.glPushMatrix();
        gl.glDisable(gl.GL_LIGHTING);
        gl.glDisable(gl.GL_TEXTURE_2D);
        gl.glLineWidth(2.0f);
        gl.glBegin(gl.GL_LINES);
            gl.glColor3d(c.r, c.g, c.b);
            gl.glVertex3d(p.x - delta, p.y, p.z);
            gl.glVertex3d(p.x + delta, p.y, p.z);
            gl.glVertex3d(p.x, p.y - delta, p.z);
            gl.glVertex3d(p.x, p.y + delta, p.z);
            gl.glVertex3d(p.x, p.y, p.z - delta);
            gl.glVertex3d(p.x, p.y, p.z + delta);
        gl.glEnd();
        gl.glPopMatrix();
    }

    public static void turnOffAllLights(GL2 gl)
    {
        int i;

        for ( i = 0; i < supportedLightsInOpenGL; i++ ) {
            gl.glDisable(gl.GL_LIGHT0 + i);
        }
    }
    
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
