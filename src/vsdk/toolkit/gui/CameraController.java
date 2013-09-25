//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//===========================================================================

package vsdk.toolkit.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
//import java.awt.event.KeyEvent;

import vsdk.toolkit.environment.Camera;

public abstract class CameraController extends Controller {
    public abstract boolean processMouseEventAwt(MouseEvent mouseEvent);
    public abstract boolean processKeyPressedEvent(KeyEvent keyEvent);
    public abstract boolean processKeyPressedEventAwt(java.awt.event.KeyEvent keyEvent);
    public abstract boolean processKeyReleasedEventAwt(java.awt.event.KeyEvent keyEvent);
    public abstract boolean processMousePressedEventAwt(MouseEvent e);
    public abstract boolean processMouseReleasedEventAwt(MouseEvent e);
    public abstract boolean processMouseClickedEventAwt(MouseEvent e);
    public abstract boolean processMouseMovedEventAwt(MouseEvent e);
    public abstract boolean processMouseDraggedEventAwt(MouseEvent e);
    public abstract boolean processMouseWheelEventAwt(MouseWheelEvent e);
    public abstract Camera getCamera();
    public abstract void setCamera(Camera camera);
    public abstract void setDeltaMovement(double factor);
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
