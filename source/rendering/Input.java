package com.gameinbucket.rendering;

import com.gameinbucket.rendering.VectorMath;

public class Input 
{
	private float[] vertex_data;
	
	private int x;
	private int y;
	
	private int width;
	private int height;
	
	private int id;
	
	public Input(int xx, int yy, int w, int h, float left, float top, float right, float bottom)
	{
		id = -1;
		
		x = xx;
		y = yy;
		
		width = w;
		height = h;
		
		vertex_data = new float[4 * VectorMath.stride_size];

		vertex_data[0] = x;
		vertex_data[1] = y + h;
		vertex_data[2] = 0.0f;
		vertex_data[3] = 1.0f;
		vertex_data[4] = 1.0f;
		vertex_data[5] = 1.0f;
		vertex_data[6] = 1.0f;
		vertex_data[7] = left;
		vertex_data[8] = top;
		vertex_data[9] = 0.0f;
		vertex_data[10] = 0.0f;
		vertex_data[11] = 1.0f;
		
		vertex_data[12] = x;
		vertex_data[13] = y;
		vertex_data[14] = 0.0f;
		vertex_data[15] = 1.0f;
		vertex_data[16] = 1.0f;
		vertex_data[17] = 1.0f;
		vertex_data[18] = 1.0f;
		vertex_data[19] = left;
		vertex_data[20] = bottom;
		vertex_data[21] = 0.0f;
		vertex_data[22] = 0.0f;
		vertex_data[23] = 1.0f;
		
		vertex_data[24] = x + w;
		vertex_data[25] = y;
		vertex_data[26] = 0.0f;
		vertex_data[27] = 1.0f;
		vertex_data[28] = 1.0f;
		vertex_data[29] = 1.0f;
		vertex_data[30] = 1.0f;
		vertex_data[31] = right;
		vertex_data[32] = bottom;
		vertex_data[33] = 0.0f;
		vertex_data[34] = 0.0f;
		vertex_data[35] = 1.0f;
		
		vertex_data[36] = x + w;
		vertex_data[37] = y + h;
		vertex_data[38] = 0.0f;
		vertex_data[39] = 1.0f;
		vertex_data[40] = 1.0f;
		vertex_data[41] = 1.0f;
		vertex_data[42] = 1.0f;
		vertex_data[43] = right;
		vertex_data[44] = top;
		vertex_data[45] = 0.0f;
		vertex_data[46] = 0.0f;
		vertex_data[47] = 1.0f;
	}
	
	public void press(float xx, float yy, int i)
	{
		if (id > -1)
			return;

		if (xx >= x && xx <= x + width && yy >= y && yy <= y + height)
			id = i;
	}
	
	public void release(int i)
	{
		if (id == i)
			id = -1;
	}
	
	public void release()
	{
		id = -1;
	}
	
	public boolean is_active()
	{
		if (id > -1)
			return true;
		
		return false;
	}
	
	public float[] image()
	{
		return vertex_data;
	}
}
