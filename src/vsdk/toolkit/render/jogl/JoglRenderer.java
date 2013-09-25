//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - July 25 2006 - Oscar Chavarro: Original base version                  =
//= - December 28 2006 - Oscar Chavarro: Added Nvidia Cg support            =
//===========================================================================

package vsdk.toolkit.render.jogl;

// Java base classes
import java.io.InputStream;
import java.io.FileInputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

// JOGL clases
import javax.media.opengl.GL2;
//import com.jogamp.opengl.cg.CgGL;
//import com.jogamp.opengl.cg.CGcontext;
//import com.jogamp.opengl.cg.CGprogram;
//import com.jogamp.opengl.cg.CGparameter;
//import com.jogamp.common.nio.Buffers;

// VitralSDK classes
import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.io.PersistenceElement;
import vsdk.toolkit.render.RenderingElement;

/**
The JoglRenderer abstract class provides an interface for Jogl*Renderer
style classes. This serves two purposes:
  - To help in design level organization of Jogl renderers (this eases the
    study of the class hierarchy)
  - To provide a place to locate operations common to all Jogl renderers
    classes and Jogl renderers' private utility/supporting classes. In this
    moment, this operations include the global framework for managing
    Nvidia Cg access and general JOGL initialization, as such verifying
    correct availavility of native OpenGL libraries.
*/

public abstract class JoglRenderer extends RenderingElement {
    // Nvidia Cg general management
    protected static boolean nvidiaCgErrorReported = false;
    private static boolean nvidiaCgAvailable = false;
    private static int nvidiaGpuVertexProfile = -1;
    private static int nvidiaGpuPixelProfile = -1;
    private static boolean renderingWithNvidiaGpuFlag = false;

    public static boolean verifyOpenGLAvailability()
    {
        return true;
    }

    public static void createDefaultAutomaticNvidiaCgShaders(String path)
    {
    }

    public static boolean setRenderingWithNvidiaGpu(boolean requested)
    {
        return false;
    }

    /**
    This method searches for the dinamic link library packed shared objects
    (.dll or .so.*) needed for Nvidia Cg to work. Returns true if libraries
    are found in standard system locations, false otherwise.

    Note that OpenGL doesn't need to be started before calling this method.
    */
    public static boolean verifyNvidiaCgAvailability()
    {
        return true;
    }

    public static boolean getNvidiaCgAvailability()
    {
        return true;
    }

    public static boolean tryToEnableNvidiaCg()
    {
        return true;
    }

    public static void enableNvidiaCgProfiles()
    {
    }

    public static void disableNvidiaCgProfiles()
    {
    }

    public static boolean needCg(RendererConfiguration quality)
    {
        return true;
    }

    public static void deactivateNvidiaGpuParameters(GL2 gl, RendererConfiguration quality)
    {
	return;
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
