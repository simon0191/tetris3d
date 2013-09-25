package model.shapes;

import vsdk.toolkit.common.ColorRgb;

public class TetrisBox extends Shape {
	
	
	public TetrisBox() {
		color = new ColorRgb(1.0,0.0,0.0);
		matrix = new boolean[2][2][2];
		matrix[0][0][0] = 
		matrix[1][0][0] =
		matrix[0][1][0] =
		matrix[1][1][0] =
		true;
	}

	@Override
	protected void resetMatrix() {
		matrix = new boolean[2][2][2];
		
	}
	
	
}

