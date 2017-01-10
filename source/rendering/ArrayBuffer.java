package com.gameinbucket.rendering;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

public class ArrayBuffer 
{
	private int[] attribute_buffer;
	private int[] index_buffer;
	
	private int index_count;
	
	public ArrayBuffer(FloatBuffer vertices, ShortBuffer indices, int vertex_c, int index_c)
	{
		index_count = index_c;
		
		attribute_buffer = new int[1];
		index_buffer = new int[1];
		
		GLES20.glGenBuffers(1, attribute_buffer, 0);
		GLES20.glGenBuffers(1, index_buffer, 0);
		
		bind();

	    GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertex_c * VectorMath.floatsize, vertices, GLES20.GL_STATIC_DRAW);
	    GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, index_c * VectorMath.shortsize, indices, GLES20.GL_STATIC_DRAW);
	    
	    unbind();
	}
	
	public void bind()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, attribute_buffer[0]);
	    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, index_buffer[0]);
	}
	
	public void unbind()
	{
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	public int ic()
	{
		return index_count;
	}
	
	public void delete()
	{
		GLES20.glDeleteBuffers(1, attribute_buffer, 0);
		GLES20.glDeleteBuffers(1, index_buffer, 0);
	}
}
