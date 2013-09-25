//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 8 2005 - Oscar Chavarro: Original base version                 =
//= - August 24 2005 - David Diaz / Cesar Bustacara: Design changes to      =
//=   decouple JOGL from the Camera data model, extra utilitary methods     =
//=   added                                                                 =
//= - August 25 2005 - Oscar Chavarro: English translation of comments      =
//= - September 12 2005 - Oscar Chavarro: generateRay updated               =
//= - November 15 2005 - Oscar Chavarro: generateRay updated (Bug?)         =
//= - November 23 2005 - Oscar Chavarro: updated methods for direct access  =
//=   of coordinate base system and access maintaining ortoghonality.       =
//= - November 24 2005 - Oscar Chavarro: new generateRay algorithm, now     =
//=   consistent with JOGL/OpenGL transformation interpretation.            =
//= - April 7 2006 - Oscar Chavarro: calculateUPlaneAtPixel, proyectPoint   =
//= - November 5 2006 - Oscar Chavarro: plane calculation methods updated   =
//= - November 5 2006 - Oscar Chavarro: added Cohen-Sutherland line         =
//=   clipping functionality                                                =
//===========================================================================

package vsdk.toolkit.environment;

import vsdk.toolkit.common.VSDK;
import vsdk.toolkit.common.Entity;
import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.Ray;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.environment.geometry.Geometry;

public class Camera extends Entity
{
    /// Check the general attribute description in superclass Entity.
    public static final long serialVersionUID = 20060502L;

    // Basic Camera Model
    private Vector3D up;
    private Vector3D front;
    private Vector3D left;
    private Vector3D eyePosition;
    private double focalDistance;
    private int projectionMode;
    private double fov;
    private double orthogonalZoom;
    private double nearPlaneDistance;
    private double farPlaneDistance;

    /// This string should be used for specific application defined
    /// functionality. Can be null.
    private String name;

    // Global constants
    public static final int OPCODE_FAR = (0x01 << 1);
    public static final int OPCODE_NEAR = (0x01 << 2);
    public static final int OPCODE_RIGHT = (0x01 << 3);
    public static final int OPCODE_LEFT = (0x01 << 4);
    public static final int OPCODE_UP = (0x01 << 5);
    public static final int OPCODE_DOWN = (0x01 << 6);

    public static final int PROJECTION_MODE_ORTHOGONAL = 4;
    public static final int PROJECTION_MODE_PERSPECTIVE = 5;

    /**
    Una `Camera` debe saber de qu&eacute; tama&ntilde;o es el viewport para
    el cual est&aacute; generando una proyecci&oacute;n, para poder modificar
    sus par&aacute;metros internos en funci&oacute;n del &aacute;ngulo de
    vision (`fov`) y de la actual proporci&oacute;n de ancho/alto del
    viewport. Las variables internas `viewportXSize` y `viewportYSize`
    representan el tama&nacute;o en pixels para el viewport, y son valores
    que solo pueden ser cambiados por el m&eacute;todo 
    `Camera::updateViewportResize`. Estos dos valores son para uso interno de
    la clase c&aacute;mara y no pueden ser consultados (i.e. son una copia
    de la configuraci&oacute;n del viewport, que debe ser administrado por
    la aplicaci&oacute;n que use `Camera`s).
    */
    private double viewportXSize;
    /// Check `viewportXSize`'s documentation
    private double viewportYSize;

    // Private values which are preprocessed to speed up calculations
    private Vector3D dx, dy, _dir, upWithScale, rightWithScale;
    private Matrix4x4 normalizingTransformation;
    
    public Camera() 
    {
        eyePosition = new Vector3D(0,-5,1);
        
        up = new Vector3D(0,0,1);
        front=new Vector3D(0,1,0);
        left=new Vector3D(-1,0,0);
        
        fov = 60;
        viewportXSize = 320;
        viewportYSize = 320;

        projectionMode = PROJECTION_MODE_PERSPECTIVE;
        orthogonalZoom = 1;
        nearPlaneDistance = 0.05;
        farPlaneDistance = 100;

        focalDistance = 10;
        updateVectors();
    }

