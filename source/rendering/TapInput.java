package com.gameinbucket.rendering;

public class TapInput 
{
	protected int bound_top;
	protected int bound_bottom;
	protected int bound_left;
	protected int bound_right;
	
	protected int id;
	
	public TapInput(int bottom, int top, int left, int right)
	{
		bound_top = top;
		bound_bottom = bottom;
		bound_left = left;
		bound_right = right;
		
		reset();
	}
	
	protected void reset()
	{
		id = -1;
	}
	
	public void press(float xx, float yy, int i)
	{
		if (id == -1 && yy >= bound_bottom && yy <= bound_top && xx >= bound_left && xx <= bound_right)
			id = i;
	}
	
	public void release(int i)
	{
		if (id == i)
			reset();
	}
	
	public void release()
	{
		reset();
	}
	
	public boolean is_active()
	{
		if (id > -1)
			return true;
		
		return false;
	}
}
