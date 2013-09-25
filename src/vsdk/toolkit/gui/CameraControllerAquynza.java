//===========================================================================

package vsdk.toolkit.gui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
//import java.awt.event.KeyEvent; // Do not include, deprecated

import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.environment.Camera;

public class CameraControllerAquynza extends CameraController {

    private Camera camera;
    private int oldMouseX;
    private int oldMouseY;
    private double deltaMov;

    public CameraControllerAquynza(Camera camera) {
        this.camera = camera;
        oldMouseX = 0;
        oldMouseY = 0;
        deltaMov = 0.25;
    }

    public double getDeltaMovement()
    {
        return deltaMov;
    }

    public void setDeltaMovement(double val)
    {
        deltaMov = val;
    }

    public boolean processMouseEventAwt(MouseEvent mouseEvent) {
      return true;
    }

    private double augmentLogarithmic(double val, double EPSILON)
    {
        if ( val < 0.001 ) val += 0.0001;
        else if ( val < 0.01 ) val += 0.001;
        else if ( val < 0.1 - EPSILON ) val += 0.01;
        else if ( val < 1 - EPSILON ) val += 0.1;
        else if ( val < 10 - EPSILON ) val += 1;
        else if ( val < 100 - EPSILON ) val += 10;
        else if ( val < 1000 - EPSILON ) val += 100;
        else if ( val < 10000 - EPSILON ) val += 1000;
        else if ( val < 100000 - EPSILON ) val += 10000;
        else if ( val < 1000000 - EPSILON ) val += 100000;
        else if ( val < 10000000 - EPSILON ) val *= 2;
        else val = 10000000;
  
        return val;
    }
  
    private double diminishLogarithmic(double val, double EPSILON)
    {
        if ( val > 10000000 + EPSILON ) val /= 2;
        else if ( val > 1000000 + EPSILON ) val -= 1000000;
        else if ( val > 100000 + EPSILON ) val -= 100000;
        else if ( val > 10000 + EPSILON ) val -= 10000;
        else if ( val > 1000 + EPSILON ) val -= 1000;
        else if ( val > 100 + EPSILON ) val -= 100;
        else if ( val > 10 + EPSILON ) val -= 10;
        else if ( val > 1 + EPSILON ) val -= 1;
        else if ( val > 0.1 + EPSILON ) val -= 0.1;
        else if ( val > 0.01 + EPSILON ) val -= 0.01;
        else if ( val > 0.001 + EPSILON ) val -= 0.001;
        else if ( val > 0.0001 + EPSILON ) val -= 0.0001;
        else val = 0.0001;
        return val;
    }
  