    public Camera(Camera b)
    {
        eyePosition = new Vector3D(b.eyePosition);
        
        up = new Vector3D(b.up);
        front=new Vector3D(b.front);
        left=new Vector3D(b.left);
        
        fov = b.fov;
        viewportXSize = b.viewportXSize;
        viewportYSize = b.viewportYSize;

        projectionMode = b.projectionMode;
        orthogonalZoom = b.orthogonalZoom;
        nearPlaneDistance = b.nearPlaneDistance;
        farPlaneDistance = b.farPlaneDistance;

        focalDistance = b.focalDistance;

        updateVectors();
    }

    public Matrix4x4 getNormalizingTransformation()
    {
        return normalizingTransformation;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String n)
    {
        name = new String(n);
    }

    public double getViewportXSize()
    {
        return viewportXSize;
    }

    public double getViewportYSize()
    {
        return viewportYSize;
    }

    public Vector3D getPosition()
    {
        return eyePosition;
    }

    public void setPosition(Vector3D eyePosition)
    {
        this.eyePosition.clone(eyePosition);
    }
    
    public Vector3D getFocusedPosition()
    {
        Vector3D partial;
        Vector3D result;
        partial = front.multiply(focalDistance);
        result = eyePosition.add(partial);
        return result;
    }

    /**
    This method changes the `front` unit vector and the `focalDistance` based 
    on `focusedPosition` parameter and current `eyePosition` value, WITHOUT
    changing any other vector. This method does NOT change the value of `up`
    of `left` vectors and can be used in advanced applications to directly
    access basic camera parameters.
     */
    public void setFocusedPositionDirect(Vector3D focusedPosition)
    {
        Vector3D partial;

        partial = focusedPosition.substract(eyePosition);
        front.clone(partial);
        focalDistance = front.length();
        front.normalize();
    }

    /**
    This method changes the `front` unit vector, the `focalDistance`, the 
    `left` vector and the `up` vector, based on:
      - `focusedPosition` parameter
      - current `eyePosition` value
      - current `up` vector (taken as a hint)
    This method CAN change the value of `up` and `left` vectors, to
    allow the user specify a left-handed orthogonal reference frame formed by
    the vectors <up, front, left>. The initial up vector is taken as a hint
    to specify a new up vector, similar to the original but forming a 90
    degree angle with the front direction. The left vector is always changed
    to form a third orthogonal vectors to former ones. Note that the three
    resulting vectors are left normalized.
    \todo
    This method FAILS if the initial value of `up` is parallel to the
    `front` direction. Validation and exception handling are needed.
     */
    public void setFocusedPositionMaintainingOrthogonality(Vector3D focusedPosition)
    {
        front.substract(focusedPosition, eyePosition);
        focalDistance = front.length();
        front.normalize();

        left = up.crossProduct(front);
        left.normalize();

        up = front.crossProduct(left);
        up.normalize();
    }

    public Vector3D getUp()
    {
        return up;
    }

    public Vector3D getUpWithScale()
    {
        return upWithScale;
    }

    public Vector3D getRightWithScale()
    {
        return rightWithScale;
    }

    public Vector3D getFront()
    {
        return front;
    }

    public Vector3D getLeft()
    {
        return left;
    }

    /**
    This method overwrites current `up` vector value, without considering the
    ill-case of getting `up` and `front` vectors pointing in the same
    direction.

    For non-advanced programmers, it is desireable to invoke the 
    `setUpMaintainingOrthogonality` method instead of this one.
    */
    public void setUpDirect(Vector3D up)
    {
        this.up.clone(up);
    }

    /**
    This method overwrites current `left` vector value, without considering the
    ill-case of getting `left` and `front` (or `up` vectors pointing in the
    same direction.

    For non-advanced programmers, it is desireable to invoke the 
    `setLeftMaintainingOrthogonality` method instead of this one.
    */
    public void setLeftDirect(Vector3D left)
    {
        this.left.clone(left);
    }

    /**
     * En este metodo no se tiene en cuenta si up y front quedan mirando 
       para el mismo lado
     */
    public void setUpMaintainingOrthogonality(Vector3D up)
    {
        up.normalize();

        left = up.crossProduct(front);
        left.normalize();
 
        this.up=front.crossProduct(left);
        this.up.normalize();
    }

    public double getFov()
    {
        return fov;
    }

    public void setFov(double fov)
    {
        this.fov = fov;
    }

    public double getNearPlaneDistance()
    {
        return nearPlaneDistance;
    }

    public void setNearPlaneDistance(double nearPlaneDistance)
    {
        this.nearPlaneDistance = nearPlaneDistance;
    }

    public double getFarPlaneDistance()
    {
        return farPlaneDistance;
    }

