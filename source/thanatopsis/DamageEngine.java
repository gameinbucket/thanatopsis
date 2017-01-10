package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.VectorMath;

public class DamageEngine
{
	public static final float w = 0.06f;
	
	private GameHandler game;
	public DamageParticle particles[];
	
	public float[] image;
	public float sine;
	public float cosine;
	
	public float acceleration;
	private int life;
	public boolean active = false;
	
	private java.util.Random random;
	
	DamageEngine(GameHandler g)
	{
		game = g;
		particles = new DamageParticle[10];

		image = new float[4 * VectorMath.stride_size];

		image[7] = 0.0f;
		image[8] = 0.0f;
		image[10] = 0.0f;

		image[19] = 0.0f;
		image[20] = 0.0f;
		image[22] = 0.0f;

		image[31] = 0.0f;
		image[32] = 0.0f;
		image[34] = 0.0f;

		image[43] = 0.0f;
		image[44] = 0.0f;
		image[46] = 0.0f;
		
		random = new java.util.Random(333);
		
		for (int i = 0; i < particles.length; i++)
			particles[i] = new DamageParticle(this);
	}
	
	public void activate(float x, float y, float z, float angle, float r, float g, float b, float a)
	{
		life = 180;
		acceleration = 0.008f;

		image[3] = r;
		image[15] = r;
		image[27] = r;
		image[39] = r;
		
		image[4] = g;
		image[16] = g;
		image[28] = g;
		image[40] = g;
		
		image[5] = b;
		image[17] = b;
		image[29] = b;
		image[41] = b;
		
		image[6] = a;
		image[18] = a;
		image[30] = a;
		image[42] = a;
		
		float ax = (float)Math.sin(angle) / 3;
		float az = -(float)Math.cos(angle) / 3;
		float v = 0.4f;
		
		for (int i = 0; i < particles.length; i++)
			particles[i].reset(x - 0.01f + random.nextFloat() * 0.02f, y - 0.01f + random.nextFloat() * 0.02f, z - 0.01f + random.nextFloat() * 0.02f, ax + random.nextFloat() * v, random.nextFloat() * v - v / 3.0f, az + random.nextFloat() * v);
		
		active = true;
	}
	
	public void update()
	{
		life--;
		
		if (life <= 0)
		{
			active = false;
			return;
		}
		
		acceleration += 0.016f;
		
		sine = (float)(w * Math.sin(game.rot_to_view));
		cosine = (float)(w * Math.cos(game.rot_to_view));
		
		for (int i = 0; i < particles.length; i++)
			particles[i].update();
	}
}
