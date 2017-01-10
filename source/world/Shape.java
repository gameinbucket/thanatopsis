package com.gameinbucket.world;

public abstract class Shape 
{
	public float center_x;
	public float center_y;
	
	public abstract void translate(float x, float y);
	public abstract void translate(Vertex v);
	public abstract void translate_to(float x, float y);
	protected abstract float[] find_min_max(Vertex axis);
	
	public abstract Vertex collision(Circle circle);
	public abstract Vertex collision(Polygon poly);
}