    public void setFarPlaneDistance(double farPlaneDistance)
    {
        this.farPlaneDistance = farPlaneDistance;
    }

    public int getProjectionMode()
    {
        return projectionMode;
    }

    public void setProjectionMode(int projectionMode)
    {
        this.projectionMode = projectionMode;
    }

    public void updateViewportResize(int dx, int dy)
    {
        viewportXSize = dx;
        viewportYSize = dy;
        updateVectors();
    }

    /**
    PRE:
      - focusedPosition y eyePosition tienen que ser diferentes!
    POST:
      - left queda normalizado
      - up queda normalizado
    @todo Document the way in which vectors are calculated, acording to
    the projection transformation.
    */
    
    public void updateVectors()
    {
        up.normalize();
        left.normalize();
        front.normalize();

        double fovFactor = viewportXSize / viewportYSize;
        _dir = front.multiply(0.5);
        upWithScale = up.multiply(Math.tan(Math.toRadians(fov/2)));
        rightWithScale = left.multiply(-fovFactor*Math.tan(Math.toRadians(fov/2)));

        //-----------------------------------------------------------------
        /*
        The normalizing transformation of current camera is such that transforms
        points in space to make it lie in the canonical view volume space,
        and it is calculated following the mechanism described on sections
        [FOLE1992].6.5.1 and [FOLE1992].6.5.2.
        */
        normalizingTransformation = new Matrix4x4();
        // 1. Translate the "VRP" point to the origin
        Vector3D VRP;
        Matrix4x4 T1 = new Matrix4x4();

        // Warning: near plane clipping
        VRP = eyePosition.add(front.multiply(nearPlaneDistance));
        //VRP = eyePosition.add(front.multiply(1));
        T1.translation(VRP.multiply(-1));

        // 2. Rotate the "VRC" coordinate system such as the front axis
        //    become the -z axis
        Matrix4x4 R1 = new Matrix4x4();
        Matrix4x4 R2 = new Matrix4x4();

        R1 = getRotation();
        R1.invert();
        R2.eulerAnglesRotation(Math.toRadians(90), Math.toRadians(-90), 0);

        // 3. Translate such that the center of projection is at the origin
        ;

        // 4. Shear such that the center line of the view volume becomes the
        //    z axis
        ;

        // 5. Scale such that the view volume becomes the canonical perspective
        //    view volume
        Matrix4x4 S1 = new Matrix4x4();
        Matrix4x4 S2 = new Matrix4x4();
        double dx, dy, dz;

        // 5.1. Non proportional scaling to adjust the slopes of the piramid
        // planes to fix 45 degrees in u and v directions
        dx = rightWithScale.length();
        dy = upWithScale.length();
        S1.scale(dx, dy, 1);
        S1.invert();

        // 5.2. Proportional scaling to adjust near / far clipping planes
        // maintaining the piramid form
        dz = nearPlaneDistance;
        S2.scale(dz, dz, dz);
        S2.invert();

        //
        normalizingTransformation = S2.multiply(S1.multiply(R2.multiply(R1.multiply(T1))));
    }

    /**
    Given a 2D integer coordinate in viewport space, this method calculates a
    proyector ray that emanates from the eye position and passes over the 
    (u, v) float coordinate in the projection plane. Note that the (u, v)
    coordinate correspond to the (x, y) coordinate.

    This method is of vital importance to many fundamental algorithms of
    visualization (i.e. ray casting, ray tracing, radiosity), object selection
    and others (simulation, colision detection, visual debugging, etc.). As it
    is important to improve the efficiency of this method, some precalculated
    values are stored in the class attributes `_dir`, `upWithScale` and
    `rightWithScale`, which values are stored in the `updateVectors`
    method, leading to the following precondition:

    PRE:
      - At least a call to the updateVectors method must be done before calling
        this method, and after changing any camera parameter the updateVectors
        method must be called again to reflect the changes in this calculation.

    /todo this method should be named "generateProjectorRay"
    */
    public final Ray generateRay(int x, int y)
    {
        double u, v;
        Ray ray;

        // 1. Convert integer image coordinates into values in the range [-0.5, 0.5]
        u = ((double)x - viewportXSize/2.0) / viewportXSize;
        v = ((viewportYSize - (double)y - 1) -  viewportYSize/2.0) / viewportYSize;

        // 2. Calculate the ray direction
        Vector3D dv;
        Vector3D du;

        if ( projectionMode == PROJECTION_MODE_ORTHOGONAL ) {
            double fovFactor = viewportXSize/viewportYSize;
            du = left.multiply(-fovFactor);
            dv = up;
            du = du.multiply(2*u/orthogonalZoom);
            dv = dv.multiply(2*v/orthogonalZoom);
            ray = new Ray(eyePosition.add(du.add(dv)), front);
            return ray;
        }
        // Default behavior is to assume planar perspective projection
        du = rightWithScale.multiply(u);
        dv = upWithScale.multiply(v);

        Vector3D dir = dv.add(du).add(_dir);

        // 3. Build up and return a ray with origin in the eye position and with calculated direction
        ray = new Ray(eyePosition, dir);

        return ray;
    }

