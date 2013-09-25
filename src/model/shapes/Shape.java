package model.shapes;

import java.util.ArrayList;

import model.Model;

import vsdk.toolkit.common.ColorRgb;
import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.linealAlgebra.Vector3D;

public abstract class Shape implements Cloneable {
	public static final double BOX_LENGTH = 1;
	public static final double SPACE = 0.1;
	
	public static final Matrix4x4 yesMat;
	public static final Matrix4x4 noMat;
	public static final Matrix4x4 maybeMat;
	
	protected boolean[][][] matrix;
	protected ColorRgb color;
	protected int desfX;
	protected int desfY;
	protected int level;
	
	static {
		yesMat = new Matrix4x4();
		yesMat.eulerAnglesRotation(0, 0, Math.PI/2.0);
		
		noMat = new Matrix4x4();
		noMat.eulerAnglesRotation(Math.PI/2.0, 0, 0);
		
		maybeMat = new Matrix4x4();
		maybeMat.eulerAnglesRotation(0, Math.PI/2.0, 0);
	}
	
	protected Shape(){
		desfX = desfY = 1; 
		level = 0;
	}
	
	public boolean[][][] getMatrix() {
		return matrix;
	}

	public void rotateYes() {
		rotate(yesMat);
	}

	public void rotateNo() {
		rotate(noMat);
	}

	public void rotateMaybe() {
		rotate(maybeMat);
	}
	
	private void rotate(Matrix4x4 rotationMatrix) {
		ArrayList<Vector3D> vec = new ArrayList<Vector3D>();
		int xMin = 0;
		int yMin = 0;
		int zMin = 0;
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				for (int k = 0; k < matrix[i][j].length; k++) {
					if(matrix[i][j][k]) {
						xMin = i;
						yMin = j;
						zMin = k;
						vec.add(rotationMatrix.multiply(new Vector3D(i, j, k)));
					}
				}
			}
		}
		
		resetMatrix();
		int auxX,auxY,auxZ;
		for(Vector3D v:vec){
			auxX = (int) (v.x<0.0?v.x-0.5:v.x+0.5);
			auxY = (int) (v.y<0.0?v.y-0.5:v.y+0.5);
			auxZ = (int) (v.z<0.0?v.z-0.5:v.z+0.5);
			xMin = Math.min(xMin, auxX);
			yMin = Math.min(yMin, auxY);
			zMin = Math.min(zMin, auxZ);
		}
		for(Vector3D v:vec){
			auxX = (int) (v.x<0.0?v.x-0.5:v.x+0.5);
			auxY = (int) (v.y<0.0?v.y-0.5:v.y+0.5);
			auxZ = (int) (v.z<0.0?v.z-0.5:v.z+0.5);
			
			matrix[auxX-xMin][auxY-yMin][auxZ-zMin] = true;
		}
		
	}
	
	protected abstract void resetMatrix();

	public ColorRgb getColor() {
		return color;
	}

	public int getDesfX() {
		return desfX;
	}

	public void setDesfX(int desfX) {
		this.desfX = desfX;
	}

	public int getDesfY() {
		return desfY;
	}

	public void setDesfY(int desfY) {
		this.desfY = desfY;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void incrementDesfX() {
		desfX++;
	}

	public void decrementDesfX() {
		desfX--;
		
	}

	public void incrementDesfY() {
		desfY++;
	}

	public void decrementDesfY() {
		desfY--;
	}

	public void rotate(int rotation) {
		switch(rotation){
		case Model.YES:
			rotateYes();
			break;
		case Model.NO:
			rotateNo();
			break;
		case Model.MAYBE:
			rotateMaybe();
			break;
		}
	}
	
}
