package view.render;

import javax.media.opengl.GL2;

import model.shapes.Shape;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;

public class WallRenderer {
	
	public static void draw(GL2 gl, int width, int height, Camera camera,
			RendererConfiguration rendererConfiguration) {
		double interSpace = Shape.SPACE;
        gl.glBegin(GL2.GL_QUADS);
        double inc = Shape.BOX_LENGTH;
        
        // Floor
        for(int i = 0; i<width; ++i) {
            for(int j = 0; j<height; ++j) {
                gl.glVertex3d(i*inc + interSpace, j*inc + interSpace, 0);
                gl.glVertex3d((i+1)*inc, j*inc + interSpace, 0);
                gl.glVertex3d((i+1)*inc, (j+1)*inc, 0);
                gl.glVertex3d(i*inc + interSpace, (j+1)*inc, 0);
            }
        }
        gl.glEnd();
        
        /*gl.glRotated(180, 0, 0, 1);
        gl.glTranslated(0, -x, 0);
        gl.glBegin(GL2.GL_QUADS);
        // Front Wall
        for(int j = 0; j<n; ++j) {
            for(int k = 0; k<n; ++k) {
                gl.glVertex3d(0, j*inc + interSpace, k*inc + interSpace);
                gl.glVertex3d(0, j*inc + interSpace, (k+1)*inc);
                gl.glVertex3d(0, (j+1)*inc, (k+1)*inc);
                gl.glVertex3d(0, (j+1)*inc, k*inc + interSpace);
            }
        }
        
        gl.glEnd();
        
        // Right Wall
        gl.glLoadIdentity();
        gl.glRotated(90, 1, 0, 0);
        gl.glTranslated(0, 0, -x);
        gl.glBegin(GL2.GL_QUADS);
        // Front Wall
        for(int i = 0; i<n; ++i) {
            for(int j = 0; j<n; ++j) {
                gl.glVertex3d(i*inc + interSpace, j*inc + interSpace, 0);
                gl.glVertex3d((i+1)*inc, j*inc + interSpace, 0);
                gl.glVertex3d((i+1)*inc, (j+1)*inc, 0);
                gl.glVertex3d(i*inc + interSpace, (j+1)*inc, 0);
            }
        }
        
        gl.glEnd();
        */
	}
}