    public double getOrthogonalZoom()
    {
        return orthogonalZoom;
    }

    public void setOrthogonalZoom(double orthogonalZoom)
    {
        this.orthogonalZoom = orthogonalZoom;
    }

    public void setRotation(Matrix4x4 R)
    {
        up.x = R.M[0][2];
        up.y = R.M[1][2];
        up.z = R.M[2][2];
        up.normalize();

        front.x = R.M[0][0];
        front.y = R.M[1][0];
        front.z = R.M[2][0];
        front.normalize();

        left.x=R.M[0][1];
        left.y=R.M[1][1];
        left.z=R.M[2][1];
        left.normalize();
    }

    public Matrix4x4 getRotation()
    {
        //------------------------------------------------------------
        Matrix4x4 R = new Matrix4x4();

        R.identity();
        R.M[0][0] = front.x; R.M[0][1] = left.x; R.M[0][2] = up.x;
        R.M[1][0] = front.y; R.M[1][1] = left.y; R.M[1][2] = up.y;
        R.M[2][0] = front.z; R.M[2][1] = left.z; R.M[2][2] = up.z;

        return R;
    }
    
    /**
    Note that projectionMatrix = transformationMatrix*viewVolumeMatrix
    */
    public Matrix4x4 calculateViewVolumeMatrix()
    {
        //- Calculate the base projection matrix ----------------------------
        double leftDistance, rightDistance, upDistance, downDistance, aspect;
        Matrix4x4 P = new Matrix4x4();

        aspect = viewportXSize / viewportYSize; 
        switch ( projectionMode ) {
          case Camera.PROJECTION_MODE_ORTHOGONAL:
            P.orthogonalProjection(-aspect/orthogonalZoom,
                                    aspect/orthogonalZoom,
                                   -1/orthogonalZoom, 1/orthogonalZoom,
                                   nearPlaneDistance, farPlaneDistance);
            break;
          case Camera.PROJECTION_MODE_PERSPECTIVE:
            upDistance = nearPlaneDistance * Math.tan(Math.toRadians(fov/2));
            downDistance = -upDistance;
            leftDistance = aspect * downDistance;
            rightDistance = aspect * upDistance;
            P.frustumProjection(leftDistance, rightDistance,
                                downDistance, upDistance,
                                nearPlaneDistance, farPlaneDistance);
            break;
        }
        return P;
    }

    /**
    Note that projectionMatrix = transformationMatrix*viewVolumeMatrix
    */
    public Matrix4x4 calculateTransformationMatrix()
    {
        //- Take into account the camera position and orientation ----------
        Matrix4x4 R;
        Matrix4x4 R1;
        Matrix4x4 T1 = new Matrix4x4();
        Matrix4x4 R_adic2 = new Matrix4x4();
        Matrix4x4 R_adic1 = new Matrix4x4();
        Matrix4x4 R2 = new Matrix4x4();

        R1 = getRotation();
        R1.invert();

        T1.translation(-eyePosition.x, -eyePosition.y, -eyePosition.z);
        R_adic2.axisRotation(Math.toRadians(90), 0, 0, 1);
        R_adic1.axisRotation(Math.toRadians(-90), 1, 0, 0);

        R = R_adic1.multiply(R_adic2.multiply(R1.multiply(T1)));

        return R;
    }

    /**
    Note that projectionMatrix = transformationMatrix*viewVolumeMatrix
    */
    public Matrix4x4 calculateProjectionMatrix()
    {
        Matrix4x4 P = calculateViewVolumeMatrix();
        Matrix4x4 R = calculateTransformationMatrix();
        return P.multiply(R);
    }

