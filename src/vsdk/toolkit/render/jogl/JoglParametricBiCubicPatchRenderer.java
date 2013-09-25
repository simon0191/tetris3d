//===========================================================================
//=-------------------------------------------------------------------------=
//= Module history:                                                         =
//= - April 20 2006 - Gina Chiquillo / David Camello: Original base version =
//= - April 28 2006 - Gina Chiquillo / Oscar Chavarro: quality check        =
//= - May 23 2006 - Gina Chiquillo: added generation of texture coords.     =
//===========================================================================

package vsdk.toolkit.render.jogl;

import javax.media.opengl.GL2;

import vsdk.toolkit.common.linealAlgebra.Vector3D;
import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.environment.Camera;
import vsdk.toolkit.environment.geometry.ParametricCurve;
import vsdk.toolkit.environment.geometry.ParametricBiCubicPatch;
import vsdk.toolkit.render.jogl.JoglParametricCurveRenderer;
import vsdk.toolkit.render.jogl.JoglGeometryRenderer;
import vsdk.toolkit.render.jogl.JoglRGBAImageRenderer;

public class JoglParametricBiCubicPatchRenderer extends JoglRenderer {

    public static void drawControlPoints(GL2 gl, ParametricBiCubicPatch p)
    {
        //-----------------------------------------------------------------
        gl.glDisable(gl.GL_LIGHTING);
        gl.glDisable(gl.GL_TEXTURE_2D);
        // Warning: Change with configured color for point
        gl.glColor3d(1, 0, 0);
        gl.glPointSize(6.0f);

        //-----------------------------------------------------------------
        int i; // Integer index in the U direction
        int j; // Integer index in the V direction
        Vector3D pos = new Vector3D();
        int n = p.getApproximationSteps()+1;
        double s, t;
        double ds, dt;

        ds = 1 / ((double)n-1);
        dt = 1 / ((double)n-1);

        //-----------------------------------------------------------------
        gl.glBegin(gl.GL_POINTS);

        for ( i = 0; i < n; i += (n-1) ) {
            s = ((double)i)*ds;
            for ( j = 0; j < n; j += (n-1) ) {
                t = ((double)j)*dt;
                p.evaluate(pos, s, t);
                gl.glVertex3d(pos.x, pos.y, pos.z);
            }
        }

        gl.glEnd();
    }

    public static void drawNormals(GL2 gl, ParametricBiCubicPatch p,
                                   double textureUSizeFactor,
                                   double textureVSizeFactor, 
                                   double textureURelaviteStart,
                                   double textureVRelativeStart) {
        int i; // Integer index in the U direction
        int j; // Integer index in the V direction
        double s, t;
        double ds, dt;
        double sizeDivTu; // Size relation between patch and texture coordinate in U direction
        double sizeDivTv; // Size relation between patch and texture coordinate in V direction
        int n;

        n = p.getApproximationSteps()+1;
        ds = 1 / ((double)n-1);
        dt = 1 / ((double)n-1);

        gl.glDisable(gl.GL_LIGHTING);
        gl.glDisable(gl.GL_TEXTURE_2D);
        // Warning: Change with configured color for vertex normals
        gl.glColor3d(1, 1, 0);
        gl.glLineWidth(1.0f);

        double reductionFactor = 20;
        gl.glBegin(gl.GL_LINES);
        for ( i = 0; i < n; i++ ) {
            s = ((double)i)*ds;
            sizeDivTv = (textureVSizeFactor / (n-1));
            for ( j = 0; j < n; j++ ) {
                t = ((double)j)*dt;
                sizeDivTu = (textureUSizeFactor / (n-1));

                Vector3D p1 = new Vector3D();
                Vector3D normal = new Vector3D();

                p.evaluate(p1, s, t);

                // Normal
                p.evaluateNormal(normal, s, t);

                gl.glColor3d(1, 1, 0);
                gl.glVertex3d(p1.x, p1.y, p1.z);
                gl.glVertex3d(p1.x + normal.x/reductionFactor,
                              p1.y + normal.y/reductionFactor, 
                              p1.z + normal.z/reductionFactor);

                // Tangent
                p.evaluateTangent(normal, s, t);

                gl.glColor3d(0.9, 0.5, 0.5);
                gl.glVertex3d(p1.x, p1.y, p1.z);
                gl.glVertex3d(p1.x + normal.x/(reductionFactor*2),
                              p1.y + normal.y/(reductionFactor*2), 
                              p1.z + normal.z/(reductionFactor*2));
                // Binormal
                p.evaluateBinormal(normal, s, t);

                gl.glColor3d(0.5, 0.9, 0.5);
                gl.glVertex3d(p1.x, p1.y, p1.z);
                gl.glVertex3d(p1.x + normal.x/(reductionFactor*2),
                              p1.y + normal.y/(reductionFactor*2), 
                              p1.z + normal.z/(reductionFactor*2));
            }
        }
        gl.glEnd();
    }

