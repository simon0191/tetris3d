//===========================================================================

// Basic Java classes

// Awt / swing classes
import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JFrame;
import javax.swing.Timer;

// JOGL classes
import javax.media.opengl.GL2;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

import model.Model;
import model.shapes.Shape;
import model.shapes.TetrisBar;
import model.shapes.TetrisBox;
import model.shapes.TetrisL;
import model.shapes.TetrisT;

// VitralSDK classes
import view.render.ModelRenderer;
import view.render.TetrisShapeRenderer;
import view.render.WallRenderer;
import vsdk.toolkit.environment.Camera;              // Model elements
import vsdk.toolkit.environment.Light;
import vsdk.toolkit.environment.Material;
import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.RendererConfiguration;
import vsdk.toolkit.common.linealAlgebra.Vector3D;

import vsdk.toolkit.render.jogl.JoglCameraRenderer;  // View elements
import vsdk.toolkit.render.jogl.JoglLightRenderer;
import vsdk.toolkit.render.jogl.JoglMaterialRenderer;
import vsdk.toolkit.render.jogl.JoglRenderer;
import vsdk.toolkit.gui.CameraController;            // Controller elements
import vsdk.toolkit.gui.CameraControllerAquynza;

/**
Note that this program is designed to work as a java application, or as a
java applet.  If current class does not extends from Applet, and `init` method
is deleted, this will continue working as a simple java application.

This is a simple program recommended for use as a template in the development
of VitralSDK programs by incremental modification.
*/
/**
 * @author simon
 * This class provides a sample test of the Toroid class and the JoglToroidRenderer class.
 */
public class Tetris3d implements 
    GLEventListener,                                                    // JOGL
    KeyListener, MouseListener, MouseMotionListener, MouseWheelListener // GUI
{

//= PROGRAM PART 1/5: ATTRIBUTES ============================================
	public static boolean band = false;
    private boolean appletMode;
    private Camera camera;
    private RendererConfiguration qualitySpec;
    private CameraController cameraController;
    private GLCanvas canvas;
    
    private Light light,light2;
    private Material testMaterial;
    private int numPoints;
    
    private Shape shape = new TetrisT();
    private Model model = new Model();
    private Timer timer; 
//= PROGRAM PART 2/5: CONSTRUCTORS ==========================================

    /**
    When running this class inside a browser (in applet mode) there is no
    warranty of calling this method, or calling before init. It is recommended
    that real initialization be done in another `createModel` method, and
    that such method be called explicity from entry point function.
    */
    public Tetris3d() {
    	
	}

    /**
    Real constructor
    */
    private void createModel() {
        
        camera = new Camera();
        //camera.setPosition(new Vector3D(2.77, -2.52, 1.10));
        camera.setPosition(new Vector3D(3.17, -6.62, -3.45));
        qualitySpec = new RendererConfiguration();
        qualitySpec.setSurfaces(true);
        qualitySpec.setWires(true);
        qualitySpec.setPoints(true);

        cameraController = new CameraControllerAquynza(camera);
        
        
        testMaterial = new Material();
        testMaterial.setAmbient(new ColorRgb(0, 0, 0));
        testMaterial.setDiffuse(shape.getColor());
        testMaterial.setSpecular(new ColorRgb(1, 1, 1));
        testMaterial.setDoubleSided(false);
        testMaterial.setPhongExponent(40.0);
        
        light = new Light(Light.POINT, new Vector3D(3.0, -3.0, 3.0), new ColorRgb(1.0, 1.0, 1.0));
        light2 = new Light(Light.AMBIENT, new Vector3D(-3.0, 3.0, -3.0), new ColorRgb(1.0, 1.0, 1.0));
        
        timer = new javax.swing.Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tick();
			}
		});
        timer.start();
    }

    private void createGUI()
    {
        canvas = new GLCanvas();
        canvas.addGLEventListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
        canvas.addMouseWheelListener(this);
        canvas.addKeyListener(this);
    }