    /**
    Provides an object to text report convertion, optimized for human
    readability and debugging. Do not use for serialization or persistence
    purposes.
    */
    public String toString()
    {
        //------------------------------------------------------------
        String msg;

        msg = "<Camera>:\n";
        msg += "  - Name: \"" + getName() + "\"\n";
        if ( projectionMode == PROJECTION_MODE_PERSPECTIVE ) {
            msg = msg + "  - Camera in PERSPECTIVE projection mode\n";
          }
          else if ( projectionMode == PROJECTION_MODE_ORTHOGONAL ) {
            msg = msg + "  - Camera in PARALEL projection mode\n";
            msg = msg + "  - Orthogonal zoom = " + orthogonalZoom + "\n";
          }
          else {
            msg = msg + "  - UNKNOWN Camera projection mode!\n";
          }
        ;

        msg = msg + "  - eyePosition(x, y, z) = " + eyePosition + "\n";
        msg = msg + "  - focusedPointPosition(x, y, z) = " + eyePosition.add(front.multiply(focalDistance)) + "\n";

        //------------------------------------------------------------
        Matrix4x4 R, TP;
        double yaw, pitch, roll;

        TP = calculateProjectionMatrix();
        R = getRotation();

        yaw = R.obtainEulerYawAngle();
        pitch = R.obtainEulerPitchAngle();
        roll = R.obtainEulerRollAngle();

        msg = msg + "  - Rotation yaw/pitch/roll: <" +
            VSDK.formatDouble(yaw) + ", " +
            VSDK.formatDouble(pitch) + ", " +
            VSDK.formatDouble(roll) + "> RAD (<" +
            VSDK.formatDouble(Math.toDegrees(yaw)) + ", " +
            VSDK.formatDouble(Math.toDegrees(pitch)) + ", " +
            VSDK.formatDouble(Math.toDegrees(roll)) + "> DEG)\n";

        msg = msg + "  - Rotation quaternion: " + R.exportToQuaternion() + "\n";

        //------------------------------------------------------------
        updateVectors();
        msg = msg + "  - Reference frame:\n";
        msg = msg + "    . Vector UP = " + up + " (length " + VSDK.formatDouble(up.length()) + ")\n";
        msg = msg + "    . Vector FRONT = " + front + " (length " + VSDK.formatDouble(front.length()) + ")\n";
        msg = msg + "    . Vector LEFT = " + left + " (length " + VSDK.formatDouble(front.length()) + ")\n";
        msg = msg + "  - Reference frame with scales:\n";
        msg = msg + "    . Vector UP' = " + upWithScale + " (length " + VSDK.formatDouble(upWithScale.length()) + ")\n";
        msg = msg + "    . Vector FRONT' = " + _dir + " (length " + VSDK.formatDouble(_dir.length()) + ")\n";
        msg = msg + "    . Vector RIGHT' = " + rightWithScale + " (length " + VSDK.formatDouble(rightWithScale.length()) + ")\n";
        msg = msg + "  - fov = " + VSDK.formatDouble(fov) + "\n";
        msg = msg + "  - nearPlaneDistance = " + VSDK.formatDouble(nearPlaneDistance) + "\n";
        msg = msg + "  - farPlaneDistance = " + VSDK.formatDouble(farPlaneDistance) + "\n";
        msg = msg + "  - Viewport size in pixels = (" + VSDK.formatDouble(viewportXSize) + ", " + VSDK.formatDouble(viewportYSize) + ")\n";
        msg = msg + "  - Transformation * projection matrix:" + TP;

        //------------------------------------------------------------
        Matrix4x4 P;
        double leftDistance, rightDistance, upDistance, downDistance, aspect;

        P = new Matrix4x4();
        aspect = viewportXSize / viewportYSize; 
        if ( projectionMode == PROJECTION_MODE_PERSPECTIVE ) {
            upDistance = nearPlaneDistance * Math.tan(Math.toRadians(fov/2));
            downDistance = -upDistance;
            leftDistance = aspect * downDistance;
            rightDistance = aspect * upDistance;
            P.frustumProjection(leftDistance, rightDistance,
                                downDistance, upDistance,
                                nearPlaneDistance, farPlaneDistance);
            msg = msg + "  - Projection matrix:" + P;
          }
          else if ( projectionMode == PROJECTION_MODE_ORTHOGONAL ) {
            P.orthogonalProjection(-aspect/orthogonalZoom, 
                                    aspect/orthogonalZoom,
                                   -1/orthogonalZoom, 1/orthogonalZoom,
                                   nearPlaneDistance, farPlaneDistance);
            msg = msg + "  - Projection matrix:" + P;
        }

        //------------------------------------------------------------
        return msg;
    }

