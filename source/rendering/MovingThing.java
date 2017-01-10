package com.gameinbucket.rendering;

public abstract class MovingThing extends AnimatedThing
{
	//rotation
	public float[] view_r;
	
	//movement
	protected float[] view_v;
	
	public MovingThing(float x, float y, float z, float rad, int f_limit, int col, int[] anim, int rx, int ry, int rz)
	{
		super(x, y, z, rad, f_limit, col, anim);
		
		view_r = new float[3];
		
		view_r[0] = rx;
		view_r[1] = ry;
		view_r[2] = rz;
		
		view_v = new float[3];
	}
	
	public void set_matrix(float[] m)
	{
		MatrixMath.rotate_x(m, VectorMath.deg_to_rad(view_r[0]));
		MatrixMath.rotate_y(m, VectorMath.deg_to_rad(view_r[1]));
		MatrixMath.rotate_z(m, VectorMath.deg_to_rad(view_r[2]));
		MatrixMath.translate(m, -view_p[0], -view_p[1], -view_p[2]);
	}
}
