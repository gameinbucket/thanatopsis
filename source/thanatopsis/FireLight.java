package com.gameinbucket.thanatopsis;

import android.opengl.GLES20;

import com.gameinbucket.rendering.Box;
import com.gameinbucket.rendering.Thing;
import com.gameinbucket.rendering.VectorMath;

public class FireLight extends Thing 
{
	private GameHandler game;
	public FireEngine engine;
	public Torch torch_box;
	
	private float attenuation_wave = 0;
	private float vibrance;
	
	private float ox = 0;
	private float oz = 0;
	
	private float gain;
	
	FireLight(float x, float y, float z, GameHandler g, float vib)
	{
		super(x, y, z, 0.7f);
		
		game = g;
		gain = vib;
		vibrance = gain + (float)Math.sin(attenuation_wave) * 0.18f;
	}
	
	public void set_offset(float x, float z)
	{
		ox = x;
		oz = z;
		
		if (ox == 0.4f)
		{
			engine = new FireEngine(view_p[0] + 0.2f, view_p[1] - 0.2f, view_p[2], game, 0.16f);
			torch_box = new Torch(view_p[0], view_p[1] - 0.6f, view_p[2], 0.14f, 0.7f, 0.14f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1, Box.box_front);
		}
		else if (ox == -0.4f)
		{
			engine = new FireEngine(view_p[0] - 0.2f, view_p[1] - 0.2f, view_p[2], game, 0.16f);
			torch_box = new Torch(view_p[0], view_p[1] - 0.6f, view_p[2], 0.14f, 0.7f, 0.14f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1, Box.box_bottom);
		}
		else if (oz == 0.4f)
		{
			engine = new FireEngine(view_p[0], view_p[1] - 0.2f, view_p[2] + 0.2f, game, 0.16f);
			torch_box = new Torch(view_p[0], view_p[1] - 0.6f, view_p[2], 0.14f, 0.7f, 0.14f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1, Box.box_right);
		}
		else if (oz == -0.4f)
		{
			engine = new FireEngine(view_p[0], view_p[1] - 0.2f, view_p[2] - 0.2f, game, 0.16f);
			torch_box = new Torch(view_p[0], view_p[1] - 0.6f, view_p[2], 0.14f, 0.7f, 0.14f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1, Box.box_left);
		}
		else
		{
			engine = new FireEngine(view_p[0], view_p[1] - 0.18f, view_p[2], game, 0.32f);
			torch_box = new Torch(view_p[0] - 0.48f, view_p[1] - 0.295f, view_p[2] - 0.16f, 0.96f, 0.2f, 0.32f, VectorMath.texture_8, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0 , 1, 1, Short.MAX_VALUE);
		}
	}

	public void set_light_uniforms(int s)
	{
		GLES20.glUniform3f(GLES20.glGetUniformLocation(s, "u_light_position"), view_p[0] + ox, view_p[1], view_p[2] + oz);
		GLES20.glUniform3f(GLES20.glGetUniformLocation(s, "u_light_color"), vibrance, vibrance * 0.5f, 0.0f);
		GLES20.glUniform1f(GLES20.glGetUniformLocation(s, "u_light_attenuation"), 0.01f);
	}
	
	public void update()
	{
		attenuation_wave += VectorMath.deg_to_rad(6);
		vibrance = gain + (float)Math.sin(attenuation_wave) * 0.18f;
		engine.update();
	}
	
	public float[] image()
	{
		return null;
	}
}
