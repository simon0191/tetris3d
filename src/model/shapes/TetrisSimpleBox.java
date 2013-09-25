package model.shapes;

public class TetrisSimpleBox extends Shape {
	
	private int x;
	private int y;
	private int z;
	public TetrisSimpleBox(int x, int y, int z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getZ() {
		return z;
	}
	public void setZ(int z) {
		this.z = z;
	}
	@Override
	protected void resetMatrix() {
		// TODO Auto-generated method stub
		
	}

}
