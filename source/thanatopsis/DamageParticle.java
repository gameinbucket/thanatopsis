package com.gameinbucket.thanatopsis;

public class DamageParticle
{
	private static final float h = DamageEngine.w * 2;
	
	private DamageEngine engine;
	private float[] view_p;
	
	private float vel_x;
	private float vel_y;
	private float vel_z;

	public DamageParticle(DamageEngine e)
	{
		engine = e;
		view_p = new float[3];
	}

	public void reset(float x, float y, float z, float vx, float vy, float vz)
	{
		view_p[0] = x;
		view_p[1] = y;
		view_p[2] = z;
		
		vel_x = vx;
		vel_y = vy;
		vel_z = vz;
	}

	public void update()
	{
		vel_x *= 0.8f;
		vel_y -= engine.acceleration;
		vel_z *= 0.8f;
		
		view_p[0] += vel_x;
		view_p[1] += vel_y;
		view_p[2] += vel_z;
	}

	public float[] image()
	{
		engine.image[0] = view_p[0] - engine.cosine;
		engine.image[1] = view_p[1] + h;
		engine.image[2] = view_p[2] + engine.sine;

		engine.image[12] = view_p[0] - engine.cosine;
		engine.image[13] = view_p[1];
		engine.image[14] = view_p[2] + engine.sine;

		engine.image[24] = view_p[0] + engine.cosine;
		engine.image[25] = view_p[1];
		engine.image[26] = view_p[2] - engine.sine;

		engine.image[36] = view_p[0] + engine.cosine;
		engine.image[37] = view_p[1] + h;
		engine.image[38] = view_p[2] - engine.sine;
		
		return engine.image;
	}
}
