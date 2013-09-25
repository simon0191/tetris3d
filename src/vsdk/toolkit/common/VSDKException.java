//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - December 8 2006 - Oscar Chavarro: Original base version               =
//===========================================================================

package vsdk.toolkit.common;

/**
The VSDKException abstract class provides an interface for *Exception
style classes inside the Vitral SDK. This serves two purposes:
  - To help in design level organization of exceptions (this eases the
    study of the class hierarchy)
  - To provide a place to locate possible future operations, common to
    all exceptions (but none of these as been detected yet)
*/

public abstract class VSDKException extends Exception {
   public VSDKException() {
   }

   public VSDKException(String message) {
       super(message);
   }

   public VSDKException(String message, Throwable cause) {
       super(message, cause);
   }

   public VSDKException(Throwable cause) {
       super(cause);
   }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
