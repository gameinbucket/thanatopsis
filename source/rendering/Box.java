package com.gameinbucket.rendering;

public class Box 
{
	public static final short box_sides = 6;
	public static final short box_front = 0;
	public static final short box_back = 1;
	public static final short box_left = 2;
	public static final short box_right = 3;
	public static final short box_top = 4;
	public static final short box_bottom = 5;

	//position
	public float[] view_p;

	public float width;
	public float length;

	protected float[][] box;

	public Box(float x, float y, float z, float w, float h, float l, float s, float f1x, float f1y, float f2x, float f2y, float b1x, float b1y, float b2x, float b2y, float l1x, float l1y, float l2x, float l2y, float r1x, float r1y, float r2x, float r2y, float u1x, float u1y, float u2x, float u2y, float d1x, float d1y, float d2x, float d2y)
	{
		view_p = new float[3];
		
		view_p[0] = x;
		view_p[1] = y;
		view_p[2] = z;
		
		width = w;
		length = l;
		
		box = VectorMath.create_box(w, h, l, s, f1x, f1y, f2x, f2y, b1x, b1y, b2x, b2y, l1x, l1y, l2x, l2y, r1x, r1y, r2x, r2y, u1x, u1y, u2x, u2y, d1x, d1y, d2x, d2y);
		
		for (int side = 0; side < box_sides; side++)
		{
			for (int i = 0; i < box[0].length; i += VectorMath.stride_size)
			{
				box[side][i] += view_p[0];
				box[side][i + 1] += view_p[1];
				box[side][i + 2] += view_p[2];
			}
		}
	}
	
	public void activate()
	{
		
	}

	public float[] image(int s)
	{
		return box[s];
	}
}
