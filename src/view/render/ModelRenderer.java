package view.render;

import javax.media.opengl.GL2;

import model.Model;
import model.shapes.Shape;

import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.Material;
import vsdk.toolkit.environment.geometry.Box;
import vsdk.toolkit.render.jogl.JoglBoxRenderer;
import vsdk.toolkit.render.jogl.JoglMaterialRenderer;

public class ModelRenderer {

    private final static ColorRgb[] colors = {
        new ColorRgb(1, 1, 1),
        new ColorRgb(1, 1, 0),
        new ColorRgb(1, 0, 1),
        new ColorRgb(1, 0, 0),
        new ColorRgb(0, 0, 1),
        new ColorRgb(0, 0, 0),
        new ColorRgb(0, 1, 0),
        new ColorRgb(0, 1, 1),
        new ColorRgb(0.5, 0.5, 0.5),
        new ColorRgb(0.5, 0.5, 0)
    };

    public static void drawModel(GL2 gl, Model model, Camera camera,
            RendererConfiguration rendererConfiguration) {
        boolean[][][] matrix = model.getMatrix();
        Box box;
        Material material;



        material = new Material();
        material.setAmbient(new ColorRgb(0, 0, 0));
        material.setSpecular(new ColorRgb(1, 1, 1));
        material.setDoubleSided(false);
        material.setPhongExponent(40.0);

        gl.glPushMatrix();
        {
            for (int i = 0; i < matrix.length; ++i) {
                //INI 2
                gl.glPushMatrix();
                {
                    for (int j = 0; j < matrix[i].length; ++j) {
                        //INI 3
                        gl.glPushMatrix();
                        {
                            for (int k = 0; k < matrix[i][j].length; ++k) {
                                if (matrix[i][j][k]) {
                                    material.setDiffuse(colors[k]);
                                    JoglMaterialRenderer.activate(gl, material);
                                    box = new Box(Shape.BOX_LENGTH, Shape.BOX_LENGTH, Shape.BOX_LENGTH);
                                    JoglBoxRenderer.draw(gl, box, camera, rendererConfiguration);
                                }
                                gl.glTranslated(0, 0, Shape.BOX_LENGTH + Shape.SPACE);
                            }
                            //FIN 3
                        }
                        gl.glPopMatrix();
                        gl.glTranslated(0, Shape.BOX_LENGTH + Shape.SPACE, 0);
                    }
                    //FIN 2
                }
                gl.glPopMatrix();
                gl.glTranslated(Shape.BOX_LENGTH + Shape.SPACE, 0, 0);
            }
            //FIN 1
        }
        gl.glPopMatrix();
    }
}