    private double fpd()
    {
        return (farPlaneDistance - nearPlaneDistance) / nearPlaneDistance;
    }

    /**
    Given a point in "clipping coordinates space", this method calculates
    a six bit opcode, as explained in [FOLE1992].6.5.3, suitable for use in the
    Cohen-Suterland line clipping algorithm.

    Note that in VSDK, the clipping space correspond to the frustum for
    the minmax cube from <-1, -1, -1> to <1, 1, 1>.

    WARNING: Currently is only implementing the perspective case!
    */
    private int calculateOutcodeBits(Vector3D p)
    {
        int bits = 0x00;

        if ( p.z + p.x - 1 > 0 ) bits |= OPCODE_RIGHT;
        if ( p.z - p.x - 1 > 0 ) bits |= OPCODE_LEFT;
        if ( p.z + p.y - 1 > 0 ) bits |= OPCODE_UP;
        if ( p.z - p.y - 1 > 0 ) bits |= OPCODE_DOWN;
        // Warning: near plane clipping
        if ( p.z > 0 ) bits |= OPCODE_NEAR;
        if ( p.z < -fpd() ) bits |= OPCODE_FAR;

        return bits;
    }

    /**
    This method implements the Cohen-Sutherland line clipping algorithm with
    respect to the view volume defined by current camera. Recieves the two
    line endpoints and return true if any part of this line lies inside the
    view volume.  In the case the line crosses the view volume, the new
    resulting endpoints are calculated and returned.

    This algorithm structure follows the one proposed in [FOLE1992].3.12.3,
    generalizing it to the 3D case, as noted in [FOLE1992].6.5.3.

    The resulting clipped points are in the canonical volume reference, ready
    for projection.  If 3D clipped points are needed, they must be premultiplied
    by the inverse of the normalizingTransformation.
    */
    public boolean clipLineCohenSutherlandCanonicVolume(
                             Vector3D point0, Vector3D point1,
                             Vector3D clippedPoint0, Vector3D clippedPoint1)
    {
        //- Local variables definition ------------------------------------
        int outcode0;                // 6bit containment code for point0
        int outcode1;                // 6bit containment code for point1
        int outcodeout;              // Selected endpoint code for iteration
        Vector3D clippingMidPoint;   // Selected endpoint clipped for iteration
        Ray testRay;                 // Ray use for general line/plane clipping
        Vector3D dirFromP0ToP1;      // Temporary for testRay construction
        double l;                    // Length of dirFromP0ToP1
        int planeId;                 // A number from 1 to 6 identifying which
                                     // plane intersection is being tested

        //- Algorithm initial state ---------------------------------------
        Vector3D pp0, pp1;

        pp0 = normalizingTransformation.multiply(point0);
        pp1 = normalizingTransformation.multiply(point1);

        clippedPoint0.x = pp0.x;
        clippedPoint0.y = pp0.y;
        clippedPoint0.z = pp0.z;
        clippedPoint1.x = pp1.x;
        clippedPoint1.y = pp1.y;
        clippedPoint1.z = pp1.z;
        updateVectors();
        clippingMidPoint = new Vector3D();
        outcode0 = calculateOutcodeBits(pp0);
        outcode1 = calculateOutcodeBits(pp1);

        //- Main Cohen-Sutherland iteration cycle (incremental clipping) --
        boolean linePasses = false; // Algorithm return value
        boolean done = false;       // Iteration exit condition
        do {
            //- Trivial cases: trivial accept and trivial reject ----------
            if ( outcode0 == 0x0 && outcode1 == 0x0 ) {
                linePasses = true;
                done = true;
            }
            else if ( (outcode0 & outcode1) != 0x0 ) {
                linePasses = false;
                done = true;
            }

            //- Iterative cases: clipping with each of the 6 planes -------
            else {
                //--------------------------------------------------
                dirFromP0ToP1 = clippedPoint1.substract(clippedPoint0);
                l = dirFromP0ToP1.length();
                if ( l < VSDK.EPSILON ) {
                    // continue;
                    return false;
                }
                dirFromP0ToP1.x /= l;
                dirFromP0ToP1.y /= l;
                dirFromP0ToP1.z /= l;

                //--------------------------------------------------
                if ( outcode0 != 0 ) {
                    outcodeout = outcode0;
                  }
                  else {
                    outcodeout = outcode1;
                }
                testRay = new Ray(clippedPoint0, dirFromP0ToP1);

                //--------------------------------------------------
                planeId = 0;
                if ( (OPCODE_UP & outcodeout) != 0x0 ) {
                    planeId = 1; // up plane;
                }
                else if ( (OPCODE_DOWN & outcodeout) != 0x0 ) {
                    planeId = 2; // down plane;
                }
                else if ( (OPCODE_LEFT & outcodeout) != 0x0 ) {
                    planeId = 3; // left plane;
                }
                else if ( (OPCODE_RIGHT & outcodeout) != 0x0 ) {
                    planeId = 4; // right plane;
                }
                else if ( (OPCODE_NEAR & outcodeout) != 0x0 ) {
                    planeId = 5; // near plane
                }
                else if ( (OPCODE_FAR & outcodeout) != 0x0 ) {
                    planeId = 6; // far plane;
                }
                else {
                    // Not possible: non implemented case!
                        VSDK.reportMessage(this, VSDK.WARNING, 
                            "clipLineCohenSutherlandCanonicVolume", 
                            "Unusal case, check code and data");
                }

                double de;
                de = (outcodeout == outcode1) ? -VSDK.EPSILON: VSDK.EPSILON;

                switch ( planeId ) {
                  case 1: // up plane
                    testRay.t = 
                          (l * (1 - clippedPoint0.z - clippedPoint0.y)) / 
                          (clippedPoint1.z - clippedPoint0.z + 
                           clippedPoint1.y - clippedPoint0.y);
                    clippingMidPoint = testRay.origin.add(
                        testRay.direction.multiply(testRay.t+de));
                    linePasses = true;
                    break;
                  case 2: // down plane
                    testRay.t = 
                          (l * (clippedPoint0.y - clippedPoint0.z +1)) / 
                          (clippedPoint1.z - clippedPoint0.z - 
                           clippedPoint1.y + clippedPoint0.y);
                    clippingMidPoint = testRay.origin.add(
                        testRay.direction.multiply(testRay.t+de));
                    linePasses = true;
                    break;
                  case 3: // left plane
                    testRay.t = 
                          (l * (clippedPoint0.x - clippedPoint0.z +1)) / 
                          (clippedPoint1.z - clippedPoint0.z - 
                           clippedPoint1.x + clippedPoint0.x);
                    clippingMidPoint = testRay.origin.add(
                        testRay.direction.multiply(testRay.t+de));
                    linePasses = true;
                    break;
                  case 4: // right plane
                    testRay.t = 
                          (l * (1 - clippedPoint0.z - clippedPoint0.x)) / 
                          (clippedPoint1.z - clippedPoint0.z + 
                           clippedPoint1.x - clippedPoint0.x);
                    clippingMidPoint = testRay.origin.add(
                        testRay.direction.multiply(testRay.t+de));
                    linePasses = true;
                    break;
                  case 5: // near plane
                    // Warning: near plane clipping
                    testRay.t = 
                        ( /*(1-nearPlaneDistance)*/ -clippedPoint0.z * l) / 
                          (clippedPoint1.z - clippedPoint0.z);
                    clippingMidPoint = testRay.origin.add(
                        testRay.direction.multiply(testRay.t+de));
                    linePasses = true;
                    break;
                  case 6: // far plane
                    testRay.t = 
                        (((-fpd()-
                             clippedPoint0.z) * l) / 
                             (clippedPoint1.z - clippedPoint0.z));
                    clippingMidPoint = testRay.origin.add(
                        testRay.direction.multiply(testRay.t+de));
                    linePasses = true;
                    break;
                }

                //--------------------------------------------------
                if ( outcodeout == outcode0 ) {
                    clippedPoint0.x = clippingMidPoint.x;
                    clippedPoint0.y = clippingMidPoint.y;
                    clippedPoint0.z = clippingMidPoint.z;
                    outcode0 = calculateOutcodeBits(clippedPoint0);
                  }
                  else {
                    clippedPoint1.x = clippingMidPoint.x;
                    clippedPoint1.y = clippingMidPoint.y;
                    clippedPoint1.z = clippingMidPoint.z;
                    outcode1 = calculateOutcodeBits(clippedPoint1);
                }
            }
        } while ( !done );

        return linePasses;
    }

}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