    /**
    This method generates the OpenGL/JOGL graphics primitives needed
    to render the given parametric bicubic patch's position points.

    The code asigns texture coordinates in the (u, v) space for the
    patch. Note that when textureUSizeFactor = 1, textureUSizeFactor = 1,
    textureURelaviteStart = 0 and textureVRelaviteStart = 0, the
    texture space matches the patch's parametric space, giving as a result
    a texture covering the whole patch. Changing those 4 parameters
    results in a different texture coverage.

    Note that the `points` matrix is of size points[NumberU][NumberV][3],
    and can be understood as a matrix of [NumberU][NumberV] points
    with coordinates (x, y, z).    
    */
    public static void drawSurfaceGrid(GL2 gl,
                                       ParametricBiCubicPatch p,
                                       double textureUSizeFactor,
                                       double textureVSizeFactor, 
                                       double textureURelaviteStart,
                                       double textureVRelativeStart,
                                       RendererConfiguration q) {
        int i; // Integer index in the U direction
        int j; // Integer index in the V direction
        double s, t;
        double ds, dt;
        double sizeDivTu; // Size relation between patch and texture coordinate in U direction
        double sizeDivTv; // Size relation between patch and texture coordinate in V direction
        int n = p.getApproximationSteps()+1;

        ds = 1 / ((double)n-1);
        dt = 1 / ((double)n-1);

        if ( q.getShadingType() == q.SHADING_TYPE_FLAT ) {
            gl.glShadeModel(gl.GL_FLAT);
        }
        else {
            gl.glShadeModel(gl.GL_SMOOTH);
        }

        for ( i = 0; i < n - 1; i++ ) {
            s = ((double)i)*ds;
            sizeDivTv = (textureVSizeFactor / (n-1));
            gl.glBegin(gl.GL_QUAD_STRIP);
            for ( j = 0; j < n; j++) {
                t = ((double)j)*dt;
                sizeDivTu = (textureUSizeFactor / (n-1));
                // Now we draw some lines
                Vector3D p1 = new Vector3D();
                Vector3D p2 = new Vector3D();

                p.evaluate(p1, s, t);
                p.evaluate(p2, s+ds, t);

                Vector3D n1 = new Vector3D();
                Vector3D n2 = new Vector3D();

                p.evaluateNormal(n1, s, t);
                p.evaluateNormal(n2, s+ds, t);

                //- Generate GL primitives ------------------------------------
                // First vertex
                gl.glTexCoord3d( (i * sizeDivTu) + textureURelaviteStart,
                                 (j * sizeDivTv) + textureVRelativeStart, 0);
                gl.glNormal3d(n1.x, n1.y, n1.z);
                gl.glVertex3d(p1.x, p1.y, p1.z);

                // Second vertex
                gl.glTexCoord3d( ((i+1) * sizeDivTu) + textureURelaviteStart,
                                   (j * sizeDivTv) + textureVRelativeStart, 0);
                gl.glNormal3d(n2.x, n2.y, n2.z);
                gl.glVertex3d(p2.x, p2.y, p2.z);
            }
            gl.glEnd();
        }
    }

    public static void drawControlGrid(GL2 gl, ParametricBiCubicPatch patch,
                                       Camera c, RendererConfiguration q,
                                       ColorRgb color) {
        // Now we draw the points
        if ( patch.type == ParametricCurve.BEZIER ) {
            gl.glDisable(gl.GL_LIGHTING);
            gl.glColor3d(color.r, color.g, color.b);
            gl.glLineWidth(1);
            for (int i = 0; i < 4; i++) {
                gl.glBegin(gl.GL_LINE_STRIP);
                for (int j = 0; j < 4; j++) {
                    gl.glVertex3d(patch.Gx_MATRIX.M[i][j], patch.Gy_MATRIX.M[i][j],
                                  patch.Gz_MATRIX.M[i][j]);
                }
                gl.glEnd();
            }
            for (int i = 0; i < 4; i++) {
                gl.glBegin(gl.GL_LINE_STRIP);
                for (int j = 0; j < 4; j++) {
                    gl.glVertex3d(patch.Gx_MATRIX.M[j][i], patch.Gy_MATRIX.M[j][i],
                                  patch.Gz_MATRIX.M[j][i]);
                }
                gl.glEnd();
            }

            JoglParametricCurveRenderer.drawPoints(gl, patch.contourCurve.points);

        }
        else if (patch.type == ParametricCurve.HERMITE) {
            // Now we draw the points
            ParametricCurve c1 = new ParametricCurve();
            c1.addPoint(patch.contourCurve.getPoint(0), c1.HERMITE);
            c1.addPoint(patch.contourCurve.getPoint(1), c1.HERMITE);
            c1.addPoint(patch.contourCurve.getPoint(3), c1.HERMITE);

            JoglParametricCurveRenderer.draw(gl, c1, c, q,
                                                  new ColorRgb(1, 0.4, 0.5));
            JoglParametricCurveRenderer.drawControlPointsCurve(gl, c1);

            c1 = new ParametricCurve();
            Vector3D[] v3 = new Vector3D[] {
                patch.contourCurve.getPoint(4)[0], patch.contourCurve.getPoint(4)[2],
                patch.contourCurve.getPoint(4)[1]};
            c1.addPoint(v3, c1.HERMITE);

            v3 = new Vector3D[] {
                patch.contourCurve.getPoint(2)[0], patch.contourCurve.getPoint(2)[2],
                patch.contourCurve.getPoint(2)[1]};
            c1.addPoint(v3, c1.HERMITE);
            v3 = new Vector3D[] {
                patch.contourCurve.getPoint(3)[0], patch.contourCurve.getPoint(3)[2],
                patch.contourCurve.getPoint(3)[1]};
            c1.addPoint(v3, c1.HERMITE);
            JoglParametricCurveRenderer.draw(gl, c1, c, q,
                                                  new ColorRgb(0.5, 0.5, 0.8));
            JoglParametricCurveRenderer.drawControlPointsCurve(gl, c1);

        }
        else {

            // Now we draw the points
            JoglParametricCurveRenderer.draw(gl, patch.contourCurve, c, q,
                                                  new ColorRgb(1, 1, 1));
            JoglParametricCurveRenderer.drawControlPointsCurve(gl,
                                                                    patch.contourCurve);
        }
    }

