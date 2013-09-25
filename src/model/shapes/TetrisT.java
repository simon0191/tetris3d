package model.shapes;

import vsdk.toolkit.common.ColorRgb;

public class TetrisT extends Shape {

	public TetrisT() {
		color = new ColorRgb(0.0,1.0,1.0);
		resetMatrix();
		matrix[0][0][0] = 
		matrix[0][1][0] =
		matrix[0][2][0] =
		matrix[1][1][0] =
		true;
	}
	@Override
	protected void resetMatrix() {
		matrix = new boolean[3][3][3];
	}

}
