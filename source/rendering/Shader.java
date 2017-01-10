package com.gameinbucket.rendering;

import android.opengl.GLES20;

public class Shader 
{
	public final int id;
	public final int attribs;
	public final int[] a_index;
	
	public Shader(int location, int[] a)
	{
		id = location;
		attribs = a.length;
		a_index = a;
	}
	
	public void use()
	{
		GLES20.glUseProgram(id);
		
		for (int i = 0; i < attribs; i++)
			GLES20.glEnableVertexAttribArray(i);
	}
	
	public void pointers(GeometryBuffer gb)
	{
		for (int i = 0; i < attribs; i++)
			GLES20.glVertexAttribPointer(i, VectorMath.attribute_size[a_index[i]], GLES20.GL_FLOAT, false, VectorMath.stride_bytes, gb.vbp(VectorMath.offset_size[a_index[i]]));
	}
	
	public void pointers()
	{
		for (int i = 0; i < attribs; i++)
			GLES20.glVertexAttribPointer(i, VectorMath.attribute_size[a_index[i]], GLES20.GL_FLOAT, false, VectorMath.stride_bytes, VectorMath.offset_bytes[a_index[i]]);
	}
}
