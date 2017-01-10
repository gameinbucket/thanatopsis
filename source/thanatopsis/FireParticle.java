package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.VectorMath;

public class FireParticle
{
	private static final float h = FireEngine.w * 2;
	
	private FireEngine engine;
	
	private float[] view_p;
	public int life;
	
	private float c;

	public FireParticle(float x, float y, float z, int t, FireEngine e)
	{
		engine = e;
		
		view_p = new float[3];

		view_p[0] = x;
		view_p[1] = y;
		view_p[2] = z;

		life = t;
	}

	public void reset(float x, float y, float z)
	{
		view_p[0] = x;
		view_p[1] = y;
		view_p[2] = z;
		
		life = engine.life_left;
		c = VectorMath.lerp(1.0f, 0.0f, (float)life / engine.life_left);
	}

	public void update()
	{
		life--;
		c = VectorMath.lerp(1.0f, 0.0f, (float)life / engine.life_left);
		
		view_p[1] += engine.air;
		
		view_p[0] += engine.draft_x * c;
		view_p[2] += engine.draft_z * c;
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
		
		engine.image[3] = c;
		engine.image[15] = c - 0.04f;
		engine.image[27] = c - 0.04f;
		engine.image[39] = c;

		return engine.image;
	}
}
