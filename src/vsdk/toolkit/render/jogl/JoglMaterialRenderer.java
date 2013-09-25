//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - March 17 2006 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;
//import com.jogamp.opengl.cg.CgGL;
//import com.jogamp.opengl.cg.CGprogram;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.environment.Material;

public class JoglMaterialRenderer extends JoglRenderer {
    private static boolean errorReported = false;
    private static boolean disablingTransparency = false;

    public static void activate(GL2 gl, Material m)
    {
        //-----------------------------------------------------------------
        if ( m == null ) {
            if ( errorReported == false ) {
                VSDK.reportMessage(null, VSDK.WARNING, 
                    "JoglMaterialRenderer.activate", 
                    "Trying to activate null reference to Material." + 
                    " Avoiding further reporting.");
                errorReported = true;
            }
            return;
        }

        float opacity = (float)m.getOpacity();

        if ( opacity > 1.0f ) opacity = 1.0f;
        if ( opacity < 0.0f ) opacity = 0.0f;
        if ( opacity < 1.0f - Float.MIN_VALUE ) {
            gl.glEnable(gl.GL_BLEND);
            gl.glBlendFunc(gl.GL_SRC_ALPHA, gl.GL_ONE_MINUS_SRC_ALPHA);
            disablingTransparency = true;
          }
          else {
            // Note: Transparency blending is not compatible with
            // blending technique for anaglyphs...
            if ( disablingTransparency ) {
                gl.glDisable(gl.GL_BLEND);
            }
        }

        float phongExp = (float)m.getPhongExponent();
        float ambient[] = m.getAmbient().exportToFloatArrayVect();
        ambient[3] = opacity;
        float diffuse[] = m.getDiffuse().exportToFloatArrayVect();
        diffuse[3] = opacity;
        float specular[]  = m.getSpecular().exportToFloatArrayVect();
        specular[3] = opacity;
        //float emission[] = m.getEmission().exportToFloatArrayVect();
        //emission[3] = opacity;

        if ( m.isDoubleSided() ) {
            gl.glDisable(gl.GL_CULL_FACE);
          }
          else {
            gl.glEnable(gl.GL_CULL_FACE);
            gl.glCullFace(gl.GL_BACK);
        }

        gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_AMBIENT, ambient, 0);
        gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_DIFFUSE, diffuse, 0);
        gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_SPECULAR, specular, 0);
        //gl.glMaterialfv(gl.GL_FRONT_AND_BACK, gl.GL_EMISSION, emission, 0); // Do not set! take care!
        gl.glMaterialf(gl.GL_FRONT_AND_BACK, gl.GL_SHININESS, phongExp);
    }
/*
    public static void activateNvidiaGpuParameters(GL2 gl, Material material,
        CGprogram vertexShader, CGprogram pixelShader)
    {
        double Ka[] = material.getAmbient().exportToDoubleArrayVect();
        double Kd[] = material.getDiffuse().exportToDoubleArrayVect();
        double Ks[] = material.getSpecular().exportToDoubleArrayVect();
        CgGL.cgGLSetParameter3dv(CgGL.cgGetNamedParameter(
            vertexShader, "ambientColor"), Ka, 0);
        CgGL.cgGLSetParameter3dv(CgGL.cgGetNamedParameter(
            vertexShader, "diffuseColor"), Kd, 0);
        CgGL.cgGLSetParameter3dv(CgGL.cgGetNamedParameter(
            vertexShader, "specularColor"), Ks, 0);
        CgGL.cgGLSetParameter1d(CgGL.cgGetNamedParameter(
            pixelShader, "phongExponent"), material.getPhongExponent());
    }
*/
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
