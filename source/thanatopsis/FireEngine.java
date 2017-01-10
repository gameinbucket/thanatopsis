package com.gameinbucket.thanatopsis;

import com.gameinbucket.rendering.VectorMath;

public class FireEngine
{
	public static final float w = 0.06f;
	
	private GameHandler game;
	public FireParticle particles[];

	public float sine;
	public float cosine;
	
	public float air;
	public int life_left;
	
	private float pos_x;
	private float pos_y;
	private float pos_z;
	
	private float rad;
	private java.util.Random random;
	
	public float[] image;
	
	private float draft_rad;
	private float draft_shift_x;
	private float draft_shift_z;
	public float draft_x;
	public float draft_z;
	
	FireEngine(float x, float y, float z, GameHandler g, float r)
	{
		game = g;
		particles = new FireParticle[30];
		
		pos_x = x;
		pos_y = y;
		pos_z = z;
		
		air = 0.03f;
		life_left = 25;

		image = new float[4 * VectorMath.stride_size];
		
		image[4] = 0.0f;
		image[5] = 0.0f;
		image[6] = 1.0f;
		image[7] = 0.0f;
		image[8] = 0.0f;
		image[10] = 0.0f;

		image[16] = 0.0f;
		image[17] = 0.0f;
		image[18] = 1.0f;
		image[19] = 0.0f;
		image[20] = 0.0f;
		image[22] = 0.0f;

		image[28] = 0.0f;
		image[29] = 0.0f;
		image[30] = 1.0f;
		image[31] = 0.0f;
		image[32] = 0.0f;
		image[34] = 0.0f;

		image[40] = 0.0f;
		image[41] = 0.0f;
		image[42] = 1.0f;
		image[43] = 0.0f;
		image[44] = 0.0f;
		image[46] = 0.0f;
		
		rad = r;
		random = new java.util.Random(99);
		
		draft_rad = 0.015f;
		draft_shift_x = random.nextFloat() * draft_rad * 2 - draft_rad;
		draft_shift_z = random.nextFloat() * draft_rad * 2 - draft_rad;
		draft_x = 0.0f;
		draft_z = 0.0f;
		
		for (int i = 0; i < particles.length; i++)
			particles[i] = new FireParticle(pos_x - rad + random.nextFloat() * rad * 2, VectorMath.lerp(pos_y, pos_y + air * life_left, i / particles.length), pos_z - rad + random.nextFloat() * rad * 2, (int)VectorMath.lerp(life_left, 0.0f, (float)i / particles.length), this);
	}
	
	public void update()
	{
		sine = (float)(w * Math.sin(game.rot_to_view));
		cosine = (float)(w * Math.cos(game.rot_to_view));
		
		draft_x += draft_shift_x;
		draft_z += draft_shift_z;
		
		if (draft_x <= -draft_rad)
			draft_shift_x = random.nextFloat() * draft_rad / 30;
		else if (draft_x >= draft_rad)
			draft_shift_x = random.nextFloat() * -draft_rad / 30;
		
		if (draft_z <= -draft_rad)
			draft_shift_z = random.nextFloat() * draft_rad / 30;
		else if (draft_z >= draft_rad)
			draft_shift_z = random.nextFloat() * -draft_rad / 30;
		
		for (int i = 0; i < particles.length; i++)
		{
			particles[i].update();
			
			if (particles[i].life <= 0)
				particles[i].reset(pos_x - rad + random.nextFloat() * rad * 2, pos_y, pos_z - rad + random.nextFloat() * rad * 2);
		}
	}
}
