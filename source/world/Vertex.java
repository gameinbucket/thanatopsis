package com.gameinbucket.world;

public class Vertex 
{
	public float x;
	public float y;
	
	public Vertex(float xx, float yy)
	{
		x = xx;
		y = yy;
	}

	public void perp()
	{
		float temp = x;
		
		x = -y;
		y = temp;
	}
	
	public float dot(Vertex axis)
	{
		return (x * axis.x + y * axis.y);
	}
}
