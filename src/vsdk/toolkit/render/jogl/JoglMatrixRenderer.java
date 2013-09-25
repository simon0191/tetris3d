//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - August 22 2005 - David Diaz: Original base version                    =
//= - November 15 2005 - Oscar Chavarro: Migrated to JOGL Beta Version      =
//===========================================================================

package vsdk.toolkit.render.jogl;

import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import javax.media.opengl.GL2;

/**
This class is meant to support rendering operations in the JOGL API from
vitral internal representation of a matrix containing an homogeneous
coordinates geometrical transformation represented in the 
`vsdk.toolkit.common.Matrix4x4` class.
 */
public class JoglMatrixRenderer extends JoglRenderer {

    /**
    matrixId must be one of the internal JOGL/OpenGL variable names associated
    with matrices, like gl.GL_PROJECTION_MATRIX or gl.GL_MODELVIEW_MATRIX
    */
    public static Matrix4x4 importJOGL(GL2 gl, int matrixId) {
        double Mgl[] = new double[16];

        int row, column, pos;

        gl.glGetDoublev(matrixId, Mgl, 0);
        Matrix4x4 R = new Matrix4x4();
        for ( pos = 0, column = 0; column < 4; column++ ) {
            for ( row = 0; row < 4; row++, pos++ ) {
                R.M[row][column] = Mgl[pos];
            }
        }
        return R;
    }

    /**
    This method acumulates the matrix represented in `A` in the currently
    selected matrix stack inside the JOGL state machine.
    */
    public static void activate(GL2 gl, Matrix4x4 A)
    {
        double Mgl[] = new double[16];
        int row, column, pos;

        for ( pos = 0, column = 0; column < 4; column++ ) {
            for ( row = 0; row < 4; row++, pos++ ) {
                Mgl[pos] = A.M[row][column];
            }
        }

        gl.glMultMatrixd(Mgl, 0);
    }

    /**
    This method is designed to provide a 3D graphical representation of
    a transformation matrix, in terms of 3 vectors. The vectors are drawn
    in the order `i'`, `j'`, `k'`, with corresponding colors red, green
    and blue, and each vector is represented as an arrow. If the matrix
    `A` is an identity matrix, then the vectors correspond to the
    orthogonal i, j, k vectors. If `A` is any orthogonal rotation matrix,
    the vectors drawn will correspond to a reference frame of unit vectors.
    In a similar fashion, the transformation components of the matrix
    will determine the center of the represented reference frame, and
    the scale components will change its state. The 3d graphical 
    representation of the reference frame determined by the `A` matrix
    will reflect any inconsistent state, as null vectors or non-ortogonal
    vectors, by making different red marks.

    THIS METHOD WILL BE CHANGED TO ALLOW CUSTOMIZATION
    */
    public static void draw(GL2 gl, Matrix4x4 A)
    {
        gl.glPushMatrix();
        gl.glDisable(gl.GL_LIGHTING);
        gl.glBegin(gl.GL_LINES);
            gl.glColor3d(1, 0, 0);
            gl.glVertex3d(A.M[0][3],
                          A.M[1][3],
                          A.M[2][3]); 
            gl.glVertex3d(A.M[0][3] + A.M[0][0],
                          A.M[1][3] + A.M[1][0],
                          A.M[2][3] + A.M[2][0]);

            gl.glColor3d(0, 1, 0);
            gl.glVertex3d(A.M[0][3],
                          A.M[1][3],
                          A.M[2][3]); 
            gl.glVertex3d(A.M[0][3] + A.M[0][1],
                          A.M[1][3] + A.M[1][1],
                          A.M[2][3] + A.M[2][1]);

            gl.glColor3d(0, 0, 1);
            gl.glVertex3d(A.M[0][3],
                          A.M[1][3],
                          A.M[2][3]); 
            gl.glVertex3d(A.M[0][3] + A.M[0][2],
                          A.M[1][3] + A.M[1][2],
                          A.M[2][3] + A.M[2][2]);
        gl.glEnd();
        gl.glPopMatrix();
    }
    
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
