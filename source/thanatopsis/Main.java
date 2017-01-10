package com.gameinbucket.thanatopsis;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class Main extends Activity 
{
	private ViewHandler view;

	/*public static void print(String s)
	{
		android.util.Log.v("print", s);
	}*/

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		//print("--------------------");
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		view = new ViewHandler(this);

		super.onCreate(savedInstanceState);
		setContentView(view);

		//print("on create at " + System.currentTimeMillis());
	}

	@Override
	protected void onPause()
	{
		super.onPause();
		view.onPause();

		//print("paused at " + System.currentTimeMillis());
	}

	@Override
	protected void onResume() 
	{
		super.onResume();
		view.onResume();

		//print("resumed at " + System.currentTimeMillis());
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//print("destroyed at " + System.currentTimeMillis());
	}

	@Override
	public void onBackPressed()
	{
		view.render.state_function.input_back(this);
	}
}