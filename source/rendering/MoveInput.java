package com.gameinbucket.rendering;

public class MoveInput extends TapInput
{
	protected static final float double_time_milliseconds = 240;
	protected static final float distance_limit = 40.0f;
	
	private float origin_x;
	private float origin_y;
	
	public float distance;
	public float angle;
	
	public boolean is_double_press;
	protected long begin_time;
	
	public MoveInput(int bottom, int top, int left, int right)
	{
		super(bottom, top, left, right);
	}
	
	protected void reset()
	{
		is_double_press = false;
		id = -1;
		
		distance = 0;
	}
	
	public void press(float xx, float yy, int i)
	{
		if (id == -1 && yy >= bound_bottom && yy <= bound_top && xx >= bound_left && xx <= bound_right)
		{
			long now_time = System.currentTimeMillis();
			
			if (now_time - begin_time < double_time_milliseconds)
				is_double_press = true;
			
			begin_time = now_time;

			origin_x = xx;
			origin_y = yy;

			id = i;
		}
	}
	
	public void move(float xx, float yy, int i)
	{
		if (id == i)
		{
			distance = (float)Math.sqrt((origin_x - xx) * (origin_x - xx) + (origin_y - yy) * (origin_y - yy));
			
			if (distance > distance_limit)
				distance = distance_limit;
			
			angle = (float)Math.atan2(origin_y - yy, origin_x - xx);
		}
	}
}