//= PROGRAM PART 3/5: ENTRY POINTS ==========================================

    public static void main (String[] args) {
        // Common VitralSDK initialization
        JoglRenderer.verifyOpenGLAvailability();
        Tetris3d instance = new Tetris3d();
        instance.appletMode = false;
        instance.createModel();

        // Create application based GUI
        JFrame frame;
        Dimension size;

        instance.createGUI();
        frame = new JFrame("Tetris 3D");
        frame.add(instance.canvas, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        size = new Dimension(640, 640);
        frame.setMinimumSize(size);
        frame.setSize(size);
        frame.setVisible(true);
        instance.canvas.requestFocusInWindow();
    }

//= PROGRAM PART 4/5: JOGL-OPENGL PROCEDURES ================================

    /**
     * @param gl
     */
    private void drawObjectsGL(GL2 gl)
    {
        gl.glEnable(gl.GL_DEPTH_TEST);

        gl.glLoadIdentity();

        gl.glPolygonMode(gl.GL_FRONT_AND_BACK, gl.GL_FILL);
        //corridor.drawGL(gl);
        
     // Configure for inside looking
        gl.glEnable(gl.GL_CULL_FACE);
        gl.glCullFace(gl.GL_BACK);
        

        gl.glLineWidth((float)3.0);
        gl.glBegin(gl.GL_LINES);
            gl.glColor3d(1, 0, 0);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(1, 0, 0);

            gl.glColor3d(0, 1, 0);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(0, 1, 0);

            gl.glColor3d(0, 0, 1);
            gl.glVertex3d(0, 0, 0);
            gl.glVertex3d(0, 0, 1);
        gl.glEnd();

        //drawWalls(gl);
        
        
        gl.glRotated(-90, 1, 0, 0);
        //gl.glTranslated(Shape.BOX_LENGTH/2, -Shape.BOX_LENGTH/2, 0);
        
        
        
        gl.glEnable(gl.GL_LIGHTING);
        JoglMaterialRenderer.activate(gl, testMaterial);
        JoglLightRenderer.activate(gl, light);
        JoglLightRenderer.activate(gl, light2);
       
        ModelRenderer.drawModel(gl, model, camera, qualitySpec);
        
        TetrisShapeRenderer.draw(gl, shape, camera, qualitySpec);
        
        gl.glDisable(gl.GL_LIGHTING);
        
        
    }

	private void drawWalls(GL2 gl) {
		// Floor
		gl.glColor3d(1.0, 0.0, 0.0);
        
        gl.glPushMatrix();{
        	gl.glRotated(90, 1, 0, 0);
        	gl.glRotated(90, 0, 0, 1);
        	gl.glTranslated(0, -5*(Shape.BOX_LENGTH)-Shape.SPACE, -8.0*(Shape.BOX_LENGTH+Shape.SPACE)-Shape.SPACE);
        	WallRenderer.draw(gl, 5, 5, camera, qualitySpec);
        }gl.glPopMatrix();
        
        int width = 5, height = 9;
        
        // Walls
        gl.glPushMatrix();{
        	WallRenderer.draw(gl, width, height, camera, qualitySpec);
        }gl.glPopMatrix();
        
        gl.glPushMatrix();{
        	gl.glRotated(-90, 0, 1, 0);
        	gl.glTranslated(0, 0, -5*(Shape.BOX_LENGTH)-Shape.SPACE);
        	WallRenderer.draw(gl, width, height, camera, qualitySpec);
        }gl.glPopMatrix();
        
        gl.glPushMatrix();{
        	gl.glRotated(90, 0, 1, 0);
        	gl.glTranslated(-5*(Shape.BOX_LENGTH)-Shape.SPACE, 0, 0);
        	WallRenderer.draw(gl, width, height, camera, qualitySpec);
        }gl.glPopMatrix();
        
        gl.glPushMatrix();{
        	gl.glRotated(180, 0, 1, 0);
        	gl.glTranslated(-5*(Shape.BOX_LENGTH)-Shape.SPACE, 0, -5*(Shape.BOX_LENGTH)-Shape.SPACE);
        	WallRenderer.draw(gl, width, height, camera, qualitySpec);
        }gl.glPopMatrix();
	}

    /** Called by drawable to initiate drawing */
    public void display(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();

        gl.glClearColor(0, 0, 0, 1);
        gl.glClear(gl.GL_COLOR_BUFFER_BIT | gl.GL_DEPTH_BUFFER_BIT);
        gl.glColor3d(1, 1, 1);

        JoglCameraRenderer.activate(gl, camera);

        drawObjectsGL(gl);
    }
   
    /** Not used method, but needed to instantiate GLEventListener */
    public void init(GLAutoDrawable drawable) {
        ;
    }

    /** Not used method, but needed to instantiate GLEventListener */
    public void dispose(GLAutoDrawable drawable) {
        ;
    }

    /** Not used method, but needed to instantiate GLEventListener */
    public void displayChanged(GLAutoDrawable drawable, boolean a, boolean b) {
        ;
    }
    
    /** Called to indicate the drawing surface has been moved and/or resized */
    public void reshape (GLAutoDrawable drawable,
                         int x,
                         int y,
                         int width,
                         int height) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glViewport(0, 0, width, height); 

        camera.updateViewportResize(width, height);
    }   