    public boolean processKeyPressedEvent(KeyEvent keyEvent) {
        // Local copy of the Camera's internal parameters
        Vector3D eyePosition;
        Vector3D focusedPosition;
        Matrix4x4 R; // Camera rotation matrix
        int projectionMode;
        double fov;
        double orthogonalZoom;
        double nearPlaneDistance;
        double farPlaneDistance;

        // Internal variables to control the interaction
        char unicode_id;
        int keycode;
        double yaw;
        double pitch;
        double roll;
        double angleInc;
        boolean updated = false;
        double EPSILON = 0.0001;

        // 1. Obtain a copy of the camera's internal parameters
        eyePosition = camera.getPosition();
        focusedPosition = camera.getFocusedPosition();
        R = camera.getRotation();
        projectionMode = camera.getProjectionMode();
        fov = camera.getFov();
        orthogonalZoom = camera.getOrthogonalZoom();
        nearPlaneDistance = camera.getNearPlaneDistance();
        farPlaneDistance = camera.getFarPlaneDistance();

        // 2. Calculate variables used for interaction manipulation
        yaw = R.obtainEulerYawAngle();
        pitch = R.obtainEulerPitchAngle();
        roll = R.obtainEulerRollAngle();
  
        if ( fov > 90 ) angleInc = Math.toRadians(10);
        else if ( fov > 45 ) angleInc = Math.toRadians(5);
        else if ( fov > 15 ) angleInc = Math.toRadians(2.5);
        else if ( fov > 5 ) angleInc = Math.toRadians(1);
        else angleInc = Math.toRadians(0.1);

        // 3. Event processing: update the copy of the camera's internal parameters
        switch ( keyEvent.keycode ) {
          case KeyEvent.KEY_UP:
            pitch -= angleInc;
            if ( pitch < Math.toRadians(-90) ) pitch = Math.toRadians(-90);
            updated = true;
            break;
          case KeyEvent.KEY_DOWN:
            pitch += angleInc;
            if ( pitch > Math.toRadians(90) ) pitch = Math.toRadians(90);
            updated = true;
            break;
          case KeyEvent.KEY_LEFT:
            yaw += angleInc;
            while ( yaw >= Math.toRadians(360) ) yaw -= Math.toRadians(360);
            updated = true;
            break;
          case KeyEvent.KEY_RIGHT:
            yaw -= angleInc;
            while ( yaw < 0 ) yaw += Math.toRadians(360);
            updated = true;
            break;

          // Position
          case KeyEvent.KEY_x:
            eyePosition.x -= deltaMov; focusedPosition.x -= deltaMov;
            updated = true;
            break;
          case KeyEvent.KEY_X:
            eyePosition.x += deltaMov; focusedPosition.x += deltaMov;
            updated = true;
            break;
          case KeyEvent.KEY_y:
            eyePosition.y -= deltaMov; focusedPosition.y -= deltaMov;
            updated = true;
            break;
          case KeyEvent.KEY_Y:
            eyePosition.y += deltaMov; focusedPosition.y += deltaMov;
            updated = true;
            break;
          case KeyEvent.KEY_z:
            eyePosition.z -= deltaMov; focusedPosition.z -= deltaMov;
            updated = true;
            break;
          case KeyEvent.KEY_Z:
            eyePosition.z += deltaMov; focusedPosition.z += deltaMov;
            updated = true;
            break; 
          // Rotation
          case KeyEvent.KEY_S:
            roll -= Math.toRadians(5);
            while ( roll < 0 ) roll += Math.toRadians(360);
            updated = true;
            break;
          case KeyEvent.KEY_s:
            roll += Math.toRadians(5);
            while ( roll > Math.toRadians(360) ) roll -= Math.toRadians(360);
            updated = true;
            break;
  
          // View volume modification
          case KeyEvent.KEY_A:
            if ( camera.getProjectionMode() == camera.PROJECTION_MODE_ORTHOGONAL ) {
                orthogonalZoom /= 2;
              }
              else {
                if ( fov < 0.1 - EPSILON ) fov += 0.1;
                else if ( fov < 1 - EPSILON ) fov++;
                else if ( fov < 175 - EPSILON ) fov += 5;
            }
            updated = true;
            break;
          case KeyEvent.KEY_a:
            if ( camera.getProjectionMode() == camera.PROJECTION_MODE_ORTHOGONAL ) {
                orthogonalZoom *= 2;
              }
              else {
                if ( fov > 5 + EPSILON ) fov -= 5;
                else if ( fov > 1 + EPSILON  ) fov--;
                else if ( fov > 0.1 + EPSILON  ) fov -= 0.1;
            }
            updated = true;
            break;
  
          case KeyEvent.KEY_N:
            nearPlaneDistance = augmentLogarithmic(nearPlaneDistance, EPSILON);
            updated = true;
            break;
          case KeyEvent.KEY_n:
            nearPlaneDistance = diminishLogarithmic(nearPlaneDistance, EPSILON);
            updated = true;
            break;
  
          case KeyEvent.KEY_F:
            farPlaneDistance = augmentLogarithmic(farPlaneDistance, EPSILON);
            updated = true;
            break;
          case KeyEvent.KEY_f:
            farPlaneDistance = diminishLogarithmic(farPlaneDistance, EPSILON);
            updated = true;
            break;
  
          case KeyEvent.KEY_p: // Rote el modo de proyeccion
            switch ( projectionMode ) {
              case Camera.PROJECTION_MODE_PERSPECTIVE:
                projectionMode = Camera.PROJECTION_MODE_ORTHOGONAL;
                break;
              default:
                projectionMode = Camera.PROJECTION_MODE_PERSPECTIVE;
                break;
            }
            updated = true;
            break;
  
          // Queries
          case KeyEvent.KEY_i:
            System.out.println(camera);
            break;

        }

        // 4. Update camera's internal parameters from local copy
        R.eulerAnglesRotation(yaw, pitch, roll);
  
        camera.setPosition(eyePosition);
        camera.setFocusedPositionMaintainingOrthogonality(focusedPosition);
        camera.setRotation(R);
        camera.setOrthogonalZoom(orthogonalZoom);
        camera.setFov(fov);
        camera.setProjectionMode(projectionMode);
        camera.setNearPlaneDistance(nearPlaneDistance);
        camera.setFarPlaneDistance(farPlaneDistance);
  
        return updated;
    }

    /**
    @deprecated
    Migrated to generalized version of this class, which is
    independent of Awt.

    This method has been deprecated in january 1st 2008. It should be
    deleted by june 1st 2008.
    */
    @Deprecated
    public boolean processKeyPressedEventAwt(java.awt.event.KeyEvent keyEvent) {
        return processKeyPressedEvent(AwtSystem.awt2vsdkEvent(keyEvent));
    }

    public boolean processKeyReleasedEventAwt(java.awt.event.KeyEvent keyEvent) {
        return false;
    }

    public boolean processMousePressedEventAwt(MouseEvent e)
    {
        oldMouseX = e.getX();
        oldMouseY = e.getX();
        return false;
    }

    public boolean processMouseReleasedEventAwt(MouseEvent e)
    {
        return false;
    }

    public boolean processMouseClickedEventAwt(MouseEvent e)
    {
        return false;
    }

    public boolean processMouseMovedEventAwt(MouseEvent e)
    {
        return false;
    }

