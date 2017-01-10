package com.gameinbucket.thanatopsis;

public abstract class StateHandler 
{
	RenderHandler r;
	
	StateHandler(RenderHandler render)
	{
		r = render;
	}

	public abstract void release();
	public abstract void input_down(float x, float y, int i);
	public abstract void input_move(float x, float y, int i);
	public abstract void input_up(int i);
	public abstract void input_back(Main m);
	public abstract void update();
	public abstract void draw();
}
