package com.gameinbucket.world;

import com.gameinbucket.rendering.ArrayBuffer;

public class CubeSector 
{
	public CubeSector front;
	public CubeSector back;
	public CubeSector left;
	public CubeSector right;
	public CubeSector top;
	public CubeSector bottom;

	public int x1;
	public int y1;
	public int z1;

	public int x2;
	public int y2;
	public int z2;
	
	//public int xmid;
	//public int zmid;
	//public int size;

	public ArrayBuffer buffer;

	CubeSector(int xx1, int yy1, int zz1, int xx2, int yy2, int zz2)
	{
		x1 = xx1;
		y1 = yy1;
		z1 = zz1;

		x2 = xx2;
		y2 = yy2;
		z2 = zz2;
		
		//xmid = x1 + (x2 - x1) / 2;
		//zmid = z1 + (z2 - z1) / 2;
		//size = x2 - x1;
	}

	public void set_buffer(ArrayBuffer ab)
	{
		if (buffer != null)
			buffer.delete();

		buffer = ab;
	}

	public void delete_buffer()
	{
		if (buffer != null)
		{
			buffer.delete();
			buffer = null;
		}
	}
}