    public boolean processMouseDraggedEventAwt(MouseEvent e)
    {
        //------------------------------------------------------------
        int deltaX;
        int deltaY;
        boolean updated = false;
        double senseFactor = deltaMov/5;

        deltaX = e.getX() - oldMouseX;
        deltaY = e.getY() - oldMouseY;

        if ( deltaX > 5 ) deltaX = 5;
        if ( deltaX < -5 ) deltaX = -5;
        if ( deltaY > 5 ) deltaY = 5;
        if ( deltaY < -5 ) deltaY = -5;

        //------------------------------------------------------------
        Matrix4x4 R; // Camera rotation matrix
        Matrix4x4 DR;
        Vector3D eyePosition;
        Vector3D focusedPosition;
        double ax, ay;

        // Obtain a copy of the camera's internal parameters
        eyePosition = camera.getPosition();
        focusedPosition = camera.getFocusedPosition();

        Vector3D u, v, w;
        int modifiers;

        modifiers = e.getModifiersEx();

        R = camera.getRotation();
        u = new Vector3D(R.M[0][0], R.M[1][0], R.M[2][0]);
        v = new Vector3D(R.M[0][1], R.M[1][1], R.M[2][1]);
        w = new Vector3D(R.M[0][2], R.M[1][2], R.M[2][2]);

        if ( (modifiers & e.BUTTON1_DOWN_MASK) != 0 ) {
            // Turn
            ax = -Math.min(2, 0.01*deltaX);
            ay = Math.min(2, 0.01*deltaY);

            DR = new Matrix4x4();
            DR.axisRotation(ay, v.x, v.y, v.z);
            R = DR.multiply(R);

            DR.axisRotation(ax, w.x, w.y, w.z);
            R = DR.multiply(R);

            updated = true;
        }
        else if ( (modifiers & e.BUTTON2_DOWN_MASK) != 0 ) {
            // Move
            eyePosition = eyePosition.substract(v.multiply(senseFactor*((double)deltaX)));
            eyePosition = eyePosition.substract(w.multiply(senseFactor*((double)deltaY)));
            focusedPosition = focusedPosition.substract(v.multiply(senseFactor*((double)deltaX)));
            focusedPosition = focusedPosition.substract(w.multiply(senseFactor*((double)deltaY)));
            updated = true;
        }
        else if ( (modifiers & e.BUTTON3_DOWN_MASK) != 0 ) {
            // Advance
            eyePosition = eyePosition.substract(u.multiply(senseFactor*((double)deltaY)));
            ax = Math.min(2, 0.01*deltaX);
            DR = new Matrix4x4();
            DR.axisRotation(ax, u.x, u.y, u.z);
            R = DR.multiply(R);
            updated = true;
        }

        // Update camera's internal parameters from local copy
        //R.eulerAnglesRotation(yaw, pitch, roll);
        camera.setPosition(eyePosition);
        camera.setFocusedPositionMaintainingOrthogonality(focusedPosition);
        camera.setRotation(R);

        //------------------------------------------------------------
        oldMouseX = e.getX();  
        oldMouseY = e.getY();
        return updated;
    }

    public boolean processMouseWheelEventAwt(MouseWheelEvent e)
    {
        //------------------------------------------------------------
        double fov, angleInc;
        boolean updated = false;
        double EPSILON = 0.0001;
        double orthogonalZoom;

        fov = camera.getFov();
        orthogonalZoom = camera.getOrthogonalZoom();

        if ( fov > 90 ) angleInc = Math.toRadians(10);
        else if ( fov > 45 ) angleInc = Math.toRadians(5);
        else if ( fov > 15 ) angleInc = Math.toRadians(2.5);
        else if ( fov > 5 ) angleInc = Math.toRadians(1);
        else angleInc = Math.toRadians(0.1);

        int clicks = e.getWheelRotation();

        //------------------------------------------------------------
        if ( clicks > 0 ) {
            if ( camera.getProjectionMode() == camera.PROJECTION_MODE_ORTHOGONAL ) {
                orthogonalZoom /= clicks;
              }
              else {
                if ( fov < 0.1 - EPSILON ) fov += 0.1*clicks;
                else if ( fov < 1 - EPSILON ) fov += clicks;
                else if ( fov < 175 - EPSILON ) fov += 5*clicks;
            }
            updated = true;
        }
        else if ( clicks < 0 ) {
            if ( camera.getProjectionMode() == camera.PROJECTION_MODE_ORTHOGONAL ) {
                orthogonalZoom *= 2*clicks;
              }
              else {
                if ( fov > 5 + EPSILON ) fov -= 5*clicks;
                else if ( fov > 1 + EPSILON  ) fov -= clicks;
                else if ( fov > 0.1 + EPSILON  ) fov -= 0.1*clicks;
            }
            updated = true;
        }

        //------------------------------------------------------------
        camera.setFov(fov);
        camera.setOrthogonalZoom(orthogonalZoom);

        return updated;
    }

    public Camera getCamera()
    {
        return camera;
    }

    public void setCamera(Camera camera)
    {
        this.camera = camera;
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