//= PART 5/5: GUI PROCEDURES ================================================

    public void keyPressed(KeyEvent e) {
    	switch (e.getKeyCode()) {
		case KeyEvent.VK_Q:
			model.rotateYes(shape);			
			break;
		case KeyEvent.VK_A:
			model.rotateYesReverse(shape);
			break;
		case KeyEvent.VK_W:
			model.rotateNo(shape);
			break;
		case KeyEvent.VK_S:
			model.rotateNoReverse(shape);
			break;
		case KeyEvent.VK_E:
			model.rotateMaybe(shape);
			break;
		case KeyEvent.VK_D:
			model.rotateMaybeReverse(shape);
			break;	
		case KeyEvent.VK_F7:
			System.out.println(camera.getPosition());
			break;
		case KeyEvent.VK_F10:
			qualitySpec.setPoints(!qualitySpec.isPointsSet());
			break;
		case KeyEvent.VK_F11:
			qualitySpec.setWires(!qualitySpec.isWiresSet());
			break;
		case KeyEvent.VK_F12:
			qualitySpec.setSurfaces(!qualitySpec.isSurfacesSet());
			
			break;
		case KeyEvent.VK_RIGHT:
			model.incrementDesfX(shape);
		break;
		case KeyEvent.VK_LEFT:
			model.decrementDesfX(shape);
			break;
		case KeyEvent.VK_UP:
			model.decrementDesfY(shape);
			break;
		case KeyEvent.VK_DOWN:
			model.incrementDesfY(shape);
			break;
		case KeyEvent.VK_P:
			if(timer.isRunning()){
				timer.stop();
			}
			else{
				timer.start();
			}
			break;
		case KeyEvent.VK_1:
			shape = new TetrisBar();
			break;
		case KeyEvent.VK_2:
			shape = new TetrisBox();
			break;
		case KeyEvent.VK_3:
			shape = new TetrisT();
			break;
		case KeyEvent.VK_4:
			shape = new TetrisL();
			break;
		case KeyEvent.VK_SPACE:
			tick();
			break;
		case KeyEvent.VK_C:
			Model.band = !Model.band;
			break;
		case KeyEvent.VK_R:
			camera.setPosition(new Vector3D(3.17, -6.62, -3.45));
			break;
		default:
			break;
		}
        canvas.repaint();
    }

    public void keyReleased(KeyEvent e) {
        if ( cameraController.processKeyReleasedEventAwt(e) ) {
            canvas.repaint();
        }
    }

    /**
    Do NOT call your controller from the `keyTyped` method, or the controller
    will be invoked twice for each key. Call it only from the `keyPressed` and
    `keyReleased` method
    */
    public void keyTyped(KeyEvent e) {
        ;
    }
    public void mouseEntered(MouseEvent e) {
        canvas.requestFocusInWindow();
    }

    public void mouseExited(MouseEvent e) {
        ;
    }

    public void mousePressed(MouseEvent e) {
        if ( cameraController.processMousePressedEventAwt(e) ) {
            canvas.repaint();
        }
    }

    public void mouseReleased(MouseEvent e) {
        if ( cameraController.processMouseReleasedEventAwt(e) ) {
            canvas.repaint();
        }
    }

    public void mouseClicked(MouseEvent e) {
        if ( cameraController.processMouseClickedEventAwt(e) ) {
            canvas.repaint();
        }
    }

    public void mouseMoved(MouseEvent e) {
        if ( cameraController.processMouseMovedEventAwt(e) ) {
            canvas.repaint();
        }
    }

    public void mouseDragged(MouseEvent e) {
        if ( cameraController.processMouseDraggedEventAwt(e) ) {
            canvas.repaint();
        }
    }

    public void mouseWheelMoved(MouseWheelEvent e) {
        if ( cameraController.processMouseWheelEventAwt(e) ) {
            canvas.repaint();
        }
    }
    private void tick() {
		if(model.incrementLevel(shape)){
			int rand = (int)(Math.random()*4.0);
			switch(rand){
			case 1:
				shape = new TetrisBox();
				break;
			case 2:
				shape = new TetrisT();
				break;
			case 3:
				shape = new TetrisBar();
				break;
			case 0:
				shape = new TetrisL();
				break;
			}
		}
		canvas.repaint();
	}
}

