package model.shapes;

import vsdk.toolkit.common.ColorRgb;

public class TetrisBar extends Shape {

	public TetrisBar() {
		color = new ColorRgb(0.0,1.0,0.0);
		matrix = new boolean[4][4][4];
		matrix[0][0][0] =
		matrix[0][1][0] =
		matrix[0][2][0] =
		matrix[0][3][0] =
		true;
		
	}
	@Override
	protected void resetMatrix() {
		matrix = new boolean[4][4][4];
	}

}
