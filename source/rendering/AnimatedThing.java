package com.gameinbucket.rendering;

public abstract class AnimatedThing extends Thing
{
	//animation
	protected int frame;
	protected int frame_increment;
	private int frame_limiter;

	private int columns;
	public int[] animation;

	public AnimatedThing(float x, float y, float z, float rad, int f_limit, int col, int[] anim)
	{
		super(x, y, z, rad);

		frame_limiter = f_limit;
		columns = col;
		
		set_animation(anim, 0);
	}
	
	protected boolean is_animation_done()
	{
		if (frame == 0 && frame_increment == 0)
			return true;

		return false;
	}
	
	protected boolean is_animation_last()
	{
		if (frame == animation.length - 1 && frame_increment == 0)
			return true;

		return false;
	}

	protected void set_animation(int[] a, int f)
	{
		animation = a;

		frame = f;
		frame_increment = 0;

		texture_position();
	}

	protected void next_frame()
	{
		frame_increment++;

		if (frame_increment == frame_limiter)
		{
			frame++;
			frame_increment = 0;

			if (frame > animation.length - 1)
				frame = 0;

			texture_position();
		}
	}

	private void texture_position()
	{
		int x = animation[frame] % columns;
		int y = animation[frame] / columns;

		tex0 = x;
		tex2 = x + 1;

		tex1 = y;
		tex3 = y + 1;
	}
}
