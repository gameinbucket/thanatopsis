package com.gameinbucket.rendering;

public abstract class Sprite extends MovingThing
{
	//actions and drawing
	protected int present_action;

	//movement speed
	protected float pace;

	public Sprite(float x, float y, float z, float rad, int f_limit, int col, int[] anim, int rx, int ry, int rz, float speed)
	{
		super(x, y, z, rad, f_limit, col, anim, rx, ry, rz);

		pace = speed;
	}

	protected boolean collision_with_thing_x(Thing t, float i)
	{
		if (i - radius <= t.view_p[0] + t.radius && i + radius >= t.view_p[0] - t.radius &&
				view_p[2] - radius + 0.01f <= t.view_p[2] + t.radius && view_p[2] + radius - 0.01f >= t.view_p[2] - t.radius)
		{
			if (view_p[0] < t.view_p[0])
				view_p[0] = t.view_p[0] - t.radius - radius;
			else
				view_p[0] = t.view_p[0] + t.radius + radius;

			view_v[0] = 0;
			
			return true;
		}
		
		return false;
	}

	protected boolean collision_with_thing_z(Thing t, float i)
	{
		if (view_p[0] - radius + 0.01f <= t.view_p[0] + t.radius && view_p[0] + radius - 0.01f >= t.view_p[0] - t.radius &&
				i - radius <= t.view_p[2] + t.radius && i + radius >= t.view_p[2] - t.radius)
		{
			if (view_p[2] <  t.view_p[2])
				view_p[2] = t.view_p[2] - t.radius - radius;
			else
				view_p[2] = t.view_p[2] + t.radius + radius;

			view_v[2] = 0;
			
			return true;
		}
		
		return false;
	}
	
	protected void collision_with_box_x(Box b, float i)
	{
		
		if (i - radius <= b.view_p[0] + b.width && i + radius >= b.view_p[0] &&
				view_p[2] - radius + 0.01f <= b.view_p[2] + b.length && view_p[2] + radius - 0.01f >= b.view_p[2])
		{
			if (view_p[0] < b.view_p[0])
				view_p[0] = b.view_p[0] - radius;
			else
				view_p[0] = b.view_p[0] + b.width + radius;

			view_v[0] = 0;
		}
	}

	protected void collision_with_box_z(Box b, float i)
	{
		if (view_p[0] - radius + 0.01f <= b.view_p[0] + b.width && view_p[0] + radius - 0.01f >= b.view_p[0] &&
				i - radius <= b.view_p[2] + b.length && i + radius >= b.view_p[2])
		{
			if (view_p[2] <  b.view_p[2])
				view_p[2] = b.view_p[2] - radius;
			else
				view_p[2] = b.view_p[2] + b.length + radius;

			view_v[2] = 0;
		}
	}

	public void update()
	{
		update_input();
		update_position();
		update_animation();
	}

	protected abstract void update_input();
	protected abstract void update_position();
	protected abstract void update_animation();
}
