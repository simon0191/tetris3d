//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - November 25 2005 - Oscar Chavarro: Original base version              =
//= - December 7 2005 - Fabio Aroca / Eduardo Mendoza: importJOGLimage and  =
//=   getImageJOGL methods added                                            =
//= - March 14 2006 - Oscar Chavarro: quality check                         =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.media.Image;
import vsdk.toolkit.media.RGBImage;
import vsdk.toolkit.media.RGBAImage;

public class JoglImageRenderer extends JoglRenderer
{
    /**
    This method generates an OpenGL/JOGL MipMap structure, assoiates it with
    the given image reference and activates.

    The method keeps track of all images activated, and take that history into
    account to pass the image data to the graphics hardware only once. Note that
    this method creates and use an OpenGL/JOGL compilation list for each image,
    to ensure optimal performance.

    \todo
    In applications with changing images, the memory list of compiled lists
    and the list themselves should be cleared, or not used. This will lead to
    the creation of new methods.
    */
    public static int activate(GL2 gl, Image img)
    {
        if ( img instanceof RGBAImage ) {
            return JoglRGBAImageRenderer.activate(gl, (RGBAImage)img);
        }
        else if ( img instanceof RGBImage ) {
            return JoglRGBImageRenderer.activate(gl, (RGBImage)img);
        }
        else {
            String c = img.getClass().getName();

            VSDK.reportMessage(null, VSDK.WARNING, "JoglImageRenderer.activate",
            "Image GL activation not implemented for subclass " + c);
        }
        return -1;
    }

    public static void deactivate(GL2 gl, Image img)
    {
        if ( img instanceof RGBImage ) {
            JoglRGBImageRenderer.deactivate(gl, ((RGBImage)img));
	}
    }

    public static int activateAsNormalMap(GL2 gl, Image img, RendererConfiguration quality)
    {
        if ( img instanceof RGBAImage ) {
            return JoglRGBAImageRenderer.activateAsNormalMap(gl, (RGBAImage)img, quality);
        }
        else if ( img instanceof RGBImage ) {
            return JoglRGBImageRenderer.activateAsNormalMap(gl, (RGBImage)img, quality);
        }
        else {
            String c = img.getClass().getName();

            VSDK.reportMessage(null, VSDK.WARNING, "JoglImageRenderer.activateAsNormalMap",
            "Image GL activation not implemented for subclass " + c);
        }
        return -1;
    }

    public static void unload(GL2 gl, Image img)
    {
        if ( img instanceof RGBImage ) {
            JoglRGBImageRenderer.unload(gl, (RGBImage)img);
        }
        else if ( img instanceof RGBAImage ) {
            JoglRGBAImageRenderer.unload(gl, (RGBAImage)img);
        }
        else {
            String c = img.getClass().getName();

            VSDK.reportMessage(null, VSDK.WARNING, "JoglImageRenderer.unload",
            "Image GL unloading not implemented for subclass " + c);
        }
    }

    public static void draw(GL2 gl, Image img)
    {
        if ( img instanceof RGBAImage ) {
            JoglRGBAImageRenderer.draw(gl, (RGBAImage)img);
        }
        else if ( img instanceof RGBImage ) {
            JoglRGBImageRenderer.draw(gl, (RGBImage)img);
        }
        else {
            String c = img.getClass().getName();

            VSDK.reportMessage(null, VSDK.WARNING, "JoglImageRenderer.draw",
            "Image GL drawing not implemented for subclass " + c);
        }
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
