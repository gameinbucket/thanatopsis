package com.gameinbucket.thanatopsis;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class ViewHandler extends GLSurfaceView 
{
	public Context context;
	public RenderHandler render;
	public GameHandler game;
	
	ViewHandler(Context c)
	{
		super(c);
		context = c;

		setEGLContextClientVersion(2);
		
		//renderer
		render = new RenderHandler(this);
		
		//game state
		game = new GameHandler(this);
		
		setRenderer(render);
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		game.view_close();
		game.music.pause();
	}

	@Override
	public void onResume() 
	{
		super.onResume();
		game.music.resume();
	}

	@Override
	public boolean onTouchEvent(MotionEvent e)
	{
		if (render.state_function == null)
			return true;
		
		final int action = e.getAction();

		switch (action & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_POINTER_DOWN:
		{
			final int index = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

			render.state_function.input_down(e.getX(index), e.getY(index), e.getPointerId(index));
			break;
		}
		case MotionEvent.ACTION_MOVE:
		{
			for (int i = 0; i < e.getPointerCount(); i++)
				render.state_function.input_move(e.getX(i), e.getY(i), e.getPointerId(i));

			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_POINTER_UP:
		{
			render.state_function.input_up(e.getPointerId((action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT));
			break;
		}
		}

		return true;
	}
}