package com.gameinbucket.rendering;

public class Indices 
{
	private short offset;
	public short position;
	public short[] indices;

	public Indices(int limit)
	{
		offset = 0;
		position = 0;
		indices = new short[limit];
	}

	public void add(int vertices)
	{
		int i = 0;
		int offset_add = 1;

		for (int v = 0; v < vertices - 2; v++)
		{
			for (int s = 0; s < 3; s++)
			{
				indices[position] = (short)(i + offset);

				i += offset_add;

				if (i >= vertices)
				{
					i = 0;
					offset_add *= 2;
				}
				else if (s == 2)
					i -= offset_add;
				
				position++;
			}
		}

		offset += vertices;
	}
	
	public void clear()
	{
		offset = 0;
		position = 0;
	}
}