    /**
    Generate OpenGL/JOGL primitives needed for the rendering of recieved
    Geometry object.
    */
    public static void draw(GL2 gl, ParametricBiCubicPatch p, Camera c,
                           RendererConfiguration q) {
        if ( q.isSurfacesSet() ) {
            JoglGeometryRenderer.prepareSurfaceQuality(gl, q);

            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
            gl.glEnable(gl.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);

            if ( q.isTextureSet() ) {
                gl.glEnable(gl.GL_TEXTURE_2D);
            }
            else {
                gl.glDisable(gl.GL_TEXTURE_2D);
            }
            drawSurfaceGrid(gl, p, 1, 1, 0, 0, q);
        }
        if ( q.isWiresSet() ) {
            gl.glDisable(gl.GL_LIGHTING);
            gl.glDisable(gl.GL_CULL_FACE);
            gl.glDisable(gl.GL_TEXTURE_2D);
            gl.glShadeModel(gl.GL_FLAT);

            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
            gl.glDisable(gl.GL_POLYGON_OFFSET_LINE);
            gl.glLineWidth(1.0f);

            ColorRgb co = q.getWireColor();
            gl.glColor3d(co.r, co.g, co.b);
            gl.glDisable(gl.GL_TEXTURE_2D);

            drawSurfaceGrid(gl, p, 1, 1, 0, 0, q);
        }
        if ( q.isNormalsSet() ) {
            drawNormals(gl, p, 1, 1, 0, 0);
        }
        if ( q.isPointsSet() ) {
            drawControlPoints(gl, p);
        }
        if (q.isBoundingVolumeSet()) {
            JoglGeometryRenderer.drawMinMaxBox(gl, p, q);
        }
        if ( q.isSelectionCornersSet() ) {
            JoglGeometryRenderer.drawSelectionCorners(gl, p, q);
        }
    }

    public static void draw(GL2 gl, ParametricBiCubicPatch p, Camera c,
                           RendererConfiguration q,
                           int textureUSizeFactor, int tilling_y,
                           double textureURelaviteStart,
                           double textureVRelativeStart) {
        if ( q.isWiresSet() ) {
            gl.glDisable(gl.GL_LIGHTING);
            gl.glDisable(gl.GL_CULL_FACE);
            gl.glShadeModel(gl.GL_FLAT);

            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_LINE);
            gl.glDisable(gl.GL_POLYGON_OFFSET_LINE);
            gl.glLineWidth(1.0f);
            drawSurfaceGrid(gl, p, textureUSizeFactor, tilling_y,
                            textureURelaviteStart, textureVRelativeStart, q);
        }
        if ( q.isSurfacesSet() ) {
            JoglGeometryRenderer.prepareSurfaceQuality(gl, q);
            gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
            gl.glEnable(gl.GL_POLYGON_OFFSET_FILL);
            gl.glPolygonOffset(1.0f, 1.0f);
            drawSurfaceGrid(gl, p, textureUSizeFactor, tilling_y,
                            textureURelaviteStart, textureVRelativeStart, q);
        }
        if ( q.isNormalsSet() ) {
            drawNormals(gl, p, textureUSizeFactor, tilling_y,
                        textureURelaviteStart, textureVRelativeStart);
        }
        if ( q.isPointsSet() ) {
            drawControlPoints(gl, p);
        }
        if (q.isBoundingVolumeSet()) {
            JoglGeometryRenderer.drawMinMaxBox(gl, p, q);
        }
        if ( q.isSelectionCornersSet() ) {
            JoglGeometryRenderer.drawSelectionCorners(gl, p, q);
        }
    }
}

//===========================================================================
//= EOF                                                                     =
//===========================================================================
