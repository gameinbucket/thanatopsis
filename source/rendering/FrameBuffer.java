package com.gameinbucket.rendering;

import android.opengl.GLES20;

public class FrameBuffer 
{
	public static final short TYPE_DRAW = 0;
	public static final short TYPE_SCREEN = 1;
	public static final short TYPE_VIEW = 2;
	
	private int[] frame = new int[1];
	private int[] texture = new int[1];
	
	public int width;
	public int height;
	
	public int offset_w;
	public int offset_h;
	
	public FrameBuffer(short t, int filter, int w, int h, int ow, int oh)
	{
		width = w;
		height = h;
		
		offset_w = ow;
		offset_h = oh;
		
		if (t == TYPE_DRAW) load_draw_buffer(filter);
		else if(t == TYPE_SCREEN) load_screen_buffer(filter);
		else if (t == TYPE_VIEW) {frame[0] = 0; texture[0] = 0;}
	}
	
	public int frame_buffer()
	{
		return frame[0];
	}
	
	public int texture()
	{
		return texture[0];
	}
	
	private void load_draw_buffer(int filter)
	{
		GLES20.glGenFramebuffers(1, frame, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
		
		GLES20.glGenTextures(1, texture, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		
		int[] render_buffer = new int[1];
		GLES20.glGenRenderbuffers(1, render_buffer, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, render_buffer[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
		
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture[0], 0);
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, render_buffer[0]);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
	}

	private void load_screen_buffer(int filter)
	{
		GLES20.glGenFramebuffers(1, frame, 0);
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frame[0]);
		
		GLES20.glGenTextures(1, texture, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture[0]);
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, filter);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, filter);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
		
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, texture[0], 0);
		
		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
	}
}
