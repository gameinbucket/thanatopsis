package com.gameinbucket.rendering;

public class Vertices 
{
	public int position;
	public float[] vertices;

	public Vertices(int limit)
	{
		position = 0;
		vertices = new float[limit];
	}

	public void add(float[] v)
	{
		for (int i = 0; i < v.length; i++)
		{
			vertices[position] = v[i];
			position++;
		}
	}

	public void clear()
	{
		position = 0;
	}
}
