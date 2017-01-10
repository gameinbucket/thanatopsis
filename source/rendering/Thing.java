package com.gameinbucket.rendering;

public abstract class Thing 
{
	//position
	public float[] view_p;
	public float radius;
	
	//image
	protected int tex0;
	protected int tex1;
	protected int tex2;
	protected int tex3;
	
	public boolean remove;
	
	public Thing(float x, float y, float z, float rad)
	{
		view_p = new float[3];
		
		view_p[0] = x;
		view_p[1] = y;
		view_p[2] = z;
		
		radius = rad;
		
		remove = false;
	}
	
	public abstract void update();
	public abstract float[] image();
	
	public float angle_to_thing(Thing t)
	{		
		return (float)(Math.atan2(t.view_p[2] - view_p[2], t.view_p[0] - view_p[0]) + VectorMath.rad_90);
	}

	public float distance_to_thing(Thing t)
	{
		float dist_x = t.view_p[0] - view_p[0];
		float dist_z = t.view_p[2] - view_p[2];

		return (dist_x * dist_x + dist_z * dist_z);
	}
	
	protected boolean touching_thing(Thing t)
	{
		if (view_p[0] - radius <= t.view_p[0] + t.radius && view_p[0] + radius >= t.view_p[0] - t.radius &&
				view_p[2] - radius <= t.view_p[2] + t.radius && view_p[2] + radius >= t.view_p[2] - t.radius)
			return true;
		
		return false;
	}
}
