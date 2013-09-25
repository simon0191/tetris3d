package view.render;

import javax.media.opengl.GL2;

import model.Model;
import model.shapes.Shape;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.geometry.Box;
import vsdk.toolkit.render.jogl.JoglBoxRenderer;

public class TetrisShapeRenderer {

	public static void draw(GL2 gl, Shape shape, Camera camera,
			RendererConfiguration rendererConfiguration) {
	 	Box box;
	 	gl.glLineWidth((float) 15.0);
	 	boolean[][][] matrix = shape.getMatrix();
	 	double aux = (Shape.BOX_LENGTH+Shape.SPACE);
	 	//INI 1
	 	gl.glPushMatrix();{
	 	gl.glTranslated(shape.getDesfX()*aux, shape.getDesfY()*aux, shape.getLevel()*aux);
	 	for(int i=0;i<matrix.length;++i){
	 		//INI 2
 			gl.glPushMatrix();{
 			for(int j = 0;j<matrix[i].length;++j){
 					//INI 3
			
				gl.glPushMatrix();{
				for(int k = 0;k<matrix[i][j].length;++k){
					box = new Box(Shape.BOX_LENGTH,Shape.BOX_LENGTH,Shape.BOX_LENGTH);
					if(matrix[i][j][k]){
						rendererConfiguration.setSurfaces(true);
						JoglBoxRenderer.draw(gl, box, camera, rendererConfiguration);
					}
					
					else if(Model.band){
						rendererConfiguration.setSurfaces(false);
						JoglBoxRenderer.draw(gl, box, camera, rendererConfiguration);
					}
					
					gl.glTranslated(0, 0, Shape.BOX_LENGTH + Shape.SPACE);
				}
				//FIN 3
				}gl.glPopMatrix();
				gl.glTranslated(0, Shape.BOX_LENGTH + Shape.SPACE, 0);
 			}
 			//FIN 2
 			}gl.glPopMatrix();
 			gl.glTranslated(Shape.BOX_LENGTH + Shape.SPACE, 0, 0);
	 	}
		//FIN 1
	 	}gl.glPopMatrix();
	 	rendererConfiguration.setSurfaces(true);		
	}
	
}
