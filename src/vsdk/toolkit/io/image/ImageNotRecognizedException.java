//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - September 2 2005 - David Diaz: Original base version                  =
//= - May 22 2006 - David Diaz/Oscar Chavarro: documentation added          =
//===========================================================================

package vsdk.toolkit.io.image;
import java.io.File;

import vsdk.toolkit.common.VSDKException;

/**
This class represents the exception in which an image can not be handled
by an image persistence operation.
*/
public class ImageNotRecognizedException extends VSDKException
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    File image;

    /**
    Constructs an Image exception with a message and the image file that
    has been processed when the exception was generated.
    @param message The message to display in the stack trace
    @param image The image file that caused the exception
    */
    public ImageNotRecognizedException(String message, File image) 
    {
        super(message);
        this.image=image;
    }

    /**
    This method returns the image file that has been processed when this
    Exception was generated
    @return The image file that has been processed when the exception
    was generated
    */
    public File getImage()
    {
       return image;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
