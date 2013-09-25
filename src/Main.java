import vsdk.toolkit.common.linealAlgebra.Matrix4x4;
import vsdk.toolkit.common.linealAlgebra.Quaternion;
import vsdk.toolkit.common.linealAlgebra.Vector3D;


public class Main {
	static Matrix4x4 mat;
	
	public static void main(String[] args) {
		mat = new Matrix4x4();
		/*
		 * 
		 * yaw = no
		 * pitch = maybe
		 * roll = yes
		 */
		mat.eulerAnglesRotation(Math.PI/2.0, 0, 0);
		mat.eulerAnglesRotation(0, 0, Math.PI/2.0);
		Vector3D v1 = new Vector3D(0,0,0);
		Vector3D v2 = new Vector3D(1,0,0);
		Vector3D v3 = new Vector3D(0,-1,0);
		Vector3D v4 = new Vector3D(1,-1,0);
		
		System.out.println(mat.multiply(v1));
		System.out.println(mat.multiply(v2));
		System.out.println(mat.multiply(v3));
		System.out.println(mat.multiply(v4));
		
		
		
	}
}
